package com.aiguibin.core.common;

import com.aiguibin.core.dictionary.SuperintendentDict;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;

public class ExcelHelper {
    public static final Log logger = LogFactory.getLog(ExcelHelper.class);


    public static final String DEFAULT_MERGE_SHEET_NAME = "目录";

    private static final int BATCH_SIZE = 50000;
    private static final int MAX_IN_MEMORY_ROWS = 1000;
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    @FunctionalInterface
    public interface WorkbookProcessor {
        void process(Workbook workbook);
    }

    /**
     * 检查Excel文件中的Sheet名称及顺序是否符合预期规范
     *
     * @param folderPath     要检查的文件夹路径
     * @param expectedSheets 预期Sheet配置（JSON格式，key为sheet索引字符串，如"0"）
     * @param isRecursive    是否递归检查子目录
     */
    public static void validateSheetNames(Path folderPath, JSONObject expectedSheets, boolean isRecursive) {
        // 将JSONObject转换为Map<Integer, String>
        Map<Integer, String> sheetMap = new HashMap<>();
        for (String key : expectedSheets.keySet()) {
            try {
                int index = Integer.parseInt(key);
                sheetMap.put(index, expectedSheets.getString(key));
            } catch (NumberFormatException e) {
                logger.error("无效的Sheet索引格式: " + key + "，必须为整数");
                throw new IllegalArgumentException("Sheet索引必须是数字字符串", e);
            }
        }

        // 执行文件遍历检查
        try {
            Files.walk(folderPath, isRecursive ? Integer.MAX_VALUE : 1)
                    .filter(Files::isRegularFile)
                    .filter(ExcelHelper::isExcelFile)
                    .forEach(file -> checkSingleFile(file, sheetMap));
        } catch (IOException e) {
            logger.error("遍历目录失败: " + folderPath, e);
        }
    }

    /**
     * 检查单个Excel文件
     */
    public static void checkSingleFile(Path file, Map<Integer, String> expectedSheets) {
        try (Workbook workbook = readWorkbook(file)) {
            // 检查Sheet数量
            if (workbook.getNumberOfSheets() < expectedSheets.size()) {
                logger.warn("[文件不规范] " + file.getFileName()
                        + " - Sheet数量不足，预期:" + expectedSheets.size());
                return;
            }

            // 逐个检查Sheet名称
            boolean isValid = true;
            StringBuilder errorMsg = new StringBuilder();

            for (Map.Entry<Integer, String> entry : expectedSheets.entrySet()) {
                int sheetIndex = entry.getKey();
                String expectedName = entry.getValue();

                if (sheetIndex >= workbook.getNumberOfSheets()) {
                    errorMsg.append("缺失第").append(sheetIndex).append("个Sheet; ");
                    isValid = false;
                    continue;
                }

                String actualName = workbook.getSheetName(sheetIndex);
                if (!actualName.equals(expectedName)) {
                    errorMsg.append("Sheet").append(sheetIndex)
                            .append("名称错误（预期:'").append(expectedName)
                            .append("'，实际:'").append(actualName).append("'); ");
                    isValid = false;
                }
            }

            // 输出错误日志
            if (!isValid) {
                logger.warn("[文件不规范] " + file.getFileName()
                        + " - " + errorMsg);
            }
        } catch (Exception e) {
            logger.error("检查文件失败: " + file, e);
        }
    }


    /**
     * 带文件路径处理的通用工作簿处理方法
     *
     * @param inputFile  输入文件路径
     * @param outputFile 输出文件路径
     * @param processor  工作簿处理器
     */
    public static void processWorkbookWithHandler(Path inputFile, Path outputFile,
                                                  WorkbookProcessor processor) {
        try {
            Workbook workbook = readWorkbook(inputFile);
            processor.process(workbook);
            saveWorkbook(workbook, outputFile);
            logger.info("文件处理完成: " + outputFile);
        } catch (Exception e) {
            logger.error("文件处理失败: " + inputFile, e);
            throw new RuntimeException("Excel处理异常", e);
        }
    }

    /**
     * 处理Sheet页提取表名（通用实现）
     *
     * @param sheet             目标Sheet对象（非空）
     * @param sqlColumnIndex    SQL文本所在列的索引（从0开始）
     * @param resultColumnIndex 结果写入列的索引（从0开始）
     * @param pattern           表名匹配正则表达式（非空）
     * @throws IllegalArgumentException 当参数不合法时抛出
     */
    public static void processSheetForTableNames(Sheet sheet, int sqlColumnIndex,
                                                 int resultColumnIndex, Pattern pattern) {
        // 参数校验
        if (sheet == null) {
            throw new IllegalArgumentException("Sheet不能为null");
        }
        if (sqlColumnIndex < 0 || resultColumnIndex < 0) {
            throw new IllegalArgumentException("列索引不能为负数");
        }
        if (pattern == null) {
            throw new IllegalArgumentException("正则表达式不能为null");
        }

        // 确定数据起始行（默认跳过首行标题）
        int startRowIndex = sheet.getFirstRowNum() == 0 ? 1 : sheet.getFirstRowNum();

        // 遍历处理数据行
        for (int rowNum = startRowIndex; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row == null) continue;

            try {
                // 获取SQL文本单元格
                Cell sqlCell = row.getCell(sqlColumnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String rawSql = sqlCell.getStringCellValue();

                // 清洗SQL文本
                String cleanedSql = rawSql.replaceAll("<br>", " ")   // 替换换行标记
                        .replaceAll("\\s+", " ")   // 合并连续空格
                        .trim();

                // 提取并处理表名
                Matcher matcher = pattern.matcher(cleanedSql);
                if (matcher.find()) {
                    String tableName = matcher.group(1)
                            .split("\\s+")[0]         // 去除别名（如"table AS t"）
                            .replaceAll(".*\\.", ""); // 去除数据库模式前缀（如"schema.table"）

                    // 写入结果列
                    Cell resultCell = row.createCell(resultColumnIndex);
                    resultCell.setCellValue(tableName);
                }
            } catch (Exception e) {
                logger.error("处理第" + (rowNum + 1) + "行时发生错误: " + e.getMessage());
                // 可添加调试日志记录原始SQL文本
                logger.debug("错误行原始内容: " + rowToString(row));
            }
        }
    }


    public static String rowToString(Row row) {
        return StreamSupport.stream(row.spliterator(), false)
                .map(cell -> cell.toString())
                .collect(Collectors.joining("|"));
    }


    /**
     * 合并指定目录下的Excel文件中的目标Sheet到新文件
     *
     * @param sourceDir  源目录
     * @param outputFile 输出文件路径
     * @param sheetNames 需要合并的Sheet名称（默认"目录"）
     */
    public static void mergeDirSheetsToNewOneSheet(Path sourceDir, Path outputFile, String[] sheetNames,
                                                   int startColumn, int endColumn, boolean isNewStyle, int[] skipEmptyCellOfColumn) throws IOException {

        // 参数校验
        if (sheetNames == null || sheetNames.length == 0) {
            throw new IllegalArgumentException("至少需要指定一个Sheet名称");
        }

        if (Files.exists(outputFile) && !Files.deleteIfExists(outputFile)) {
            throw new IOException("无法删除旧文件: " + outputFile);
        }

        try (Workbook mergedWorkbook = new XSSFWorkbook()) {
            Sheet mergedSheet = mergedWorkbook.createSheet("result");
            // 全局行计数器
            AtomicInteger rowCounter = new AtomicInteger(0);

            Files.walk(sourceDir)
                    .filter(Files::isRegularFile)
                    .filter(ExcelHelper::isExcelFile)
                    .sorted() // 保证处理顺序一致性
                    .forEach(file -> processExcelFile(file, sheetNames, mergedWorkbook, mergedSheet, rowCounter, startColumn, endColumn, isNewStyle, skipEmptyCellOfColumn));

            saveWorkbook(mergedWorkbook, outputFile);
            logger.info("合并完成，结果已保存至：" + outputFile.toAbsolutePath());
        }
    }

