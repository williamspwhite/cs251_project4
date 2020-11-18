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
        int[] numStopsArray = new int[num_possible_Edges];
        Edge[] numStopsEdgeArray = new Edge[num_possible_Edges];


        int[] edgeTo = BFS(mstGraph, sortedEdges.get(0).u); // gets edge for BFS and does BFS
        for (int start_index = 0; start_index < (mstGraph.V() - 1); start_index++) {
            for (int current_index = start_index; current_index < mstGraph.V(); current_index++) {

            }
        }



        return null;
    }

    private static int findStops(int[] edgeTo, int start_index, int end_index) {
        int numStops = 0;
        int current_index = start_index;
        while (current_index != end_index) {
            if (current_index == -1) {
                current_index = end_index;
                while (current_index != -1) { //if stuck at beginning of BFS, search other direction
                    current_index = edgeTo[current_index];
                    numStops++;
                }
                return numStops;
            }
            current_index = edgeTo[current_index];
            numStops++;
        }
        return numStops;
    }


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
}