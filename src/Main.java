import javax.swing.*;
import java.util.*;
import java.awt.*;



public class Main extends JPanel{
    static Grid grid;
    static Node start;
    static Node end;

    static ArrayList<Integer> toBeConsideredCosts;
    static ArrayList<Node> toBeConsidered;
    static ArrayList<Node> considered;
    static ArrayList<Node> finalPath;

    public Main(int width, int height) {
        setSize(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        grid.draw(g2);
        tick();
    }

    private void tick() {
        consider(toBeConsidered.get(0));
    }

    private void consider(Node toConsider) {
        considered.add(toConsider);
        grid.setAt(toConsider.x, toConsider.y, 3);

    }

    private void addToBeConsidered(Node subject) {
        int cost = subject.calculateCost(end);
        int index = findOptimalIndex(cost);
        toBeConsidered.add(index, subject);
        toBeConsideredCosts.add(index, cost);
    }

    private int findOptimalIndex(int newNumber) {
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
        int consideredCostsSize = toBeConsideredCosts.size();
        int index = 0;
        for (int i = 0; i < consideredCostsSize; i++) {
            if (toBeConsideredCosts.get(i) == newNumber) {
                index = i;
            }
            else if (toBeConsideredCosts.get(i) > newNumber) {
                index = i - 1;
            }
        }
        return index;
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
    }
}
