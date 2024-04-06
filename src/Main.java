import java.io.*;
import java.util.*;

public class Main {
    public static boolean[][] track;
    public static Node[][] nodes;
    public static ArrayList<Node> path;
    public static double score;
    public static double bestScore;
    public static ArrayList<Node> bestPath;
    public static void main(String[] args) throws IOException {
        String inputFileName = "small.txt";

        // read the input file
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

        // create nodes
        nodes = new Node[map.size()][map.get(0).size()];
        for (int i = 0; i < map.size(); i++) {
            for (int j = 0; j < map.get(0).size(); j++) {
                if(map.get(i).get(j).charValue() == '0') nodes[i][j] = new Node(i, j);
                else nodes[i][j] = null;
            }
        }

        // add neighbors and set initial bounds
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

        // print the map
        for(int i = 0; i < map.size(); i++) {
            for(int j = 0; j < map.get(0).size(); j++) {
                if(nodes[i][j] == null) System.out.print("X");
                else System.out.print("0");
            }
            System.out.println();
        }

        // run the branch and bound algorithm
        path = new ArrayList<>();
        score = 0;
        bestScore = Integer.MAX_VALUE;
        for(int i = 0; i < nodes[0].length; i++) {
            if(nodes[0][i] == null) continue;
            path.add(nodes[0][i]);
            branchAndBound(nodes[0][i]);
            path.remove(0);
        }

        // print the best path
        for(int i = 0; i < map.size(); i++) {
            for(int j = 0; j < map.get(0).size(); j++) {
                if(nodes[i][j] == null) System.out.print("X");
                else {
                    if(bestPath.contains(nodes[i][j])) System.out.print("1");
                    else System.out.print("0");
                }
            }
            System.out.println();
        }

    }

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

            // recursively visit the neighbor
            branchAndBound(e.neighbor);

            // remove the neighbor from the path
            path.remove(path.size()-1);
            score -= e.weight.value;
        }
    }
}