    /**
     * 处理单个Excel文件，提取目标Sheet内容并合并到目标工作簿
     *
     * @param file           当前处理的Excel文件路径
     * @param mergedWorkbook 合并用的目标工作簿对象
     * @param mergedSheet    合并用的目标Sheet对象
     * @param rowCounter     行号计数器（线程安全）
     */
    public static void processExcelFile(Path file, String[] sheetNames, Workbook mergedWorkbook, Sheet mergedSheet,
                                        AtomicInteger rowCounter, int startColumn, int endColumn, boolean isNewStyle, int[] skipEmptyCellOfColumn) {
        try (Workbook workbook = readWorkbook(file)) {
            // 处理每个指定的Sheet
            for (String sheetName : sheetNames) {
                Sheet sourceSheet = workbook.getSheet(sheetName);
                if (sourceSheet == null) {
                    logger.warn("跳过无目标Sheet的文件: " + file.getFileName());
                    return;
                }

                // 先处理合并区域（需要基于当前行号调整）
                processMergedRegions(sourceSheet, mergedSheet, rowCounter.get(), startColumn, endColumn);
                // 再复制数据内容（自动递增行号）
                copySheetData(sourceSheet, mergedSheet, rowCounter, mergedWorkbook, startColumn, endColumn, isNewStyle, skipEmptyCellOfColumn);
            }


        } catch (Exception e) {
            logger.error("处理文件失败: " + file, e);
        }
    }

    /**
     * 处理合并单元格区域，调整行号后添加到目标Sheet
     *
     * @param sourceSheet 源数据Sheet
     * @param targetSheet 目标Sheet
     * @param baseRow     当前基础行号（用于行号偏移）
     */
    public static void processMergedRegions(Sheet sourceSheet, Sheet targetSheet, int baseRow, int startColumn, int endColumn) {

        // 遍历所有合并区域
        for (int i = 0; i < sourceSheet.getNumMergedRegions(); i++) {
            CellRangeAddress mergedRegion = sourceSheet.getMergedRegion(i);
            if (startColumn >= 0 && endColumn > 0 && startColumn < endColumn) {
                // 过滤掉不在前四列（0-3列）的合并区域
                if (mergedRegion.getLastColumn() < startColumn || mergedRegion.getFirstColumn() > endColumn) continue;
            } else {
                startColumn = -1;
                endColumn = -1;
            }


            // 创建调整后的合并区域（行号偏移，列范围限制）
            CellRangeAddress adjustedRegion = new CellRangeAddress(
                    mergedRegion.getFirstRow() + baseRow,  // 起始行偏移
                    mergedRegion.getLastRow() + baseRow,   // 结束行偏移
                    startColumn < 0 ? mergedRegion.getFirstColumn() : Math.max(mergedRegion.getFirstColumn(), startColumn),
                    endColumn < 0 ? mergedRegion.getLastColumn() : Math.min(mergedRegion.getLastColumn(), endColumn)
            );
            targetSheet.addMergedRegion(adjustedRegion);
        }
    }

    /**
     * 复制Sheet数据内容到目标Sheet
     *
     * @param sourceSheet    源数据Sheet
     * @param targetSheet    目标Sheet
     * @param rowCounter     行号计数器（自动递增）
     * @param targetWorkbook 目标工作簿（用于样式克隆）
     */
    public static void copySheetData(Sheet sourceSheet, Sheet targetSheet,
                                     AtomicInteger rowCounter, Workbook targetWorkbook,
                                     int startColumn, int endColumn, boolean isNewStyle, int[] skipEmptyCellOfColumn) {
        //
        if (isNewStyle) {
            // 创建样式（只创建一次）
            Font headerFont = createHeaderFont(targetWorkbook);
            Font contentFont = createContentFont(targetWorkbook);
            CellStyle headerStyle = createHeaderStyle(targetWorkbook, headerFont);
            CellStyle contentStyle = createContentStyle(targetWorkbook, contentFont);
            // 遍历源sheet
            sourceSheet.forEach(sourceRow -> {
                List<Integer> rowValue = new ArrayList<>();
                if (skipEmptyCellOfColumn.length > 0) {
                    for (int i = 0; i < skipEmptyCellOfColumn.length; i++) {
                        String cellValue = getCellValueAsString(sourceRow.getCell(i)).trim();
                        if (null != cellValue && "" != cellValue) {
                            rowValue.add(i);
                        }
                    }
                }

                int targetRowIndex = rowCounter.getAndIncrement();
                boolean rowIsNotNullByColumn = skipEmptyCellOfColumn.length == rowValue.size();
                boolean isCreatetargetRow = targetRowIndex == 0 ? true : sourceRow.getRowNum() == 0 ? false : rowIsNotNullByColumn;
                if (isCreatetargetRow) {
                    // 创建新行并递增行号
                    Row targetRow = targetSheet.createRow(targetRowIndex);
                    // 根据行类型应用不同样式
                    boolean isHeaderRow = (targetRowIndex == 0);
                    copyRow(sourceRow, targetRow, targetWorkbook, startColumn, endColumn, isHeaderRow ? headerStyle : contentStyle);
                } else {
                    rowCounter.set(targetRowIndex);
                }


            });
        } else {
            sourceSheet.forEach(sourceRow -> {
                // 创建新行并递增行号
                Row targetRow = targetSheet.createRow(rowCounter.getAndIncrement());
                copyRow(sourceRow, targetRow, targetWorkbook, startColumn, endColumn, null);
            });
        }
    }

    /**
     * 复制单行数据
     *
     * @param sourceRow      源数据行
     * @param targetRow      目标行
     * @param targetWorkbook 目标工作簿（用于创建样式）
     */
    public static void copyRow(Row sourceRow, Row targetRow, Workbook targetWorkbook, int startColumn, int endColumn, CellStyle cellStyle) {

        if ((targetRow.getRowNum() == 0 && sourceRow.getRowNum() == 0) || (targetRow.getRowNum() > 0 && sourceRow.getRowNum() != 0)) {
            if (startColumn >= 0 && endColumn > 0 && startColumn < endColumn) {
                // 仅复制前四列（0到3列）
                for (int i = startColumn; i < endColumn; i++) {
                    // 获取单元格（不存在则创建空单元格）
                    copyCell(sourceRow, targetRow, targetWorkbook, i, cellStyle);
                }
            } else {
                for (int j = sourceRow.getFirstCellNum(); j < sourceRow.getLastCellNum(); j++) {
                    copyCell(sourceRow, targetRow, targetWorkbook, j, cellStyle);
                }
            }
        }


    }

