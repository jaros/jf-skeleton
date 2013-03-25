package ee.ut.jf2013.homework7;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageProjector {
    public static void viewImage(final String title, final BufferedImage image) {
        new JFrame(title) {
            {
                setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                setSize(image.getWidth(), image.getHeight());
                add(new JPanel() {
                    protected void paintComponent(Graphics g) {
                        int x = (getWidth() - image.getWidth()) / 2;
                        int y = (getHeight() - image.getHeight()) / 2;
                        g.drawImage(image, x, y, this);
                    }
                });
                setLocationByPlatform(true);
                setVisible(true);
            }
        };
    }
}
