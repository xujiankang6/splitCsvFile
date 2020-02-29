package com.jiankang.splitfile.service.impl;

import com.jiankang.splitfile.bean.Model;
import com.jiankang.splitfile.service.ExportBillService;
import com.jiankang.splitfile.utils.ExcelUtils;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.util.*;

@Service
public class ExportBillServiceImpl implements ExportBillService {

    @Autowired
    ExcelUtils excelUtils;

    private static Logger logger = LoggerFactory.getLogger(ExportBillServiceImpl.class);


    @Override
    public void makeMonthBill(File excelFiles, LinkedHashMap<String, Model> stringModelMap,
                              HttpServletRequest request, HttpServletResponse response) {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        //获取第一张表的数据
        Map<Integer, List<String>> data = Collections.emptyMap();
        XSSFWorkbook oneWorkBook = null;
        try {
            oneWorkBook = new XSSFWorkbook(Objects.requireNonNull(excelFiles.listFiles())[0]);
            data = excelUtils.transferExcel(oneWorkBook);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            //导出excel排版
            XSSFWorkbook monthBillFile = new XSSFWorkbook();
            XSSFSheet sheet = monthBillFile.createSheet(data.get(0).get(0) + "月度账单");
            XSSFRow row1 = sheet.createRow(0); //在sheet里创建第一行，参数为行索引
            XSSFCell cell = row1.createCell(0); //创建单元格

            cell.setCellValue(data.get(0).get(0) + "月度账单"); //设置标题行
            //合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
            int column = data.get(1).size();
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, column));
            for (int row = 1; row < 3; row++) {
                XSSFRow row2 = sheet.createRow(row);
                List<String> strings = data.get(row);
                for (int i = 0; i < column; i++) {
                    row2.createCell(i).setCellValue(strings.get(i));
                }
            }
            //插入主要数据
            double sum = 0;
            int row = 3;
            for (Map.Entry<String, Model> entry : stringModelMap.entrySet()) {
                sum += entry.getValue().getSumPrice();
            }

            ArrayList<String> strings = new ArrayList<>(stringModelMap.keySet());


            for (int i = 0; i < strings.size(); i += 2) {
                XSSFRow row2 = sheet.createRow(row);
                if (i < strings.size() && !StringUtils.isEmpty(stringModelMap.get(strings.get(i)))) {
                    row2.createCell(0).setCellValue(stringModelMap.get(strings.get(i)).getId());
                    row2.createCell(1).setCellValue(stringModelMap.get(strings.get(i)).getName());
                    row2.createCell(2).setCellValue(stringModelMap.get(strings.get(i)).getUnit());
                    row2.createCell(3).setCellValue(stringModelMap.get(strings.get(i)).getNumber());
                    row2.createCell(4).setCellValue(stringModelMap.get(strings.get(i)).getOnePrice());
                    row2.createCell(5).setCellValue(stringModelMap.get(strings.get(i)).getSumPrice());
                }
                if (i + 1 < strings.size() && !StringUtils.isEmpty(stringModelMap.get(strings.get(i+1)))) {
                    row2.createCell(6).setCellValue(stringModelMap.get(strings.get(i+1)).getId());
                    row2.createCell(7).setCellValue(stringModelMap.get(strings.get(i+1)).getName());
                    row2.createCell(8).setCellValue(stringModelMap.get(strings.get(i+1)).getUnit());
                    row2.createCell(9).setCellValue(stringModelMap.get(strings.get(i+1)).getNumber());
                    row2.createCell(10).setCellValue(stringModelMap.get(strings.get(i+1)).getOnePrice());
                    row2.createCell(11).setCellValue(stringModelMap.get(strings.get(i+1)).getSumPrice());
                }
                row++;
            }

            XSSFRow lastRow = sheet.createRow(row);
            lastRow.createCell(0).setCellValue("总计：");
            lastRow.createCell(1).setCellValue(sum);


            //输出Excel文件

            OutputStream output = response.getOutputStream();
            response.reset();
            //设置响应头，
            response.setHeader("Content-disposition", "attachment; filename=monthBill.xlsx");
            response.setContentType("application/msexcel");
            monthBillFile.write(output);

            output.close();
            monthBillFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        stopWatch.stop();
        String result = "导出月度账单";
        logger.info("制作月度账单成功，共耗时：{} s", stopWatch.getTotalTimeSeconds());

    }
}
