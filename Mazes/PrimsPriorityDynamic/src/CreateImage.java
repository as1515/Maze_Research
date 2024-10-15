import java.io.File;
import java.io.IOException;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class CreateImage {
    private int rows, cols;
    private Cell[][] grid;

    public CreateImage(int rows, int cols, Cell[][] grid) {
        this.rows = rows;
        this.cols = cols;
        this.grid = grid;
    }

    public void createMazeImage(String path, int cellSize, int entryPosition, int exitPosition) {
        int imageWidth = cols * cellSize;
        int imageHeight = rows * cellSize;
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, imageWidth, imageHeight);

        g.setColor(Color.black);
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                Cell cell = grid[i][j];
                int cellX = j * cellSize;
                int cellY = i * cellSize;

                // Draw top wall
                if (cell.walls[0]) {
                    g.drawLine(cellX, cellY, cellX + cellSize, cellY);
                }
                // Draw right wall
                if (cell.walls[1]) {
                    g.drawLine(cellX + cellSize, cellY, cellX + cellSize, cellY + cellSize);
                }
                // Draw Bottom wall
                if (cell.walls[2]) {
                    g.drawLine(cellX, cellY + cellSize, cellX + cellSize, cellY + cellSize);
                }
                // Draw left wall
                if (cell.walls[3]) {
                    g.drawLine(cellX, cellY, cellX, cellY + cellSize);
                }
            }
        }

        // creating the bottom wall
        for (int j = 0; j < cols; j++) {
            int x = j * cellSize;
            g.drawLine(x, imageHeight - 1, x + cellSize, imageHeight - 1);
        }

        // Creating the right wall
        for (int i = 0; i < rows; i++) {
            int y = i * cellSize;
            g.drawLine(imageWidth - 1, y, imageWidth - 1, y + cellSize);
        }

        g.drawRect(0, 0, imageWidth + cellSize, imageHeight - 1);

        if (entryPosition != -1) {
            g.setColor(Color.GRAY);
            g.fillRect(0, entryPosition * cellSize, cellSize / 4, cellSize);
        }

        if (exitPosition != -1) {
            g.setColor(Color.GRAY);
            g.fillRect(imageWidth - cellSize / 4, exitPosition * cellSize, cellSize / 4, cellSize);
        }

        g.dispose();

        try {
            ImageIO.write(image, "jpg", new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
