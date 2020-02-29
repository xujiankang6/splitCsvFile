package com.jiankang.splitfile.utils;


import com.jiankang.splitfile.bean.Model;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
public class ExcelUtils {

    private static Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    // 显示的导出表的标题
    private String title;
    // 导出表的列名
    private String[] rowName;
    private List<Object[]> dataList = new ArrayList<Object[]>();

    public ExcelUtils() {

    }

    // 构造函数，传入要导出的数据
    public ExcelUtils(String title, String[] rowName, List<Object[]> dataList) {
        this.dataList = dataList;
        this.rowName = rowName;
        this.title = title;
    }

    // 导出数据
/*
    public void export(OutputStream out) throws Exception {
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet(title);

            // 产生表格标题行
            HSSFRow rowm = sheet.createRow(0);
            HSSFCell cellTitle = rowm.createCell(0);


            //sheet样式定义【】
            HSSFCellStyle columnTopStyle = this.getColumnTopStyle(workbook);
            HSSFCellStyle style = this.getStyle(workbook);
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, (rowName.length - 1)));
            cellTitle.setCellStyle(columnTopStyle);
            cellTitle.setCellValue(title);

            // 定义所需列数
            int columnNum = rowName.length;
            HSSFRow rowRowName = sheet.createRow(2);

            // 将列头设置到sheet的单元格中
            for (int n = 0; n < columnNum; n++) {
                HSSFCell cellRowName = rowRowName.createCell(n);
                cellRowName.setCellType(HSSFCell.CELL_TYPE_STRING);
                HSSFRichTextString text = new HSSFRichTextString(rowName[n]);
                cellRowName.setCellValue(text);
                cellRowName.setCellStyle(columnTopStyle);

            }
            // 将查询到的数据设置到sheet对应的单元格中
            for (int i = 0; i < dataList.size(); i++) {
                Object[] obj = dataList.get(i);// 遍历每个对象
                HSSFRow row = sheet.createRow(i + 3);// 创建所需的行数

                for (int j = 0; j < obj.length; j++) {
                    HSSFCell cell = null;
                    if (j == 0) {
                        cell = row.createCell(j, HSSFCell.CELL_TYPE_NUMERIC);
                        cell.setCellValue(i + 1);
                    } else {
                        cell = row.createCell(j, HSSFCell.CELL_TYPE_STRING);
                        if (!"".equals(obj[j]) && obj[j] != null) {
                            cell.setCellValue(obj[j].toString());
                        }
                    }
                    cell.setCellStyle(style);

                }

            }

            // 让列宽随着导出的列长自动适应
            for (int colNum = 0; colNum < columnNum; colNum++) {
                int columnWidth = sheet.getColumnWidth(colNum) / 256;
                for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                    HSSFRow currentRow;
                    if (sheet.getRow(rowNum) == null) {
                        currentRow = sheet.createRow(rowNum);
                    } else {
                        currentRow = sheet.getRow(rowNum);
                    }
                    if (currentRow.getCell(colNum) != null) {
                        HSSFCell currentCell = currentRow.getCell(colNum);
                        if (currentCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            int length = currentCell.getStringCellValue().getBytes().length;
                            if (columnWidth < length) {
                                columnWidth = length;
                            }
                        }
                    }
                }
                if (colNum == 0) {
                    sheet.setColumnWidth(colNum, (columnWidth - 2) * 256);
                } else {
                    sheet.setColumnWidth(colNum, (columnWidth + 4) * 256);
                }
            }

            if (workbook != null) {
                try {
                    workbook.write(out);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
        }
    }
*/


    /**
     * 将所有行的数据做成一个map
     * @param workBook
     * @param map
     * @return
     */
    public static Map<String, Model> dealWorkBook(Workbook workBook, Map<String, Model> map) {
        Sheet sheet = workBook.getSheetAt(0); // 获取第一个sheet
        Map<Integer, List<String>> data = new HashMap<Integer, List<String>>(); //第一个参数表示行数 第二个List保存该行的cell数据
        int i = 0;
        for (Row row : sheet) {
            data.put(i, new ArrayList<String>());
            for (Cell cell : row) { // 遍历当前行的所有cell
                switch (cell.getCellType()) {
                    case STRING:
                        data.get(i).add(cell.getRichStringCellValue().getString());
                        break;
                    case _NONE:
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            data.get(i).add(String.valueOf(cell.getDateCellValue()));
                        } else {
                            data.get(i).add(String.valueOf(cell.getNumericCellValue()));
                        }
                        break;
                    case BOOLEAN:
                        data.get(i).add(String.valueOf(cell.getBooleanCellValue()));
                        break;
                    case FORMULA:
                        try {
                            data.get(i).add(String.valueOf(cell.getStringCellValue()));
                        } catch (IllegalStateException e) {
                            data.get(i).add(String.valueOf(cell.getNumericCellValue()));
                        }
                        break;
                    case BLANK:
                        data.get(i).add("");
                        break;
                    case ERROR:
                        break;
                }
            }
            i++;
        }
        Set<Integer> keys = data.keySet();
        for (int row = 3; row < keys.size() - 1; row++) {
            List<String> strings = data.get(row);
            Model model = new Model();
            Model model2 = new Model();
            if (!StringUtils.isEmpty(strings.get(2))) {
                model.setId(strings.get(0));
                model.setName(strings.get(1));
                model.setUnit(strings.get(2));
                if (StringUtils.isEmpty(strings.get(3))) {
                    model.setNumber(0);
                } else {
                    model.setNumber(Float.valueOf(strings.get(3)));
                }
                model.setOnePrice(Float.valueOf(strings.get(4)));
                model.setSumPrice(Double.valueOf(strings.get(5)));
            }
            if (!StringUtils.isEmpty(strings.get(7))) {
                model2.setId(strings.get(6));
                model2.setName(strings.get(7));
                model2.setUnit(strings.get(8));
                if (StringUtils.isEmpty(strings.get(9))) {
                    model2.setNumber(0);
                } else {
                    model2.setNumber(Float.valueOf(strings.get(9)));
                }
                model2.setOnePrice(Float.valueOf(strings.get(10)));
                model2.setSumPrice(Double.valueOf(strings.get(11)));
            }
            if (model.getId() != null) {
                if (map.get(model.getId() + "-" + model.getName()) == null) {
                    map.put(model.getId() + "-" + model.getName(), model);
                } else {
                    Model mapModel = map.get(model.getId() + "-" + model.getName());
                    float number = mapModel.getNumber() + model.getNumber();
                    double sumPrice = mapModel.getSumPrice() + model.getSumPrice();
                    mapModel.setNumber(number);
                    mapModel.setSumPrice(sumPrice);
                    map.put(model.getId() + "-" + model.getName(), mapModel);
                }
            }
            if (model2.getId() != null) {
                if (map.get(model2.getId() + "-" + model2.getName()) == null) {
                    map.put(model2.getId() + "-" + model2.getName(), model2);
                } else {
                    Model mapModel = map.get(model2.getId() + "-" + model2.getName());
                    float number = mapModel.getNumber() + model2.getNumber();
                    double sumPrice = mapModel.getSumPrice() + model2.getSumPrice();
                    mapModel.setNumber(number);
                    mapModel.setSumPrice(sumPrice);
                    map.put(model2.getId() + "-" + model2.getName(), mapModel);
                }
            }
        }
        return map;
    }
}
