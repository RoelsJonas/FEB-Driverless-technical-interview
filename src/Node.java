import java.util.ArrayList;
import java.util.Comparator;

public class Node {
    public final int x;
    public final int y;
    public double cost;
    public double heuristic;
    public double predicion;
    public final ArrayList<Edge> neighbors;
    public Node parent;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        cost = Double.POSITIVE_INFINITY;
        heuristic = 0;
        predicion = Double.POSITIVE_INFINITY;
        neighbors = new ArrayList<>();
        parent = null;
    }

    public void addNeighbor(Node neighbor) {
        if(x != neighbor.x && y != neighbor.y)
            neighbors.add(new Edge(neighbor, WEIGHT.SQRT_TWO));
        else
            neighbors.add(new Edge(neighbor, WEIGHT.ONE));
    }
}
