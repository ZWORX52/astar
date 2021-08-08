// import java.util.*;


// I need Node for two reasons: the backtracking and generation tracking (for the a* algorithm, a square's score is distance from end + generation)
public class Node {
    public int x;
    public int y;
    private final int generation;
    private final Node parent;

    Node(int x, int y, Node parent) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.generation = parent.generation + 1;
    }

    Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.parent = null;
        this.generation = 0;
    }

    public int calculateCost(Node this, Node end) {
        // Cost is the generation + (x distance to end ^ 2 + y distance to end ^ 2)
        int xDistance = Math.abs(this.x - end.x);
        int yDistance = Math.abs(this.y - end.x);
        int distance = xDistance * xDistance + yDistance * yDistance;
        return distance + this.generation;
    }

    public Node getParent() {
        return this.parent;
    }
}
