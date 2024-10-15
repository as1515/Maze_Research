import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class CreateImage {
    private int rows, cols;
    private int[][] grid;

    public CreateImage(int rows, int cols, int[][] grid) {
        this.rows = rows;
        this.cols = cols;
        this.grid = grid;
    }

    public void createMazeImage(String filename, int cellSize, int entryRow, int entryCol, int exitRow, int exitCol,
            MazeDirection biasDirection) {
        int width = cols * 2 + 1;
        int height = rows * 2 + 1;

        int imageWidth = width * cellSize;
        int imageHeight = height * cellSize;

        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, imageWidth, imageHeight);

        graphics.setColor(Color.BLACK);
        for (int i = 0; i < rows * 2 - 1; i++) {
            for (int j = 0; j < cols * 2 - 1; j++) {
                if (grid[i][j] == 0) {
                    graphics.fillRect((j + 1) * cellSize, (i + 1) * cellSize, cellSize, cellSize);
                }
            }
        }

        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, imageWidth, cellSize); // top boundary
        graphics.fillRect(0, 0, cellSize, imageHeight); // left boundary
        graphics.fillRect(0, imageHeight - cellSize, imageWidth, cellSize); // bottom boundary
        graphics.fillRect(imageWidth - cellSize, 0, cellSize, imageHeight); // Right boundary

        graphics.setColor(Color.GRAY);
        if (biasDirection == MazeDirection.EAST || biasDirection == MazeDirection.WEST) {
            // For EAST or WEST bias, place entry and exit on left/right edges.
            graphics.fillRect(entryCol * cellSize, entryRow * cellSize, cellSize, cellSize);
            graphics.fillRect((exitCol + 2) * cellSize, (exitRow + 2) * cellSize, cellSize, cellSize);
        } else {
            // For NORTH or SOUTH bias, place entry and exit on top/bottom edges.
            graphics.fillRect(entryCol * cellSize, entryRow * cellSize, cellSize, cellSize);
            graphics.fillRect((exitCol + 2) * cellSize, (exitRow + 2) * cellSize, cellSize, cellSize);
        }

        graphics.dispose();

        File outputFile = new File(filename);
        try {
            ImageIO.write(image, "jpg", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
