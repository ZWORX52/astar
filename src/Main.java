import javax.swing.*;
import java.util.*;
import java.awt.*;



public class Main extends JPanel{
    static Grid grid;
    static Node start;
    static Node end;

    static ArrayList<Node> toBeConsidered;
    static ArrayList<Node> toBeConsideredCosts;
    static ArrayList<Node> considered;
    static ArrayList<Node> finalPath;
    static ArrayList<Integer> consideredCosts;

    public Main(int width, int height) {
        setSize(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        grid.draw(g2);
        /*
        There are a few steps to each tick:
        Consider the first item in the SORTED toBeConsidered
        Add it to considered if it's not the end
        I'll finish this later xD
        */
    }

    private void consider(int toBeConsideredIndex) {
        Node toConsider = toBeConsidered.get(toBeConsideredIndex);
        considered.add(toConsider);
        grid.setAt(toConsider.x, toConsider.y, 3);

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
        int consideredCostsSize = consideredCosts.size();
        int index = 0;
        for (int i = 0; i < consideredCostsSize; i++) {
            if (consideredCosts.get(i) == newNumber) {
                index = i;
            }
            else if (consideredCosts.get(i) > newNumber) {
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
