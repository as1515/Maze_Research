import java.util.PriorityQueue;

public class MazeGenerator {
    private MazeGrid grid;
    private DisjointSets sets;
    private PriorityQueue<Wall> walls;
    private int rows;
    private int cols;
    private int entryCol;
    private int entryRow;
    private int exitCol;
    private int exitRow;
    private MazeDirection biasDirection;
    private double adjustmentFact;
    private String weightTechnique;

    public MazeGenerator(int rows, int cols, MazeDirection biasDirection, String runType, int[] entryExit,
            double adjustmentFact, String weightTechnique) {
        this.rows = rows * 2 - 1;
        this.cols = cols * 2 - 1;
        this.biasDirection = biasDirection;
        this.adjustmentFact = adjustmentFact;
        this.weightTechnique = weightTechnique;
        grid = new MazeGrid(rows, cols);
        sets = new DisjointSets(rows * cols);
        walls = new PriorityQueue<>();

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int cellIndex = y * cols + x;

                if (x < cols - 1) {
                    walls.add(new Wall(cellIndex, cellIndex + 1));
                }

                if (y < rows - 1) {
                    walls.add(new Wall(cellIndex, cellIndex + cols));
                }
            }
        }

        defineAndOpenEntryExit(biasDirection, runType, entryExit);
    }

    public boolean step() {
        if (walls.isEmpty()) {
            return false;
        }

        Wall wall = walls.poll();
        int set1 = sets.find(wall.cell1);
        int set2 = sets.find(wall.cell2);

        if (set1 != set2) {
            sets.union(set1, set2);
            grid.adjustWeight(wall, exitRow, exitCol, biasDirection,
                    adjustmentFact, weightTechnique);
            grid.removeWall(wall);
        }

        return !walls.isEmpty();
    }

    public MazeGrid getGrid() {
        return grid;
    }

    public void defineAndOpenEntryExit(MazeDirection biasDirection, String runType, int[] entryExit) {
        if (runType == "Random") {
            switch (biasDirection) {
                case EAST:
                case WEST:
                    entryRow = 2 * (int) (Math.random() * ((rows - 1) / 2)) * 2 + 1;
                    entryCol = 0;
                    grid.setCell(entryCol, entryRow, 1);

                    exitRow = 2 * (int) (Math.random() * ((rows - 1) / 2)) * 2 + 1;
                    exitCol = cols * 2 + 1;
                    grid.setCell(exitCol, exitRow, 1);
                    break;
                case NORTH:
                case SOUTH:
                    entryRow = 0;
                    entryCol = 2 * (int) (Math.random() * ((cols - 1) / 2)) * 2 + 1;
                    grid.setCell(entryCol, entryRow, 1);

                    exitCol = 2 * (int) (Math.random() * ((cols - 1) / 2)) * 2 + 1;
                    exitRow = rows * 2 + 1;
                    grid.setCell(exitCol, exitRow, 1);
                    break;
            }
        } else if (runType == "Manual") {
            entryRow = entryExit[0];
            entryCol = entryExit[1];
            exitRow = entryExit[2];
            exitCol = entryExit[3];
        }

    }
}
