package com.focosee.qingshow;
import org.imgscalr.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
/**
 * Created by wxy325 on 2/8/15.
 */
public class ImageResizer {
    public static void resizeImage(String imageName, String destName) {
        try {
            File file = new File(imageName);
            BufferedImage img = ImageIO.read(file);
            int width = img.getWidth(null);
            int height = img.getHeight(null);

            BufferedImage resizedImage = Scalr.resize(img, width / 2, height / 2);

            File destFile = new File(destName);
            FileOutputStream out = new FileOutputStream(destFile);
            int dotIndex = destName.lastIndexOf('.');
            String format = destName.substring(dotIndex + 1);
            ImageIO.write(resizedImage, format, out);
            out.close();
            System.out.println("Resize success: " + imageName);
            System.out.println("    => " + destName);
        } catch (Exception e) {
            System.out.println("Resize fail: " + imageName);
        }
    }
}
