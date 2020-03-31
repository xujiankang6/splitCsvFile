package com.jiankang.splitfile.controller;


import com.jiankang.splitfile.utils.ExportZipUtils;
import com.jiankang.splitfile.utils.ForceDeleteFilesUtils;
import com.jiankang.splitfile.utils.SplitUtils;
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
import java.io.InputStream;


@Controller
@RequestMapping("/split")
public class SplitFileCtrl {

    private static Logger logger = LoggerFactory.getLogger(SplitFileCtrl.class);
    public static final String CSV = "csv";
    public static final String XLS = "xls";
    public static final String XLSX = "xlsx";

    @Autowired
    SplitUtils splitUtils;

    @Autowired
    ExportZipUtils exportZipUtils;

    @Autowired
    ForceDeleteFilesUtils forceDeleteFilesUtils;


    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String uploadAndGetSplitZip(@RequestParam("file") MultipartFile file, @RequestParam("size") String size,
                                       HttpServletRequest request, HttpServletResponse response) {
        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String[] split = originalFilename.split("\\.");
            String fileType = split[split.length - 1];
            int index = originalFilename.lastIndexOf(".");
            String fileName = originalFilename.substring(0, index);
            int splitSize = Integer.valueOf(size);
            logger.info("文件信息：文件名：{}  文件类型： {}" +
                            "  文件大小：{}MB   要求拆分文件最大行数： {}", fileName, fileType,
                    file.getSize() / 1024 / 1024, splitSize);
            //拆分后的文件路径
            String splitFilePath = "";
            switch (fileType) {
                case CSV:
                    splitFilePath = SplitUtils.getCsvZipPath(inputStream, fileName, splitSize);
                    break;
                case XLS:
                    splitFilePath = SplitUtils.getXlsZipPath(inputStream, fileName, splitSize);
                    break;
                case XLSX:
                    splitFilePath = SplitUtils.getXlsxZipPath(inputStream, fileName, splitSize);
                    break;
                default:
                    return "fail";
            }
            exportZipUtils.zipExport(splitFilePath, request, response);
            forceDeleteFilesUtils.deleteAllFilesOfDir(new File(splitFilePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }


}
