package com.jiankang.splitfile.controller;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Controller
public class IndexPageCtrl {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView m = new ModelAndView();
        m.setViewName("calculate");
        return m;
    }


    @RequestMapping(value = "/test", method = RequestMethod.POST)
    @ResponseBody
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HSSFWorkbook wb = new HSSFWorkbook();//创建HSSFWorkbook对象

        HSSFSheet sheet = wb.createSheet("成绩表");//建立sheet对象

        HSSFRow row1 = sheet.createRow(0); //在sheet里创建第一行，参数为行索引

        HSSFCell cell = row1.createCell(0); //创建单元格

        cell.setCellValue("学生成绩表"); //设置单元格内容

        //合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        //在sheet里创建第二行

        HSSFRow row2 = sheet.createRow(1);

        //创建单元格并设置单元格内容

        row2.createCell(0).setCellValue("姓名");

        row2.createCell(1).setCellValue("班级");

        row2.createCell(2).setCellValue("语文成绩");

        row2.createCell(3).setCellValue("数学成绩");

        row2.createCell(4).setCellValue("英语成绩");

        //在sheet里创建第三行

        HSSFRow row3 = sheet.createRow(2);

        row3.createCell(0).setCellValue("小明");

        row3.createCell(1).setCellValue("1班");

        row3.createCell(2).setCellValue(80);

        row3.createCell(3).setCellValue(75);

        row3.createCell(4).setCellValue(88);

        HSSFRow row4 = sheet.createRow(3);

        row4.createCell(0).setCellValue("小红");

        row4.createCell(1).setCellValue("1班");

        row4.createCell(2).setCellValue(82);

        row4.createCell(3).setCellValue(70);

        row4.createCell(4).setCellValue(90);

        //输出Excel文件

        OutputStream output = response.getOutputStream();

        response.reset();

//设置响应头，

        response.setHeader("Content-disposition", "attachment; filename=Student.xls");

        response.setContentType("application/msexcel");

        wb.write(output);

        output.close();

    }

}
