package com.jiankang.splitfile.controller;

import com.jiankang.splitfile.bean.Model;
import com.jiankang.splitfile.utils.ExcelUtils;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TestOneExcel {
    public static void main(String[] args) {
        Workbook workBook = null;
        try {


            workBook = new XSSFWorkbook("C:\\Users\\徐健康\\Desktop\\（恒丰模板）呷哺呷哺蔬菜送货单.xlsx"); // 使用XSSFWorkbook
            Map<String, Model> stringModelMap = ExcelUtils.dealWorkBook(workBook, new HashMap<String, Model>());

            for (Map.Entry<String, Model> entry : stringModelMap.entrySet()) {
                System.out.println(entry.getKey()+entry.getValue());
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally { //关流
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
