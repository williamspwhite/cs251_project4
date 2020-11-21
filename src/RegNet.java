import java.util.ArrayList;

public class RegNet
{
    //creates a regional network
    //G: the original graph
    //max: the budget
    public static Graph run(Graph G, int max) 
    {
        if (G == null) {
            return null;
        }
        System.out.println("Max: " + max);
        // Step 1
        Graph mstGraph = findMST(G);
        System.out.println("Printing MST:");
        System.out.println(mstGraph.toString());

        ArrayList<Edge> sortedEdges = mstGraph.sortedEdges();
        System.out.println("Printing Sorted Edges");
        System.out.println(sortedEdges.toString());
        int sortedEdges_index = sortedEdges.size() - 1;
        while (mstGraph.totalWeight() > max) {

            System.out.println("\n" + mstGraph.toString() + sortedEdges.toString() + "\n");

            Edge currentEdge = sortedEdges.get(sortedEdges_index);
            if ((mstGraph.adj(currentEdge.ui()).size() <= 1) || //if the adjacency list of either edge is empty, remove
                mstGraph.adj(currentEdge.vi()).size() <= 1) {

                System.out.println("removing edge");
                sortedEdges.remove(sortedEdges_index);
                mstGraph.removeEdge(currentEdge);
                sortedEdges_index = sortedEdges.size(); //possibly remove this //TODO

            }
            sortedEdges_index--;
            //if totalWeight is still below max, then start over
            if (sortedEdges_index <= 0) { //not sure if >= or >
                sortedEdges_index = sortedEdges.size() - 1;
            }
        }
        mstGraph = mstGraph.connGraph();
        System.out.println("STEP 2");
        System.out.println("Printing MST:");
        System.out.println(mstGraph.toString());

        // Step 2
        int num_possible_Edges = (int) (.5 * (mstGraph.V() - 1) * mstGraph.V());
        System.out.println("Num possible Edges: " + num_possible_Edges);
        EdgeAndStops[] edgeAndStops_arr = new EdgeAndStops[num_possible_Edges];
        int edgeAndStops_index = 0;


//        //prints out edgeTo list
//        System.out.println();
//        for (int i = 0; i < edgeTo.length; i++) {
//            System.out.println("" + mstGraph.getCode(i) + ", " + edgeTo[i]);
//        }

        for (int start_index = 0; start_index < (mstGraph.V() - 1); start_index++) { // -2 bc start index should be one less than highest index
            int[] edgeTo = BFS(mstGraph, mstGraph.getCode(start_index)); // gets edge for BFS and does BFS

            for (int current_index = start_index + 1; current_index < mstGraph.V(); current_index++) {
                edgeAndStops_arr[edgeAndStops_index] = new EdgeAndStops(G.getEdge(G.index(mstGraph.getCode(start_index)), G.index(mstGraph.getCode(current_index))),
                        findStops(edgeTo, start_index, current_index));
                edgeAndStops_index++;
            }
        }
        System.out.println("edgeAndStops_arr:");
        for (int i = 0; i < edgeAndStops_arr.length; i++) {
            System.out.println(", " + edgeAndStops_arr[i]);
        }
        System.out.println("Quicksorting...");
        edgeAndStops_arr = edgeStop_QuickSort(edgeAndStops_arr, 0, edgeAndStops_arr.length - 1);

        System.out.println("edgeAndStops_arr:");
        for (int i = 0; i < edgeAndStops_arr.length; i++) {
            System.out.println(", " + edgeAndStops_arr[i]);
        }


        edgeAndStops_index = edgeAndStops_arr.length - 1; //index from last element bc list is least to greatest
        while ((mstGraph.totalWeight() < max) && (edgeAndStops_index >= 0)) {
            if (edgeAndStops_arr[edgeAndStops_index].getNumStops() != 0) {
                if (mstGraph.totalWeight() + edgeAndStops_arr[edgeAndStops_index].getEdge().w < max) {
                    mstGraph.addEdge(edgeAndStops_arr[edgeAndStops_index].getEdge());
                } else if ((edgeAndStops_index != 0) &&
                        (edgeAndStops_arr[edgeAndStops_index - 1].getNumStops() !=
                        edgeAndStops_arr[edgeAndStops_index].getNumStops()) &&
                        (mstGraph.totalWeight() + edgeAndStops_arr[edgeAndStops_index - 1].getEdge().w > max)) {
                   break;
                }
            }
            edgeAndStops_index--;
        }
        return mstGraph;

    }


