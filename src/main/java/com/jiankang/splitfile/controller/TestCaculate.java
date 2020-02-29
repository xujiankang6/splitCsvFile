package com.jiankang.splitfile.controller;

import com.jiankang.splitfile.bean.Model;
import com.jiankang.splitfile.utils.ExcelUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//测试计算准确度
public class TestCaculate {
    public static void main(String[] args) throws IOException, InvalidFormatException {
        File file = new File("C:\\Users\\徐健康\\Desktop\\menu");
        Map<String, Model> stringModelMap = new HashMap<>();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                XSSFWorkbook workBook = null;
                try {
                    workBook = new XSSFWorkbook(files[i]);
                    stringModelMap = ExcelUtils.dealWorkBook(workBook, stringModelMap);
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
        System.out.println(sum);
    }


}

