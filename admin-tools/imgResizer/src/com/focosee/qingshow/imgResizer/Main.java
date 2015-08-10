package com.focosee.qingshow.imgResizer;

import com.focosee.qingshow.imgResizer.file.FileNameUtil;
import com.focosee.qingshow.imgResizer.file.FileWalker;
import com.focosee.qingshow.imgResizer.file.IFileHandler;

import java.io.File;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please input a path of folder");
            return;
        }
        String path = args[0];

        final ArrayList<String> suffixList = new ArrayList<String>();
        final ArrayList<Double> radiusList = new ArrayList<Double>();
        suffixList.add("_l");
        radiusList.add(2.0);
        suffixList.add("_s");
        radiusList.add(1 / 2.0);
        suffixList.add("_xs");
        radiusList.add(1 / 2.0 / 2.0);
        suffixList.add("_xxs");
        radiusList.add(1 / 2.0 / 2.0 / 2.0);
        suffixList.add("_xxxs");
        radiusList.add(1 / 2.0 / 2.0 / 2.0 / 2.0);

        FileWalker.walkThroughDirectory(path, new IFileHandler() {
            @Override
            public void handleFile(File file) {
                String filePath = file.getAbsolutePath();
                if (FileNameUtil.checkIsNormalImage(filePath, suffixList)) {
                    for (int i = 1; i < suffixList.size(); i++) {
                        String suffix = suffixList.get(i);
                        double radius = radiusList.get(i);
                        String name = FileNameUtil.getNameWithSuffix(filePath, suffix);
                        File f = new File(name);
                        if (f.exists()) {
                            f.delete();
                        }
                        ImageResizer.resizeImage(filePath, name, radius);
                    }
                }
            }
        });

        System.out.println("Completed");
    }




}
