import java.util.ArrayList;
import java.util.Comparator;

public class Node {
    public int x;
    public int y;
    public double lowerbound;
    public double upperbound;
    public ArrayList<Edge> neighbors;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        lowerbound = 0;
        upperbound = Integer.MAX_VALUE/2;
        neighbors = new ArrayList<>();
    }

    public void addNeighbor(Node neighbor) {
        if(x != neighbor.x && y != neighbor.y)
            neighbors.add(new Edge(neighbor, WEIGHT.SQRT_TWO));
        else
            neighbors.add(new Edge(neighbor, WEIGHT.ONE));
    }

    public void sortNeighbors() {
        neighbors.sort(Comparator.comparingDouble(e -> e.neighbor.lowerbound));
    }
}
