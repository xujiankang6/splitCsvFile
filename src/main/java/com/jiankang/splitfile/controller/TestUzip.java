package com.jiankang.splitfile.controller;

import com.jiankang.splitfile.utils.UnZipUtil;
import org.apache.tika.detect.AutoDetectReader;
import org.apache.tika.exception.TikaException;

import javax.lang.model.element.VariableElement;
import java.io.*;
import java.util.zip.ZipInputStream;

public class TestUzip {

    private static String defaultDir = System.getProperty("java.io.tmpdir") + File.separator;

    public static void main(String[] args) throws IOException, TikaException {
        File file = new File("C:\\Users\\徐健康\\Desktop\\menu.zip");
        UnZipUtil.unZipFiles(file.getAbsolutePath(),defaultDir+"menu"+File.separator);
    }
}
