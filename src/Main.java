import java.io.*;
import java.util.*;

public class Main {
    private static String inputFileName = "recruitment.txt";
    private static String outputFileName = "output.txt";
    private static boolean DEBUG = false;
    private static Node[][] nodes;
    private static double bestScore;
    private static ArrayList<Node> bestPath;

    public static void main(String[] args) throws IOException {
        if(args.length > 0) inputFileName = args[0];
        if(args.length > 1) outputFileName = args[1];
        if(args.length > 2) DEBUG = Boolean.parseBoolean(args[2]);

        ArrayList<ArrayList<Character>> map = readInput();
        createNodes(map);
        initializeGraph(map);

        // print the map
        if(DEBUG) {
            for (int i = 0; i < map.size(); i++) {
                for (int j = 0; j < map.get(0).size(); j++) {
                    if (nodes[i][j] == null) System.out.print("X");
                    else System.out.print("0");
                }
                System.out.println();
            }
        }

        bestScore = Integer.MAX_VALUE;
        bestPath = new ArrayList<>();
        bestPath.add(null);

        long startMillis = System.currentTimeMillis();
        dijkstra();
        System.out.println("Processing time: " + (System.currentTimeMillis() - startMillis) + "ms");

        generateOutput(map);
        writeOutput(map);

        // print the output
        if(DEBUG) {
            for(int i = 0; i < map.size(); i++) {
                for(int j = 0; j < map.get(0).size(); j++) {
                    System.out.print(map.get(i).get(j));
                }
                System.out.println();
            }
        }
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
            }
        }
    }

    // create the nodes from the map
    private static void createNodes(ArrayList<ArrayList<Character>> map) {
        nodes = new Node[map.size()][map.get(0).size()];
        for (int i = 0; i < map.size(); i++) {
            for (int j = 0; j < map.get(0).size(); j++) {
                if(map.get(i).get(j).charValue() == '0') nodes[i][j] = new Node(i, j);
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

        // run the dijkstra algorithm
        while(!queue.isEmpty()) {

            // get the next node from the queue and check if we already visited it
            Node current = queue.poll();
            if(visited.contains(current)) continue;
            visited.add(current);

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

        // find the best ending node
        for(int i = 0; i < nodes[nodes.length-1].length; i++) {
            if(nodes[nodes.length-1][i] == null) continue;
            if(nodes[nodes.length-1][i].cost < bestScore) {
                bestScore = nodes[nodes.length-1][i].cost;
                bestPath.set(0, nodes[nodes.length-1][i]);
            }
        }

        // reconstruct the path
        Node current = bestPath.get(0);
        while(current.parent != null) {
            bestPath.add(0, current.parent);
            current = current.parent;
        }
    }
}