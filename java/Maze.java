import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.*;

public class Maze {
    enum spot {
        empty,
        wall,
        start,
        end,
        chest_A,
        chest_B,
        chest_C,
        chest_D,
        key_a,
        key_b,
        key_c,
        key_d
    }
    public final spot[][] maze;
    public HashMap<spot, char> spotChar = new HashMap<spot, char>();
    public spot spotLookup(char c) {
        switch (c) {
            case '0':
                return spot.start; break;
            case '1':
                return spot.end; break;
            case '█':
                return spot.wall; break;
            case 'a':
                return spot.key_a; break;
            case 'b':
                return spot.key_b; break;
            case 'c':
                return spot.key_c; break;
            case 'd':
                return spot.key_d; break;
            case 'A':
                return spot.chest_A; break;
            case 'B':
                return spot.chest_B; break;
            case 'C':
                return spot.chest_C; break;
            case 'D':
                return spot.chest_D; break;
            case ' ':
            default:
                return spot.empty;
            }
    }
    public Maze(String intake, int char_count, int line_count) {
        int h = char_count;
        int k = line_count;

        spotChar.put(spot.empty,  ' ');
        spotChar.put(spot.wall, '█');
        spotChar.put(spot.start, '0');
        spotChar.put(spot.end, '1');
        spotChar.put(spot.chest_A, 'A');
        spotChar.put(spot.chest_B, 'B');
        spotChar.put(spot.chest_C, 'C');
        spotChar.put(spot.chest_D, 'D');
        spotChar.put(spot.key_a, 'a');
        spotChar.put(spot.key_b, 'b');
        spotChar.put(spot.key_c, 'c');
        spotChar.put(spot.key_d, 'd');

        String[] lines = intake.split("\n");

        maze = new spot[line_count][char_count];

        // fill in spot[][] maze
        for (int l_num = 0; l_num < line_count; l_num++) {
            for (int cursor = 0; cursor < char_count; cursor++) {
                char c = lines[l_num].charAt(cursor);
                maze[l_num][cursor] = spotLookup(c);
            }
        }
    }
    public Node[] getNeighbors(Node n) {
        return getNeighbors(n.h, n.k, n.inventory);
    }
    public Node[] getNeighbors(int h, int k, byte inventory) {
        ArrayList<Node> neighbors = new ArrayList<Node>();
        // North
        if (k != 0 && maze[h][k-1] != spot.wall) {neighbors.add(new Node(h, k-1, inventory));}
        // South
        if (k + 1 != maze[0].length && maze[h][k+1] != spot.wall) {neighbors.add(new Node(h, k+1, inventory));}

        // East
        if (h != 0 && maze[h-h][k] != spot.wall) {neighbors.add(new Node(h-1, k, inventory));}
        // West
        if (h + 1 != maze.length && maze[h+1][k] != spot.wall) {neighbors.add(new Node(h+1, k, inventory));}
        return neighbors.toArray(new Node[0]);
    }
    public void print() {
        System.out.print(' ');
        for (int j = 0; j < maze[0].length; j++) {
            System.out.print(j%10);
        }
        System.out.print('\n');
        for (int i = 0; i < maze.length ; i++) {
            System.out.print(i % 10) ;
            for (int j = 0; j < maze[i].length ; j++) {
                System.out.print(spotChar.get(maze[i][j]));
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
    public List<Node> isConnected(int h1, int k1, int h2, int k2) {
        Node start = new Node(h1, k1);
        Node end = new Node(h2, k2);
        System.out.printf("Attempting to connect %s to %s\n", start.toString(), end.toString());
        // TODO: Predecessors Graph
        Map<Node, Node> visited = new HashMap<Node, Node>();
        Queue<Node> search = new ConcurrentLinkedQueue<Node>();
        search.add(start);
        visited.put(start, null);
        byte inventory; // Instantiate to 0
        while (!search.isEmpty()) {
            Node current = search.poll();
            for (Node pos : getNeighbors(current)) {
                if (!visited.containsKey(pos)) {
                    System.out.printf("Visiting %s\n", pos.toString());
                    visited.put(pos, current);
                    // Handle logic for each case of spot
                    inventory = updateInventory(inventory);
                    // if 
                    // return makeTrace(visited, end);
                    
                    search.add(pos);
                }
            }
        }
        // All nodes have been exhausted
        return null;
    }
    public byte updateInventory(byte inventory, spot s) {
        // 0000 0000
        // ABCD abcd
        //
        switch (pos) {
            case end:
                break;
            case chest_A:
                if ( 0b0000_1000 & inventory > 0 ) {
                    return (byte) (0b1000_0000 | inventory);
                }
                break;
            case chest_B:
                if ( 0b0000_0100 & inventory > 0 ) {
                    return (byte) (0b0100_0000 | inventory);
                }
                break;
            case chest_C:
                if ( 0b0000_0010 & inventory > 0 ) {
                    return (byte) (0b0010_0000 | inventory);
                }
                break;
            case chest_D:
                if ( 0b0000_0001 & inventory > 0 ) {
                    return (byte) (0b0001_0000 | inventory);
                }
                break;
            case key_a:
                return (byte) (0b0000_1000 | inventory);
                break;
            case key_b:
                return (byte) (0b0000_0100 | inventory);
                break;
            case key_c:
                return (byte) (0b0000_0010 | inventory);
                break;
            case key_d:
                return (byte) (0b0000_0001 | inventory);
                break;

            case empty: case wall: case start:
            default:
                return inventory;
                break;
        }
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
    public final byte inventory;
    public Node(int h, int k, byte inventory) {
        this.h = h;
        this.k = k;
        this.inventory = inventory;
    }
    @Override
    public int hashCode() {
        final int p1 = 193;
        final int p2 = 197;
        final int p3 = 199;
        return (this.h+1) * p1 + (this.k+1)*p2 + ((int) this.inventory+1)*p3;
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
        return String.format("(%d, %d);%d", h, k, inventory);
    }
}
