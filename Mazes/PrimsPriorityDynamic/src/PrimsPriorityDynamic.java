import java.util.PriorityQueue;

public class PrimsPriorityDynamic {
    private int rows, cols;
    private Cell[][] grid;
    private PriorityQueue<Wall> mazeWalls;

    public PrimsPriorityDynamic(int rows, int cols) {
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

    public void generateMaze(String path, String filename, int cellSize, String weightTechnique) {
        EditWalls editWalls = new EditWalls(rows, cols, grid, mazeWalls, weightTechnique);
        int entryPosition = (int) (Math.random() * rows);
        Cell start = grid[entryPosition][0];
        int exitPosition = (int) (Math.random() * rows);
        Cell exit = grid[exitPosition][cols - 1];
        start.visited = true;
        editWalls.addWalls(start, exit);

        while (!mazeWalls.isEmpty()) {
            Wall wall = mazeWalls.poll();

            Cell first = wall.first;
            Cell second = wall.second;
            if (!second.visited) {
                second.visited = true;
                editWalls.removeWalls(first, second);
                editWalls.adjustWallWeights(wall, exit);
                editWalls.addWalls(second, exit);
            }
        }
        CreateImage createImage = new CreateImage(rows, cols, grid);
        createImage.createMazeImage(path, cellSize, entryPosition, exitPosition);
        editWalls.saveWallConfiguration(grid, filename, entryPosition, exitPosition);
    }

    public static void main(String[] args) throws Exception {
        String weightTechnique = "ead_vna_slpd_clpd"; // option: ead, vna, slpd and clpd. Combinations like ead_vna +
                                                      // ....
        String path = "MazeImage" + weightTechnique + ".jpg";
        String filename = "MazeConfig" + weightTechnique + ".txt";
        int cellSize = 50;

        PrimsPriorityDynamic maze = new PrimsPriorityDynamic(50, 50);
        maze.generateMaze(path, filename, cellSize, weightTechnique);
    }
}

// wall weight exponential adjustment -- ead
// wall weight visitedNeighbors adjustment -- vna
// wall weight simple local path density adjustment -- slpd
// wall weight complex local path density adjustment -- clpd