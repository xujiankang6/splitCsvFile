package com.jiankang.splitfile.controller;

import com.jiankang.splitfile.bean.Model;
import com.jiankang.splitfile.utils.ExcelUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.*;

//测试计算准确度
public class TestCaculate {
    public static void main(String[] args) throws IOException, InvalidFormatException {
        File file = new File("C:\\Users\\徐健康\\Desktop\\aaa");
        Map<String, Model> stringModelMap = new HashMap<>();

        ExcelUtils excelUtils = new ExcelUtils();
        Map<Integer, List<String>> data= Collections.emptyMap();

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            XSSFWorkbook oneWorkBook= new XSSFWorkbook(files[0]);
            data = excelUtils.transferExcel(oneWorkBook);
            for (int i = 0; i < files.length; i++) {
                XSSFWorkbook workBook = null;
                try {
                    workBook = new XSSFWorkbook(files[i]);

                    stringModelMap = excelUtils.dealWorkBook(workBook, stringModelMap);

                } catch (Exception e) {

                } finally {
                    if (workBook != null) {
                        try {
                            workBook.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }


            }
        }
        double sum= 0;
        for (Map.Entry<String, Model> entry : stringModelMap.entrySet()) {
            sum+=entry.getValue().getSumPrice();
        }


        //导出excel排版
        XSSFWorkbook workbook = new XSSFWorkbook();
        List<String> strings = data.get(0);
        XSSFSheet sheet = workbook.createSheet(data.get(0).get(0)+"月度账单");
        XSSFRow rowm = sheet.createRow(0);
        XSSFCell cellTitle = rowm.createCell(0);
    }


}

