package com.jiankang.splitfile.service;

import com.jiankang.splitfile.bean.Model;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public interface ExportBillService {
    /**
     * 返回制作的月度账单路径
     * @param excelFiles
     * @param map
     * @return
     */

    public void makeMonthBill(File excelFiles, LinkedHashMap<String, Model> map, HttpServletRequest request, HttpServletResponse response);
}
