import java.util.*;
import java.awt.*;



public class Grid {
    private int[][] grid;
    private int width;
    private int height;
    private int boxSize;

    private final Color empty = new Color(0x000000);
    private final Color wall = new Color(0xFF0000);
    private final Color onConsideration = new Color(0x00FF00);
    private final Color considered = new Color(0xFF7800);
    private final Color finalPath = new Color(0x0000FF);
    private final Color start = new Color(0x7800FF);
    private final Color end = new Color(0xFFFF00);

    public Grid(int width, int height, int windowWidth, int windowHeight) {
        this.grid = new int[width][height];
        this.width = width;
        this.height = height;
        if (windowWidth != windowHeight) return;
        this.boxSize = 10;
    }

    public void fillGrid(float rate) {
        Random rand = new Random();
        rate = Math.max(Math.min(1 - rate, 1), 0);
        for (int r = 0; r < width; r++) {
            for (int c = 0; c < height; c++) {
                if (rand.nextFloat() > rate) {
                    grid[r][c] = 1;
                }
                else {
                    grid[r][c] = 0;
                }
            }
        }
    }

    public void draw(Graphics2D g2) {
        for (int r = 0; r < width; r++) {
            for (int c = 0; c < height; c++) {
                switch (grid[r][c]) {
                    case 0 -> g2.setPaint(empty);
                    case 1 -> g2.setPaint(wall);
                    case 2 -> g2.setPaint(onConsideration);
                    case 3 -> g2.setPaint(considered);
                    case 4 -> g2.setPaint(finalPath);
                    case 5 -> g2.setPaint(start);
                    case 6 -> g2.setPaint(end);
                }
                int x = c * boxSize;
                int y = r * boxSize;
                g2.fillRect(x, y, boxSize, boxSize);
            }
        }
    }

    public Node chooseStartAndEnd() {
        Random rand = new Random();
        int startX = rand.nextInt(width);
        int startY = rand.nextInt(height);
        int endX = rand.nextInt(width);
        int endY = rand.nextInt(height);
        while (grid[startX][startY] == 1) {
            startX = rand.nextInt(width);
            startY = rand.nextInt(height);
        }
        while (grid[endX][endY] == 1 || (endX == startX && endY == startY)) {
            endX = rand.nextInt(width);
            endY = rand.nextInt(height);
        }
        grid[startX][startY] = 5;
        grid[endX][endY] = 6;
        return new Node(startX, startY, new Node(endX, endY));
    }

    public void setAt(int x, int y, int newValue) {
        grid[x][y] = newValue;
    }

    public int getAt(int x, int y) {
        if (x < 0 || y < 0 || x >= height || y >= width) {
            return -1;
        }
        return grid[x][y];
    }
}
