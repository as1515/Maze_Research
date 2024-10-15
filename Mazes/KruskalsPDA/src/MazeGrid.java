import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class MazeGrid {
    private int width;
    private int height;
    private int[][] grid;
    private double globalPathDensity;

    public MazeGrid(int cellsWide, int cellsHigh) {
        this.width = cellsWide * 2;
        this.height = cellsHigh * 2;
        this.grid = new int[height][width];
        initializeGrid();
    }

    private void initializeGrid() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i % 2 == 0 && j % 2 == 0) {
                    grid[i][j] = 1;
                } else {
                    grid[i][j] = 0;
                }
            }
        }
    }

    public void setCell(int x, int y, int value) {
        if (x >= 0 && x < width - 1 && y >= 0 && y < height - 1) {
            grid[y][x] = value;
        }
    }

    public int getCell(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return grid[y][x];
        }

        return -1;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // study the other ones coordinate system
    public void removeWall(Wall wall) {
        int cell1X = wall.cell1 % ((width + 1) / 2);
        int cell1Y = wall.cell1 / ((width + 1) / 2);
        int cell2X = wall.cell2 % ((width + 1) / 2);
        int cell2Y = wall.cell2 / ((width + 1) / 2);

        int wallX = cell1X + cell2X;
        int wallY = cell1Y + cell2Y;

        setCell(wallX, wallY, 1);
    }

    // change to height and width for this and
    public void adjustWeight(Wall wall, int exitRow, int exitCol, MazeDirection biasDirection,
            double adjustmentFact, String weightTechnique) {
        int cell1Row = wall.cell1 / ((width + 1) / 2);
        int cell1Col = wall.cell1 % ((width + 1) / 2);
        int cell2Row = wall.cell2 / ((width + 1) / 2);
        int cell2Col = wall.cell2 % ((width + 1) / 2);

        int distanceFirst = Math.abs(exitRow - cell1Row) + Math.abs(exitCol - cell1Col);
        int distanceSecond = Math.abs(exitRow - cell2Row) + Math.abs(exitCol - cell2Col);

        int distance = Math.min(distanceFirst, distanceSecond);
        if (weightTechnique.contains("sad")) {
            wall.weight += (1.0 / (distance + 1));
        }
        if (weightTechnique.contains("ead")) {
            wall.weight += Math.pow(2, (1.0 / (distance + 1)));
        }

        if (weightTechnique.contains("sld")) {
            int density1 = calculateLocalPathDensity(wall.cell1, grid);
            int density2 = calculateLocalPathDensity(wall.cell2, grid);

            int averageDensity = (density1 + density2) / 2;

            wall.weight += averageDensity;
        }

        boolean isHorizontal = cell1Row == cell2Row;

        switch (biasDirection) {
            case EAST:
            case WEST:
                if (isHorizontal) {
                    wall.weight -= adjustmentFact;
                }
                break;
            case NORTH:
            case SOUTH:
                if (!isHorizontal) {
                    wall.weight -= adjustmentFact;
                }
                break;

        }
    }

    private int calculateLocalPathDensity(int cell, int[][] grid) {
        int cellRow = cell / ((width + 1) / 2);
        int cellCol = cell % ((width + 1) / 2);

        int density = 0;

        int[] dRow = { -1, 1, 0, 0 };
        int[] dCol = { 0, 0, -1, 1 };

        for (int i = 0; i < 4; i++) {
            int newRow = cellRow * 2 + dRow[i];
            int newCol = cellCol * 2 + dCol[i];

            if (newRow >= 0 && newRow < height && newCol >= 0 && newCol < width) {
                if (grid[newRow][newCol] == 1) {
                    density++;
                }
            }
        }

        return density;
    }

    private double calculateGlobalPathDensity(MazeGrid grid) {
        int pathCells = 0;
        int totalCells = 0;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i % 2 == 0 && j % 2 == 0) {
                    totalCells++;
                    if (isPartOfPath(grid, i, j)) {
                        pathCells++;
                    }
                }
            }
        }

        return totalCells > 0 ? (double) pathCells / totalCells : 0;
    }

    private boolean isPartOfPath(MazeGrid grid, int i, int j) {
        int connections = 0;

        if (i > 0 && grid.getCell(j, i - 1) == 1) {
            connections++; // Up
        }
        if (i < height - 2 && grid.getCell(j, i + 1) == 1) {
            connections++; // Down
        }
        if (j > 0 && grid.getCell(j - 1, i) == 1) {
            connections++; // left
        }
        if (j < width - 2 && grid.getCell(j + 1, i) == 1) {
            connections++; // right
        }

        return connections >= 2;
    }

    // public void saveWallConfiguration(MazeGrid grid, String filename, int
    // entryRow, int entryCol, int exitRow,
    // int exitCol) {
    // globalPathDensity = calculateGlobalPathDensity(grid);
    // try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
    // writer.write(weightTechnique);
    // writer.newLine();

    // writer.write(String.valueOf(globalPathDensity));
    // writer.newLine();

    // writer.write(height + " " + width);
    // writer.newLine();

    // writer.write(entryRow + " " + entryCol);
    // writer.newLine();

    // writer.write(exitRow + " " + exitCol);
    // writer.newLine();

    // for (int[] row : grid) {
    // for (int cell : row) {
    // writer.write(cell + " ");
    // }
    // writer.newLine();
    // }
    // writer.close();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }
}
