package com.jiankang.splitfile.service.impl;

import com.jiankang.splitfile.bean.Model;
import com.jiankang.splitfile.service.ExportBillService;
import com.jiankang.splitfile.utils.ExcelUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
@Service
public class ExportBillServiceImpl implements ExportBillService {

    @Autowired
    ExcelUtils excelUtils;

    @Override
    public String makeMonthBill(File excelFiles, Map<String, Model> map) {
        XSSFWorkbook workBook=null;
        if(excelFiles.isDirectory()){
            File[] files = excelFiles.listFiles();
            try {
                assert files != null;
                workBook = new XSSFWorkbook(files[0]);
                Map<Integer, List<String>> data = excelUtils.transferExcel(workBook);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
