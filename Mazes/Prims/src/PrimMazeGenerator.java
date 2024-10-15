import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class PrimMazeGenerator {
    private int rows, cols;
    private Cell[][] grid;
    private List<Wall> mazeWalls;

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

    private static class Wall {
        Cell first;
        Cell second;

        public Wall(Cell first, Cell second) {
            this.first = first;
            this.second = second;
        }

    }

    private void addWalls(Cell cell) {
        int x = cell.x;
        int y = cell.y;

        if (x > 0 && !grid[y][x - 1].visited) {
            mazeWalls.add(new Wall(cell, grid[y][x - 1]));
        }
        if (y > 0 && !grid[y - 1][x].visited) {
            mazeWalls.add(new Wall(cell, grid[y - 1][x]));
        }
        if (x < cols - 1 && !grid[y][x + 1].visited) {
            mazeWalls.add(new Wall(cell, grid[y][x + 1]));
        }
        if (y < rows - 1 && !grid[y + 1][x].visited) {
            mazeWalls.add(new Wall(cell, grid[y + 1][x]));
        }
    }

    private void removeWalls(Cell first, Cell second) {
        int dx = second.x - first.x;
        int dy = second.y - first.y;

        // Up
        if (dy < 0) {
            first.walls[0] = false;
            second.walls[2] = false;
        }
        // Right
        if (dx > 0) {
            first.walls[1] = false;
            second.walls[3] = false;
        }
        // Down
        if (dy > 0) {
            first.walls[2] = false;
            second.walls[0] = false;
        }
        // left
        if (dx < 0) {
            first.walls[3] = false;
            second.walls[1] = false;
        }
    }

    public PrimMazeGenerator(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        grid = new Cell[rows][cols];
        mazeWalls = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Cell(j, i);
            }
        }
    }

    public void generateMaze() {
        Cell start = grid[0][0];
        start.visited = true;
        addWalls(start);

        while (!mazeWalls.isEmpty()) {
            Collections.shuffle(mazeWalls);
            Wall wall = mazeWalls.remove(0);

            Cell first = wall.first;
            Cell second = wall.second;
            if (!second.visited) {
                second.visited = true;
                removeWalls(first, second);
                addWalls(second);
            }
        }
        setEnter();
        setExit();
    }

    public void createMazeImage(String path, int cellSize) {
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
                int cellX = i * cellSize;
                int cellY = j * cellSize;
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

        int entryPosition = -1;
        for (int i = 0; i < rows; i++) {
            if (!grid[i][0].walls[3]) {
                entryPosition = i;
                break;
            }
        }

        if (entryPosition != -1) {
            g.setColor(Color.GRAY);
            g.fillRect(0, entryPosition * cellSize, cellSize / 4, cellSize);
        }

        int exitPosition = -1;
        for (int i = 0; i < rows; i++) {
            if (!grid[i][cols - 1].walls[1]) {
                exitPosition = i;
                break;
            }
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

    // random exit on the right
    private void setExit() {
        int position = (int) (Math.random() * rows);
        grid[position][cols - 1].walls[1] = false;
    }

    // random entry on the left
    private void setEnter() {
        int position = (int) (Math.random() * rows);
        grid[position][0].walls[3] = false;
    }

    public static void main(String[] args) throws Exception {
        PrimMazeGenerator maze = new PrimMazeGenerator(50, 50);
        maze.generateMaze();
        maze.createMazeImage("maze.jpg", 50);
    }
}

// first need to implement the other 2 maze making algorithms
// try merging maze with an image using canva
// couple of ideas
// simulate particles like water or a paste by defining physical properties like
// viscocity, density and what not
// path finding using A* algorithm
// if we provide an image can they place it onto the image using an empty circle
// to place it in, and then define entry points to that!!

// animate how the maze is generated
// make a method so that you can save the cell.walls information in a txt and
// can generate a
// txt to get the same maze out each time.
// implement prims with priorityqueue
