package com.focosee.qingshow;

import java.io.File;

/**
 * Created by wxy325 on 2/8/15.
 */
public class FileWalker {
    public static void walkThroughDirectory(String directoryPath, IFileHandler handler) {
        File f = new File(directoryPath);

        if (!f.isDirectory()) {
        	System.out.println(directoryPath + " is not a directory.");
            return;
        }

        File[] fList = f.listFiles();
        for (File file : fList) {
            if (file.isDirectory()) {
                FileWalker.walkThroughDirectory(file.getAbsolutePath(), handler);
            } else {
                handler.handleFile(file);
            }
        }
    }
}
