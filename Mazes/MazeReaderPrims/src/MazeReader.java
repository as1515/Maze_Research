import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class MazeReader {
    private int rows, cols;
    private Cell[][] grid;

    public class Cell {
        int x, y;
        boolean visited = false;
        boolean[] walls = new boolean[4];

        Cell(int x, int y) {
            this.x = x;
            this.y = y;
            for (int i = 0; i < walls.length; i++) {
                walls[i] = true;
            }
        }
    }

    public void loadWallConfiguration(String filename, String outputImage, int cellSize) {
        // Cell[][] grid = null;
        int entryPosition = -1;
        int exitPosition = -1;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // String weightTechnique = reader.readLine();ß
            // String globalPathDensity = reader.readLine();
            String dimesionLine = reader.readLine();
            String positionLine = reader.readLine();
            System.out.println("positionLine" + positionLine);
            if (dimesionLine != null && positionLine != null) {
                String[] dimensions = dimesionLine.split(" ");
                this.rows = Integer.parseInt(dimensions[0]);
                this.cols = Integer.parseInt(dimensions[1]);
                this.grid = new Cell[this.rows][this.cols];

                String[] position = positionLine.split(" ");
                entryPosition = Integer.parseInt(position[0]);
                exitPosition = Integer.parseInt(position[1]);

                String line;
                int y = 0;

                while ((line = reader.readLine()) != null && y < this.rows) {
                    String[] cellInfos = line.split(" ");
                    for (int x = 0; x < this.cols && x < cellInfos.length; x++) {
                        this.grid[y][x] = new Cell(x, y);
                        String cellInfo = cellInfos[x];
                        for (int i = 0; i < cellInfo.length() && i < 4; i++) {
                            this.grid[y][x].walls[i] = cellInfo.charAt(i) == '1';
                        }
                    }
                    y++;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        createMazeImage(outputImage, cellSize, entryPosition, exitPosition, this.rows, this.cols);
    }

    public void createMazeImage(String path, int cellSize, int entryPosition, int exitPosition, int rows, int cols) {
        int imageWidth = cols * cellSize;
        int imageHeight = rows * cellSize;
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // fill background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, imageWidth, imageHeight);

        // Draw Maze
        g.setColor(Color.BLACK);
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

        for (int i = 0; i < rows; i++) {
            int y = i * cellSize;
            g.drawLine(imageWidth - 1, y, imageWidth - 1, y + cellSize);
        }

        for (int j = 0; j < cols; j++) {
            int x = j * cellSize;
            g.drawLine(x, imageHeight - 1, x + cellSize, imageHeight - 1);
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

    public static void main(String[] args) throws Exception {
        MazeReader readMaze = new MazeReader();
        readMaze.loadWallConfiguration("mazeConf.txt", "maze.jpg", 50);

    }
}
