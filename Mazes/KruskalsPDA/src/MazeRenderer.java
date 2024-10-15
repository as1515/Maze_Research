import java.awt.Color;
import java.awt.Graphics;

public class MazeRenderer {
    public void render(Graphics g, MazeGrid grid, int cellSize) {
        int width = grid.getWidth();
        int height = grid.getHeight();

        for (int x = 0; x < width - 1; x++) {
            for (int y = 0; y < height - 1; y++) {
                if (grid.getCell(x, y) == 0) {
                    g.setColor(Color.BLACK);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
            }

        }
    }
}
