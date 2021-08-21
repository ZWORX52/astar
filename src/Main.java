import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;


public class Main extends JPanel {
    static Grid grid;
    static Node start;
    static Node end;
    static JFrame window;
    static int c;
    static int tick;
    static int pathLength;
    static boolean done;
    static boolean running;
    static boolean mouseInWindow;

    static ArrayList<Integer> toBeConsideredCosts = new ArrayList<> ();
    static ArrayList<Node> toBeConsidered = new ArrayList<> ();
    static ArrayList<Node> considered = new ArrayList<> ();
    static ArrayList<Node> finalPath = new ArrayList<> ();

    public Main(int width, int height) {
        setSize(width, height);
        setupKeyListener();
        setupMouseListener();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        grid.draw(g2);
        if (!done && running) {
            tick();
        }
        c++;
        repaint();
    }

    public void setupKeyListener() {
        addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                char key = e.getKeyChar();
                if (key == 'r') {
                    resetGrid();
                }
                else if (key == 's') {
                    running = true;
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });
    }

    public void setupMouseListener() {
        addMouseListener(new MouseListener() {
            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                Point mousePos = window.getMousePosition();
                // IT IS NOT WRONG, I NEED IT LIKE THIS BECAUSE OF COORDS -> ARRAY LOCATIONS
                int gridX = mousePos.y / 10 - 3;
                int gridY = mousePos.x / 10 - 1;
                System.out.println("X and Y are: " + mousePos.x + " " + mousePos.y);
                if (SwingUtilities.isLeftMouseButton(e) && !running && mouseInWindow) {
                    if (grid.getAt(gridX, gridY) == 0) grid.setAt(gridX, gridY, 1);
                }
                else if (SwingUtilities.isRightMouseButton(e) && !running && mouseInWindow) {
                    if (grid.getAt(gridX, gridY) == 1) grid.setAt(gridX, gridY, 0);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                mouseInWindow = true;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseInWindow = false;
            }
        });
    }

    public static void resetGrid() {
        done = false;
        running = false;
        grid.fillGrid(.25f);
        Node temp = grid.chooseStartAndEnd();
        start = new Node(temp.x, temp.y);
        end = temp.getParent();
        toBeConsideredCosts = new ArrayList<> ();
        toBeConsidered = new ArrayList<> ();
        considered = new ArrayList<> ();
        finalPath = new ArrayList<> ();
        pathLength = 0;
        c = 0;
        toBeConsidered.add(start);
        toBeConsideredCosts.add(start.calculateCost(end));
    }

    private static void tick() {
        try {
            consider(toBeConsidered.remove(0));
        }
        catch (Exception e) {
            System.out.println("No path was found");
            done = true;
        }
    }

    private static void consider(Node toConsider) {
        considered.add(toConsider);
        grid.setAt(toConsider.x, toConsider.y, 3);
        if (toConsider.x == end.x && toConsider.y == end.y) {
            calculateFinalPath(toConsider);
            System.out.println(pathLength);
            done = true;
            return;
        }
        int[][] directions = {{0, 1}, {1, 0}, {-1, 0}, {0, -1}};
        for (int r = 0; r < 4; r++) {
            int newX = toConsider.x + directions[r][0];
            int newY = toConsider.y + directions[r][1];
            if (grid.getAt(newX, newY) == 0 || grid.getAt(newX, newY) == 6) {
                addToBeConsidered(new Node(newX, newY, toConsider));
            }
        }
    }

    private static void calculateFinalPath(Node gotToEnd) {
        Node nextToLookAt = gotToEnd;
        while (nextToLookAt != null) {
            finalPath.add(nextToLookAt);
            grid.setAt(nextToLookAt.x, nextToLookAt.y, 4);
            pathLength++;
            nextToLookAt = nextToLookAt.getParent();
        }
    }

    private static void addToBeConsidered(Node subject) {
        int cost = subject.calculateCost(end);
        int index = findOptimalIndex(cost);
        if (index >= toBeConsidered.size()) {
            toBeConsidered.add(subject);
        }
        else {
            toBeConsidered.add(index, subject);
        }
        toBeConsideredCosts.add(index, cost);
        grid.setAt(subject.x, subject.y, 2);
    }

    private static int findOptimalIndex(int newNumber) {
        int index = Arrays.binarySearch(toBeConsideredCosts.toArray(), newNumber);
        if (index >= 0) {
            return index;
        }
        return -(index + 1);
    }

    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int width = 666;
        int height = 700;
        window.setBounds(0, 0, width, height + 22); // (x, y, w, h) 22 due to title bar.

        JPanel screenPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        Main panel = new Main(width, height);
        panel.setFocusable(true);
        panel.setVisible(true);

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.ipadx = 616;
        c.ipady = 616;
        screenPanel.add(panel, c);

        JSlider slider = new JSlider(JSlider.HORIZONTAL, 1, 31, 1);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 1;
        c.ipadx = 100;
        c.ipady = 84;
        screenPanel.add(slider, c);

        window.add(screenPanel);
        window.setVisible(true);
        window.setResizable(true);
        Main.window = window;

        grid = new Grid(60, 60, width, height);
        grid.fillGrid(.25f);
        Node temp = grid.chooseStartAndEnd();
        start = new Node(temp.x, temp.y);
        end = temp.getParent();
        // Now, start the algorithm by putting the start in the toBeConsidered list
        toBeConsidered.add(start);
        toBeConsideredCosts.add(start.calculateCost(end));
    }
}
