package ee.ut.jf2013.homework7;

public class Color {
    float red;
    float green;
    float blue;

    void addPixel(int pixel) {
        red += (float) ((pixel & 0x00ff0000) >> 16) / 2;
        green += (float) ((pixel & 0x0000ff00) >> 8) / 2;
        blue += (float) (pixel & 0x000000ff) / 2;
    }

    int reassembleDestinationPixel() {
        // Re-assemble destination pixel.
        return (0xff000000) |
                (((int) red) << 16) |
                (((int) green) << 8) |
                ((int) blue);

    }
}