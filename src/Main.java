import java.io.*;
import java.util.*;

public class Main {
    public static final String inputFileName = "recruitment.txt";
    public static final String outputFileName = "output.txt";
    public static final boolean DEBUG = false;
    public static boolean[][] track;
    public static Node[][] nodes;
    public static ArrayList<Node> path;
    public static double score;
    public static double bestScore;
    public static ArrayList<Node> bestPath;
    public static void main(String[] args) throws IOException {
        long startMillis = System.currentTimeMillis();
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

        // run the branch and bound algorithm
        path = new ArrayList<>();
        score = 0;
        bestScore = Integer.MAX_VALUE;
        // loop over all possible starting positions
        for(int i = 0; i < nodes[0].length; i++) {
            if(nodes[0][i] == null) continue;
            path.add(nodes[0][i]);
            branchAndBound(nodes[0][i]);
            path.remove(0);
        }

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

        // print the processing time
        System.out.println("Processing time: " + (System.currentTimeMillis() - startMillis) + "ms");
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

    // initialize the graph with neighbors and bounds
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

                nodes[i][j].sortNeighbors();

                // initial lowerbound is the number of nodes from that node to the bottom row
                nodes[i][j].lowerbound = nodes.length - i - 1;
                nodes[i][j].upperbound = Integer.MAX_VALUE/2;
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

    // recursive branch and bound algorithm
    public static void branchAndBound(Node currentNode) {
        // check if destination is reached
        if(currentNode.x == nodes.length-1) {
            // check if the current path is better than the best path
            if(score < bestScore) {
                bestScore = score;
                bestPath = new ArrayList<>(path);
                System.out.println("New best path found with score: " + bestScore);
            }
            return;
        }

        // Recursively visit all neighbors
        for(Edge e : currentNode.neighbors) {
            // check if the neighbor isn't already on the path
            if(path.contains(e.neighbor)) continue;

            // check if this branch can be pruned
            if(score + e.weight.value + e.neighbor.lowerbound >= bestScore) continue;

            // add the neighbor to the path
            path.add(e.neighbor);
            score += e.weight.value;

            // update the lowerbound of the neighbor
//            e.neighbor.lowerbound = Math.max(e.neighbor.lowerbound, currentNode.lowerbound - 1); // TODO: check if this is correct
            // TODO UPDATE BOUNDS/SCORE
            // TODO REMOVE NODES THAT ALSO ARE NEIGHBORS OF THE PREVIOUS NODES (DIRECT PATH IS ALWAYS BETTER)


            // recursively visit the neighbor if the current score is lower then the previous best to reach this node
            if(score < e.neighbor.upperbound) {
                // Adjust the (local) upperbound (minimum score necessary to reach this node)
                e.neighbor.upperbound = score;
                branchAndBound(e.neighbor);
            }

            // remove the neighbor from the path
            path.remove(path.size()-1);
            score -= e.weight.value;
        }
    }
}