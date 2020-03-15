package com.jiankang.splitfile.controller;

import com.jiankang.splitfile.bean.Model;
import com.jiankang.splitfile.service.CalculateBillService;
import com.jiankang.splitfile.service.ExportBillService;
import com.jiankang.splitfile.utils.ExcelUtils;
import com.jiankang.splitfile.utils.ForceDeleteFilesUtils;
import com.jiankang.splitfile.utils.UnZipUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/calculate")
public class CalculateExcelCtrl {

    private static Logger logger = LoggerFactory.getLogger(CalculateExcelCtrl.class);
    private static String defaultDir = System.getProperty("java.io.tmpdir") + File.separator + "menu" + File.separator;

    @Autowired
    ExcelUtils excelUtils;

    @Autowired
    ForceDeleteFilesUtils forceDeleteFilesUtils;

    @Autowired
    CalculateBillService calculateBillService;

    @Autowired
    ExportBillService exportBillService;


    @RequestMapping(value = "/summary", method = RequestMethod.POST)
    @ResponseBody
    public void uploadAndGetSplitZip(@RequestParam("file") MultipartFile file,
                                     HttpServletRequest request, HttpServletResponse response) throws IOException {

        //将zip包解压到一个目录
        File file2 = new File(System.getProperty("java.io.tmpdir") + File.separator + file.getOriginalFilename());
        file.transferTo(file2);
        UnZipUtil.unZipFiles(file2.getAbsolutePath(), defaultDir);

        //获取解压缩的文件路径
        String originalFilename = file.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");
        String fileName = originalFilename.substring(0, index);
        File excelFiles = new File(defaultDir + fileName);
        //获取主要bill数据
        LinkedHashMap<String, Model> stringModelMap = calculateBillService.calculateBill(excelFiles);
        //导出月度账单
        exportBillService.makeMonthBill(excelFiles, stringModelMap, request, response);
        file2.deleteOnExit();
        forceDeleteFilesUtils.deleteAllFilesOfDir(new File(defaultDir));
    }
}
