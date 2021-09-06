import java.awt.event.*;
import java.util.*;
import java.awt.*;

import javax.swing.*;


public class Main extends JFrame implements KeyListener, MouseListener {
    static Grid grid;
    static Node start;
    static Node end;
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

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point mousePos = getMousePosition();
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

    public Main() {
        JPanel panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                System.out.println("I WAS HERE");
                Graphics2D g2 = (Graphics2D)g;
                grid.draw(g2);
                if (!done && running) {
                    tick();
                }
                c++;
                repaint();
            }
        };
        int width = 666;
        int height = 700;

        setVisible(true);
        setResizable(true);
        setFocusable(true);
        setBounds(0, 0, width, height + 22);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel.setVisible(true);
        panel.setSize(width, height);
        add(panel);


        grid = new Grid(60, 60, width, height);
        grid.fillGrid(.25f);
        Node temp = grid.chooseStartAndEnd();
        start = new Node(temp.x, temp.y);
        end = temp.getParent();
        // Now, start the algorithm by putting the start in the toBeConsidered list
        toBeConsidered.add(start);
        toBeConsideredCosts.add(start.calculateCost(end));
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
}
