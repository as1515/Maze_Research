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
