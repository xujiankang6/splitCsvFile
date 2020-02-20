package com.forcecloud.splitfile.controller;


import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Controller
@RequestMapping("/")
public class SplitFileCtrl {

    private static Logger logger = LoggerFactory.getLogger(SplitFileCtrl.class);
    private static String defaultDir = System.getProperty("java.io.tmpdir") + File.separator;


    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String uploadAndGetSplitZip(@RequestParam("file") MultipartFile file, @RequestParam("size") String size,
                                       HttpServletRequest request, HttpServletResponse response) {
        try {
            InputStream inputStream = file.getInputStream();



            String originalFilename = file.getOriginalFilename();
            String[] split = originalFilename.split("\\.");
            String type = split[split.length - 1];
            int index = originalFilename.lastIndexOf(".");
            String fileName = originalFilename.substring(0, index);
            int splitSize = Integer.valueOf(size);
            logger.info("文件信息：文件名：{}  文件类型： {}" +
                    "  文件大小：{}MB   要求拆分文件最大行数： {}",fileName,type,
                    file.getSize()/1024/1024,splitSize);
            String zipPath = getZipPath(inputStream, fileName, splitSize);
            zipExport(zipPath, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView m = new ModelAndView();
        m.setViewName("index");
        return m;

    }


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


    /**
     * 拆分文件并返回文件夹路径
     * @param inputStream
     * @param filename
     * @param splitSize
     * @return
     */
    public static String getZipPath(InputStream inputStream, String filename, int splitSize) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            Stream<String> lines = bufferedReader.lines();
            List<String> contents = lines.collect(Collectors.toList());
            long fileCount = contents.size();
            int splitNumber = (int) ((fileCount % splitSize == 0) ? (fileCount / splitSize) : (fileCount / splitSize + 1));
            logger.info("文件总行数： {}行  拆分文件个数：{}个",fileCount,splitNumber);
            //将创建的拆分文件写入流放入集合中
            List<BufferedWriter> listWriters = new ArrayList<>();
            //创建存放拆分文件的目录
            File dir = new File(defaultDir + filename);
            //文件夹存在，可能里面有内容，删除所有内容
            if (dir.exists()) {
                delAllFile(dir.getAbsolutePath());
            }
            dir.mkdir();
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
            logger.info("拆分文件失败  ：" + e);
            e.printStackTrace();
        }
        stopWatch.stop();
        logger.info("文件拆分共花费：  " + stopWatch.getTotalTimeMillis() + " ms");
        return defaultDir + filename + File.separator;
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
            java.io.File myFilePath = new java.io.File(filePath);
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
