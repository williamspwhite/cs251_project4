import javax.print.DocFlavor;
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

        // Step 1
        Graph mstGraph = findMST(G);
        ArrayList<Edge> sortedEdges = mstGraph.sortedEdges();
        int sortedEdges_index = 0;
        while (mstGraph.totalWeight() > max) {
            Edge currentEdge = sortedEdges.get(sortedEdges_index);
            if (mstGraph.adj(currentEdge.ui()).isEmpty() || //if the adjacency list of either edge is empty, remove
                mstGraph.adj(currentEdge.vi()).isEmpty()) {
                mstGraph.removeEdge(currentEdge);
                sortedEdges.remove(sortedEdges_index);
            } else { //if edge is not removed, iterate index
                sortedEdges_index++;
            }
            //if totalWeight is still below max, then start over
            if (sortedEdges_index >= sortedEdges.size()) { //not sure if >= or >
                sortedEdges_index = 0;
            }
        }


        // Step 2
        int num_possible_Edges = (int) (.5 * (mstGraph.V() - 1) * mstGraph.V());
        EdgeAndStops[] edgeAndStops_arr = new EdgeAndStops[num_possible_Edges];
        int edgeAndStops_index = 0;

        int[] edgeTo = BFS(mstGraph, sortedEdges.get(0).u); // gets edge for BFS and does BFS
        for (int start_index = 0; start_index < (mstGraph.V() - 1); start_index++) {
            for (int current_index = start_index; current_index < mstGraph.V(); current_index++) {
                edgeAndStops_arr[edgeAndStops_index] = new EdgeAndStops(mstGraph.getEdge(start_index, current_index),
                        findStops(edgeTo, start_index, current_index));
                edgeAndStops_index++;
            }
        }

        edgeAndStops_arr = edgeStop_QuickSort(edgeAndStops_arr, 0, edgeAndStops_arr.length - 1);










        return null;
    }


    /*
     * findMST()
     * finds an MST on Graph G using Kruskal's algorithm
     */
    private static Graph findMST(Graph G) {
        Graph tempGraph = G.connGraph();
        ArrayList<Edge> sortedEdges = tempGraph.sortedEdges();
        Graph mstGraph = new Graph(tempGraph.V());

        UnionFind union = new UnionFind(tempGraph.V());

        int numTempGraphEdges = tempGraph.E();

        int sortedEdges_index = 0;

        while (mstGraph.E() < (numTempGraphEdges - 1)) {
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
     * runs BFS on Graph G given a starting vertice
     */
    private static int[] BFS(Graph G, String start_code) {
        if ((G == null) || (start_code == null)) {
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
        int numStops = -1;
        int current_index;
        if (edgeTo[start_index] == -1) {
            current_index = start_index;
        } else {
            current_index = end_index;
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

        int pivot = high;
        int lessThan = low;

        for (int i = low; i < high; i++) {
            if (edgeStop_arr[i].compare(edgeStop_arr[pivot]) <= 0) {

                //swap i and pivot
                EdgeAndStops temp = edgeStop_arr[i];
                edgeStop_arr[i] = edgeStop_arr[pivot];
                edgeStop_arr[pivot] = temp;

                lessThan++;
            }
        }

        //swap lessThan and pivot
        EdgeAndStops temp = edgeStop_arr[lessThan];
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
