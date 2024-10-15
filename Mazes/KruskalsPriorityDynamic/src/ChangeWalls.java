import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ChangeWalls {
    int[][] grid;
    String weightTechnique;
    private double globalPathDensity;

    public ChangeWalls(int[][] grid, String weightTechnique) {
        this.grid = grid;
        this.weightTechnique = weightTechnique;
    }

    public void adjustWeight(Wall wall, int exitRow, int exitCol, int rows, int cols, MazeDirection biasDirection,
            double adjustmentFact) {
        int cell1Row = wall.cell1 / cols;
        int cell1Col = wall.cell1 % cols;
        int cell2Row = wall.cell2 / cols;
        int cell2Col = wall.cell2 % cols;

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
            int density1 = calculateLocalPathDensity(wall.cell1, grid, rows, cols);
            int density2 = calculateLocalPathDensity(wall.cell2, grid, rows, cols);

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

    private int calculateLocalPathDensity(int cell, int[][] grid, int rows, int cols) {
        int cellRow = cell / cols;
        int cellCol = cell % cols;

        int density = 0;

        int[] dRow = { -1, 1, 0, 0 };
        int[] dCol = { 0, 0, -1, 1 };

        for (int i = 0; i < 4; i++) {
            int newRow = cellRow * 2 + dRow[i];
            int newCol = cellCol * 2 + dCol[i];

            if (newRow >= 0 && newRow < rows * 2 - 1 && newCol >= 0 && newCol < cols * 2 - 1) {
                if (grid[newRow][newCol] == 1) {
                    density++;
                }
            }
        }

        return density;
    }

    public void removeWall(Wall wall, int cols) {
        int cell1Row = wall.cell1 / cols;
        int cell1Col = wall.cell1 % cols;
        int cell2Row = wall.cell2 / cols;
        int cell2Col = wall.cell2 % cols;

        int wallRow = cell1Row + cell2Row;
        int wallCol = cell1Col + cell2Col;

        grid[wallRow][wallCol] = 1;
    }

    private double calculateGlobalPathDensity(int[][] grid) {
        int pathCells = 0;
        int totalCells = 0;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
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

    private boolean isPartOfPath(int[][] grid, int i, int j) {
        int connections = 0;

        if (i > 0 && grid[i - 1][j] == 1) {
            connections++; // Up
        }
        if (i < grid.length - 2 && grid[i + 1][j] == 1) {
            connections++; // Down
        }
        if (j > 0 && grid[i][j - 1] == 1) {
            connections++; // left
        }
        if (j < grid[0].length - 2 && grid[i][j + 1] == 1) {
            connections++; // right
        }

        return connections >= 2;
    }

    public void saveWallConfiguration(int[][] grid, String filename, int entryRow, int entryCol, int exitRow,
            int exitCol) {
        globalPathDensity = calculateGlobalPathDensity(grid);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(weightTechnique);
            writer.newLine();

            writer.write(String.valueOf(globalPathDensity));
            writer.newLine();

            writer.write(grid.length + " " + grid[0].length);
            writer.newLine();

            writer.write(entryRow + " " + entryCol);
            writer.newLine();

            writer.write(exitRow + " " + exitCol);
            writer.newLine();

            for (int[] row : grid) {
                for (int cell : row) {
                    writer.write(cell + " ");
                }
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
