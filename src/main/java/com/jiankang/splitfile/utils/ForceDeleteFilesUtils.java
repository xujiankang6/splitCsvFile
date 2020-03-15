package com.jiankang.splitfile.utils;

import org.springframework.stereotype.Component;

import java.io.File;

/*
 *@create by jiankang
 *@date 2019/12/7 time 16:24
 */
@Component
public class ForceDeleteFilesUtils {

    /**
     * 删除文件夹（强制删除）
     *
     * @param path
     */
    public  void deleteAllFilesOfDir(File path) {
        if (null != path) {
            if (!path.exists()){
                return;
            }

            if (path.isFile()) {
                boolean result = path.delete();
                int tryCount = 0;
                while (!result && tryCount++ < 10) {
                    System.gc(); // 回收资源
                    result = path.delete();
                }
            }
            File[] files = path.listFiles();
            if (null != files) {
                for (int i = 0; i < files.length; i++) {
                    deleteAllFilesOfDir(files[i]);
                }
            }
            path.delete();
        }
    }

    /**
     * 删除文件
     */
    public  boolean deleteFile(String pathname) {
        boolean result = false;
        File file = new File(pathname);
        if (file.exists()) {
            file.delete();
            result = true;
            System.out.println("文件已经被成功删除");
        }
        return result;
    }


}
