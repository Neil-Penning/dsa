import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.*;

public class Maze {
    public final boolean[][] maze;
    public Maze(String intake, int char_count, int line_count) {
        int h = char_count;
        int k = line_count;

        String[] lines = intake.split("\n");

        maze = new boolean[line_count][char_count];

        // fill in boolean[][] maze
        for (int l_num = 0; l_num < line_count; l_num++) {
            for (int cursor = 0; cursor < char_count; cursor++) {
                char c = lines[l_num].charAt(cursor);
                maze[l_num][cursor] = (c == ' ');
            }
        }
        // use boolean[][] maze to fill in Node[][] graph
        for (int l_num = 0; l_num < line_count; l_num++) {
            for (int cursor = 0; cursor < char_count; cursor++) {
                char c = lines[l_num].charAt(cursor);
                maze[l_num][cursor] = (c == ' ');
            }
        }
    }
    public Node[] getNeighbors(Node n) {
        return getNeighbors(n.h, n.k);
    }
    public Node[] getNeighbors(int h, int k) {
        ArrayList<Node> neighbors = new ArrayList<Node>();
        // North
        if (k != 0 && maze[h][k-1] == true) {neighbors.add(new Node(h, k-1));}
        // South
        if (k + 1 != maze[0].length && maze[h][k+1] == true) {neighbors.add(new Node(h, k+1));}

        // East
        if (h != 0 && maze[h-h][k] == true) {neighbors.add(new Node(h-1, k));}
        // West
        if (h + 1 != maze.length && maze[h+1][k] == true) {neighbors.add(new Node(h+1, k));}
        return neighbors.toArray(new Node[0]);
    }
    public void print() {
        System.out.print(' ');
        for (int j = 0; j < maze[0].length ; j++) {
            System.out.print(j%10);
        }
        System.out.print('\n');
        for (int i = 0; i < maze.length ; i++) {
            System.out.print(i % 10) ;
            for (int j = 0; j < maze[i].length ; j++) {
                System.out.print(maze[i][j] ? ' ' : '█');
            }
            System.out.print(i % 10) ;
            System.out.print("\n");
        }
        System.out.print(' ');
        for (int j = 0; j < maze[0].length ; j++) {
            System.out.print(j%10);
        }
        System.out.println();
    }
    // 1. make char[][] charArr
    // 2. iterate through charArr to made Node array

    public List<Node> isConnected(int h1, int k1, int h2, int k2) {
        Node start = new Node(h1, k1);
        Node end = new Node(h2, k2);
        System.out.printf("Attempting to connect %s to %s\n", start.toString(), end.toString());
        // TODO: Predecessors Graph
        Map<Node, Node> visited = new HashMap<Node, Node>();
        Queue<Node> search = new ConcurrentLinkedQueue<Node>();
        search.add(start);
        visited.put(start, null);
        while (!search.isEmpty()) {
            Node current = search.poll();
            for (Node pos : getNeighbors(current)) {
                if (!visited.containsKey(pos)) {
                    System.out.printf("Visiting %s\n", pos.toString());
                    visited.put(pos, current);
                    if (pos.equals(end)) {
                        return makeTrace(visited, end);
                    }
                    search.add(pos);
                }
            }
        }
        // All nodes have been exhausted
        return null;
    }
    public List<Node> makeTrace(Map<Node, Node> visited, Node end) {
        ArrayList<Node> trace = new ArrayList<Node>();
        Node current = end;
        while (current != null) {
            trace.add(current);
            System.out.printf("traced %s\n", current.toString());
            current = visited.get(current);
            //System.out.printf("currently at %s\n", current.toString());
        }
        return trace;
    }
    public static void main(String[] args) {
        Maze m_minimal = new Maze(
            "███████\n"+
            "       \n"+
            "███████\n"+
            "       ", 7, 4);
        m_minimal.print();
        System.out.println(m_minimal.isConnected(1, 0, 1, 4));
        System.out.println();
        Maze m_small = new Maze(
            "      █\n"+
            " ████ █\n"+
            " ███  █\n"+
            " ██████\n"+
            "       ", 7, 5);
        Maze m_medium = new Maze(
            " ██  ███████████\n"+
            " ███       █████\n"+
            " █████████ █████\n"+
            " █████████  ████\n"+
            " ██████████  ███\n"+
            "          ██    \n"+
            "█████████  ████ \n",
            15, 7);
        Maze m_large = new Maze( new String(
            "████████████████\n"+
            "████████████████\n"+
            "████████████████\n"+
            "████████████████\n"+
            "████████████████\n"+
            "████████████████\n"+
            "████████████████\n"),
            7, 5);
        m_small.print();
        System.out.println(null != m_small.isConnected(2, 4, 4, 6));
        System.out.println();
        System.out.println();
        //m_medium.print();
        System.out.println();
        System.out.println();
        //m_large.print();
    }
}


class Node { 
    public final int h;
    public final int k;
    public Node(int h, int k) {
        this.h = h;
        this.k = k;
    }
    @Override
    public int hashCode() {
        final int p1 = 71;
        final int p2 = 73;
        return (this.h+1) * p1 + (this.k+1)*p2;
    }
    @Override
    public boolean equals(Object o) {
        try {
            return ((Node) o).h == this.h && ((Node) o).k == this.k;
        } catch (Exception e) { 
            return false;
        }
    }
    public String toString() {
        return String.format("(%d, %d)", h, k);
    }
}
