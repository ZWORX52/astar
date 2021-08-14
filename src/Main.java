// import java.awt.event.*;
import java.util.Timer;
import javax.swing.*;
import java.util.*;
import java.awt.*;


public class Main extends JPanel {
    static Grid grid;
    static Node start;
    static Node end;
    static int c;
    static boolean done;

    static ArrayList<Integer> toBeConsideredCosts = new ArrayList<>();
    static ArrayList<Node> toBeConsidered = new ArrayList<>();
    static ArrayList<Node> considered = new ArrayList<> ();
    static ArrayList<Node> finalPath = new ArrayList<> ();

    public Main(int width, int height) {
        setSize(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        grid.draw(g2);
        if (!done && c % 4 == 0) {
            tick();
        }
        c++;
        repaint();
    }

    private static void tick() {
        consider(toBeConsidered.remove(0));
    }

    private static void consider(Node toConsider) {
        considered.add(toConsider);
        grid.setAt(toConsider.x, toConsider.y, 3);
        if (toConsider.x == end.x && toConsider.y == end.y) {
            calculateFinalPath(toConsider);
            done = true;
            return;
        }
        int[][] directions = {{0, 1}, {1, 0}, {-1, 0}, {0, -1}};
        for (int r = 0; r < 4; r++) {
            int newX = toConsider.x + directions[r][0];
            int newY = toConsider.y + directions[r][1];
            if (grid.getAt(newX, newY) == 0) {
                addToBeConsidered(new Node(newX, newY, toConsider));
            }
        }
    }

    private static void calculateFinalPath(Node gotToEnd) {
        Node nextToLookAt = gotToEnd;
        while (nextToLookAt != null) {
            finalPath.add(nextToLookAt);
            grid.setAt(nextToLookAt.x, nextToLookAt.y, 4);
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
        // Just binary search.
        // int consideredCostsSize = consideredCosts.size();
        // int currentLookingAtIndex = consideredCostsSize / 2;
        // int stepSize = consideredCostsSize / 2;
        // int previousLookingAtIndex;
        // while (true) {
        //     if (consideredCosts.get(currentLookingAtIndex) == newNumber) {
        //         break;
        //     }
        //     else if (consideredCosts.get(currentLookingAtIndex) < newNumber) {
        //         previousLookingAtIndex = currentLookingAtIndex;
        //         currentLookingAtIndex /= 2;
        //     }
        //     else {
        //         previousLookingAtIndex = currentLookingAtIndex;
        //         currentLookingAtIndex = currentLookingAtIndex / 2 + currentLookingAtIndex;
        //     }
        // }
        // return currentLookingAtIndex;
        // Never mind, normal search
        // int consideredCostsSize = toBeConsideredCosts.size();
        // int index = 0;
        // boolean indexWasSet = false;
        // for (int i = 0; i < consideredCostsSize; i++) {
        //     if (toBeConsideredCosts.get(i) == newNumber) {
        //         index = i;
        //         indexWasSet = true;
        //     }
        //     else if (toBeConsideredCosts.get(i) > newNumber) {
        //         index = (i == 0) ? i : i - 1;
        //         indexWasSet = true;
        //     }
        // }
        // return !indexWasSet ? consideredCostsSize - 1 : index;
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
        Main panel = new Main(width, height);
        panel.setFocusable(true);
        panel.grabFocus();
        window.add(panel);
        window.setVisible(true);
        window.setResizable(true);
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
