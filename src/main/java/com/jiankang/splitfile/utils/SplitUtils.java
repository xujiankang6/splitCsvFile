package com.jiankang.splitfile.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
public class SplitUtils {

    private static Logger logger = LoggerFactory.getLogger(SplitUtils.class);
    private static String defaultDir = System.getProperty("java.io.tmpdir") + File.separator;


    /**
     * 拆分csv文件并返回文件夹路径
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
            logger.info("文件总行数： {}行  拆分文件个数：{}个", fileCount, splitNumber);
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
