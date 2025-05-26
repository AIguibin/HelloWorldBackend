package com.aiguibin.core.common;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
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
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ExcelHelper {
    public static final Log logger = LogFactory.getLog(ExcelHelper.class);


    public static final String DEFAULT_MERGE_SHEET_NAME = "目录";

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
    public static void processExcelFile(Path file, Workbook mergedWorkbook, Sheet mergedSheet, AtomicInteger rowCounter) {
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
    public static void processMergedRegions(Sheet sourceSheet, Sheet targetSheet, int baseRow) {
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
    public static void copySheetData(Sheet sourceSheet, Sheet targetSheet,
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
    public static void copyRow(Row sourceRow, Row targetRow, Workbook targetWorkbook) {
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
            Sheet sheet = workbook.createSheet("Data"); // 创建默认工作表

            // 预定义字体和样式（提升性能，避免重复创建）
            Font headerFont = createHeaderFont(workbook);   // 表头字体
            Font contentFont = createContentFont(workbook); // 内容字体
            CellStyle headerStyle = createHeaderStyle(workbook, headerFont); // 表头样式
            CellStyle contentStyle = createContentStyle(workbook, contentFont); // 内容样式

            createHeaderRow(sheet, headers, headerStyle); // 生成表头行
            populateDataRows(sheet, data, contentStyle);  // 填充数据内容
            autoSizeColumns(sheet, headers.length);       // 自动调整列宽

            workbook.write(outputStream); // 将工作簿写入输出流
        }
    }


    /**
     * 创建表头字体样式
     *
     * @param workbook 工作簿对象
     * @return 配置完成的字体对象
     * @实现说明 - 字体加粗，12号字
     * - 字体优先使用「WPS灵秀黑」，若不可用则回退到「微软雅黑」
     */
    private static Font createHeaderFont(Workbook workbook) {
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
    private static Font createContentFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10); // 内容字号
        font.setFontName(
                isFontAvailable("WPS灵秀黑") ? "WPS灵秀黑" : "微软雅黑" // 继承表头策略
        );
        return font;
    }

    /**
     * 创建表头单元格样式
     *
     * @param workbook 工作簿对象
     * @param font     表头字体
     * @return 配置完成的单元格样式
     */
    private static CellStyle createHeaderStyle(Workbook workbook, Font font) {
        CellStyle style = workbook.createCellStyle();
        style.setFont(font); // 应用字体
        style.setAlignment(HorizontalAlignment.CENTER); // 水平居中
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
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
    private static CellStyle createContentStyle(Workbook workbook, Font font) {
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);

        // 统一边框样式配置
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        // 设置边框颜色（浅绿色）
        short aqua = IndexedColors.AQUA.getIndex();
        style.setTopBorderColor(aqua);
        style.setBottomBorderColor(aqua);
        style.setLeftBorderColor(aqua);
        style.setRightBorderColor(aqua);

        return style;
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
    private static void createHeaderRow(Sheet sheet, String[] headers, CellStyle style) {
        Row headerRow = sheet.createRow(0); // 首行作为表头
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]); // 设置列标题
            cell.setCellStyle(style);      // 应用样式
        }
    }

    /**
     * 填充数据行
     *
     * @param sheet 工作表对象
     * @param data  二维数据集合
     * @param style 内容单元格样式
     * @注意 - 数据从第2行开始写入（索引1）
     * - 允许不同行的列数不一致，但可能导致表格错位
     */
    private static void populateDataRows(Sheet sheet, List<List<String>> data, CellStyle style) {
        int rowNum = 1; // 数据起始行索引
        for (List<String> rowData : data) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < rowData.size(); i++) {
                Cell cell = row.createCell(i);
                String value = rowData.get(i);
                cell.setCellValue(value != null ? value : ""); // 空值处理
                cell.setCellStyle(style);
            }
        }
    }

    /**
     * 自动调整列宽（适配中文）
     *
     * @param sheet       工作表对象
     * @param columnCount 需要调整的列数
     *
     * @实现原理
     * 1. 调用autoSizeColumn获取基础宽度
     * 2. 对宽度进行1.2倍补偿（中文字符宽度补偿）
     * 3. 强制限制列宽不超过Excel允许的最大值255字符
     */
    private static void autoSizeColumns(Sheet sheet, int columnCount) {
        final int MAX_COLUMN_WIDTH = 255 * 256; // Excel列宽上限 (255字符 x 256单位)

        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i); // 自动计算基础宽度

            int baseWidth = sheet.getColumnWidth(i);
            int adjustedWidth = (int)(baseWidth * 1.2); // 中文宽度补偿

            // 强制限制列宽不超过最大值
            if (adjustedWidth > MAX_COLUMN_WIDTH) {
                adjustedWidth = MAX_COLUMN_WIDTH;
            } else if (adjustedWidth < 0) {
                adjustedWidth = 0; // 防止负值
            }

            sheet.setColumnWidth(i, adjustedWidth);
        }
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
    private static boolean isFontAvailable(String fontName) {
        // 模拟逻辑：Windows系统认为存在灵秀黑字体
        return fontName.contains("灵秀黑") ?
                System.getProperty("os.name").contains("Windows") :
                true; // 其他字体默认存在
    }

    /**
     * 删除A列SQL语句重复的行（保留第一个出现的行）
     *
     * @param sheet 要处理的工作表对象
     */
    public static void removeDuplicateSqlRows(Sheet sheet, int column) {
        DataFormatter formatter = new DataFormatter();
        Set<String> sqlSet = new HashSet<>();
        List<Integer> rowsToDelete = new ArrayList<>();

        // 遍历所有数据行（从第1行开始）
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            // 获取A列单元格内容
            Cell cell = row.getCell(column);
            String sql = formatter.formatCellValue(cell).trim();

            // 检测重复
            if (sqlSet.contains(sql)) {
                rowsToDelete.add(i);
            } else {
                sqlSet.add(sql);
            }
        }

        // 倒序删除重复行
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
            if (trimStr.length() > 0) {
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
}