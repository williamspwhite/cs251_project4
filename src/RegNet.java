import java.util.ArrayList;

public class RegNet
{
    //creates a regional network
    //G: the original graph
    //max: the budget
    public static Graph run(Graph G, int max) 
    {
        Graph tempGraph = G.connGraph();
        ArrayList<Edge> sortedEdges = tempGraph.sortedEdges();

        UnionFind union = new UnionFind(tempGraph.V());

        int numTempGraphEdges = tempGraph.E();
        int numMSTEdges = 0;

        int sortedEdges_index = 0;

        while (numMSTEdges < (numTempGraphEdges - 1)) {
            if (union.find(sortedEdges.get(sortedEdges_index).ui())
        }



        return null;
	    //To be implemented
    }
}