package jorus.util;

import java.io.Serializable;

public class YUV422SPImage implements Serializable, ConvertableImage {

    // Generated by eclipse
    private static final long serialVersionUID = -3513891213723981644L;

    public final int width;

    public final int height;

    public final byte[] pixels;

    public YUV422SPImage(int width, int height) {
        this.width = width;
        this.height = height;
        // YUV422SP uses 2 bytes per pixel
        pixels = new byte[width * height * 2];
    }

    public YUV422SPImage(int width, int height, byte[] image) {
        this.width = width;
        this.height = height;
        this.pixels = image;
    }

    public RGB24Image toRGB24() {
        final int offsetU = width * height;
        // 3 bytes per pixel in a RGB24Image
        final byte[] out = new byte[width * height * 3];

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w += 2) {

                int Y1 = (0xff & pixels[h * width + w]) - 16;
                int Y2 = (0xff & pixels[h * width + (w + 1)]) - 16;

                int U = (0xff & pixels[offsetU + h * width + w]) - 128;
                int V = (0xff & pixels[offsetU + h * width + w + 1]) - 128;

                int r1 = clipAndScale((298 * Y1 + 409 * U + 128) >> 8);
                int r2 = clipAndScale((298 * Y2 + 409 * U + 128) >> 8);

                int g1 = clipAndScale((298 * Y1 - 100 * V - 208 * U + 128) >> 8);
                int g2 = clipAndScale((298 * Y2 - 100 * V - 208 * U + 128) >> 8);

                int b1 = clipAndScale((298 * Y1 + 516 * V + 128) >> 8);
                int b2 = clipAndScale((298 * Y2 + 516 * V + 128) >> 8);

                out[h * width * 3 + w * 3] = (byte) (0xff & r1);
                out[h * width * 3 + w * 3 + 1] = (byte) (0xff & g1);
                out[h * width * 3 + w * 3 + 2] = (byte) (0xff & b1);

                out[h * width * 3 + w * 3 + 3] = (byte) (0xff & r2);
                out[h * width * 3 + w * 3 + 4] = (byte) (0xff & g2);
                out[h * width * 3 + w * 3 + 5] = (byte) (0xff & b2);
            }
        }
        return new RGB24Image(width, height, out);
    }

    private static final byte clipAndScale(int value) {

        if (value > 255)
            value = 255;

        if (value < 0)
            value = 0;

        return (byte) (0xff & ((value * 220) / 256));
    }
}
