package ee.ut.jf2013.homework7;

import java.awt.image.BufferedImage;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import static java.lang.System.nanoTime;

public class ForkMerge extends RecursiveAction {
    private int[] source;
    private int start;
    private int length;
    private int[] output;

    private int width;

    public ForkMerge(int[] inputSource, int start, int length, int[] outputSource, int imageWidth) {
        this.source = inputSource;
        this.start = start;
        this.length = length;
        this.output = outputSource;
        this.width = imageWidth;
    }

    // Average pixels from source, write results into destination.
    protected void computeDirectly() {
        for (int index = start; index < start + length; index++) {
            if (index % width > width / 2) {
                mirrorPixels(index);
            } else {
                output[index] = source[index];
            }
        }
    }

    private void mirrorPixels(int index) {
        // Calculate average.
        Color color = new Color();
        color.addPixel(source[index]);
        color.addPixel(source[getOppositeIndex(index)]);
        output[index] = color.reassembleDestinationPixel();
    }

    private int getOppositeIndex(int index) {
        // Find opposite index
        int originalPosition = index % width;
        int oppositePosition = width - originalPosition;
        return oppositePosition + (index - originalPosition);
    }

    protected static int sThreshold = 10000;

    protected void compute() {
        if (length < sThreshold) {
            computeDirectly();
            return;
        }

        int split = length / 2;

        invokeAll(new ForkMerge(source, start, split, output, width),
                new ForkMerge(source, start + split, length - split, output, width));
    }

    public static BufferedImage blur(BufferedImage srcImage) throws InterruptedException {
        int imageWidth = srcImage.getWidth();
        int imageHeight = srcImage.getHeight();

        int[] inputSource = srcImage.getRGB(0, 0, imageWidth, imageHeight, null, 0, imageWidth);
        int[] outputSource = new int[inputSource.length];

        System.out.println("Array size is " + inputSource.length);
        System.out.println("Threshold is " + sThreshold);

        ForkMerge forkMerge = new ForkMerge(inputSource, 0, inputSource.length, outputSource, imageWidth);

        ForkJoinPool pool = new ForkJoinPool();

        long startTime = nanoTime();
        pool.invoke(forkMerge);
        System.out.println("Image merge took " + (nanoTime() - startTime) + " nanoseconds.");

        BufferedImage dstImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        dstImage.setRGB(0, 0, imageWidth, imageHeight, outputSource, 0, imageWidth);

        return dstImage;
    }
}