    /*
     * findMST()
     * finds an MST on Graph G using Kruskal's algorithm
     */
    private static Graph findMST(Graph G) {
        Graph tempGraph = G.connGraph();

        ArrayList<Edge> sortedEdges = tempGraph.sortedEdges();
        Graph mstGraph = new Graph(tempGraph.V());
        mstGraph.setCodes(tempGraph.getCodes());

        UnionFind union = new UnionFind(tempGraph.V());

        int numTempGraphVertices = tempGraph.V();
        System.out.println("num verts: " + tempGraph.V());

        int sortedEdges_index = 0;

        while (mstGraph.E() < (numTempGraphVertices - 1)) {
            Edge current_edge = sortedEdges.get(sortedEdges_index);
            if (union.find(current_edge.ui()) != union.find(current_edge.vi())) {
                mstGraph.addEdge(current_edge.ui(), current_edge.vi(), current_edge.w);
                union.union(current_edge.ui(), current_edge.vi());
            }
            sortedEdges_index++;
        }

        return mstGraph;
    }

    /*
     * BFS()
     * runs BFS on Graph G given a starting vertex
     */
    private static int[] BFS(Graph G, String start_code) {
        if ((G == null) || (start_code == null)) {
            System.out.println("BIG FAIL BFS");
            return null;
        }
        ArrayList<String> queue = new ArrayList<String>();
        int[] visited = new int[G.V()];
        int[] edgeTo = new int[G.V()];
        edgeTo[G.index(start_code)] = -1;
        queue.add(start_code);
        while (!queue.isEmpty()) {
            String vert_code = queue.remove(0);
            visited[G.index(vert_code)] = 1;
            for (Integer adj_v_int : G.adj(vert_code)) {

                if (visited[adj_v_int] != 1) {
                    queue.add(G.getCode(adj_v_int));
                    visited[adj_v_int] = 1;
                    edgeTo[adj_v_int] = G.index(vert_code);
                }
            }
        }
        return edgeTo;
    }

    /*
     * finds number of stops between 2 vertices given the edgeTo result
     * from a BFS
     */
    private static int findStops(int[] edgeTo, int start_index, int end_index) {
        if (edgeTo == null) {
            System.out.println("BIG FAIL IN FINDSTOPS");
        }
        int numStops = -1;
        int current_index;
        if (edgeTo[start_index] == -1) {
            current_index = end_index;
            end_index = start_index;
        } else {
            current_index = start_index;
        }

        while (current_index != end_index) {
            if (current_index == -1) {
                current_index = end_index;
                while (current_index != -1) { //if stuck at beginning of BFS, search other direction
                    current_index = edgeTo[current_index];
                    numStops++;
                }
                return numStops - 2; //subtract 2 bc "-1" vertex counted twice
            }
            current_index = edgeTo[current_index];
            numStops++;
        }
        return numStops;
    }


    //TODO
    //possible problems with sorting
    /*
     * edgeStop_QuickSort
     * sorts an array of edges and stops
     */
    private static EdgeAndStops[] edgeStop_QuickSort(EdgeAndStops[] edgeStop_arr, int low, int high) {
        if ((low < 0) || (high < 0) || (high >= edgeStop_arr.length) || (low >= high)) {
            System.out.println("BIG FAIL IN QUICKSORT");
            return edgeStop_arr;
        }

        EdgeAndStops temp = edgeStop_arr[high];
        edgeStop_arr[high] = edgeStop_arr[low + ((high - low) / 2)];
        edgeStop_arr[low + ((high - low) / 2)] = temp;

        int pivot = high;
        int lessThan = low;

        for (int i = low; i < high; i++) {
            if (edgeStop_arr[i].compare(edgeStop_arr[pivot]) <= 0) {

                //swap lessThan and i
                temp = edgeStop_arr[lessThan];
                edgeStop_arr[lessThan] = edgeStop_arr[i];
                edgeStop_arr[i] = temp;

                lessThan++;
            }
        }

        //swap lessThan and pivot
        temp = edgeStop_arr[lessThan];
        edgeStop_arr[lessThan] = edgeStop_arr[pivot];
        edgeStop_arr[pivot] = temp;

        edgeStop_arr = edgeStop_QuickSort(edgeStop_arr, low, lessThan - 1);
        edgeStop_arr = edgeStop_QuickSort(edgeStop_arr, lessThan + 1, high);
        return edgeStop_arr;
    }
}

class EdgeAndStops {
    private Edge e;
    private int numStops;
    public EdgeAndStops(Edge e, int numStops) {
        if (e == null) {
            System.out.println("BAD EDGE");
        }
        this.e = e;
        this.numStops = numStops;
    }

    public int getNumStops() { return numStops; }
    public Edge getEdge() { return e; }


    /*
     * compare()
     * compares to EdgeAndStops using NumStops
     * and uses if NumStops are equal
     */
    public int compare(EdgeAndStops es) {
        if (this.getNumStops() == es.getNumStops()) {
            return es.getEdge().w - this.getEdge().w; //reverse because smaller edge
                                            // distances are more important
        } else {
            return this.getNumStops() - es.getNumStops();
        }
    }

    public String toString() {
        return "" + this.numStops + ", " + this.e.toString();
    }
}
