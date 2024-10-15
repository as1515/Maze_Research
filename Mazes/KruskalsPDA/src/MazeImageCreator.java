import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MazeImageCreator {
    public void createImage(String outpath, MazeGrid grid, int cellSize) {

        MazeRenderer renderer = new MazeRenderer();
        BufferedImage image = new BufferedImage(grid.getWidth() * cellSize, grid.getHeight() * cellSize,
                BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        renderer.render(g, grid, cellSize);
        g.dispose();

        try {
            ImageIO.write(image, "png", new File(outpath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
