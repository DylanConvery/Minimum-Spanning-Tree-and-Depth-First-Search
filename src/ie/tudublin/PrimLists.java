package ie.tudublin;

import java.io.*;

// generic stack
class Stack<T> {
    class Node {
        T data;
        Node next;
    }

    private Node top; // points at top of stack
    private int size; // keeps track of the size of the stack

    // default constructor
    public Stack() {
        size = 0;
        top = null;
    }

    // if top points to null, stack is empty
    public boolean isEmpty() {
        return top == null;
    }

    // adds a new node to top of stack
    public void push(T x) {
        Node t = new Node(); // create new node
        t.data = x; // set new node data = x
        t.next = top; // point new node at what top currently points to
        size++; // increment size
        top = t; // point top at new node
    }

    // remove node from the top of stack
    public T pop() throws Exception {
        if (!isEmpty()) {
            T data = top.data; // save node data
            top = top.next; // move stack forward
            size--; // decrememnt size
            return data; // return saved data
        } else {
            throw new Exception("List empty\n");
        }
    }

    // checks if a node is already in the stack.
    public boolean isMember(T x) {
        Node temp = top;
        while (temp != null) { // loop and compare data
            if (temp.data == x) {
                return true;
            }
            temp = temp.next;
        }
        return false;
    }

    // get size
    public int size() {
        return size;
    }

    // displays stack contents
    public void display() {
        Node t = top;
        System.out.println("\nStack contents are:  ");
        while (t != null) {
            System.out.print(t.data + " ");
            t = t.next;
        }
        System.out.println("\n");
    }
}


class Heap {
    private int[] heap; // heap array
    private int[] heap_position; // hPos[a[k]] == k
    private int[] distance; // dist[v] = priority of v

    private int size; // heap size

    // the heap constructor gets passed from the Graph:
    // 1. maximum heap size
    // 2. reference to the dist[] array
    // 3. reference to the hPos[] array
    public Heap(int maxSize, int[] _dist, int[] _hPos) {
        size = 0;
        heap = new int[maxSize + 1];
        distance = _dist;
        heap_position = _hPos;
        heap[0] = 0;
        distance[0] = Integer.MIN_VALUE;
    }

    // check if heap is empty
    public boolean isEmpty() {
        return size == 0;
    }

    //
    public void siftUp(int position) {
        int value = heap[position];
        while (distance[value] < distance[heap[position / 2]]) {
            heap[position] = heap[position / 2]; // child = parent
            heap_position[heap[position]] = position; // vertex position within heap
            position = position / 2; // position = parent position
        }
        heap[position] = value; // current element = value
        if (size >= 1) {
            heap_position[value] = position; // set appropriate vertex position
        }
    }

    public void siftDown(int position) {
        int value = heap[position];
        while (position <= size / 2) {
            int child = position * 2; // get left child
            if (child < size && distance[heap[child]] < distance[heap[child + 1]]) {
                ++child; // get right child
            }
            if (distance[value] <= distance[heap[child]]) {
                break;
            }
            heap[position] = heap[child]; // swap parent and child
            heap_position[heap[position]] = position;
            position = child; // position in array now = child
        }
        heap[position] = value;
        if (size >= 1) {
            heap_position[value] = position;
        }
    }

    public void insert(int data) {
        heap[++size] = data;
        heap_position[data] = size;
        siftUp(size);
    }


    public int remove() {
        int v = heap[1];
        heap_position[v] = 0; // v is no longer in heap
        heap[1] = heap[size--];
        siftDown(1);
        heap[size + 1] = 0; // put null node into empty spot
        return v;
    }
}


class Graph {
    class Node {
        Node(int neighbour_vertex, int weight, Node next_vertex) {
            this.neighbour_vertex = neighbour_vertex;
            this.edge_weight = weight;
            this.next_vertex = next_vertex;
        }

        int index;
        int neighbour_vertex;
        int edge_weight;
        Node next_vertex;
    }

    // V = number of vertices
    // E = number of edges
    // adj[] is the adjacency lists array
    private int vertices, edges;
    private Node[] adjacency_list;
    private int[] minimum_spanning_tree;

    // used for traversing graph
    private int[] visited;
    private int id;

