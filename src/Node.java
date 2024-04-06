import java.util.ArrayList;

public class Node {
    public int x;
    public int y;
    public int lowerbound;
    public int upperbound;
    public ArrayList<Node> neighbors;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        lowerbound = 0;
        upperbound = Integer.MAX_VALUE/2;
        neighbors = new ArrayList<>();
    }

    public void addNeighbor(Node neighbor) {
        neighbors.add(neighbor);
    }
}