    public static void copyCell(Row sourceRow, Row targetRow, Workbook targetWorkbook, int column, CellStyle cellStyle) {
        Cell sourceCell = sourceRow.getCell(column, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        Cell targetCell = targetRow.createCell(column);
        if (null == cellStyle) {
            // 复制样式
            copyCellStyle(sourceCell, targetCell, targetWorkbook);
        } else {
            // 应用样式
            targetCell.setCellStyle(cellStyle);
        }
        // 复制值
        copyCellValue(sourceCell, targetCell);
    }

    /**
     * 复制单元格样式
     *
     * @param sourceCell     源单元格
     * @param targetCell     目标单元格
     * @param targetWorkbook 目标工作簿（用于创建新样式）
     */
    public static void copyCellStyle(Cell sourceCell, Cell targetCell, Workbook targetWorkbook) {
        CellStyle newStyle = targetWorkbook.createCellStyle();
        // 区分XSSF和HSSF格式处理
        if (sourceCell.getCellStyle() instanceof XSSFCellStyle) {
            newStyle.cloneStyleFrom(sourceCell.getCellStyle());  // XSSF直接克隆完整样式
        } else {
            applyBasicStyle(sourceCell, newStyle, targetWorkbook);  // HSSF转换基础样式
        }
        targetCell.setCellStyle(newStyle);
    }

    /**
     * 应用基础样式（用于HSSF到XSSF的样式转换）
     *
     * @param sourceCell     源单元格
     * @param newStyle       新创建的样式对象
     * @param targetWorkbook 目标工作簿
     */
    public static void applyBasicStyle(Cell sourceCell, CellStyle newStyle, Workbook targetWorkbook) {
        CellStyle sourceStyle = sourceCell.getCellStyle();
        // 克隆字体属性
        Font sourceFont = targetWorkbook.getFontAt(sourceStyle.getFontIndex());
        Font newFont = targetWorkbook.createFont();
        newFont.setFontName(sourceFont.getFontName());
        newFont.setFontHeightInPoints(sourceFont.getFontHeightInPoints());
        newStyle.setFont(newFont);

        // 克隆数据格式
        newStyle.setDataFormat(
                targetWorkbook.createDataFormat().getFormat(sourceStyle.getDataFormatString())
        );

        // 对齐方式
        newStyle.setAlignment(sourceStyle.getAlignment());
        newStyle.setVerticalAlignment(sourceStyle.getVerticalAlignment());

        // 边框设置
        newStyle.setBorderTop(sourceStyle.getBorderTop());
        newStyle.setBorderBottom(sourceStyle.getBorderBottom());
        newStyle.setBorderLeft(sourceStyle.getBorderLeft());
        newStyle.setBorderRight(sourceStyle.getBorderRight());
    }

    /**
     * 复制单元格值（处理不同数据类型）
     *
     * @param sourceCell 源单元格
     * @param targetCell 目标单元格
     */
    public static void copyCellValue(Cell sourceCell, Cell targetCell) {
        switch (sourceCell.getCellType()) {
            case STRING:
                // 处理字符串转义（双引号转义）
                targetCell.setCellValue(escapeStringValue(sourceCell.getStringCellValue()));
                break;
            case NUMERIC:
                // 区分日期格式和普通数值
                if (DateUtil.isCellDateFormatted(sourceCell)) {
                    targetCell.setCellValue(sourceCell.getDateCellValue());  // 日期类型
                } else {
                    targetCell.setCellValue(sourceCell.getNumericCellValue()); // 数值类型
                }
                break;
            case BOOLEAN:
                targetCell.setCellValue(sourceCell.getBooleanCellValue());
                break;
            case FORMULA:
                targetCell.setCellFormula(sourceCell.getCellFormula());  // 保留公式
                break;
            case ERROR:
                targetCell.setCellErrorValue(sourceCell.getErrorCellValue()); // 错误代码
                break;
            default:
                targetCell.setCellValue("");  // 空值处理
        }
    }

    // 安全获取单元格值的工具方法
    public static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    /**
     * 转义字符串中的双引号
     *
     * @param value 原始字符串
     * @return 转义后的字符串（"替换为""）
     */
    public static String escapeStringValue(String value) {
        return value.contains("\"") ?
                value.replace("\"", "\"\"") :  // 双引号转义
                value;
    }


    /**
     * 合并指定目录下的所有Excel文件到目标目录（自动处理重名文件）
     *
     * @param sourceDir 源目录
     * @param targetDir 目标目录
     * @return 复制的文件总数
     */
    public static int mergeExcelFiles(Path sourceDir, Path targetDir) throws IOException {
        Files.createDirectories(targetDir);
        AtomicInteger counter = new AtomicInteger();

        Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (isExcelFile(file)) {
                    copyFileWithUniqueName(file, targetDir);
                    counter.incrementAndGet();
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                logger.error("文件访问失败: " + file, exc);
                return FileVisitResult.CONTINUE;
            }
        });

