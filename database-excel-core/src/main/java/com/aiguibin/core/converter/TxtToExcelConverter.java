package com.aiguibin.core.converter;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Data
class DataModel {
    @ExcelProperty("Sheet名称")
    private String sheetName;

    @ExcelProperty("B列值")
    private String bColumn;

    @ExcelProperty("D列值")
    private String dColumn;

    @ExcelProperty("C列值")
    private String cColumn;
}

public class TxtToExcelConverter {
    public static void main(String[] args) {
        String inputFilePath = "F:\\Desktop\\output1.txt";
        String outputFilePath = "F:\\Desktop\\output1.xlsx";

        List<DataModel> dataList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) { // 跳过标题行（如果不需要可以删除此if块）
                    isFirstLine = false;
                    continue;
                }

                String[] columns = line.split("\t");
                if (columns.length >= 4) {
                    DataModel data = new DataModel();
                    data.setSheetName(columns[0]);
                    data.setBColumn(columns[1]);
                    data.setDColumn(columns[2]);
                    data.setCColumn(columns[3]);
                    dataList.add(data);
                }
            }

            // 写入Excel
            EasyExcel.write(outputFilePath, DataModel.class)
                    .sheet("Sheet1")
                    .doWrite(dataList);

            System.out.println("Excel文件已生成：" + outputFilePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}