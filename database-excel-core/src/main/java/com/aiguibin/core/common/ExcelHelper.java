package com.aiguibin.core.common;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ExcelHelper {
    private static final Log logger = LogFactory.getLog(ExcelHelper.class);


    private static final String DEFAULT_MERGE_SHEET_NAME = "目录";


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
    private static void checkSingleFile(Path file, Map<Integer, String> expectedSheets) {
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


    private static String rowToString(Row row) {
        return StreamSupport.stream(row.spliterator(), false)
                .map(cell -> cell.toString())
                .collect(Collectors.joining("|"));
    }


    /**
     * 合并指定目录下的Excel文件中的目标Sheet到新文件
     *
     * @param sourceDir  源目录
     * @param outputFile 输出文件路径
     * @param sheetName  需要合并的Sheet名称（默认"目录"）
     */
    public static void mergeSheets(Path sourceDir, Path outputFile, String sheetName) throws IOException {
        String targetSheetName = sheetName != null ? sheetName : DEFAULT_MERGE_SHEET_NAME;

        if (Files.exists(outputFile) && !Files.deleteIfExists(outputFile)) {
            throw new IOException("无法删除旧文件: " + outputFile);
        }

        try (Workbook mergedWorkbook = new XSSFWorkbook()) {
            Sheet mergedSheet = mergedWorkbook.createSheet(targetSheetName);
            AtomicInteger rowCounter = new AtomicInteger(0);

            Files.walk(sourceDir)
                    .filter(Files::isRegularFile)
                    .filter(ExcelHelper::isExcelFile)
                    .forEach(file -> processExcelFile(file, mergedWorkbook, mergedSheet, rowCounter));

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
    private static void processExcelFile(Path file, Workbook mergedWorkbook, Sheet mergedSheet, AtomicInteger rowCounter) {
        try (Workbook workbook = readWorkbook(file)) {
            // 获取指定名称的Sheet页（默认"目录"）
            Sheet sourceSheet = workbook.getSheet(DEFAULT_MERGE_SHEET_NAME);
            if (sourceSheet == null) {
                logger.warn("跳过无目标Sheet的文件: " + file.getFileName());
                return;
            }

            // 先处理合并区域（需要基于当前行号调整）
            processMergedRegions(sourceSheet, mergedSheet, rowCounter.get());
            // 再复制数据内容（自动递增行号）
            copySheetData(sourceSheet, mergedSheet, rowCounter, mergedWorkbook);

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
    private static void processMergedRegions(Sheet sourceSheet, Sheet targetSheet, int baseRow) {
        // 遍历所有合并区域
        for (int i = 0; i < sourceSheet.getNumMergedRegions(); i++) {
            CellRangeAddress mergedRegion = sourceSheet.getMergedRegion(i);
            // 过滤掉不在前四列（0-3列）的合并区域
            if (mergedRegion.getLastColumn() < 0 || mergedRegion.getFirstColumn() > 3) continue;

            // 创建调整后的合并区域（行号偏移，列范围限制在0-3）
            CellRangeAddress adjustedRegion = new CellRangeAddress(
                    mergedRegion.getFirstRow() + baseRow,  // 起始行偏移
                    mergedRegion.getLastRow() + baseRow,   // 结束行偏移
                    Math.max(mergedRegion.getFirstColumn(), 0),  // 列起始不小于0
                    Math.min(mergedRegion.getLastColumn(), 3)   // 列结束不大于3
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
    private static void copySheetData(Sheet sourceSheet, Sheet targetSheet,
                                      AtomicInteger rowCounter, Workbook targetWorkbook) {
        // 遍历源Sheet的每一行
        sourceSheet.forEach(sourceRow -> {
            // 创建新行并递增行号
            Row targetRow = targetSheet.createRow(rowCounter.getAndIncrement());
            copyRow(sourceRow, targetRow, targetWorkbook);
        });
    }

    /**
     * 复制单行数据
     *
     * @param sourceRow      源数据行
     * @param targetRow      目标行
     * @param targetWorkbook 目标工作簿（用于创建样式）
     */
    private static void copyRow(Row sourceRow, Row targetRow, Workbook targetWorkbook) {
        targetRow.setHeight(sourceRow.getHeight());  // 复制行高
        // 仅复制前四列（0到3列）
        for (int i = 0; i < 4; i++) {
            // 获取单元格（不存在则创建空单元格）
            Cell sourceCell = sourceRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            Cell targetCell = targetRow.createCell(i);
            copyCellStyle(sourceCell, targetCell, targetWorkbook);  // 复制样式
            copyCellValue(sourceCell, targetCell);                  // 复制值
        }
    }

    /**
     * 复制单元格样式
     *
     * @param sourceCell     源单元格
     * @param targetCell     目标单元格
     * @param targetWorkbook 目标工作簿（用于创建新样式）
     */
    private static void copyCellStyle(Cell sourceCell, Cell targetCell, Workbook targetWorkbook) {
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
    private static void applyBasicStyle(Cell sourceCell, CellStyle newStyle, Workbook targetWorkbook) {
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
    private static void copyCellValue(Cell sourceCell, Cell targetCell) {
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

    /**
     * 转义字符串中的双引号
     *
     * @param value 原始字符串
     * @return 转义后的字符串（"替换为""）
     */
    private static String escapeStringValue(String value) {
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

    private static boolean isExcelFile(Path file) {
        String fileName = file.getFileName().toString().toLowerCase();
        return fileName.endsWith(".xls") || fileName.endsWith(".xlsx");
    }

    /**
     * 复制文件到目标目录并确保文件名唯一（自动添加数字后缀处理重名）
     *
     * @param sourceFile 源文件路径
     * @param targetDir  目标目录路径
     * @throws IOException 当文件复制失败时抛出
     */
    private static void copyFileWithUniqueName(Path sourceFile, Path targetDir) throws IOException {
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

    //------------------------ 核心处理方法 ------------------------

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
     *
     * @param file Excel文件路径
     * @return 解析后的Workbook对象
     * @throws IOException 当文件读取失败时抛出
     */
    public static Workbook readWorkbook(Path file) throws IOException {
        // 使用try-with-resources确保自动关闭输入流
        try (InputStream is = Files.newInputStream(file)) {
            return WorkbookFactory.create(is);  // 自动识别xls/xlsx格式
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
                                    OpenOption... options) throws IOException {
        // 使用try-with-resources确保自动关闭输出流
        try (OutputStream os = Files.newOutputStream(outputPath, options)) {
            workbook.write(os);  // 写入工作簿内容
        }
    }

    /**
     * 处理单个Excel文件(读取->处理->保存)
     *
     * @param file      待处理的Excel文件路径
     * @param processor 工作簿处理器
     */
    private static void processFile(Path file, WorkbookProcessor processor) {
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

    //------------------------ 接口定义 ------------------------

    @FunctionalInterface
    public interface WorkbookProcessor {
        void process(Workbook workbook);
    }
}