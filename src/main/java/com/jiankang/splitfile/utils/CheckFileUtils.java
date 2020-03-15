package com.jiankang.splitfile.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;


@Component
public class CheckFileUtils {

    private static Logger logger = LoggerFactory.getLogger(CheckFileUtils.class);

    public static void checkFile(MultipartFile file){
        if(file.isEmpty()){

        }
        //获取zip包解压到一个目录
        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String[] split = originalFilename.split("\\.");
            String type = split[split.length - 1];
            int index = originalFilename.lastIndexOf(".");
            String fileName = originalFilename.substring(0, index);
            logger.info("文件信息：文件名：{}  文件类型： {}" +
                            "  文件大小：{}B ", fileName, type,
                    file.getSize() );

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
