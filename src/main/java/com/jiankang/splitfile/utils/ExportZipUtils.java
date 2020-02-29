package com.jiankang.splitfile.utils;


import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class ExportZipUtils {

    private static Logger logger = LoggerFactory.getLogger(ExportZipUtils.class);

    public void zipExport(String filePath, HttpServletRequest request, HttpServletResponse response) {
        //zip包的名称
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String zipName = "package.zip";
        //要打包的文件夹路径
        String packagePath = filePath;
        response.setContentType("octets/stream");
        response.setCharacterEncoding("UTF-8");
        String agent = request.getHeader("USER-AGENT");
        try {
            if (agent.contains("MSIE") || agent.contains("Trident")) {
                zipName = URLEncoder.encode(zipName, "UTF-8");
            } else {
                zipName = new String(zipName.getBytes("UTF-8"), "ISO-8859-1");
            }
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        response.setHeader("Content-Disposition", "attachment;fileName=\"" + zipName + "\"");
        ZipOutputStream zipos = null;
        try {
            zipos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
            zipos.setMethod(ZipOutputStream.DEFLATED);
            zipos.setLevel(0);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        DataOutputStream os = null;
        InputStream is = null;
        try {
            String[] fileNameList = new File(packagePath).list();
            for (int i = 0; i < fileNameList.length; i++) {
                File file = new File(packagePath + File.separator + fileNameList[i]);
                zipos.putNextEntry(new ZipEntry(fileNameList[i]));
                os = new DataOutputStream(zipos);
                is = new FileInputStream(file);
                //输入流转换为输出流
                IOUtils.copy(is, os);
                is.close();
                zipos.closeEntry();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            stopWatch.stop();
            logger.info("输出zip包共耗时： " + stopWatch.getTotalTimeMillis() + " ms");
            // 推荐使用try-with-resource
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.flush();
                    os.close();
                }
                if (zipos != null) {
                    zipos.flush();
                    zipos.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
