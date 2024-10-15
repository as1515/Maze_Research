import java.util.PriorityQueue;

public class KruskalsPriorityMaze {
    private int rows, cols;
    private PriorityQueue<Wall> mazeWalls;
    private int[][] grid;
    private DisjointSets disjointSets;
    private int entryRow;
    private int entryCol;
    private int exitRow;
    private int exitCol;

    public KruskalsPriorityMaze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.mazeWalls = new PriorityQueue<>();
        this.grid = new int[rows * 2 - 1][cols * 2 - 1];
        this.disjointSets = new DisjointSets(rows * cols);

        initializeGrid();
        generateWalls();
    }

    private void initializeGrid() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (i % 2 == 0 && j % 2 == 0) {
                    grid[i][j] = 1; // Cell
                } else {
                    grid[i][j] = 0; // Wall
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

    public void generateMaze(String path, int cellSize, String filename, String weightTechnique,
            MazeDirection biasDirection, double adjustmentFact, String runType, int[] entryExit) {
        ChangeWalls changeWall = new ChangeWalls(grid, weightTechnique);
        for (Wall wall : mazeWalls) {
            int set1 = disjointSets.find(wall.cell1);
            int set2 = disjointSets.find(wall.cell2);

            if (set1 != set2) {
                disjointSets.union(set1, set2);
                changeWall.adjustWeight(wall, exitRow, exitCol, rows, cols, biasDirection, adjustmentFact);
                changeWall.removeWall(wall, cols);
            }
        }

        defineAndOpenEntryExit(biasDirection, runType, entryExit);
        CreateImage createImage = new CreateImage(rows, cols, grid);
        createImage.createMazeImage(path, cellSize, entryRow, entryCol, exitRow, exitCol, biasDirection);
        changeWall.saveWallConfiguration(grid, filename, entryRow, entryCol, exitRow, exitCol);
    }

    private void defineAndOpenEntryExit(MazeDirection biasDirection, String runType, int[] entryExit) {
        if (runType == "Random") {
            switch (biasDirection) {
                case EAST:
                case WEST:
                    entryRow = 2 * (int) (Math.random() * (rows - 1));
                    entryCol = 0;
                    grid[entryRow][entryCol] = 1;

                    exitRow = 2 * (int) (Math.random() * (rows - 1));
                    exitCol = cols * 2 - 2;
                    grid[exitRow][exitCol] = 1;
                    break;
                case NORTH:
                case SOUTH:
                    entryRow = 0;
                    entryCol = 2 * (int) (Math.random() * (cols - 1));
                    grid[entryRow][entryCol] = 1;

                    exitCol = 2 * (int) (Math.random() * (cols - 1));
                    exitRow = rows * 2 - 2;
                    grid[exitRow][exitCol] = 1;
                    break;
            }
        } else if (runType == "Manual") {
            entryRow = entryExit[0];
            entryCol = entryExit[1];
            exitRow = entryExit[2];
            exitCol = entryExit[3];
        }

    }

    public static void main(String[] args) throws Exception {
        KruskalsPriorityMaze maze = new KruskalsPriorityMaze(50, 50);

        String prefix = "maze";
        String weightTechnique = "sad+sld";
        String path = prefix + weightTechnique + ".jpg";
        String file = prefix + weightTechnique + ".txt";
        MazeDirection direction = MazeDirection.NORTH;
        double adjustmentFact = 0.3;
        String runType = "Random";
        // manual { entryRow, entryCol, exitRow -- > 0 - row * 2 - 2, exitCol -- > 0 -
        // cols * 2 - 2}
        int[] entryExit = { 0, 0, 98, 98 };
        maze.generateMaze(path, 50, file, weightTechnique, direction, adjustmentFact, runType, entryExit);
    }
}

// Direction bias

// EAST, WEST, NORTH, SOUTH

// We need to animate the creation of the maze during generateMaze method and
// create a gif from that.
