package ee.ut.jf2013.homework7;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import static ee.ut.jf2013.homework7.ForkMerge.blur;
import static ee.ut.jf2013.homework7.ImageProjector.viewImage;

public class ImageTransformer {
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length == 0) {
            throw new IllegalArgumentException("The file name must be specified in the arguments");
        }
        String fileName = args[0];

        final BufferedImage image = ImageIO.read(Files.newInputStream(FileSystems.getDefault().getPath(fileName)));
        viewImage("ForkMerge - original", image);
        viewImage("ForkMerge - processed", blur(image));
    }
}
