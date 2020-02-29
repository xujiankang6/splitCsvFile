package com.jiankang.splitfile.service;

import com.jiankang.splitfile.bean.Model;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

@Service
public interface ExportBillService {
    /**
     * 返回制作的月度账单路径
     * @param excelFiles
     * @param map
     * @return
     */

    public String makeMonthBill(File excelFiles,Map<String, Model> map);
}
