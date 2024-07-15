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
    public Node start; // Removed `final` 
    public Node end; // Removed `final` 
    public HashMap<spot, Character> spotChar = new HashMap<spot, Character>();
    public spot spotLookup(char c) {
        switch (c) {
            case '0':
                return spot.start; 
            case '1':
                return spot.end;
            case '█':
                return spot.wall;
            case 'a':
                return spot.key_a;
            case 'b':
                return spot.key_b;
            case 'c':
                return spot.key_c;
            case 'd':
                return spot.key_d;
            case 'A':
                return spot.chest_A;
            case 'B':
                return spot.chest_B;
            case 'C':
                return spot.chest_C;
            case 'D':
                return spot.chest_D;
            case ' ':
            default:
                return spot.empty;
            }
    }
    public Maze(String[] lines) {
        int char_count = lines[0].length();
        int line_count = lines.length;

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

        maze = new spot[line_count][char_count];

        // fill in spot[][] maze
        for (int l_num = 0; l_num < line_count; l_num++) {
            for (int cursor = 0; cursor < char_count; cursor++) {
                char c = lines[l_num].charAt(cursor);
                maze[l_num][cursor] = spotLookup(c);
                if (maze[l_num][cursor] == spot.start) {
                    // May run into a problem where multiple starts are defined
                    this.start = new Node(l_num, cursor, (byte) 0);
                } else if (maze[l_num][cursor] == spot.end) {
                    // May run into a problem where multiple ends are defined
                    this.end = new Node(l_num, cursor, (byte) 0xFFFFFF);
                }
            }
        }
    }
    public Node[] getNeighbors(Node n) {
        return getNeighbors(n.h, n.k, n.inventory);
    }
    public Node[] getNeighbors(int h, int k, byte inventory) {
        ArrayList<Node> neighbors = new ArrayList<Node>();
        // North
        if (k != 0 && maze[h][k-1] != spot.wall) {
            neighbors.add(new Node(h, k-1, updateInventory(inventory, maze[h][k-1])));
        }
        // South
        if (k + 1 != maze[0].length && maze[h][k+1] != spot.wall) {
            neighbors.add(new Node(h, k+1, updateInventory(inventory, maze[h][k+1])));
        }

        // East
        if (h != 0 && maze[h-1][k] != spot.wall) {
            neighbors.add(new Node(h-1, k, updateInventory(inventory, maze[h-1][k])));
        }
        // West
        if (h + 1 != maze.length && maze[h+1][k] != spot.wall) {
            neighbors.add(new Node(h+1, k, updateInventory(inventory, maze[h+1][k])));
        }
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
    public List<Node> isConnected() {
        return isConnected(start.h, start.k, end.h, end.k);
    }
    private List<Node> isConnected(int h1, int k1, int h2, int k2) {
        Node start = new Node(h1, k1, (byte) 0b0000_0000);
        Node end = new Node(h2, k2, (byte) 0b1111_1111);
        System.out.printf("Attempting to connect %s to %s\n", start.toString(), end.toString());
        // TODO: Predecessors Graph
        // Map<Long, Long>
        Map<Long, Long> visited = new HashMap<Long, Long>();
        Queue<Node> search = new ConcurrentLinkedQueue<Node>();
        search.add(start);
        visited.put(start.longHashCode(), null);
        while (!search.isEmpty()) {
            Node current = search.poll();
            System.out.printf("Current: %s\n", current.toString());
            if (current.equals(end)) {
                return makeTrace(visited, end);
            }
            for (Node pos : getNeighbors(current)) {
                if (!visited.containsKey(pos.longHashCode())) {
                    System.out.printf("Visiting %s\n", pos.toString());
                    visited.put(pos.longHashCode(), current.longHashCode());
                    // Handle logic for each case of spot
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
        switch (s) {
            case end:
                break;
            case chest_A:
                if ( (0b0000_1000 & inventory) > 0 ) {
                    return (byte) (0b1000_0000 | inventory);
                }
                break;
            case chest_B:
                if ( (0b0000_0100 & inventory) > 0 ) {
                    return (byte) (0b0100_0000 | inventory);
                }
                break;
            case chest_C:
                if ( (0b0000_0010 & inventory) > 0 ) {
                    return (byte) (0b0010_0000 | inventory);
                }
                break;
            case chest_D:
                if ( (0b0000_0001 & inventory) > 0 ) {
                    return (byte) (0b0001_0000 | inventory);
                }
                break;
            case key_a:
                return (byte) (0b0000_1000 | inventory);
            case key_b:
                return (byte) (0b0000_0100 | inventory);
            case key_c:
                return (byte) (0b0000_0010 | inventory);
            case key_d:
                return (byte) (0b0000_0001 | inventory);

            case empty: case wall: case start:
            default:
                return inventory;
        }
        return inventory;
    }
    public List<Node> makeTrace(Map<Long, Long> visited, Node end) {
        System.out.println("Making Trace");
        ArrayList<Long> fingerprintTrace = new ArrayList<Long>();
        Long currentFingerprint = end.longHashCode();

        while (currentFingerprint != null) {
            System.out.printf("traced %s\n", currentFingerprint.toString());
            fingerprintTrace.add(currentFingerprint);
            currentFingerprint = visited.get(currentFingerprint);
        }
        List<Node> trace = new ArrayList<Node>();
        Node currentNode = this.start;
        trace.add(this.start);
        for (Long fingerprint : fingerprintTrace.reversed()) {
            for (Node neighbor : getNeighbors(currentNode)) {
                if (neighbor.longHashCode() == fingerprint) {
                    trace.add(neighbor);
                    currentNode = neighbor;
                    break;
                }
            }
        }
        return trace;
    }
    public static void main(String[] args) {
        Maze m_medium = new Maze( new String[] {
            "0█A  ███████████",
            " ███   b   █████",
            " ██████ ██ █████",
            " ██a    ██c ████",
            " ████ █████  ███",
            "D         ██C d ",
            "█████████B ███1 "});
        Maze m_large = new Maze( new String[] {
"0 ███████████████████████████████████████", 
"  █   █  D      █ █ █ █       █ █     █ █", 
"█ ███ █████████ █ █ █ █ █ █████ █████ █ █", 
"█ █       █   █   █     █               █", 
"█ █ ███ ███ ███ █ ███ █████████████████ █", 
"█   █ █       █ █         █   █ █   █ █ █", 
"███ █ █ ███ █████ ███ ███ █ ███ █ █ █ █ █", 
"█ █ █     █     █   █ █       █   █   █A█", 
"█ ███ ███ ███ █ ███████ █ █ █████ █ ███ █", 
"█     █ █   █ █   █ █ █ █ █ █     █     █", 
"█████ █ █ ███████ █ █ █ █ ███ ███ ███ █ █", 
"█   █ █     █   c   █ █ █ █     █   █ █ █", 
"█ █████ █ ███ █ █ ███ ███████ ███ █████ █", 
"█       █ █ █ █ █   █         █       █ █", 
"███████ ███ █ █████ ███ █████ ███████ ███", 
"█   █ █ █         █B█       █ █   █     █", 
"███ █ ███ ███████ ███████ ███ █ ███ █████", 
"█   █   █ █ █     █ █       █ █         █", 
"███ ███ █ █ █ ███ █ █ █████ ███ ███ ███ █", 
"█ █ █     █ █ █         █   █ █   █ █ █ █", 
"█ █ ███ ███ ███████ █ ███ ███ ███ █ █ █ █", 
"█   █ █ █ █     █ █ █   █ █   █   █ █   █", 
"█ █ █ ███ ███ ███ ███ ███ ███ ███████ ███", 
"█ █   █ █     █       █     █           █", 
"███ ███ █ █ █████ ███████████ █████ ███ █", 
"█   █   █ █ █ █         █ █   █   █ █   █", 
"███ █ █ █ █ █ █ ███ ███ █ █████ ███████ █", 
"█ █ █ █   █ █     █ █     b █     █ █ █ █", 
"█ █ █ ███████ █ ███████ █ █ █████ █ █ ███", 
"█     █ █     █   █     █ █           █a█", 
"█ █ █ █ █ ███ █ ███████ ███ █ █ ███████ █", 
"█ █ █     █   █ █ █   █ █   █ █ █       █", 
"█████ ███████ ███ ███ █ ███ █ ███ █ █████", 
"█   █ █     █     █ █     █ █     █     █", 
"█ ███ █ █████████ █ █████████ █ █ ███ █ █", 
"█       █   █ █   █   █     █ █ █ █   █ █", 
"█ ███ ███ ███ ███ █ ███ █ █████ ███ █████", 
"█ █           █ █   █   █   █   █ █   █ █", 
"█ █ ███ ███████ █ █ █ █ █ █████ █ ███ █ █", 
"█C█d█       █     █   █ █     █     █    ", 
"███████████████████████████████████████ 1"});
        System.out.println();
        m_medium.print();
        List<Node> medium_trace = m_medium.isConnected();
        if (medium_trace == null) {
            System.out.println("Medium is not connected");
        } else {
            for (Node n : medium_trace) {
                System.out.printf("Tracing %s\n", n.toString());
            }
        }
        // System.out.println();
        // m_large.print();
        // System.out.println(null != m_large.isConnected());
        System.out.println();
        m_large.print();
        List<Node> large_trace = m_large.isConnected();
        if (large_trace == null) {
            System.out.println("Large is not connected");
        } else {
            for (Node n : large_trace) {
                System.out.printf("Tracing %s\n", n.toString());
            }
        }
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
    public long longHashCode() {
        final long p1 = 2;
        final long p2 = 61;
        final long p3 = 199;
        return (this.h+1) * p1 + (this.k+1)*p2 + ((long) (this.inventory+1))*p3;
    }
    @Override
    public boolean equals(Object o) {
        try {
            return ((Node) o).h == this.h 
                && ((Node) o).k == this.k 
                && ((Node) o).inventory == this.inventory;
        } catch (Exception e) { 
            return false;
        }
    }
    public String toString() {
        return String.format("(%d, %d);0x%02X", h, k, inventory);
    }
}
