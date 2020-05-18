package ie.tudublin;

// Simple weighted graph representation
// Uses an Adjacency Linked Lists, suitable for sparse graphs

import java.io.*;

class Graphs {
    class Node {
        Node(int vert, int wgt, Node next) {
            this.vert = vert;
            this.wgt = wgt;
            this.next = next;
        }

        int vert;
        int wgt;
        Node next;
    }

    // V = number of vertices
    // E = number of edges
    // adj[] is the adjacency lists array
    private int V, E;
    private Node[] adj;

    // used for traversing graph
    private int[] visited;
    private int id;

    // default constructor
    public Graphs(String graphFile) throws IOException {
        int u, v;
        int e, wgt;
        Node t;

        FileReader fr = new FileReader(graphFile);
        BufferedReader reader = new BufferedReader(fr);

        String splits = ", ";
        String line = reader.readLine();
        String[] parts = line.split(splits);
        System.out.println("\nParts[] = " + parts[0] + " " + parts[1]);

        V = Integer.parseInt(parts[0]);
        E = Integer.parseInt(parts[1]);

        // create adjacency lists
        adj = new Node[V + 1];
        for (v = 1; v <= V; ++v) {
            adj[v] = null;
        }

        visited = new int[V + 1];

        // read the edges
        System.out.println("Reading edges from text file");
        for (e = 1; e <= E; ++e) {
            line = reader.readLine();
            parts = line.split(splits);
            u = Integer.parseInt(parts[0]);
            v = Integer.parseInt(parts[1]);
            wgt = Integer.parseInt(parts[2]);

            System.out.println("Edge " + toChar(u) + "--(" + wgt + ")--" + toChar(v));

            t = new Node(v, wgt, adj[u]);
            adj[u] = t;
            t = new Node(u, wgt, adj[v]);
            adj[v] = t;
        }
        reader.close();
    }

    // convert vertex into char for pretty printing
    private char toChar(int u) {
        return (char) (u + 64);
    }

    // method to display the graph representation
    public void display() {
        for (int v = 1; v <= V; ++v) {
            System.out.print("\nadj[" + toChar(v) + "] ->");
            for (Node n = adj[v]; n != null; n = n.next) {
                System.out.print(" |" + toChar(n.vert) + " | " + n.wgt + "| ->");
            }
            System.out.print(" \u2205");
        }
        System.out.println("");
    }

    private void DF(int s) {
        id = 0;
        for (int v = 1; v <= V; ++v) {
            visited[v] = 0;
        }
        DF_recursive(0, s);
    }

    // Recursive Depth First Traversal for adjacency matrix
    private void DF_recursive(int prev, int v) {
        visited[v] = ++id;
        if (prev == 0) {
            System.out.println("\nEntering at vertex " + toChar(v));
        } else {
            System.out.println("Visited vertex " + toChar(v) + " along edge " + toChar(prev)
                    + " -- " + toChar(v));
        }
        // System.out.print(toChar(v) + " ");
        for (Node u = adj[v]; u != null; u = u.next) {
            if (visited[u.vert] == 0) {
                DF_recursive(v, u.vert);
            }
        }
    }

    // Method to initialise Recursive Depth First Traversal of a disconnected Graph
    public void DF_disconnected() {
        int count = 0;
        for (int i = 1; i <= V; i++) {
            if (visited[i] == 0) {
                ++count;
                DF(i);
            }
        }
        if (count == 1) {
            System.out.println("\nThere is " + count + " component.");
        } else {
            System.out.println("\nThere are " + count + " component.");
        }
    }
}


public class DisconnectedDepthFirstSearch {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Please enter the path to your disconnected graph file: ");
        String path = reader.readLine();
        File file = new File(path);
        while (!file.isFile()) {
            System.out.print("Please enter a valid path to your disconnected graph file: ");
            path = reader.readLine();
            file = new File(path);
        }
        Graphs g = new Graphs(path);
        g.display();
        g.DF_disconnected();
    }
}
