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

        int



        return null;
	    //To be implemented
    }
}