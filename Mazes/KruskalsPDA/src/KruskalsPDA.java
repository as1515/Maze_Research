import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class KruskalsPDA extends JPanel implements ActionListener {
    private MazeGenerator generator;
    private MazeRenderer renderer;
    private Timer timer;
    private int cellSize;
    private int rows;
    private int cols;

    public KruskalsPDA(int rows, int cols, MazeDirection biasDirection, String runType, int[] entryExit,
            double adjustmentFact, String weightTechnique) {
        this.rows = rows;
        this.cols = cols;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double screenWidth = screenSize.getWidth();
        double screenHeight = screenSize.getHeight();

        int maxMazeWidth = (int) screenWidth - 100;
        int maxMazeHeight = (int) screenHeight - 100;

        int maxCellSizeWidth = maxMazeWidth / (cols * 2);
        int maxCellSizeHeight = maxMazeHeight / (rows * 2);

        this.cellSize = Math.min(maxCellSizeWidth, maxCellSizeHeight);

        generator = new MazeGenerator(rows, cols, biasDirection, runType, entryExit, adjustmentFact, weightTechnique);
        renderer = new MazeRenderer();

        timer = new Timer(1, this);
        setPreferredSize(new Dimension((cols * 2) * cellSize, (rows * 2) * cellSize));
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.render(g, generator.getGrid(), cellSize);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!generator.step()) {
            timer.stop();
            MazeImageCreator image = new MazeImageCreator();
            image.createImage("MazeExample.png", generator.getGrid(), cellSize);
            return;
        }
        repaint();
    }

    public static void main(String[] args) throws Exception {
        int rows = 50;
        int cols = 50;
        double adjustmentFact = 0.5;
        MazeDirection direction = MazeDirection.EAST;
        // NORTH, SOUTH, EAST, WEST
        String runType = "Random";
        // Random, Manual
        int[] entryExit = { 0, 0, 98, 98 };
        // If Manual { entryRow, entryCol, exitRow -- > 0 - row * 2 - 2, exitCol -- > 0
        // -
        // cols * 2 - 2}
        String weightTechnique = "sad";
        // wall weight linear adjustment - 'sad'
        // wall weight exponential adjustment -- ead
        // wall weight simple local path density adjustment -- sld

        JFrame frame = new JFrame("Maze Animation");
        KruskalsPDA animation = new KruskalsPDA(rows, cols, direction, runType, entryExit, adjustmentFact,
                weightTechnique);
        frame.add(animation);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

// Finally we need to animate
// need to create a gif from this.
// We need to animate the solution. using A*
