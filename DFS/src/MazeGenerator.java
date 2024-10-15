import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import javax.imageio.ImageIO;

public class MazeGenerator {
    private int cols, rows;
    private Cell[][] grid;
    private Stack<Cell> stack = new Stack<>();

    class Cell {
        int x, y;
        boolean visited = false;
        boolean[] walls = { true, true, true, true };

        Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public MazeGenerator(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        grid = new Cell[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }
    }

    private void generateMaze() {
        Cell current = grid[0][0]; // initial current cell is the top left corner of the grid 0,0
        stack.push(current); // push this current onto the stack
        current.visited = true; // and ofcourse each grid you visit needs visited to be true;
        while (!stack.isEmpty()) { // until the stack is not empty (while loop)
            current = stack.peek(); // peek into the first entry of the stack
            Cell next = chooseUnvisitedNeighborsCell(current.x, current.y); // assign Cell next using
            // Checkunvisitedneighbor method which will // take in an x and a y
            if (next != null) {// if (!= next is null)
                removeWalls(current, next); // use the current and next to remove wall (check for corner cases)
                stack.push(next); // push next onto the stack
                next.visited = true; // next.visited
            } else {
                stack.pop(); // stack.pop()
            }
        }
        setEnter();
        setExit();
    }

    private Cell chooseUnvisitedNeighborsCell(int x, int y) {
        // goal of this is to choose a random neighbor (after checking corner and edge
        // cases) the return the Cell chosen
        // Use an ArrayList to keep track of the neighboring cells
        ArrayList<Cell> neighbors = new ArrayList<>();

        // Edge and corner cases and assigning random neighbor to array list
        if (x > 0 && !grid[x - 1][y].visited) {
            neighbors.add(grid[x - 1][y]);
        }
        if (y > 0 && !grid[x][y - 1].visited) {
            neighbors.add(grid[x][y - 1]);
        }
        if (x < cols - 1 && !grid[x + 1][y].visited) {
            neighbors.add(grid[x + 1][y]);
        }
        if (y < rows - 1 && !grid[x][y + 1].visited) {
            neighbors.add(grid[x][y + 1]);
        }
        // if the arraylist is not null, get a random neighbor entry according to a
        // random index
        if (neighbors.size() > 0) {
            int index = (int) (Math.random() * neighbors.size());
            return neighbors.get(index);
        } else {
            return null;
        }
    }

    private void removeWalls(Cell current, Cell next) {
        int dx = next.x - current.x;
        int dy = next.y - current.y;

        // adjust the boolean array based on the direction of the random cell that was
        // chosen
        if (dx == 1) {
            current.walls[1] = false;
            next.walls[3] = false;
        } else if (dx == -1) {
            current.walls[3] = false;
            next.walls[1] = false;
        }

        if (dy == 1) {
            current.walls[2] = false;
            next.walls[0] = false;
        } else if (dy == -1) {
            current.walls[0] = false;
            next.walls[2] = false;
        }
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
        grid[0][position].walls[3] = false;
    }
    // We need to add two lines which shows the entry and the exit on the left and
    // the right of the image. according to the entry and exit points.

    public static void main(String[] args) throws Exception {
        MazeGenerator maze = new MazeGenerator(50, 50);
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
