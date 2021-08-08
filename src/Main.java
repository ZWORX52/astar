import javax.swing.*;
import java.util.*;
import java.awt.*;



public class Main extends JPanel{
    static Grid grid;
    ArrayList<Node> toBeConsidered;
    ArrayList<Node> toBeConsideredCosts;
    ArrayList<Node> considered;
    ArrayList<Node> finalPath;

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
        Consider the first item in the SORTED toBeConsideredCosts
        */
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
        Node start = new Node(temp.x, temp.y);
        Node end = temp.getParent();
        // Now, start the algorithm by putting the start in the toBeConsidered list

    }
}