    // default constructor
    public Graph(String graphFile) throws IOException {
        FileReader fr = new FileReader(graphFile);
        BufferedReader reader = new BufferedReader(fr);

        String splits = ", ";
        String line = reader.readLine();
        String[] parts = line.split(splits);
        System.out.println("\nParts[] = " + parts[0] + " " + parts[1]);

        vertices = Integer.parseInt(parts[0]);
        edges = Integer.parseInt(parts[1]);

        // create adjacency lists
        adjacency_list = new Node[vertices + 1];
        for (int vertex = 1; vertex <= vertices; ++vertex) {
            adjacency_list[vertex] = null;
        }

        visited = new int[vertices + 1];

        // read the edges
        System.out.println("Reading edges from text file");
        for (int edge = 1; edge <= edges; ++edge) {
            line = reader.readLine();
            parts = line.split(splits);
            int vertex_a = Integer.parseInt(parts[0]);
            int vertex_b = Integer.parseInt(parts[1]);
            int weight = Integer.parseInt(parts[2]);

            System.out.println(
                    "Edge " + toChar(vertex_a) + "--(" + weight + ")--" + toChar(vertex_b));

            Node temp = new Node(vertex_b, weight, adjacency_list[vertex_a]);
            adjacency_list[vertex_a] = temp;
            temp = new Node(vertex_a, weight, adjacency_list[vertex_b]);
            adjacency_list[vertex_b] = temp;
        }
        reader.close();
    }

    // convert vertex into char for pretty printing
    private char toChar(int value) {
        return (char) (value + 'A' - 1);
    }

    // display graph representation
    public void display() {
        for (int vertex = 1; vertex <= vertices; ++vertex) {
            System.out.print("\nadj[" + toChar(vertex) + "] ->");
            for (Node n = adjacency_list[vertex]; n != null; n = n.next_vertex) {
                System.out
                        .print(" |" + toChar(n.neighbour_vertex) + " | " + n.edge_weight + "| ->");
            }
            System.out.print(" \u2205");
        }
        System.out.println("");
    }

    // implementation of prim's algorithm, creates minimum spanning tree
    public void MST_Prim(int index) {
        int vertex, neighbouring_vertex, weight;
        int[] distance = new int[vertices + 1]; // stores edge weights and which vertice we've
                                                // visted previously
        int[] parent = new int[vertices + 1];
        int[] heap_position = new int[vertices + 1]; // stores the vertex positions within the heap

        // initialize our variables used within algorithm
        for (int i = 0; i <= vertices; ++i) {
            distance[i] = Integer.MAX_VALUE;
            parent[i] = 0;
            heap_position[i] = 0;
        }

        // set inital vertex edge weight to 0 to start algorithm
        distance[index] = 0; // starting vertex distance always 0

        Heap h = new Heap(vertices, distance, heap_position); // create min heap to keep track of
                                                              // MST vertice
        h.insert(index);// insert initial vertex into heap

        int total_weight = 0; // used to store total weight of MST
        while (!h.isEmpty()) { // while our heap isn't empty
            vertex = h.remove(); // dequeue/remove first index in heap
            System.out.print("\nVisted vertex " + toChar(vertex));
            total_weight += distance[vertex]; // add the edge weight it took to get to this vertex
            distance[vertex] = -distance[vertex]; // marks vertex as visited
            // cycle through all neighbouring vertice
            for (Node t = adjacency_list[vertex]; t != null; t = t.next_vertex) {
                neighbouring_vertex = t.neighbour_vertex;
                weight = t.edge_weight;
                // check to see if we've been here before or if this edge weight is less than the
                // previously connected vertice i.e (d -- (15) -- e) > (f -- (8) -- e)
                if (weight < distance[neighbouring_vertex]) {
                    distance[neighbouring_vertex] = weight; // update our weight
                    parent[neighbouring_vertex] = vertex; // update our MST
                    // siftdown our neighbour if it isn't in our heap
                    if (heap_position[neighbouring_vertex] == 0) {
                        h.insert(neighbouring_vertex);
                        // siftup neighbour if already in heap
                    } else {
                        h.siftUp(heap_position[neighbouring_vertex]);
                    }
                }
            }
        }
        System.out.print("\n\nWeight of MST = " + total_weight + "\n");
        // set MST = our newly made MST
        minimum_spanning_tree = parent;
    }

