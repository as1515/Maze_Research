import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class MazeReaderK {
    private int rows, cols;
    private int[][] grid;
    private int entryRow;
    private int entryCol;
    private int exitRow;
    private int exitCol;

    public void loadWallConfiguration(String filename, String outputImage, int cellSize) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String weightTechnique = reader.readLine();
            String globalDensity = reader.readLine();
            String dimensionLine = reader.readLine();
            String entryLine = reader.readLine();
            String exitLine = reader.readLine();

            if (dimensionLine != null && entryLine != null && exitLine != null) {
                String[] dimensions = dimensionLine.split(" ");
                this.rows = Integer.parseInt(dimensions[0]);
                this.cols = Integer.parseInt(dimensions[1]);

                grid = new int[rows * 2 + 1][cols * 2 + 1];

                String[] entry = entryLine.split(" ");
                entryRow = Integer.parseInt(entry[0]);
                entryCol = Integer.parseInt(entry[1]);

                String[] exit = exitLine.split(" ");
                exitRow = Integer.parseInt(exit[0]);
                exitCol = Integer.parseInt(exit[1]);

                String line;
                int y = 0;

                while ((line = reader.readLine()) != null && y < this.rows) {
                    String[] cellInfo = line.split(" ");
                    for (String info : cellInfo) {
                        if (y == this.rows - 1) {
                            System.out.print(info);

                        }
                    }
                    for (int x = 0; x < this.cols && x < cellInfo.length; x++) {
                        grid[y][x] = Integer.parseInt(cellInfo[x]);
                    }
                    y++;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        createMazeImage(outputImage, cellSize, entryRow, entryCol, exitRow, exitCol);
    }

    private void createMazeImage(String filename, int cellSize, int entryRow, int entryCol, int exitRow, int exitCol) {
        int width = cols + 2;
        int height = rows + 2;

        int imageWidth = width * cellSize;
        int imageHeight = height * cellSize;

        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, imageWidth, imageHeight);

        graphics.setColor(Color.BLACK);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
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
        graphics.fillRect(entryCol * cellSize, entryRow * cellSize, cellSize, cellSize);
        graphics.fillRect((exitCol + 2) * cellSize, (exitRow + 2) * cellSize, cellSize, cellSize);

        graphics.dispose();

        File outputFile = new File(filename);
        try {
            ImageIO.write(image, "jpg", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        MazeReaderK readMaze = new MazeReaderK();
        readMaze.loadWallConfiguration("mazesad+sld.txt", "mazesad+sld.jpg", 50);

    }
}
