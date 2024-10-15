import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.PriorityQueue;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class PrimsPriorityMazeGenerator {
    private int rows, cols;
    private Cell[][] grid;
    private PriorityQueue<Wall> mazeWalls;

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

    private static class Wall implements Comparable<Wall> {
        Cell first;
        Cell second;
        Double weight;

        public Wall(Cell first, Cell second) {
            this.first = first;
            this.second = second;
            this.weight = Math.random();
        }

        @Override
        public int compareTo(Wall other) {
            return Double.compare(this.weight, other.weight);
        }
    }

    private void addWalls(Cell cell, Cell exit) {
        int x = cell.x;
        int y = cell.y;

        if (y > 0 && !grid[y - 1][x].visited) {
            Wall newWall = new Wall(cell, grid[y - 1][x]);
            adjustWallWeights(newWall, exit);
            mazeWalls.add(newWall);
        }

        if (x > 0 && !grid[y][x - 1].visited) {
            Wall newWall = new Wall(cell, grid[y][x - 1]);
            adjustWallWeights(newWall, exit);
            mazeWalls.add(newWall);
        }

        if (y < rows - 1 && !grid[y + 1][x].visited) {
            Wall newWall = new Wall(cell, grid[y + 1][x]);
            adjustWallWeights(newWall, exit);
            mazeWalls.add(newWall);
        }

        if (x < cols - 1 && !grid[y][x + 1].visited) {
            Wall newWall = new Wall(cell, grid[y][x + 1]);
            adjustWallWeights(newWall, exit);
            mazeWalls.add(newWall);
        }
    }

    private void removeWalls(Cell first, Cell second) {
        int dx = second.x - first.x;
        int dy = second.y - first.y;

        // Top
        if (dy < 0) {
            first.walls[0] = false;
            second.walls[2] = false;
        }
        // Right
        if (dx > 0) {
            first.walls[1] = false;
            second.walls[3] = false;
        }
        // Bottom
        if (dy > 0) {
            first.walls[2] = false;
            second.walls[0] = false;
        }
        // Left
        if (dx < 0) {
            first.walls[3] = false;
            second.walls[1] = false;
        }
    }

    public PrimsPriorityMazeGenerator(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        grid = new Cell[rows][cols];
        mazeWalls = new PriorityQueue<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Cell(j, i);
            }
        }

    }

    private void adjustWallWeights(Wall wall, Cell exit) {
        int distanceFirst = Math.abs(exit.x - wall.first.x) + Math.abs(exit.y - wall.first.y);
        int distanceSecond = Math.abs(exit.x - wall.second.x) + Math.abs(exit.y - wall.second.y);

        int distance = Math.min(distanceFirst, distanceSecond);

        wall.weight += Math.pow(2, (1.0 / (distance + 1)));

        if (isContinuingStraightPath(wall, grid)) {
            wall.weight *= 1.1;
        }
    }

    private boolean isContinuingStraightPath(Wall wall, Cell[][] grid) {
        boolean isHorizontal = wall.first.y == wall.second.y;

        if (isHorizontal) {
            int x = wall.first.x;
            int y = wall.first.y;

            boolean pathContinuesUp = y > 0 && !grid[y - 1][x].walls[2];
            if (x < grid[y].length - 1) { // Make sure x+1 does not go out of bounds
                pathContinuesUp = pathContinuesUp && !grid[y - 1][x + 1].walls[2];
            }

            boolean pathContinuesDown = y < grid.length - 1 && !grid[y + 1][x].walls[0];
            if (x < grid[y].length - 1) { // Make sure x+1 does not go out of bounds
                pathContinuesDown = pathContinuesDown && !grid[y + 1][x + 1].walls[0];
            }

            return pathContinuesUp || pathContinuesDown;
        } else {
            int x = wall.first.x;
            int y = wall.first.y;

            boolean pathContinuesLeft = x > 0 && !grid[y][x - 1].walls[1];
            if (y < grid.length - 1) { // Make sure y+1 does not go out of bounds
                pathContinuesLeft = pathContinuesLeft && !grid[y + 1][x - 1].walls[1];
            }

            boolean pathContinuesRight = x < grid[0].length - 1 && !grid[y][x + 1].walls[3];
            if (y < grid.length - 1) { // Make sure y+1 does not go out of bounds
                pathContinuesRight = pathContinuesRight && !grid[y][x + 1].walls[3];
            }

            return pathContinuesLeft || pathContinuesRight;
        }
    }

    public void generateMaze(String path, String filename, int cellSize) {
        int entryPosition = (int) (Math.random() * rows);
        Cell start = grid[entryPosition][0];
        int exitPosition = (int) (Math.random() * rows);
        Cell exit = grid[exitPosition][cols - 1];
        start.visited = true;
        addWalls(start, exit);

        while (!mazeWalls.isEmpty()) {
            Wall wall = mazeWalls.poll();

            Cell first = wall.first;
            Cell second = wall.second;
            if (!second.visited) {
                second.visited = true;
                removeWalls(first, second);
                adjustWallWeights(wall, exit);
                addWalls(second, exit);
            }
        }
        createMazeImage(path, cellSize, entryPosition, exitPosition);
        saveWallConfiguration(grid, filename, entryPosition, exitPosition);
    }

    public void createMazeImage(String path, int cellSize, int entryPosition, int exitPosition) {
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

    public void saveWallConfiguration(Cell[][] grid, String filename, int entryPosition, int exitPosition) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(grid.length + " " + grid[0].length);
            writer.newLine();

            writer.write(entryPosition + " " + exitPosition);
            writer.newLine();

            for (Cell[] row : grid) {
                for (Cell cell : row) {
                    for (boolean wall : cell.walls) {
                        writer.write(wall ? '1' : '0');
                    }
                    writer.write(' ');
                }
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        PrimsPriorityMazeGenerator maze = new PrimsPriorityMazeGenerator(50, 50);
        maze.generateMaze("mazePowStraight.jpg", "mazeConf.txt", 50);
    }
}

// Dynamic Weight Adjustment: Instead of using a fixed formula, you could
// dynamically adjust weights based on the current state of the maze generation.
// For instance, you could analyze the local density of paths or the number of
// adjacent visited cells to make certain areas denser and more complex.

// Bias Towards Longer Detours: You can introduce a bias that favors walls
// removal leading to longer detours, especially as one gets closer to the exit.
// This could involve analyzing potential future paths and preferring those that
// do not lead directly to the exit.