import java.util.*;


// I need Node for two reasons: the backtracking and generation tracking (for the a* algorithm, a square's score is distance from end + generation)
public class Node {
    public int x;
    public int y;
    public int cost;
    private final int generation;
    private final Node parent;

    Node(int x, int y, Node parent) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.generation = parent.generation + 1;
    }

    public void calculate_cost(int endX, int endY) {
        // Cost is the generation + (x distance to end ^ 2 + y distance to end ^ 2)
        int xDistance = Math.abs(this.x - endX);
        int yDistance = Math.abs(this.y - endY);
        int distance = xDistance * xDistance + yDistance * yDistance;
        this.cost = distance + this.generation;
    }

    public Node getParent() {
        return this.parent;
    }
}
