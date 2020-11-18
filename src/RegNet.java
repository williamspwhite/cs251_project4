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
            } else {
                sortedEdges_index++;
            }
            if (sortedEdges_index >= sortedEdges.size()) { //not sure if >= or >
                sortedEdges_index = 0;
            }
        }
        // Step 2
        int num_possible_Edges = (int) .5 * (mstGraph.V() - 1) * mstGraph.V();
        int[] numStopsArray = new int[num_possible_Edges];
        Edge[] numStopsEdgeArray = new Edge[num_possible_Edges];

        for (int i = 0; i < mstGraph.V(); i++) {

        }



        return null;
    }


    private static int BFS(Graph G, String start_code, int end_index) {
        ArrayList<String> queue = new ArrayList();
        int[] visited = new int[G.V()];

        queue.add(start_code);
        while (!queue.isEmpty()) {
            String current_code = queue.remove(0);
            visited[G.index(current_code)] = 1;
            for (Integer integer : G.adj(current_code)) {
                if (visited[integer.intValue()] != 1) {
                    queue.add(G.getCode(integer.intValue()));
                    visited[integer.intValue()] = 1;

                }
            }
        }
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