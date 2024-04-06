import java.io.*;
import java.util.*;

public class Main {
    public static boolean[][] track;
    public static Node[][] nodes;
    public static ArrayList<Node> path;
    public static int score;
    public static int bestScore;
    public static ArrayList<Node> bestPath;
    public static void main(String[] args) throws IOException {
        String inputFileName = "recruitment.txt";

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
            }
        }

        // add neighbors and set initial bounds
        for (int i = 0; i < map.size(); i++) {
            for (int j = 0; j < map.get(0).size(); j++) {
                if (i > 0 && nodes[i-1][j] != null) nodes[i][j].addNeighbor(nodes[i-1][j]);
                if (i < map.size()-1 && nodes[i+1][j] != null) nodes[i][j].addNeighbor(nodes[i+1][j]);
                if (j > 0 && nodes[i][j-1] != null) nodes[i][j].addNeighbor(nodes[i][j-1]);
                if (j < map.get(0).size()-1 && nodes[i][j+1] != null) nodes[i][j].addNeighbor(nodes[i][j+1]);

                // initial lowerbound is the number of nodes from that node to the bottom row
                nodes[i][j].lowerbound = nodes.length - i - 1;
                nodes[i][j].upperbound = Integer.MAX_VALUE/2;
            }
        }
    }
}