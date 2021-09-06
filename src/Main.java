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
    static int rate = 1;
    static int pathLength;
    static float fillRate = .25f;
    static boolean done;
    static boolean running;

    static Algorithm algorithm = Algorithm.ASTAR;

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
        Graphics2D g2 = (Graphics2D) g;
        grid.draw(g2);
        if (!done && running && c % rate == 0) tick();
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
                switch (key) {
                    case 'r':
                        resetGrid();
                        break;
                    case 's':
                        running = true;
                        break;
                    case 'a':
                        grid.clear();
                        break;
                    case 'n':
                        try {
                            Point mousePos = window.getMousePosition();
                            int gridR = mousePos.y / 10 - 3;
                            int gridC = mousePos.x / 10 - 1;
                            grid.setNewStart(gridR, gridC);
                            start = new Node(gridR, gridC);
                            toBeConsidered.set(0, start);
                            toBeConsideredCosts.set(0, start.calculateCost(end, algorithm));
                        } catch (NullPointerException ignored) {
                        }
                        break;
                    case 'm':
                        try {
                            Point mousePos = window.getMousePosition();
                            int gridR = mousePos.y / 10 - 3;
                            int gridC = mousePos.x / 10 - 1;
                            grid.setNewEnd(gridR, gridC);
                            end = new Node(gridR, gridC);
                            toBeConsidered.set(0, start);
                            toBeConsideredCosts.set(0, start.calculateCost(end, algorithm));
                        } catch (NullPointerException ignored) {
                        }
                        break;
                    case 'q':
                        try {
                            if (!running) {
                                rate = Integer.parseInt(JOptionPane.showInputDialog(window,
                                        "Enter a new algorithm speed", "Algorithm rate",
                                        JOptionPane.PLAIN_MESSAGE));
                                if (rate < 1) rate = 1;
                            }
                        }
                        catch (NumberFormatException ignored) {
                        }
                        break;
                    case 'w':
                        try {
                            if (!running) {
                                float newFillRate = Float.parseFloat(JOptionPane.showInputDialog(window,
                                        "Enter a new fill rate for the grid", "Fill rate",
                                        JOptionPane.PLAIN_MESSAGE));
                                // Clamp it from 0 to 1. ;)
                                fillRate = newFillRate % 1;
                            }
                        }
                        catch (NumberFormatException ignored) {
                        }
                        break;
                    case 'e':
                        if (!running) {
                            Object[] options = {"ASTAR", "BRUTEFORCE", "STRAIGHTLINE"};
                            int result = JOptionPane.showOptionDialog(window, "Pick an algorithm...",
                                    "Pick Algorithm", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                                    null, options, options[0]);
                            switch (result) {
                                case 0 -> algorithm = Algorithm.ASTAR;
                                case 1 -> algorithm = Algorithm.BRUTEFORCE;
                                case 2 -> algorithm = Algorithm.STRAIGHTLINE;
                            }
                        }
                        break;
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });
    }

    public void setupMouseListener() {
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                try {
                    Point mousePos = window.getMousePosition();
                    // IT IS NOT WRONG, I NEED IT LIKE THIS BECAUSE OF COORDS -> ARRAY LOCATIONS
                    int gridX = mousePos.y / 10 - 3;
                    int gridY = mousePos.x / 10 - 1;
                    if (SwingUtilities.isLeftMouseButton(e) && !running) {
                        if (grid.getAt(gridX, gridY) == 0) grid.setAt(gridX, gridY, 1);
                    } else if (SwingUtilities.isRightMouseButton(e) && !running) {
                        if (grid.getAt(gridX, gridY) == 1) grid.setAt(gridX, gridY, 0);
                    }
                }
                catch (NullPointerException ignored) {
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });
    }

    public static void resetGrid() {
        done = false;
        running = false;
        grid.fillGrid(fillRate);
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
        toBeConsideredCosts.add(start.calculateCost(end, algorithm));
    }

    private static void tick() {
        try {
            consider(toBeConsidered.remove(0));
        }
        catch (IndexOutOfBoundsException e) {
            done = true;
            running = false;
        }
    }

    private static void consider(Node toConsider) {
        considered.add(toConsider);
        // So that it only sets color if we're NOT considering the start.
        if (toBeConsideredCosts.size() > 1) grid.setAt(toConsider.x, toConsider.y, 3);
        if (toConsider.x == end.x && toConsider.y == end.y) {
            calculateFinalPath(toConsider);
            done = true;
            running = false;
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
        int cost = subject.calculateCost(end, algorithm);
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
        int width = 616;
        int height = 616;
        window.setBounds(0, 0, width, height + 22); // (x, y, w, h) 22 due to title bar.

        JOptionPane.showMessageDialog(window,
                """
                        Welcome to Pathfinding! Controls are:
                            Q: Change algorithm speed; Default: 1
                            W: Change fill rate; Default: .25 (try .375!)
                            E: Change algorithm
                            R: Reset grid and pick new start and end positions
                            A: Clear grid (except for start and end)
                            S: Start algorithm
                            N and M: Move start and end (respectively) to your mouse position
                            Left/Right mouse button: Make square the mouse is over wall or empty (draggable!)
                        """,
                "Instructions",
                JOptionPane.INFORMATION_MESSAGE
        );

        Main panel = new Main(width, height);
        panel.setFocusable(true);
        panel.grabFocus();
        window.add(panel);
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