        logger.info("合并完成，共复制 " + counter.get() + " 个Excel文件");
        return counter.get();
    }

    public static boolean isExcelFile(Path file) {
        String fileName = file.getFileName().toString().toLowerCase();
        return fileName.endsWith(".xls") || fileName.endsWith(".xlsx") || !fileName.startsWith("~$");
    }

    /**
     * 复制文件到目标目录并确保文件名唯一（自动添加数字后缀处理重名）
     *
     * @param sourceFile 源文件路径
     * @param targetDir  目标目录路径
     * @throws IOException 当文件复制失败时抛出
     */
    public static void copyFileWithUniqueName(Path sourceFile, Path targetDir) throws IOException {
        // 解析文件名和扩展名
        String originalName = sourceFile.getFileName().toString();
        int dotIndex = originalName.lastIndexOf('.');               // 定位扩展名分隔点
        String baseName = originalName.substring(0, dotIndex);      // 主文件名（不含扩展名）
        String extension = originalName.substring(dotIndex);        // 扩展名（含.符号）

        // 初始化目标路径为原始文件名
        Path targetFile = targetDir.resolve(originalName);
        int copyCount = 1;

        // 循环处理文件名冲突
        while (Files.exists(targetFile)) {
            // 生成带序号的新文件名（例如：file_1.txt）
            String newName = baseName + "_" + copyCount + extension;
            targetFile = targetDir.resolve(newName);  // 更新目标路径
            copyCount++;                             // 递增序号
        }

        // 执行文件复制操作
        Files.copy(sourceFile, targetFile);
    }

    /**
     * 处理工作簿（支持自定义处理策略）
     *
     * @param workbook  目标工作簿
     * @param processor 处理策略
     */
    public static void processWorkbook(Workbook workbook, WorkbookProcessor processor) {
        Objects.requireNonNull(workbook, "Workbook cannot be null");
        Objects.requireNonNull(processor, "Processor cannot be null");
        processor.process(workbook);
    }

    /**
     * 批量处理目录中的Excel文件
     *
     * @param dir        目标目录
     * @param fileFilter 文件过滤器
     * @param processor  工作簿处理器
     */
    public static void batchProcess(Path dir,
                                    Predicate<Path> fileFilter,
                                    WorkbookProcessor processor) throws IOException {
        Files.walk(dir)
                .filter(Files::isRegularFile)
                .filter(fileFilter)
                .forEach(file -> processFile(file, processor));
    }

    /**
     * 创建Sheet页删除处理器(根据Sheet名称匹配删除)
     *
     * @param sheetNames 需要删除的Sheet名称集合
     * @return WorkbookProcessor 处理器实例，可用于批量处理工作簿
     */
    public static WorkbookProcessor createSheetRemover(Set<String> sheetNames) {
        return workbook -> {
            List<Integer> sheetsToDelete = new ArrayList<>();
            // 遍历所有Sheet页，记录需要删除的索引
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                String sheetName = workbook.getSheetName(i);
                if (sheetNames.contains(sheetName)) {
                    sheetsToDelete.add(i);  // 记录待删除Sheet的索引
                    logger.debug("标记待删除Sheet: " + sheetName);
                }
            }
            // 逆序排序索引，避免删除时导致后续索引变化
            Collections.sort(sheetsToDelete, Collections.reverseOrder());
            // 执行批量删除操作
            sheetsToDelete.forEach(workbook::removeSheetAt);
            logger.info("共删除 " + sheetsToDelete.size() + " 个Sheet");
        };
    }

    /**
     * 读取Excel文件并创建Workbook对象
     * POI的安全机制检测到Excel文件的压缩比异常（Zip bomb）
     *
     * @param file Excel文件路径
     * @return 解析后的Workbook对象
     * @throws IOException 当文件读取失败时抛出
     */

    public static Workbook readWorkbook(Path file) throws IOException {
        // 设置安全阈值允许低压缩率文件
        ZipSecureFile.setMinInflateRatio(0.0001); // 将阈值降低到0.001

        try (InputStream is = Files.newInputStream(file)) {
            return WorkbookFactory.create(is);
        } catch (Exception e) {
            throw new IOException("读取工作簿失败: " + file, e);
        } finally {
            // 恢复默认值（可选）
            ZipSecureFile.setMinInflateRatio(0.01);
        }
    }

    /**
     * 保存Workbook到指定路径
     *
     * @param workbook   需要保存的工作簿对象
     * @param outputPath 输出文件路径
     * @param options    文件打开选项(如覆盖、追加等)
     * @throws IOException 当文件写入失败时抛出
     */
    public static void saveWorkbook(Workbook workbook, Path outputPath,
                                    OpenOption... options) {
        // 使用try-with-resources确保自动关闭输出流
        try (OutputStream os = Files.newOutputStream(outputPath, options)) {
            workbook.write(os);  // 写入工作簿内容
        } catch (Exception e) {
            logger.error(String.format("文件写入失败时抛出,输出文件路径:%s", outputPath.toString()), e);
        }
    }

    /**
     * 处理单个Excel文件(读取->处理->保存)
     *
     * @param file      待处理的Excel文件路径
     * @param processor 工作簿处理器
     */
    public static void processFile(Path file, WorkbookProcessor processor) {
        try {
            // 1. 读取工作簿
            Workbook workbook = readWorkbook(file);
            // 2. 执行处理逻辑
            processWorkbook(workbook, processor);
            // 3. 保存修改（覆盖原文件）
            saveWorkbook(workbook, file, StandardOpenOption.TRUNCATE_EXISTING);
            logger.info("成功处理文件: " + file.getFileName());
        } catch (Exception e) {
            // 统一捕获异常并记录错误日志
            logger.error("处理文件失败: " + file, e);
        }
    }

    /**
     * 将数据导出到Excel文件并写入输出流
     *
     * @param headers      表头数组，定义各列标题
     * @param data         二维数据集合，外层List表示行，内层List表示列
     * @param outputStream 输出流，用于写入生成的Excel文件
     * @throws IOException 当发生I/O错误时抛出
     * @示例 <pre>{@code
     * String[] headers = {"ID", "名称", "价格"};
     * List<List<String>> data = Arrays.asList(
     *     Arrays.asList("1", "商品A", "100"),
     *     Arrays.asList("2", "商品B", "200")
     * );
     * try (FileOutputStream fos = new FileOutputStream("report.xlsx")) {
     *     ExcelExporter.exportToExcel(headers, data, fos);
     * }
     * }</pre>
     */
    public static void exportToExcel(String[] headers,
                                     List<List<String>> data,
                                     OutputStream outputStream) throws IOException {
        // 使用try-with-resources确保工作簿资源自动关闭
        try (Workbook workbook = new XSSFWorkbook()) { // 创建XLSX格式工作簿
            Sheet sheet = workbook.createSheet("table"); // 创建默认工作表

            // 预定义字体和样式（提升性能，避免重复创建）
            Font headerFont = createHeaderFont(workbook);   // 表头字体
            Font contentFont = createContentFont(workbook); // 内容字体
            CellStyle headerStyle = createHeaderStyle(workbook, headerFont); // 表头样式
            CellStyle contentStyle = createContentStyle(workbook, contentFont); // 内容样式

            createHeaderRow(sheet, headers, headerStyle); // 生成表头行
            populateDataRows(sheet, headers, data, contentStyle);  // 填充数据内容
            autoSizeColumns(sheet, headers.length);       // 自动调整列宽


            workbook.write(outputStream); // 将工作簿写入输出流
        }
    }


    /**
     * 删除指定多列同时重复的行（保留第一个出现的行）
     *
     * @param sheet 要处理的工作表对象
     * @param columns 需要检查重复的列索引数组
     */
    public static void removeDuplicateRowsByColumns(Sheet sheet, int[] columns) {
        DataFormatter formatter = new DataFormatter();
        // 存储已出现的多列组合键
        Set<String> seenKeys = new HashSet<>();
        List<Integer> rowsToDelete = new ArrayList<>();

        // 遍历所有数据行（跳过标题行，从第1行开始）
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            // 构建当前行的多列组合键
            StringBuilder keyBuilder = new StringBuilder();
            for (int colIndex : columns) {
                Cell cell = row.getCell(colIndex);
                String value = (cell != null) ?
                        formatter.formatCellValue(cell).trim() : "";
                keyBuilder
                        .append(value)
                        .append("|");  // 使用特殊分隔符连接多列值
            }
            String rowKey = keyBuilder.toString();

            // 检查是否已存在相同键值
            if (seenKeys.contains(rowKey)) {
                rowsToDelete.add(i);  // 标记重复行
            } else {
                seenKeys.add(rowKey); // 记录新键值
            }
        }

        // 倒序删除重复行（避免索引变化影响）
        Collections.sort(rowsToDelete, Collections.reverseOrder());
        for (int rowIndex : rowsToDelete) {
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                // 删除行并移动后续行
                int lastRowNum = sheet.getLastRowNum();
                sheet.removeRow(row);
                if (rowIndex < lastRowNum) {
                    sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
                }
            }
        }
    }

    /**
     * 删除指定多列同时重复的行（保留第一个出现的行）
     *
     * @param inputFile  输入Excel文件路径
     * @param outputFile 输出Excel文件路径
     * @param sheetIndex 工作表索引
     * @param columns    需要检查重复的列索引数组
     */
    public static void removeDuplicatesInLargeFile(String inputFile, String outputFile,
                                                   int sheetIndex, int[] columns) throws Exception {



        // 1. 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        // 2. 初始化输入流
        try (InputStream is = new FileInputStream(inputFile);
             Workbook inputWorkbook = WorkbookFactory.create(is);
             Workbook outputWorkbook = new SXSSFWorkbook(MAX_IN_MEMORY_ROWS)) {

            Sheet inputSheet = inputWorkbook.getSheetAt(sheetIndex);
            SXSSFSheet outputSheet = ((SXSSFWorkbook) outputWorkbook).createSheet("result");

            // 3. 处理标题行
            processHeaderRow(inputSheet, outputSheet);

            // 4. 分块处理数据行
            int totalRows = inputSheet.getLastRowNum() + 1;
            Long2BooleanOpenHashMap globalKeyMap = new Long2BooleanOpenHashMap(totalRows / 2);

            List<Future<BatchResult>> futures = new ArrayList<>();
            for (int startRow = 1; startRow < totalRows; startRow += BATCH_SIZE) {
                int endRow = Math.min(startRow + BATCH_SIZE, totalRows);
                futures.add(executor.submit(
                        new BatchProcessor(inputSheet, startRow, endRow, columns, globalKeyMap)
                ));
            }

            // 5. 收集并写入结果
            int outputRowIndex = 1;
            for (Future<BatchResult> future : futures) {
                BatchResult result = future.get();
                for (RowData rowData : result.rowsToKeep) {
                    writeRow(outputSheet, outputRowIndex++, rowData);
                }
                System.gc(); // 及时释放内存
            }

            // 6. 保存结果
            try (OutputStream os = new FileOutputStream(outputFile)) {
                outputWorkbook.write(os);
            }
        } finally {
            executor.shutdown();
        }
    }

    // 处理标题行
    private static void processHeaderRow(Sheet inputSheet, Sheet outputSheet) {
        Row headerRow = inputSheet.getRow(0);
        if (headerRow != null) {
            Row newHeader = outputSheet.createRow(0);
            for (Cell cell : headerRow) {
                Cell newCell = newHeader.createCell(cell.getColumnIndex());
                newCell.setCellValue(cell.getStringCellValue());
                // newCell.setCellStyle();
            }
        }
    }

    // 写入行数据
    private static void writeRow(SXSSFSheet sheet, int rowIndex, RowData rowData) {
        Row row = sheet.createRow(rowIndex);
        for (int i = 0; i < rowData.values.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(rowData.values[i]);
            // cell.setCellStyle();
        }
    }

    // 批处理任务类
    private static class BatchProcessor implements Callable<BatchResult> {
        private final Sheet sheet;
        private final int startRow;
        private final int endRow;
        private final int[] columns;
        private final Long2BooleanOpenHashMap globalKeyMap;

        public BatchProcessor(Sheet sheet, int startRow, int endRow,
                              int[] columns, Long2BooleanOpenHashMap globalKeyMap) {
            this.sheet = sheet;
            this.startRow = startRow;
            this.endRow = endRow;
            this.columns = columns;
            this.globalKeyMap = globalKeyMap;
        }

        @Override
        public BatchResult call() {
            BatchResult result = new BatchResult();
            DataFormatter formatter = new DataFormatter();

            for (int i = startRow; i < endRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // 计算行键
                long rowKey = calculateRowKey(row, columns, formatter);

                // 检查是否重复（同步块确保线程安全）
                boolean isDuplicate;
                synchronized (globalKeyMap) {
                    if (globalKeyMap.containsKey(rowKey)) {
                        isDuplicate = true;
                    } else {
                        globalKeyMap.put(rowKey, true);
                        isDuplicate = false;
                    }
                }

                // 非重复行添加到结果集
                if (!isDuplicate) {
                    result.addRow(row, formatter);
                }
            }
            return result;
        }
    }

    // 批处理结果类
    private static class BatchResult {
        List<RowData> rowsToKeep = new ArrayList<>();

        void addRow(Row row, DataFormatter formatter) {
            String[] values = new String[row.getLastCellNum()];
            for (int i = 0; i < values.length; i++) {
                Cell cell = row.getCell(i);
                values[i] = (cell != null) ? formatter.formatCellValue(cell) : "";
            }
            rowsToKeep.add(new RowData(values));
        }
    }

    // 行数据容器
    private static class RowData {
        final String[] values;

        RowData(String[] values) {
            this.values = values;
        }
    }

    // 高效行键计算（双哈希降低冲突概率）
    private static long calculateRowKey(Row row, int[] columns, DataFormatter formatter) {
        long hash1 = 0x7f3a21b6dL; // FNV1a offset basis
        long hash2 = 0x811c9dc5L;   // 第二个哈希的初始值

        for (int colIndex : columns) {
            Cell cell = row.getCell(colIndex);
            String value = (cell != null) ? formatter.formatCellValue(cell).trim() : "";

            // 计算第一个哈希
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                hash1 ^= c;
                hash1 *= 0x100000001b3L; // FNV prime
            }

            // 计算第二个哈希（使用不同算法）
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                hash2 = (hash2 << 5) - hash2 + c; // DJB2算法
            }
        }

        // 组合两个哈希值
        return (hash1 << 32) | (hash2 & 0xFFFFFFFFL);
    }

    /**
     * 将文件夹中所有Excel文件的指定Sheet页内容导出到文本文件（递归遍历子目录）
     *
     * @param directory     Excel文件所在根目录
     * @param sheetNames    需要导出的Sheet名称列表
     * @param outputFile    输出的文本文件路径
     * @param delimiter     列分隔符（如逗号、制表符）
     * @param includeHeader 是否包含表头
     * @param columnsToExport 导出指定列
     * @param columnsToReplaceNewlines 指定列去掉换行符为空格
     * @throws IOException 当文件读写失败时抛出
     */
    /**
     * 递归导出指定Sheet的指定列到文本文件
     */
    public static void exportSheetsToText(Path directory, List<String> sheetNames,
                                          Path outputFile, String delimiter, boolean includeHeader,
                                          int[] columnsToExport, int... columnsToReplaceNewlines) throws IOException {
        // 参数校验
        if (columnsToExport == null || columnsToExport.length == 0) {
            throw new IllegalArgumentException("必须指定要导出的列索引");
        }

        List<Path> excelFiles = collectExcelFiles(directory);
        if (!Files.exists(outputFile)) {
            Files.createDirectories(outputFile.getParent());
            Files.createFile(outputFile);
        }

        boolean isFirstFile = true;
        for (Path file : excelFiles) {
            try (Workbook workbook = readWorkbook(file)) {
                for (String sheetName : sheetNames) {
                    Sheet sheet = workbook.getSheet(sheetName);
                    if (sheet == null) {
                        logger.warn("文件 " + file.getFileName() + " 中未找到Sheet: " + sheetName);
                        continue;
                    }
                    // 转换时仅处理指定列
                    List<String> lines = convertSheetToText(
                            sheet, delimiter, includeHeader,
                            columnsToExport, columnsToReplaceNewlines
                    );

                    Files.write(
                            outputFile,
                            lines,
                            isFirstFile ? StandardOpenOption.TRUNCATE_EXISTING : StandardOpenOption.APPEND
                    );
                    isFirstFile = false;
                }
            } catch (Exception e) {
                logger.error("处理文件失败: " + file, e);
            }
        }
    }

    /**
     * 将Sheet内容转换为文本行
     *
     * @param sheet     工作表对象
     * @param delimiter 列分隔符
     * @return 文本行列表
     */
    public static List<String> convertSheetToText(Sheet sheet, String delimiter, boolean includeHeader,
                                                  int[] columnsToExport, int... columnsToReplaceNewlines) {
        List<String> lines = new ArrayList<>();
        int startRow = includeHeader ? sheet.getFirstRowNum() : sheet.getFirstRowNum() + 1;
        Set<Integer> replaceNewlineColumns = Arrays.stream(columnsToReplaceNewlines).boxed().collect(Collectors.toSet());

        for (int rowNum = startRow; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row == null) continue;

            List<String> cellValues = new ArrayList<>();
            for (int colIndex : columnsToExport) {
                Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String value = getCellValueAsString(cell);

                // 处理指定列的换行符替换
                if (replaceNewlineColumns.contains(colIndex)) {
                    value = value.replace("\n", " ");
                }
                cellValues.add(value.replace(delimiter, "\\" + delimiter));
            }
            String trimStr = String.join("", cellValues).trim();
            if (trimStr.length() > 0 && cellValues.get(1).length() > 0 && cellValues.get(2).length() > 0) {
                lines.add(String.join(delimiter, cellValues).toUpperCase());
            }
        }
        return lines;
    }

    /**
     * 递归收集目录下所有Excel文件
     */
    public static List<Path> collectExcelFiles(Path directory) throws IOException {
        List<Path> excelFiles = new ArrayList<>();
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (isExcelFile(file)) {
                    excelFiles.add(file);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                logger.error("文件访问失败: " + file, exc);
                return FileVisitResult.CONTINUE;
            }
        });
        return excelFiles;
    }

    /**
     * 将文本文件转换为Excel文件（支持自定义表头样式）
     *
     * @param textFilePath 输入的文本文件路径
     * @param delimiter    列分隔符（需与文本文件一致）
     * @param outputExcel  输出的Excel文件路径
     * @param headers      表头数组（需与列数匹配）
     * @throws IOException 当文件读写失败时抛出
     */
    public static void convertTextToExcel(Path textFilePath, String delimiter,
                                          Path outputExcel, String[] headers) throws IOException {
        List<List<String>> data = readTextFile(textFilePath, delimiter);
        // 校验数据列数与headers一致
        if (!data.isEmpty() && data.get(0).size() != headers.length) {
            throw new IllegalArgumentException(
                    "文本文件列数(" + data.get(0).size() + ")与表头数量(" + headers.length + ")不匹配"
            );
        }

        try (FileOutputStream fos = new FileOutputStream(outputExcel.toFile())) {
            exportToExcel(headers, data, fos);
        }
    }

    /**
     * 读取文本文件并解析为二维数据列表
     */
    public static List<List<String>> readTextFile(Path textFilePath, String delimiter) throws IOException {
        List<List<String>> data = new ArrayList<>();
        try (Stream<String> lines = Files.lines(textFilePath)) {
            lines.forEach(line -> {
                List<String> row = Arrays.stream(line.split(delimiter))
                        .map(s -> s.replace("\\" + delimiter, delimiter)) // 还原转义符
                        .collect(Collectors.toList());
                data.add(row);
            });
        }
        return data;
    }


    /**
     * 读取Excel文件返回指定SHEET或SHEETS
     */

    public static List<Sheet> sheetList(Path filePath, String[] sheetNames) {
        List<Sheet> sheets = new ArrayList<>();
        if (!isExcelFile(filePath)) {
            logger.warn("文件非Excel格式: " + filePath.getFileName());
            return sheets;
        }

        try (Workbook workbook = readWorkbook(filePath)) {
            for (String sheetName : sheetNames) {
                Sheet sheet = workbook.getSheet(sheetName);
                if (sheet != null) {
                    sheets.add(sheet);
                } else {
                    logger.warn("Sheet '" + sheetName + "' 不存在于文件: " + filePath.getFileName());
                }
            }
        } catch (IOException e) {
            logger.error("读取Excel文件失败: " + filePath, e);
        } catch (IllegalArgumentException e) {
            logger.error("文件格式错误或损坏: " + filePath, e);
        }
        return sheets;
    }


    /**
     * 递归检查目录下所有Excel文件是否包含指定的Sheet集合，缺失时打印文件路径及缺失的Sheet名称
     *
     * @param directory   要检查的目录路径
     * @param sheetNames  需要检查的Sheet名称集合
     * @param isRecursive 是否递归子目录
     * @return 存在缺失Sheet的文件数量
     */
    public static int checkSheetsInDirectory(Path directory, Set<String> sheetNames, boolean isRecursive) {
        final AtomicInteger errorCount = new AtomicInteger(0);

        try {
            Files.walk(directory, isRecursive ? Integer.MAX_VALUE : 1)
                    .filter(Files::isRegularFile)
                    .filter(ExcelHelper::isExcelFile)
                    .forEach(file -> {
                        if (!checkSheetsExistence(file, sheetNames)) {
                            errorCount.incrementAndGet();
                        }
                    });
        } catch (IOException e) {
            logger.error("遍历目录失败: " + directory, e);
        }

        return errorCount.get();
    }

    /**
     * 检查单个Excel文件是否存在指定Sheet（私有方法，供目录检查调用）
     */
    public static boolean checkSheetsExistence(Path filePath, Set<String> sheetNames) {
        if (!isExcelFile(filePath)) {
            logger.debug("文件非Excel格式: " + filePath.getFileName());
            return false;
        }

        try (Workbook workbook = readWorkbook(filePath)) {
            Set<String> missingSheets = new HashSet<>(sheetNames);
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                missingSheets.remove(workbook.getSheetName(i));
            }

            if (!missingSheets.isEmpty()) {
                logger.debug("[Sheet缺失] 文件: " + filePath
                        + " - 缺失Sheet: " + String.join(", ", missingSheets));
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("检查文件失败: " + filePath, e);
            return false;
        }
    }

    /**
     * 多个Excel文件转成单个Excel文件，中间使用text（支持指定列导出）
     * Convert multiple Excel files into a single Excel file with specified columns, using text in the middle
     *
     * @param columnsToExport 需要导出的列索引数组（如B列为1，D列为3）
     */
    public static void multipleExcelToSigleToExcelByText(Path sourcePath, Path middleTextPath, Path targetExcelPath,
                                                         List<String> sheetNames, String[] headers, String delimiter,
                                                         int[] columnsToExport, int... columnsToReplaceNewlines) {
        try {
            // 递归导出指定列到文本
            exportSheetsToText(
                    sourcePath, sheetNames, middleTextPath,
                    delimiter, false, columnsToExport, columnsToReplaceNewlines
            );
            logger.debug("指定列文本合并完成: " + middleTextPath);

            // 文本转Excel（需确保headers与导出的列顺序一致）
            convertTextToExcel(middleTextPath, delimiter, targetExcelPath, headers);
            logger.debug("Excel文件生成成功: " + targetExcelPath);
        } catch (IOException e) {
            logger.error("合并文本失败或生成Excel失败", e);
        }
    }

    /**
     * 为已存在的Excel文件添加负责人列
     *
     * @param excelFile      目标Excel文件路径
     * @param centerColIndex 微服务中心列的索引（从0开始）
     * @param enColIndex     负责人英文名新列的索引
     * @param cnColIndex     负责人中文名新列的索引
     */
    public static void addResponsibleColumns(Path excelFile, int centerColIndex, int enColIndex, int cnColIndex) {
        try (Workbook workbook = readWorkbook(excelFile)) {
            Sheet sheet = workbook.getSheetAt(0); // 第一个sheet

            // 添加表头
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) headerRow = sheet.createRow(0);

            // 确保列存在（创建列）
            if (headerRow.getCell(enColIndex) == null) {
                headerRow.createCell(enColIndex).setCellValue("负责人英文名");
            }
            if (headerRow.getCell(cnColIndex) == null) {
                headerRow.createCell(cnColIndex).setCellValue("负责人中文名");
            }

            // 填充数据
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String center = getCellValueAsString(row.getCell(centerColIndex));
                SuperintendentDict.superintendentInfo responsible = SuperintendentDict.getSuperintendent(center);

                // 创建负责人列单元格
                Cell enCell = row.createCell(enColIndex);
                enCell.setCellValue(responsible.getEnName());

                Cell cnCell = row.createCell(cnColIndex);
                cnCell.setCellValue(responsible.getCnName());
            }

            // 自动调整列宽
            autoSizeColumns(sheet, Math.max(enColIndex, cnColIndex) + 1);

            // 保存修改
            saveWorkbook(workbook, excelFile, StandardOpenOption.TRUNCATE_EXISTING);
            logger.info("负责人列添加成功: " + excelFile);
        } catch (Exception e) {
            logger.error("添加负责人列失败: " + excelFile, e);
        }
    }


    /**
     * Excel Vlookup式列更新
     *
     * @param mappingFile      映射关系文件
     * @param mappingSheetName 映射文件sheet名称（null=第一个sheet）
     * @param mappingKeyCol    映射文件匹配键列索引（0-based）
     * @param mappingValueCols 映射文件取值列索引列表
     * @param sourceDir        源文件目录
     * @param sourceSheetName  源文件sheet名称
     * @param sourceKeyCol     源文件匹配键列索引
     * @param sourceValueCols  源文件目标列索引列表
     * @param caseSensitive    是否区分大小写
     */
    public static void excelVlookupUpdate(
            Path mappingFile,
            String mappingSheetName,
            int mappingKeyCol,
            List<Integer> mappingValueCols,
            Path sourceDir,
            String sourceSheetName,
            int sourceKeyCol,
            List<Integer> sourceValueCols,
            boolean caseSensitive) {

        // 参数校验
        if (mappingValueCols.size() != sourceValueCols.size()) {
            logger.error(String.format("列数量不匹配: 映射值列=%s, 目标值列=%s", mappingValueCols.size(), sourceValueCols.size()));
            return;
        }

        // 1. 构建映射字典
        Map<String, List<String>> mappingDict = buildMappingDict(
                mappingFile, mappingSheetName, mappingKeyCol, mappingValueCols, caseSensitive);

        // 2. 处理目录中的文件
        processFiles(sourceDir, sourceSheetName, sourceKeyCol, sourceValueCols, mappingDict, caseSensitive);
    }

    public static Map<String, List<String>> buildMappingDict(
            Path mappingFile, String sheetName, int keyCol, List<Integer> valueCols, boolean caseSensitive) {

        Map<String, List<String>> dict = new HashMap<>();

        try (Workbook workbook = readWorkbook(mappingFile)) {
            Sheet sheet = getSheet(workbook, sheetName);
            if (sheet == null) return dict;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 从第2行开始
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // 获取键
                Cell keyCell = row.getCell(keyCol);
                if (keyCell == null) continue;
                String key = normalizeKey(getCellValueAsString(keyCell), caseSensitive);
                if (key.isEmpty()) continue;

                // 获取值列表
                List<String> values = new ArrayList<>();
                for (int colIndex : valueCols) {
                    Cell valueCell = row.getCell(colIndex);
                    values.add(valueCell != null ? getCellValueAsString(valueCell) : "");
                }

                dict.put(key, values);
            }
        } catch (Exception e) {
            logger.error("构建映射字典失败: " + mappingFile, e);
        }
        return dict;
    }

    public static void processFiles(
            Path sourceDir, String sheetName, int keyCol, List<Integer> targetCols,
            Map<String, List<String>> mappingDict, boolean caseSensitive) {

        try {
            Files.walk(sourceDir)
                    .filter(path -> isExcelFile(path) && !Files.isDirectory(path))
                    .forEach(file -> processSingleFile(file, sheetName, keyCol, targetCols, mappingDict, caseSensitive));
        } catch (IOException e) {
            logger.error("遍历目录失败: " + sourceDir, e);
        }
    }

    public static void processSingleFile(
            Path file, String sheetName, int keyCol, List<Integer> targetCols,
            Map<String, List<String>> mappingDict, boolean caseSensitive) {

        try (Workbook workbook = WorkbookFactory.create(file.toFile())) {
            Sheet sheet = getSheet(workbook, sheetName);
            if (sheet == null) return;

            boolean updated = false;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 从第2行开始
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // 获取键
                Cell keyCell = row.getCell(keyCol);
                if (keyCell == null) continue;
                String key = normalizeKey(getCellValueAsString(keyCell), caseSensitive);
                if (key.isEmpty()) continue;

                // 查找映射值
                List<String> values = mappingDict.get(key);
                if (values == null) continue;

                // 更新目标列
                for (int j = 0; j < targetCols.size(); j++) {
                    int targetCol = targetCols.get(j);
                    String value = values.get(j);

                    Cell targetCell = row.getCell(targetCol);
                    if (targetCell == null) {
                        targetCell = row.createCell(targetCol);
                    }
                    targetCell.setCellValue(value);
                }
                updated = true;
            }

            // 保存有修改的文件
            if (updated) {
                Path tempFile = Files.createTempFile("excel_temp_", ".xlsx");
                logger.debug(String.format("临时文件目录: %s", tempFile.toString()));
                try (FileOutputStream fos = new FileOutputStream(tempFile.toFile())) {
                    workbook.write(fos);
                }
                // 替换原文件
                Files.move(tempFile, file, StandardCopyOption.REPLACE_EXISTING);
                logger.debug(String.format("文件更新成功: %s", file.getFileName()));
            }
        } catch (Exception e) {
            logger.error("处理文件失败: " + file.getFileName(), e);
        }
    }

    public static String normalizeKey(String key, boolean caseSensitive) {
        key = key.trim();
        return caseSensitive ? key : key.toUpperCase();
    }

    public static Sheet getSheet(Workbook workbook, String sheetName) {
        if (sheetName == null || sheetName.trim().isEmpty()) {
            return workbook.getSheetAt(0);
        }
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            logger.warn(String.format("Sheet不存在: {}", sheetName));
        }
        return sheet;
    }


    // ======

    /**
     * 创建表头字体样式
     *
     * @param workbook 工作簿对象
     * @return 配置完成的字体对象
     * @实现说明 - 字体加粗，12号字
     * - 字体优先使用「WPS灵秀黑」，若不可用则回退到「微软雅黑」
     */
    public static Font createHeaderFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true); // 加粗
        font.setFontHeightInPoints((short) 12); // 字号

        // 字体回退策略
        if (isFontAvailable("WPS灵秀黑")) {
            font.setFontName("WPS灵秀黑");
        } else {
            font.setFontName("微软雅黑"); // 兼容性回退
        }
        return font;
    }

    /**
     * 创建内容字体样式
     *
     * @param workbook 工作簿对象
     * @return 配置完成的字体对象
     * @注意 字体策略与表头保持一致，确保视觉统一
     */
    public static Font createContentFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10); // 内容字号
        font.setFontName(
                isFontAvailable("WPS灵秀黑") ? "WPS灵秀黑" : "微软雅黑" // 继承表头策略
        );
        return font;
    }

    /**
     * 检测字体可用性（模拟实现）
     *
     * @param fontName 字体名称
     * @return 字体是否可用
     * @注意 实际开发中应使用以下代码检测：
     * <pre>{@code
     * GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
     * return Arrays.stream(ge.getAvailableFontFamilyNames())
     *             .anyMatch(name -> name.equals(fontName));
     * }</pre>
     */
    public static boolean isFontAvailable(String fontName) {
        // 模拟逻辑：Windows系统认为存在灵秀黑字体
        return !fontName.contains("WPS灵秀黑") || System.getProperty("os.name").contains("Windows"); // 其他字体默认存在
    }

    /**
     * 创建表头单元格样式
     *
     * @param workbook 工作簿对象
     * @param font     表头字体
     * @return 配置完成的单元格样式
     */
    public static CellStyle createHeaderStyle(Workbook workbook, Font font) {
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        // 新增：设置背景色为深绿色
        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 新增：设置字体颜色为白色
        Font whiteFont = workbook.createFont();
        whiteFont.setColor(IndexedColors.WHITE.getIndex());
        whiteFont.setBold(true);
        whiteFont.setFontHeightInPoints((short) 12);
        whiteFont.setFontName(font.getFontName());
        style.setFont(whiteFont);

        // 新增：设置边框（浅绿色细边框）
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        short aqua = IndexedColors.AQUA.getIndex();
        style.setTopBorderColor(aqua);
        style.setBottomBorderColor(aqua);
        style.setLeftBorderColor(aqua);
        style.setRightBorderColor(aqua);
        // 设置垂直居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // 设置水平居中
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    /**
     * 创建内容单元格样式
     *
     * @param workbook 工作簿对象
     * @param font     内容字体
     * @return 配置完成的单元格样式
     * @样式特性 - 浅绿色细边框（IndexedColors.AQUA）
     * - 继承内容字体配置
     */
    public static CellStyle createContentStyle(Workbook workbook, Font font) {
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);

        // 新增：设置边框（浅绿色细边框）
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        short aqua = IndexedColors.AQUA.getIndex();
        style.setTopBorderColor(aqua);
        style.setBottomBorderColor(aqua);
        style.setLeftBorderColor(aqua);
        style.setRightBorderColor(aqua);
        // 关键：设置单元格样式启用自动换行
        style.setWrapText(true);
        // 2. 设置垂直居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // 3. （可选）设置水平居中
        // style.setAlignment(HorizontalAlignment.CENTER);

        return style;
    }

    /**
     * 自动调整列宽（适配中文）
     *
     * @param sheet       工作表对象
     * @param columnCount 需要调整的列数
     * @实现原理 1. 调用autoSizeColumn获取基础宽度
     * 2. 对宽度进行1.2倍补偿（中文字符宽度补偿）
     * 3. 强制限制列宽不超过Excel允许的最大值50-255字符
     */
    public static void autoSizeColumns(Sheet sheet, int columnCount) {
        final int MAX_COLUMN_WIDTH = 50 * 256; // 限制最大列宽为50字符

        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
            int currentWidth = sheet.getColumnWidth(i);
            int adjustedWidth = (int) (currentWidth * 1.2); // 中文宽度补偿

            // 强制限制列宽不超过50字符
            if (adjustedWidth > MAX_COLUMN_WIDTH) {
                sheet.setColumnWidth(i, MAX_COLUMN_WIDTH);
            } else if (adjustedWidth < 0) {
                sheet.setColumnWidth(i, 0);
            } else {
                sheet.setColumnWidth(i, adjustedWidth);
            }
        }
    }

    /**
     * 创建表头行
     *
     * @param sheet   工作表对象
     * @param headers 表头数组
     * @param style   表头样式
     * @实现细节 - 固定在第0行创建表头
     * - 根据headers数组长度创建对应列数
     */
    public static void createHeaderRow(Sheet sheet, String[] headers, CellStyle style) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }


    /**
     * 填充数据行
     *
     * @param sheet   工作表对象
     * @param headers 表头数组
     * @param data    二维数据集合
     * @param style   内容单元格样式
     * @注意 - 数据从第2行开始写入（索引1）
     * - 允许不同行的列数不一致，但可能导致表格错位
     */
    public static void populateDataRows(Sheet sheet, String[] headers, List<List<String>> data, CellStyle style) {
        int rowNum = 1;
        for (List<String> rowData : data) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < rowData.size(); i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(rowData.get(i) != null ? rowData.get(i) : "");
                cell.setCellStyle(style); // 应用统一的边框样式
            }
        }

        // 新增：设置最外层边框加粗
        int lastRow = sheet.getLastRowNum();
        int lastCol = headers.length - 1;
        // 整个数据区域
        // setThickBorder(sheet, 0, 0, lastRow, lastCol);
    }

    // 新增：设置指定区域的外边框加粗
    public static void setThickBorder(Sheet sheet, CellStyle noumalStyle, int firstRow, int firstCol, int lastRow, int lastCol) {
        try {
            // 设置上边框
            for (int col = firstCol; col <= lastCol; col++) {
                Cell cell = sheet.getRow(firstRow).getCell(col);
                CellStyle style = cell.getCellStyle();
                if (style != null) {
                    style.setBorderTop(BorderStyle.MEDIUM);
                    cell.setCellStyle(style);
                }
            }

            // 设置下边框
            for (int col = firstCol; col <= lastCol; col++) {
                Cell cell = sheet.getRow(lastRow).getCell(col);
                CellStyle style = cell.getCellStyle();
                if (style != null) {
                    style.setBorderBottom(BorderStyle.MEDIUM);
                    cell.setCellStyle(style);
                }
            }

            // 设置左边框
            for (int row = firstRow; row <= lastRow; row++) {
                Cell cell = sheet.getRow(row).getCell(firstCol);
                CellStyle style = cell.getCellStyle();
                if (style != null) {
                    style.setBorderLeft(BorderStyle.MEDIUM);
                    cell.setCellStyle(style);
                }
            }

            // 设置右边框
            for (int row = firstRow; row <= lastRow; row++) {
                Cell cell = sheet.getRow(row).getCell(lastCol);
                CellStyle style = cell.getCellStyle();
                if (style != null) {
                    style.setBorderRight(BorderStyle.MEDIUM);
                    cell.setCellStyle(style);
                }
            }
        } catch (Exception e) {
            logger.error("设置最外层边框加粗出错！！！");
        }
    }
}