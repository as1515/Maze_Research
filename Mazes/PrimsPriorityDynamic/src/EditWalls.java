import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.PriorityQueue;

public class EditWalls {
    private int rows, cols;
    private Cell[][] grid;
    private PriorityQueue<Wall> mazeWalls;
    private String weightTechnique;
    private double globalPathDensity;

    public EditWalls(int rows, int cols, Cell[][] grid, PriorityQueue<Wall> mazeWalls, String weightTechnique) {
        this.rows = rows;
        this.cols = cols;
        this.grid = grid;
        this.mazeWalls = mazeWalls;
        this.weightTechnique = weightTechnique;
    }

    public void addWalls(Cell cell, Cell exit) { // pass in grid, rows and cols as variables instead of instantiating
                                                 // them here!
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

    public void removeWalls(Cell first, Cell second) {
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

    public void adjustWallWeights(Wall wall, Cell exit) {
        if (weightTechnique.contains("ead")) {
            int distanceFirst = Math.abs(exit.x - wall.first.x) + Math.abs(exit.y - wall.first.y);
            int distanceSecond = Math.abs(exit.x - wall.second.x) + Math.abs(exit.y - wall.second.y);

            int distance = Math.min(distanceFirst, distanceSecond);

            wall.weight += Math.pow(2, (1.0 / (distance + 1)));
        }

        if (weightTechnique.contains("vna")) {
            int visitedNeighbors = countVisitedNeighbors(wall.first) + countVisitedNeighbors(wall.second);

            wall.weight *= 1 + (0.1 * visitedNeighbors);

        }

        if (weightTechnique.contains("slpd")) {
            double simpleLocalDensity = calculateSimpleLocalPathDensity(wall.first, wall.second);

            wall.weight *= (1.0 + simpleLocalDensity);
        }

        if (weightTechnique.contains("clpd")) {
            double complexLocalDensity = calculateComplexLocalPathDensity(wall.first, wall.second);

            wall.weight *= (1.0 + complexLocalDensity);
        }
    }

    private int countVisitedNeighbors(Cell cell) {
        int count = 0;
        int x = cell.x;
        int y = cell.y;

        if (y > 0 && !grid[y - 1][x].visited) {
            count++;
        }
        if (x > 0 && !grid[y][x - 1].visited) {
            count++;
        }
        if (y < rows - 1 && !grid[y + 1][x].visited) {
            count++;
        }
        if (x < cols - 1 && !grid[y][x + 1].visited) {
            count++;
        }

        return count;
    }

    private double calculateSimpleLocalPathDensity(Cell first, Cell second) {
        int range = 2;
        int pathCount = 0;
        int areaCount = 0;

        for (int i = -range; i <= range; i++) {
            for (int j = -range; j <= range; j++) {
                int firstNeighborX = first.x + i;
                int firstNeighborY = first.y + j;
                int secondNeighborX = second.x + i;
                int secondNeighborY = second.y + j;

                if (firstNeighborX >= 0 && firstNeighborX < cols && firstNeighborY >= 0 && firstNeighborY < rows) {
                    areaCount++;
                    if (!hasAllWalls(grid[firstNeighborY][firstNeighborX])) {
                        pathCount++;
                    }
                }

                if ((secondNeighborX != firstNeighborX || secondNeighborY != firstNeighborY) && secondNeighborX >= 0
                        && secondNeighborX < cols && secondNeighborY >= 0 && secondNeighborY < rows) {
                    areaCount++;
                    if (!hasAllWalls(grid[secondNeighborY][secondNeighborX])) {
                        pathCount++;
                    }
                }
            }
        }

        return pathCount / areaCount;
    }

    private boolean hasAllWalls(Cell cell) {
        for (boolean wall : cell.walls) {
            if (!wall) {
                return false;
            }
        }
        return true;
    }

    private double calculateComplexLocalPathDensity(Cell first, Cell second) {
        int range = 2;
        int connectionCount = 0;
        int areaCount = 0;

        for (int i = -range; i <= range; i++) {
            for (int j = -range; j <= range; j++) {
                int firstNeighborX = first.x + i;
                int firstNeighborY = first.y + j;
                int secondNeighborX = second.x + i;
                int secondNeighborY = second.y + j;

                if (firstNeighborX >= 0 && firstNeighborX < cols && firstNeighborY >= 0 && firstNeighborY < rows) {
                    areaCount++;
                    if (!isPartOfPath(grid[firstNeighborY][firstNeighborX])) {
                        connectionCount++;
                    }
                }

                if ((secondNeighborX != firstNeighborX || secondNeighborY != firstNeighborY) && secondNeighborX >= 0
                        && secondNeighborX < cols && secondNeighborY >= 0 && secondNeighborY < rows) {
                    areaCount++;
                    if (!isPartOfPath(grid[secondNeighborY][secondNeighborX])) {
                        connectionCount++;
                    }
                }
            }
        }

        return connectionCount / areaCount;
    }

    private double calculateGlobalPathDensity(Cell[][] grid) {
        int pathCells = 0;
        int totalCells = rows * cols;

        for (Cell[] row : grid) {
            for (Cell cell : row) {
                if (isPartOfPath(cell)) {
                    pathCells++;
                }
            }
        }
        return (double) pathCells / totalCells;
    }

    private boolean isPartOfPath(Cell cell) {
        int openWalls = 4;
        for (boolean wall : cell.walls) {
            if (!wall) {
                openWalls--;
            }
        }
        return openWalls <= 2;
    }

    public void saveWallConfiguration(Cell[][] grid, String filename, int entryPosition, int exitPosition) {
        globalPathDensity = calculateGlobalPathDensity(grid);
        System.out.println(globalPathDensity);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(weightTechnique);
            writer.newLine();

            writer.write(String.valueOf(globalPathDensity));
            writer.newLine();

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

}
