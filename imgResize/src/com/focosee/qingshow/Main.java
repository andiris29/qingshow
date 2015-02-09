package com.focosee.qingshow;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please input a path of folder");
            return;
        }
        String path = args[0];
        FileWalker.walkThroughDirectory(path, new IFileHandler() {
            @Override
            public void handleFile(File file) {
                String filePath = file.getAbsolutePath();
                if (Main.checkIs2x(filePath)) {
                    String name = Main.getNameWithout2x(filePath);
                    File f = new File(name);
                    if (!f.exists()) {
                        ImageResizer.resizeImage(filePath, name);
                    }
                }
            }
        });
//        String path = args[0];
//        ImageResizer.resizeImage("/Users/wxy325/Downloads/bc/share_icon@2x.png", "/Users/wxy325/Downloads/bc/share_icon@.png");
    }
    private static boolean checkIs2x(String fileName) {
        try {
            int index = fileName.lastIndexOf('.');
            String subStr = fileName.substring(index - 3, index);
            return subStr.equals("@2x");
        } catch (Exception e) {
            return false;
        }
    }
    private static String getNameWithout2x(String fileName) {
        if (!Main.checkIs2x(fileName)) {
            return fileName;
        }
        int index = fileName.lastIndexOf('.');
        String name = fileName.substring(0, index - 3) + fileName.substring(index);
        return name;
    }
}
