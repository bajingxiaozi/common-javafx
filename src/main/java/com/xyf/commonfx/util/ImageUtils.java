package com.xyf.commonfx.util;

import com.xyf.common.annotation.WorkThread;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ImageUtils {

    @Nonnull
    @WorkThread
    public static Image tint(@Nonnull Image image, int color) {
        final int width = (int) image.getWidth();
        final int height = (int) image.getHeight();
        PixelReader pixelReader = image.getPixelReader();
        WritableImage writableImage = new WritableImage(pixelReader, width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                final int fromColor = pixelReader.getArgb(i, j);
                final int fromColorAlpha = (fromColor & 0xFF000000) >> 6;
                if (fromColorAlpha != 0) {
                    final int toColor = (fromColor & 0xFF000000) | (color & 0x00FFFFFF);
                    pixelWriter.setArgb(i, j, toColor);
                }
            }
        }
        return writableImage;
    }

    @WorkThread
    public static void save(@Nonnull Image image, @Nonnull File file) throws IOException {
        FileUtils.forceMkdirParent(file);
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
    }

    @WorkThread
    public static void tint(@Nonnull Image image, int color, @Nonnull File file) throws IOException {
        final Image tint = tint(image, color);
        save(tint, file);
    }

}
