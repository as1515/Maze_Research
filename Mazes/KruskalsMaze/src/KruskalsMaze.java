import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

public class KruskalsMaze {
    private int rows, cols;
    private List<Wall> mazeWalls;
    private int[][] grid;
    private DisjointSets disjointSets;
    public int entryRow;
    public int exitRow;

    public KruskalsMaze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.mazeWalls = new ArrayList<>();
        this.grid = new int[2 * rows - 1][2 * cols - 1];
        this.disjointSets = new DisjointSets(rows * cols);

        initializeGrid();
        generateWalls();
    }

    private void initializeGrid() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (i % 2 == 0 && j % 2 == 0) {
                    grid[i][j] = 1; // cell
                } else {
                    grid[i][j] = 0; // wall
                }
            }
        }
    }

    private void generateWalls() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int cellIndex = i * cols + j;
                if (j < cols - 1) {
                    mazeWalls.add(new Wall(cellIndex, cellIndex + 1));
                }

                if (i < rows - 1) {
                    mazeWalls.add(new Wall(cellIndex, cellIndex + cols));
                }
            }
        }
    }

    public void generateMaze() {
        Collections.shuffle(mazeWalls);

        for (Wall wall : mazeWalls) {
            int set1 = disjointSets.find(wall.cell1);
            int set2 = disjointSets.find(wall.cell2);

            if (set1 != set2) {
                disjointSets.union(set1, set2);
                removeWall(wall);
            }
        }

        defineAndOpenEntryExit();
    }

    private void defineAndOpenEntryExit() {
        entryRow = 2 * (int) (Math.random() * (rows - 1));
        grid[entryRow][0] = 1;

        exitRow = 2 * (int) (Math.random() * (rows - 1));
        grid[exitRow][cols * 2 - 2] = 1;
    }

    private void removeWall(Wall wall) {
        int cell1Row = wall.cell1 / cols;
        int cell1Col = wall.cell1 % cols;
        int cell2Row = wall.cell2 / cols;
        int cell2Col = wall.cell2 % cols;

        int wallRow = cell1Row + cell2Row;
        int wallCol = cell1Col + cell2Col;

        grid[wallRow][wallCol] = 1;
    }

    public void createMazeImage(String filename, int cellSize) {
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
        graphics.fillRect(0, entryRow * cellSize, cellSize, cellSize);

        exitRow = exitRow * 2 + 1;
        graphics.fillRect(imageWidth - cellSize, exitRow * cellSize, cellSize, cellSize);

        graphics.dispose();

        File outputFile = new File(filename);
        try {
            ImageIO.write(image, "jpg", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        KruskalsMaze maze = new KruskalsMaze(50, 50);
        maze.generateMaze();
        maze.createMazeImage("maze.jpg", 50);
    }
}
