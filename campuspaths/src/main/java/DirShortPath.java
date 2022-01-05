package pathfinder;

import graph.DirGraph;
import shortestPath.GraphNeighbors;
import java.util.*;

/**
 * This class contains static method for finding shortest path for a DirGraph
 */
public class DirShortPath {

    // This class is not an ADT

    /**
     * Find the shortest weighted path between two nodes in a directed graph DirGraph
     *
     * @param g a directed graph DirGraph to search path from
     * @param source Source Node of searching path
     * @param dest Destination Node of searching path
     * @param <N> Node data type
     * @param <E> Edge data type
     * @return a list of DirGraph.Edge objects that represent the shortest path between
     * two buildings
     */
        public static <N, E extends Number> List<DirGraph<N, E>.Edge> shortestPath(DirGraph<N, E> g, N source, N dest) {
        List<shortestPath.GraphNeighbors.Edge<N, E>> shortestPath
                = shortestWeightedPath(g, source, dest);
        List<DirGraph<N, E>.Edge> edges = new ArrayList<>();
        if (shortestPath != null) {
            // convert all edges to edges from our ADT.
            for (shortestPath.GraphNeighbors.Edge<N, E> edge : shortestPath) {
                edges.add(g.createEdge(edge.getSource(), edge.getDest(), edge.getLabel()));
            }
        }
        return edges;
    }

    public static <Node, Label extends Number> List<shortestPath.GraphNeighbors.Edge<Node, Label>>
        shortestWeightedPath(GraphNeighbors<Node, Label> graph, Node source, Node dest) {
        Map<Node, Double> explored = new HashMap();
        explored.put(source, 0.0D);
        double infinity = 1.0D / 0.0;
        Objects.requireNonNull(explored);
        Queue<Node> frontier = new PriorityQueue(Comparator.comparing(explored::get));
        frontier.add(source);
        Map<Node, shortestPath.GraphNeighbors.Edge<Node, Label>> parents = new HashMap();
        parents.put(source, null);
        Object current = null;

        while(!frontier.isEmpty()) {
            current = frontier.remove();
            double minCost = (Double)explored.get(current);
            if (current.equals(dest)) {
                break;
            }

            Iterator var11 = graph.outgoingEdges((Node)current).iterator();

            while(var11.hasNext()) {
                shortestPath.GraphNeighbors.Edge<Node, Label> edge = (shortestPath.GraphNeighbors.Edge)var11.next();
                double currentCost = minCost + ((Number)edge.getLabel()).doubleValue();
                Node next = edge.getDest();
                double knownCost = (Double)explored.getOrDefault(next, 1.0D / 0.0);
                if (!(currentCost >= knownCost)) {
                    explored.put(next, currentCost);
                    parents.put(next, edge);
                    frontier.add(next);
                }
            }
        }

        if (!parents.containsKey(dest)) {
            return null;
        } else {
            shortestPath.GraphNeighbors.Edge edge;
            ArrayList shortestPath;
            for(shortestPath = new ArrayList(); parents.get(current) != null; current = edge.getSource()) {
                edge = (shortestPath.GraphNeighbors.Edge)parents.get(current);
                shortestPath.add(0, edge);
            }

            return shortestPath;
        }
    }

}
