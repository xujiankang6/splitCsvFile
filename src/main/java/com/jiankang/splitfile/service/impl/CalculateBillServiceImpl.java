package com.jiankang.splitfile.service.impl;

import com.jiankang.splitfile.bean.Model;
import com.jiankang.splitfile.controller.SplitFileCtrl;
import com.jiankang.splitfile.service.CalculateBillService;
import com.jiankang.splitfile.utils.ExcelUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class CalculateBillServiceImpl implements CalculateBillService {

    private static Logger logger = LoggerFactory.getLogger(CalculateBillServiceImpl.class);

    @Autowired
    ExcelUtils excelUtils;

    @Override
    public  Map<String, Model> calculateBill(File excelFiles) {
        Map<String, Model> stringModelMap = new HashMap<>();
        if (excelFiles.isDirectory()) {
            File[] files = excelFiles.listFiles();
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
        return stringModelMap;

    }
}
