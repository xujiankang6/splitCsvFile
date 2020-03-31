package com.jiankang.splitfile.utils;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
public class SplitUtils {

    private static Logger logger = LoggerFactory.getLogger(SplitUtils.class);
    private static String defaultDir = System.getProperty("java.io.tmpdir") + File.separator;


    /**
     * 拆分csv文件并返回文件夹路径
     *
     * @param inputStream
     * @param filename
     * @param splitSize
     * @return
     */
    public static String getCsvZipPath(InputStream inputStream, String filename, int splitSize) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            Stream<String> lines = bufferedReader.lines();
            List<String> contents = lines.collect(Collectors.toList());
            long fileCount = contents.size();
            int splitNumber = (int) ((fileCount % splitSize == 0) ? (fileCount / splitSize) : (fileCount / splitSize + 1));
            logger.info("csv文件总行数： {}行  拆分文件个数：{}个", fileCount, splitNumber);
            //将创建的拆分文件写入流放入集合中
            List<BufferedWriter> listWriters = new ArrayList<>();
            //创建存放拆分文件的目录
            File dir = new File(defaultDir + filename);
            //文件夹存在，可能里面有内容，删除所有内容
            if (dir.exists()) {
                delAllFile(dir.getAbsolutePath());
            }
            dir.mkdirs();
            for (int i = 0; i < splitNumber; i++) {
                String splitFilePath = defaultDir + filename + File.separator + filename + i + ".csv";
                File splitFileName = new File(splitFilePath);
                splitFileName.createNewFile();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(splitFileName)));
                listWriters.add(bufferedWriter);
            }
            for (int i = 0; i < fileCount; i++) {
                if (i == 0) {
                    for (int count = 0; count < splitNumber; count++) {
                        listWriters.get(count).write(contents.get(i));
                        listWriters.get(count).newLine();
                    }
                } else {
                    listWriters.get(i % splitNumber).write(contents.get(i));
                    listWriters.get(i % splitNumber).newLine();
                }
            }
            //关流
            listWriters.forEach(it -> {
                try {
                    it.flush();
                    it.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            logger.info("csv拆分文件失败  ：" + e);
            e.printStackTrace();
        }
        stopWatch.stop();
        logger.info("csv文件拆分共花费：  " + stopWatch.getTotalTimeMillis() + " ms");
        return defaultDir + filename + File.separator;
    }

    /**
     * 拆分xls文件并返回文件夹路径
     * @param inputStream
     * @param fileName
     * @param splitSize
     * @return
     */
    public static String getXlsZipPath(InputStream inputStream, String fileName, int splitSize) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Workbook workBook = null;
        try {
            workBook = WorkbookFactory.create(inputStream);
        } catch (Exception e) {
            logger.error("Load excel file error!", e);
            return null;
        }
        try {
            Sheet sheet = workBook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.rowIterator();
            int totalRows = sheet.getPhysicalNumberOfRows();
            int splitNumber = (int) ((totalRows % splitSize == 0) ? (totalRows / splitSize) : (totalRows / splitSize + 1));
            logger.info("xls文件总行数： {}行  拆分文件个数：{}个", totalRows, splitNumber);
            List<HSSFWorkbook> hssfWorkbooks = new ArrayList<>();
            //创建存放拆分文件的目录，文件夹存在，可能里面有内容，删除所有内容
            File dir = new File(defaultDir + fileName);
            if (dir.exists()) {
                delAllFile(dir.getAbsolutePath());
            }
            dir.mkdirs();
            //创建的拆分文件写入流并放入集合中
            for (int count = 0; count < splitNumber; count++) {
                HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
                hssfWorkbook.createSheet().createRow(0);
                hssfWorkbooks.add(hssfWorkbook);
            }
            int oldRow = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() == 0) {
                    oldRow++;
                    Iterator<Cell> cellIterator = row.cellIterator();
                    int index = 0;
                    while (cellIterator.hasNext()) {
                        Cell next = cellIterator.next();
//                        解决获取excel数据的方法与实际类型不符
                        next.setCellType(CellType.STRING);
                        String value = next.getStringCellValue();
                        for (int count = 0; count < splitNumber; count++) {
                            HSSFRow row1 = hssfWorkbooks.get(count).getSheetAt(0).getRow(0);
                            row1.createCell(index).setCellValue(value);
                        }
                        index++;
                    }
                }
                if (row.getRowNum() != 0) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    HSSFWorkbook hssfWorkbook = hssfWorkbooks.get(oldRow % splitNumber);
                    HSSFSheet sheet1 = hssfWorkbook.getSheetAt(0);
                    int index = 0;
                    HSSFRow row1 = sheet1.createRow(sheet1.getLastRowNum() + 1);
                    while (cellIterator.hasNext()) {
                        Cell next = cellIterator.next();
                        next.setCellType(CellType.STRING);
                        String value = next.getStringCellValue();
                        row1.createCell(index++).setCellValue(value);
                    }
                    oldRow++;
                }
            }
            //创建拆分文件并关流
            for (int i = 0; i < hssfWorkbooks.size(); i++) {
                String splitFilePath = defaultDir + fileName + File.separator + fileName + i + ".xlsx";
                File file = new File(splitFilePath);
                file.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(file);
                hssfWorkbooks.get(i).write(outputStream);
                hssfWorkbooks.get(i).close();
            }
        } catch (IOException e) {
            logger.error("拆分xls文件失败  ：" + e);
        }
        stopWatch.stop();
        logger.info("xls文件拆分共花费：  " + stopWatch.getTotalTimeMillis() + " ms");
        return defaultDir + fileName + File.separator;
    }

    /**
     * 拆分xlsx文件并返回文件夹路径
     *
     * @param inputStream
     * @param fileName
     * @param splitSize
     * @return
     */
    public static String getXlsxZipPath(InputStream inputStream, String fileName, int splitSize) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Workbook workBook = null;
        try {
            workBook = WorkbookFactory.create(inputStream);
        } catch (Exception e) {
            logger.error("Load excel file error!", e);
            return null;
        }
        try {
            Sheet sheet = workBook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.rowIterator();
            int totalRows = sheet.getPhysicalNumberOfRows();
            int splitNumber = (int) ((totalRows % splitSize == 0) ? (totalRows / splitSize) : (totalRows / splitSize + 1));
            logger.info("xlsx文件总行数： {}行  拆分文件个数：{}个", totalRows, splitNumber);
            List<XSSFWorkbook> xssfWorkbooks = new ArrayList<>();
            //创建存放拆分文件的目录，文件夹存在，可能里面有内容，删除所有内容
            File dir = new File(defaultDir + fileName);
            if (dir.exists()) {
                delAllFile(dir.getAbsolutePath());
            }
            dir.mkdirs();
            //创建的拆分文件写入流并放入集合中
            for (int count = 0; count < splitNumber; count++) {
                XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
                xssfWorkbook.createSheet().createRow(0);
                xssfWorkbooks.add(xssfWorkbook);
            }
            int oldRow = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() == 0) {
                    oldRow++;
                    Iterator<Cell> cellIterator = row.cellIterator();
                    int index = 0;
                    while (cellIterator.hasNext()) {
                        Cell next = cellIterator.next();
                        next.setCellType(CellType.STRING);
                        String value = next.getStringCellValue();
                        for (int count = 0; count < splitNumber; count++) {
                            XSSFRow row1 = xssfWorkbooks.get(count).getSheetAt(0).getRow(0);
                            row1.createCell(index).setCellValue(value);
                        }
                        index++;
                    }
                }
                if (row.getRowNum() != 0) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    XSSFWorkbook xssfWorkbook = xssfWorkbooks.get(oldRow % splitNumber);
                    XSSFSheet sheet1 = xssfWorkbook.getSheetAt(0);
                    int index = 0;
                    XSSFRow row1 = sheet1.createRow(sheet1.getLastRowNum() + 1);
                    while (cellIterator.hasNext()) {
                        Cell next = cellIterator.next();
                        next.setCellType(CellType.STRING);
                        String value = next.getStringCellValue();
                        row1.createCell(index++).setCellValue(value);
                    }
                    oldRow++;
                }
            }
            //创建拆分文件并关流
            for (int i = 0; i < xssfWorkbooks.size(); i++) {
                String splitFilePath = defaultDir + fileName + File.separator + fileName + i + ".xlsx";
                File file = new File(splitFilePath);
                file.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(file);
                xssfWorkbooks.get(i).write(outputStream);
                xssfWorkbooks.get(i).close();
            }
        } catch (IOException e) {
            logger.error("拆分xlsx文件失败  ：" + e);
        }
        stopWatch.stop();
        logger.info("xlsx文件拆分共花费：  " + stopWatch.getTotalTimeMillis() + " ms");
        return defaultDir + fileName + File.separator;
    }


    /***
     * 删除文件夹
     *
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); // 删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /***
     * 删除指定文件夹下所有文件
     *
     * @param path 文件夹完整绝对路径
     * @return
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);// 再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }
}