    // print our minimum spanning tree
    public void showMST() {
        System.out.print("\nMinimum Spanning tree parent array is:\n");
        for (int v = 1; v <= vertices; ++v) {
            System.out.println(toChar(v) + " -> " + toChar(minimum_spanning_tree[v]));
        }
        System.out.println("");
    }

    // setup method for DF_recursive
    public void DF(int vertex) throws Exception {
        id = 0;
        for (int i = 1; i <= vertices; ++i) { // reset visited
            visited[i] = 0;
        }
        DF_recursive(0, vertex);
    }

    // iterative Depth First Traversal for adjacency list
    public void DF_iterative(int vertex) throws Exception {
        if (vertex <= vertices && vertex > 0) {
            id = 0;
            for (int i = 1; i <= vertices; ++i) { // reset visited
                visited[i] = 0;
            }
            Stack<Integer> stack = new Stack<Integer>(); // stack used to store our vertex's
            stack.push(vertex); // add the current vertex index to the stack
            while (!stack.isEmpty()) { // while we haven't visted all nodes
                int v = stack.pop(); // pop the current vertex index
                if (visited[v] == 0) { // if we haven't been to this vertex index
                    visited[v] = ++id; // mark current vertex index as visited
                    System.out.print("\nVisited vertex " + toChar(v));
                    // cycle through all nodes in adjacency list [index]
                    for (Node u = adjacency_list[v]; u != null; u = u.next_vertex) {
                        if (visited[u.neighbour_vertex] == 0) { // if we haven't been to the u's
                            stack.push(u.neighbour_vertex); // neighbouring vertex, push the
                        } // neighbours vertex index to the stack
                    }
                }
            }
            System.out.println();
        } else {
            throw new Exception("vertex out of range\n");
        }
    }

    // recursive Depth First Traversal for adjacency matrix
    private void DF_recursive(int prev, int index) throws Exception {
        if (index <= vertices && index > 0) {
            visited[index] = ++id; // mark current vertex index as visited
            if (prev == 0) { // if we're at the inital vertex
                System.out.println("\nEntering at vertex " + toChar(index));
            } else {
                System.out.println("Visited vertex " + toChar(index) + " along edge " + toChar(prev)
                        + " -- " + toChar(index));
            }
            // cycle through all nodes in adjacency list [index]
            for (Node u = adjacency_list[index]; u != null; u = u.next_vertex) {
                if (visited[u.neighbour_vertex] == 0) { // if we haven't been to the
                    DF_recursive(index, u.neighbour_vertex); // u's neighbouring vertex call
                } // ourselves and pass the neighbours index
            }
        } else {
            throw new Exception("vertex out of range\n");
        }
    }
}


public class PrimLists {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // validate the users input
        System.out.print("Please enter the path to your graph file: ");
        String path = reader.readLine();
        File file = new File(path);
        while (!file.isFile()) {
            System.out.print("Please enter a valid path to your graph file: ");
            path = reader.readLine();
            file = new File(path);
        }

        // validate vertex entry
        System.out.print("Please enter a starting vertex position: ");
        String line = reader.readLine();
        while (!line.chars().allMatch(Character::isAlphabetic)
                && !line.chars().allMatch(Character::isDigit)
                || line.chars().allMatch(Character::isAlphabetic) && line.length() > 1) {
            System.out.print("Please enter a valid starting vertex position: ");
            line = reader.readLine();
        }
        reader.close();

        // check whether vertex was entered using characters or numbers
        // and then convert to the appropriate type
        int vertex = 0;
        if (line.chars().allMatch(Character::isDigit)) {
            vertex = Integer.parseInt(line);
        } else {
            vertex = (int) Character.toUpperCase(line.charAt(0)) - 64;
        }

        try {
            Graph g = new Graph(path);
            System.out.print("\nAdjacency List is as follows: ");
            g.display();
            System.out.print("\nDepth-First Search Using Recursion: ");
            g.DF(vertex);
            System.out.print("\nDepth-First Search Using Iteration: ");
            g.DF_iterative(vertex);
            System.out.print("\nPrim's Minimum Spanning Tree: ");
            g.MST_Prim(vertex);
            g.showMST();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
