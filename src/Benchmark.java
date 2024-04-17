import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Benchmark {
    private static String inputFileName = "recruitment.txt";
    private static String outputFileName = "output.txt";
    private static boolean DEBUG = false;
    private static int iterations = 100;
    private static Node[][] nodes;
    private static ArrayList<Node> path;
    private static double score;
    private static double bestScore;
    private static ArrayList<Node> bestPath;
    private static long[] dijkstraTimes;
    private static long[] branchAndBoundTimes;
    private static long[] AStarTimes;

    public static void main(String[] args) throws IOException {


        if(args.length > 0) inputFileName = args[0];
        if(args.length > 1) outputFileName = args[1];
        if(args.length > 2) DEBUG = Boolean.parseBoolean(args[2]);
        if(args.length > 3) iterations = Integer.parseInt(args[3]);

        dijkstraTimes = new long[iterations];
        branchAndBoundTimes = new long[iterations];
        AStarTimes = new long[iterations];

        System.out.println("Dijkstra (ns); Branch and Bound (ns); A* (ns)");
        for(int i = 0; i < iterations; i++) {
            benchmarkDijkstra(i);
            clearResult();
            benchmarkBranchAndBound(i);
            clearResult();
            benchmarkAStar(i);
            clearResult();
            System.out.println(dijkstraTimes[i] + "; " + branchAndBoundTimes[i] + "; " + AStarTimes[i]);
        }


    }

    private static void clearResult() {
        nodes = null;
        path = null;
        score = 0;
        bestScore = Integer.MAX_VALUE;
        bestPath = null;
    }

    private static void benchmarkDijkstra(int iteration) throws IOException {


        ArrayList<ArrayList<Character>> map = readInput();
        createNodes(map);
        initializeGraph(map);

        path = new ArrayList<>();
        score = 0;
        bestScore = Integer.MAX_VALUE;
        long startMillis = System.nanoTime();
        dijkstra();
        dijkstraTimes[iteration] = System.nanoTime() - startMillis;
        generateOutput(map);
        writeOutput(map);


    }

    private static void benchmarkBranchAndBound(int iteration) throws IOException {


        ArrayList<ArrayList<Character>> map = readInput();
        createNodes(map);
        initializeGraph(map);

        path = new ArrayList<>();
        score = 0;
        bestScore = Integer.MAX_VALUE;
        long startMillis = System.nanoTime();
        for(int i = 0; i < nodes[0].length; i++) {
            if(nodes[0][i] == null) continue;
            path.add(nodes[0][i]);
            branchAndBound(nodes[0][i]);
            path.remove(0);
        }
        branchAndBoundTimes[iteration] = System.nanoTime() - startMillis;

        generateOutput(map);
        writeOutput(map);
    }

    private static void benchmarkAStar(int iteration) throws IOException {


        ArrayList<ArrayList<Character>> map = readInput();
        createNodes(map);
        initializeGraph(map);

        path = new ArrayList<>();
        score = 0;
        bestScore = Integer.MAX_VALUE;
        long startMillis = System.nanoTime();
        AStar();
        AStarTimes[iteration] = System.nanoTime() - startMillis;
        generateOutput(map);
        writeOutput(map);


    }

    // write the output to a file
    private static void writeOutput(ArrayList<ArrayList<Character>> map) throws IOException {
        FileWriter writer = new FileWriter(outputFileName);
        for(int i = 0; i < map.size(); i++) {
            for(int j = 0; j < map.get(0).size(); j++) {
                writer.write(map.get(i).get(j));
            }
            writer.write("\n");
        }
        writer.write("Total cost: " + bestScore + "\n");
        writer.close();
    }

    // generate the output path
    private static void generateOutput(ArrayList<ArrayList<Character>> map) {
        for(int i = 0; i < bestPath.size() - 1; i++) {
            Node current = bestPath.get(i);
            Node next = bestPath.get(i+1);

            if(current.x == next.x) {
                if(current.y < next.y) map.get(current.x).set(current.y, '>');
                else map.get(current.x).set(current.y, '<');
            } else if(current.y == next.y) {
                if(current.x < next.x) map.get(current.x).set(current.y, 'v');
                else map.get(current.x).set(current.y, '^');
            } else {
                if(current.x < next.x && current.y < next.y) map.get(current.x).set(current.y, '\\');
                else if(current.x < next.x && current.y > next.y) map.get(current.x).set(current.y, '/');
                else if(current.x > next.x && current.y < next.y) map.get(current.x).set(current.y, '/');
                else map.get(current.x).set(current.y, '\\');
            }
        }
        Node last = bestPath.get(bestPath.size()-1);
        map.get(last.x).set(last.y, 'v');
    }

    // initialize the graph with neighbors
    private static void initializeGraph(ArrayList<ArrayList<Character>> map) {
        for (int i = 0; i < map.size(); i++) {
            for (int j = 0; j < map.get(0).size(); j++) {
                if(nodes[i][j] == null) continue;

                if (i > 0 && nodes[i-1][j] != null) nodes[i][j].addNeighbor(nodes[i-1][j]);
                if (i < map.size()-1 && nodes[i+1][j] != null) nodes[i][j].addNeighbor(nodes[i+1][j]);
                if (j > 0 && nodes[i][j-1] != null) nodes[i][j].addNeighbor(nodes[i][j-1]);
                if (j < map.get(0).size()-1 && nodes[i][j+1] != null) nodes[i][j].addNeighbor(nodes[i][j+1]);
                if(i > 0 && j > 0 && nodes[i-1][j-1] != null) nodes[i][j].addNeighbor(nodes[i-1][j-1]);
                if(i > 0 && j < map.get(0).size()-1 && nodes[i-1][j+1] != null) nodes[i][j].addNeighbor(nodes[i-1][j+1]);
                if(i < map.size()-1 && j > 0 && nodes[i+1][j-1] != null) nodes[i][j].addNeighbor(nodes[i+1][j-1]);
                if(i < map.size()-1 && j < map.get(0).size()-1 && nodes[i+1][j+1] != null) nodes[i][j].addNeighbor(nodes[i+1][j+1]);

                // sort neighbors based on lowerbound (ascending) to optimize the amount of pruning that can be done
                nodes[i][j].sortNeighbors();
            }
        }
    }

    // create the nodes from the map
    private static void createNodes(ArrayList<ArrayList<Character>> map) {
        nodes = new Node[map.size()][map.get(0).size()];
        for (int i = 0; i < map.size(); i++) {
            for (int j = 0; j < map.get(0).size(); j++) {
                if(map.get(i).get(j).charValue() == '0') {
                    nodes[i][j] = new Node(i, j);

                    // initial lowerbound is the number of nodes from that node to the bottom row
                    nodes[i][j].lowerbound = nodes.length - i - 1;
                    nodes[i][j].heuristic = nodes.length - i - 1;
                    nodes[i][j].upperbound = Integer.MAX_VALUE/2;
                }
                else nodes[i][j] = null;
            }
        }
    }

    // read the input from the file
    private static ArrayList<ArrayList<Character>> readInput() throws FileNotFoundException {
        ArrayList<ArrayList<Character>> map = new ArrayList<>();
        File file = new File(inputFileName);
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            ArrayList<Character> row = new ArrayList<>();
            for (int i = 0; i < line.length(); i++) {
                row.add(line.charAt(i));
            }
            map.add(row);
        }
        return map;
    }

    // recursive branch and bound algorithm
    public static void branchAndBound(Node currentNode) {
        // check if destination is reached
        if(currentNode.x == nodes.length-1) {
            // check if the current path is better than the best path
            if(score < bestScore) {
                bestScore = score;
                bestPath = new ArrayList<>(path);
                if(DEBUG) System.out.println("New best path found with score: " + bestScore);
            }
            return;
        }

        // Recursively visit all neighbors
        for(Edge e : currentNode.neighbors) {
            // check if the neighbor isn't already on the path
            if(path.contains(e.neighbor)) continue;

            // check if this branch can be pruned (still possible to decrease overall cost and lowest cost yet to reach this neighbor)
            if(score + e.weight.value + e.neighbor.lowerbound >= bestScore) continue;
            if(score + e.weight.value >= e.neighbor.upperbound) continue;

            // add the neighbor to the path
            path.add(e.neighbor);
            score += e.weight.value;

            // update the lowerbound of the neighbor
//            e.neighbor.lowerbound = Math.max(e.neighbor.lowerbound, currentNode.lowerbound - 1); // TODO: check if this is correct
            // TODO UPDATE BOUNDS/SCORE
            // TODO REMOVE NODES THAT ALSO ARE NEIGHBORS OF THE PREVIOUS NODES (DIRECT PATH IS ALWAYS BETTER)


            // recursively visit the neighbor
            // Adjust the (local) upperbound (minimum score necessary to reach this node)
            e.neighbor.upperbound = score;
            branchAndBound(e.neighbor);

            // remove the neighbor from the path
            path.remove(path.size()-1);
            score -= e.weight.value;
        }
    }

    public static void dijkstra() {
        // create the queue
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(n -> n.cost));
        Set<Node> visited = new HashSet<>();

        // add all starting nodes
        for(int i = 0; i < nodes[0].length; i++) {
            if(nodes[0][i] != null) {
                nodes[0][i].cost = 0;
                queue.offer(nodes[0][i]);
            }
        }

        Node current = nodes[0][0];
        // run the dijkstra algorithm
        while(!queue.isEmpty()) {
            // get the next node from the queue
            current = queue.poll();

            // check if we already visited the current node
            if(visited.contains(current)) continue;
            visited.add(current);

            // check if we have reached the end (first time reaching this is guaranteed to be the best)
            // Dijkstra is a single source, all destinations algorithm
            if(current.x == nodes.length-1) {
                bestPath = new ArrayList<>();
                bestPath.add(current);
                bestScore = current.cost;
                break;
            }

            // loop over all neighbors
            for(Edge e : current.neighbors) {
                // check if we have already visited the neighbor
                if(visited.contains(e.neighbor)) continue;

                // calculate the new cost and update the parent if necessary
                double newCost = current.cost + e.weight.value;
                if(newCost < e.neighbor.cost) {
                    e.neighbor.parent = current;
                    e.neighbor.cost = newCost;
                    queue.offer(e.neighbor);
                }
            }
        }

        // reconstruct the path
        while(current.parent != null) {
            bestPath.add(0, current.parent);
            current = current.parent;
        }
    }

    // Implementation of the Dijkstra algorithm with A* heuristic
    public static void AStar() {
        // create the queue
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(n -> n.predicion));
        Set<Node> visited = new HashSet<>();

        // add all starting nodes
        for(int i = 0; i < nodes[0].length; i++) {
            if(nodes[0][i] != null) {
                nodes[0][i].cost = 0;
                nodes[0][i].predicion = nodes[0][i].cost + nodes[0][i].heuristic;
                queue.offer(nodes[0][i]);
            }
        }

        Node current = nodes[0][0];
        // run the dijkstra algorithm
        while(!queue.isEmpty()) {
            // get the next node from the queue
            current = queue.poll();

            // check if we already visited the current node
            if(visited.contains(current)) continue;
            visited.add(current);

            // check if we have reached the end (first time reaching this is guaranteed to be the best)
            // Dijkstra is a single source, all destinations algorithm
            if(current.x == nodes.length-1) {
                bestPath = new ArrayList<>();
                bestPath.add(current);
                bestScore = current.cost;
                break;
            }

            // loop over all neighbors
            for(Edge e : current.neighbors) {
                // check if we have already visited the neighbor
                if(visited.contains(e.neighbor)) continue;

                // calculate the new cost and update the parent if necessary
                double newCost = current.cost + e.weight.value;
                if(newCost < e.neighbor.cost) {
                    e.neighbor.parent = current;
                    e.neighbor.cost = newCost;
                    e.neighbor.predicion = e.neighbor.cost + e.neighbor.heuristic;
                    queue.offer(e.neighbor);
                }
            }
        }

        // reconstruct the path
        while(current.parent != null) {
            bestPath.add(0, current.parent);
            current = current.parent;
        }
    }
}
