public class Edge {
    public final Node neighbor;
    public final WEIGHT weight;

    public Edge(Node neighbor, WEIGHT weight) {
        this.neighbor = neighbor;
        this.weight = weight;
    }

}

enum WEIGHT {
    ONE(1),
    SQRT_TWO(Math.sqrt(2));

    public final double value;

    WEIGHT(double i) {
        value = i;
    }
}
