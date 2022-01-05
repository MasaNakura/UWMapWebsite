package graph;

import shortestPath.GraphNeighbors;

import java.util.*;

/**
 * <b>DirGraph</b> is a mutable directed graph with nodes and edges labeled by unique labels
 * (no two nodes will have same label, no two edges will have same label).
 *
 * <p>A DirGraph can be described by a pair of collections, (n, e), where n is a collection of
 * nodes and e is a collection of edges. A collection of nodes can be described as n[n1, n2...]
 * where n[] is an empty graph, n[n1] is a one node graph, and so on. A collection of Edges between
 * the nodes can be described as e[(n1,n2), (n2, n3) ...] where e[] represents a graph with no
 * edges, e[(n1, n2)] represents a graph with one edge, and so on. (n1,n1) indicates a directed
 * edge from Node n1 to Node n2. In this case, node n1 is referred to as the Parent Node while
 * node n2 is a child node of Node n1.
 *
 * <p>Collections of nodes or edges can be described constructively with the append operation
 * ':' such that [n1]:N is the result of adding node n1 in the collection of Node N.
 *
 * <p>Examples of DirGraph include (n[n1, n2, n3], e[(n1, n2), (n3,n2)]), (n[n1, n2]), e[]),
 * and (n[], e[]).
 *
 * Type parameter N is the type of node labels and E is the type for edge labels in this DirGraph
 */
public class DirGraph<N, E> implements GraphNeighbors<N, E> {

    // AF(this) = a directed labeled graph where 'nodes' is a map that associates
    // node labels to a corresponding Node Objects (Node Objects store incoming and outgoing
    // edges. Check a more detailed description of node below). DirGraph could be described
    // by the Abstract State described above in javadoc spec:
    // (nodes.keySet(), collection of all outgoing OR all incoming edges).
    //
    // Rep Inv: nodes != null. Node label that are keys in nodes map must not be null
    // Node Objects associated to maps may not be null. Edges stored as outgoing edge must
    // be stored as an incoming Edge in the corresponding child node. There cannot be a
    // duplicate edge with the same labels, same parents, and same child.

    /**
     * Whether to check if rep. inv. is violated or not
     */
    private static final boolean DEBUG = false;

    /**
     * A Map associating node labels to Node objects
     */
    private final Map<N, Node> nodes;

    /**
     * @spec.effects Constructs a new DirGraph, (n[], e[]).
     */
    public DirGraph() {
        nodes = new HashMap<>();
        checkRep();
    }

    /**
     * Adds a node with label 'n' to this graph.
     *
     * @param n the label for the added Node
     * @spec.requires n != null
     * @spec.modifies this
     * @spec.effects if this = (N, E), this_post = ([n]:N, E).
     * @return true if the node with label 'n' is successfully added,
     * false if node 'n' already exists in this graph
     */
    public boolean addNode(N n) {
        checkRep();
        if (this.containsNode(n)) return false;
        // node label is mapped to a Node object
        nodes.put(n, new Node());
        checkRep();
        return true;
    }

     /**
      * Adds the directed Edge 'label' to this graph from nodes 'parent' to 'child'
      *
      * @param parent the parent node
      * @param child the child node
      * @param label the label of the Edge
      * @spec.requires label, child, parent cannot be null
      * @spec.modifies this
      * @spec.effects if this = (N, E) and contains both parent and child, this_post = (N, [label]:E).
      * otherwise if 'parent' or 'child' is not contained in this, adds the missing nodes in this
      * graph as well as the Edge 'label'
      * @return true if the edge is successfully added, false if edge 'label' with same
      * connected nodes already exists in this graph
      */
    public boolean addEdge(N parent, N child, E label) {
        checkRep();
        Edge e = new Edge(parent, child, label);
        if (containsEdge(parent, child, label)) return false;
        // adds new nodes if passed nodes doesn't exist
        this.addNode(e.getParent());
        this.addNode(e.getChild());
        // edge label stored as outgoing in parent node
        nodes.get(e.getParent()).addOut(e);
        // edge label stored as incoming in child node
        nodes.get(e.getChild()).addIn(e);
        checkRep();
        return true;
    }

    /**
     * Removes a node and all edges to and from the node with the label 'n' from this map.
     *
     * @param n the label for the node to be removed
     * @spec.requires n != null
     * @spec.modifies this
     * @spec.effects if this = ([n]:N, [all edges connected to n]:E), this_post = (N, E)
     * @return true if the Node is successfully removed, false if
     * there is no existing node with the label 'n'
     */
    public boolean removeNode(N n) {
        checkRep();
        if (!nodes.containsKey(n)) return false;
        // remove all outgoing edges for this node
        Iterator<Edge> edge =  nodes.get(n).outIterator();
        while (edge.hasNext()) {
            Edge e = edge.next();
            removeEdge(e.getParent(), e.getChild(), e.getLabel());
        }
        // remove all incoming edges for this node
        edge =  nodes.get(n).inIterator();
        while (edge.hasNext()) {
            Edge e = edge.next();
            removeEdge(e.getParent(), e.getChild(), e.getLabel());
        }
        nodes.remove(n);
        checkRep();
        return true;
    }

    /**
     * Removes Edge 'label' from 'parent' to 'child' from this graph.
     *
     * @param parent the parent node
     * @param child the child node
     * @param label the label of the Edge
     * @spec.requires label, child, parent cannot be null
     * @spec.modifies this
     * @spec.effects if this = (N, [e]:E), this_post = (N, E)
     * @return true if the Node is successfully removed, false if
     * there is no existing node with the label 'label' that is connected from
     * 'parent' to 'child'
     */
    public boolean removeEdge(N parent, N child, E label) {
        checkRep();
        Edge e = new Edge(parent, child, label);
        if (!containsEdge(parent, child, label)) return false;
        // remove edges label from the parent and child node
        nodes.get(e.getParent()).removeOut(e);
        nodes.get(e.getChild()).removeIn(e);
        checkRep();
        return true;
    }

    /**
     * Returns an iterator over Edges that point to the given 'parent' node.
     *
     * @param parent the node checked if it has any edges pointing out from this node
     * @spec.requires this.containsNode(parent), parent != null
     * @return an unmodifiable Iterator over edges pointing out from the 'parent'
     * node represented by their Edge objects.
     */
    public Iterator<Edge> getEdgesFrom(N parent) {
        checkRep();
        Iterator<Edge> outEdges = nodes.get(parent).outIterator();
        checkRep();
        return outEdges;
    }

    /**
     * Returns an iterator over edges that point to the given 'child' node.
     *
     * @param child the node checked if it has any edges pointing to this node
     * @spec.requires this.containsNode(child), child != null
     * @return an unmodifiable Iterator over the edges pointing in to the child node
     * represented by their Edge objects
     */
    public Iterator<Edge> getEdgesTo(N child) {
        checkRep();
        Iterator<Edge> inEdges = nodes.get(child).inIterator();
        checkRep();
        return inEdges;
    }

    /**
     * Return an Iterator over nodes that are child nodes of 'parent' node
     *
     * @param parent label for node checked for its child nodes
     * @spec.requires this.containsNode(parent), parent != null
     * @return an unmodifiable Iterator of all child node labels of given
     * 'parent' node
     */
    public Iterator<N> allChildNode(N parent) {
        checkRep();
        Iterator<N> childIterator = nodeIterator(parent, true);
        checkRep();
        return childIterator;
    }

    /**
     * Return an Iterator over nodes that are parent nodes of 'child' node
     *
     * @param child label for node checked for its parent
     * @spec.requires this.containsNode(child), child != null
     * @return an unmodifiable Iterator of all parent node labels of given
     * 'child' node
     */
    public Iterator<N> allParentNode(N child) {
        checkRep();
        Iterator<N> parentNodes = nodeIterator(child, false);
        checkRep();
        return parentNodes;
    }

    /**
     * Returns an iterator of connected nodes to the given node 'n'.
     *
     * @param n label of the node checked for its connected nodes
     * @param parent Boolean indicating whether given 'n' is a parent node or not
     * @spec.requires n != null and this.containsNode(n)
     * @return If 'parent' is true, returns an unmodifiable iterator of all child
     * nodes of 'n', otherwise returns an unmodifiable Iterator of all parent nodes of 'n'.
     */
    private Iterator<N> nodeIterator(N n, boolean parent) {
        Set<N> storedNodes = new HashSet<>();
        // outgoing edges for parent, incoming edges for child nodes
        Iterator<Edge> edge = getEdgesFrom(n);
        if (!parent) edge = getEdgesTo(n);
        // for all edges add the other connected node to Set.
        while (edge.hasNext()) {
            if (parent) {
                // store the child nodes
                storedNodes.add(edge.next().getChild());
            } else {
                // store the parent nodes
                storedNodes.add(edge.next().getParent());
            }
        }
        Iterator<N> iterateNodes = Collections.unmodifiableSet(storedNodes).iterator();
        checkRep();
        return iterateNodes;
    }

    /**
     * Returns true if this graph contains an Node labeled 'n'.
     *
     * @param n a label to be checked if it is an existing label for a Node in the graph
     * @spec.requires n != null
     * @return true if the graph contains a Node with the given label, false otherwise
     */
    public boolean containsNode(N n) {
        checkRep();
        return nodes.containsKey(n);
    }

    /**
     * Returns the label of the Node that the given edge 'e' points to.
     *
     * @param e a Edge to be checked for a child node
     * @spec.requires e != null
     * @return a label for the child node that is pointed by the edge with the given
     * 'e'. If Edge 'e' does not exist, returns null.
     */
    public N getChild(Edge e) {
        checkRep();
        if (!this.containsEdge(e.getParent(), e.getChild(), e.getLabel())) return null;
        N child = e.getChild();
        checkRep();
        return child;
    }

    /**
     * Returns the label of the Node that the given edge 'e' points from.
     *
     * @param e a Edge to be checked for a parent node
     * @spec.requires e != null
     * @return a label for the parent node that points the edge with the given
     * 'e'. If edge 'e' does not exist, returns null.
     */
    public N getParent(Edge e) {
        checkRep();
        if (!this.containsEdge(e.getParent(), e.getChild(), e.getLabel())) return null;
        N parent = e.getParent();
        checkRep();
        return parent;
    }

    /**
     * Returns true if this graph contains an edge 'label' that points from
     * 'parent' to 'child'
     *
     * @param parent the Node that the label points from
     * @param child the Node that the label points to
     * @param label a Edge label to be checked if it is an existing edge in the graph
     * @spec.requires e != null
     * @return true if the graph contains the given Edge, false otherwise
     */
    public boolean containsEdge(N parent, N child, E label) {
        checkRep();
        if (!nodes.containsKey(parent) || !nodes.containsKey(child)) {
            return false;
        }
        Edge e = new Edge(parent, child, label);
        boolean correctOut = nodes.get(parent).containsOut(e);
        boolean correctIn = nodes.get(child).containsIn(e);
        checkRep();
        return correctOut && correctIn;
    }

    /**
     * Returns true if this graph does not contain any nodes
     *
     * @return true if this graphs contains no nodes, false otherwise
     */
    public boolean isEmpty() {
        checkRep();
        return nodes.isEmpty();
    }

    /**
     * creates and returns an Edge object labeled 'label' from node 'parent' to 'child'
     *
     * @param parent Node that edge points out from
     * @param child Node that edge points to
     * @param label label of the edge
     * @spec.requires parent, child, and label cannot be null
     * @return returns an Edge object labeled 'label' from parent to child
     */
    public Edge createEdge(N parent, N child, E label) {
        checkRep();
        Edge e = new Edge(parent, child, label);
        checkRep();
        return e;
    }

    /**
     * Returns true if this graph contains any edges
     *
     * @return true if this graph contains any edges, false otherwise
     */
    public boolean noEdges() {
        checkRep();
        boolean empty = true;
        for (N n : nodes.keySet()) {
            if (!nodes.get(n).isEmpty()) {
                empty = false;
            }
        }
        checkRep();
        return empty;
    }

    /**
     * Returns an iterator over labels of all nodes.
     *
     * @return an unmodifiable Iterator over the Nodes represented by their
     * labels in this graph.
     */
    public Iterator<N> allNodes() {
        checkRep();
        return Collections.unmodifiableSet(nodes.keySet()).iterator();
    }

    /**
     * Clears all nodes and edges in this Graph
     *
     * @spec.modifies this
     * @spec.effects this_post = (n[], e[])
     */
    public void clearAll() {
        checkRep();
        nodes.clear();
        checkRep();
    }

    /**
     * Clears all edges in this node without clearing Nodes
     *
     * @spec.modifies this
     * @spec.effects if this = (N, E), this_post = (N, e[])
     */
    public void clearEdges() {
        checkRep();
        for (N node : nodes.keySet()) {
            nodes.get(node).clear();
        }
        checkRep();
    }

    /**
     * Find all outgoing edges of a node.
     *
     * @param node the node to find the outgoing edges of
     * @spec.requires node != null and this.containsNode(node)
     * @return the set of all outgoing edges represented by GraphNeighbors.Edge
     */
    public Set<GraphNeighbors.Edge<N, E>> outgoingEdges(N node) {
        Iterator<Edge> outEdges = getEdgesFrom(node);
        Set<GraphNeighbors.Edge<N, E>> allOut = new HashSet<>();
        while (outEdges.hasNext()) {
            Edge e = outEdges.next();
            allOut.add(new GraphNeighbors.Edge<>(e.getParent(), e.getChild(), e.getLabel()));
        }
        return allOut;
    }

    /**
     * Throws an AssertionError if the representation invariant of DirGraph is violated.
     */
    private void checkRep() {
        assert (nodes != null) : "nodes cannot be null";
        if (DEBUG) {
            // stores all incoming edges of all nodes to unseenOut and checks
            // if there are no duplicate incoming edges.
            for (N n : nodes.keySet()) {
                assert (n != null)
                        : "node labels cannot be null";
                Node node = nodes.get(n);
                assert (node != null) : "node object cannot be null";
                Iterator<Edge> incoming = node.inIterator();
                Set<Edge> inNode = new HashSet<>();
                while (incoming.hasNext()) {
                    Edge edge = incoming.next();
                    boolean added = inNode.add(edge);
                    assert (added) : "Same node cannot have same in edges";
                }
                Iterator<Edge> outgoing = node.outIterator();
                Set<Edge> outNode = new HashSet<>();
                while (outgoing.hasNext()) {
                    Edge edge = outgoing.next();
                    N child = edge.getChild();
                    boolean added = outNode.add(edge);
                    assert (added)
                            : "same node cannot have same out edges";
                    assert (nodes.get(child).containsIn(edge))
                            : "child node does not have paired incoming edge";
                }
            }
        }
    }
    /**
     * This class represents a directed labeled edge from a parent
     * node to a child node in this graph.
     *
     * An Edge can be described as e(A, B), where A is the label for parent node,
     * B is the label for child node, and e is the label for the Edge
     *
     * Type parameter N from outer class is the type of nodes and E is the
     * type of edge labels
     */
    public class Edge {

        // AF(this) = a directed edge labeled 'label', pointing from node
        // labeled this.parent to node labeled this.child. Can be also
        // represented as Edge, label(parent, child).
        //
        // Rep Inv: parent, child, and label cannot be null
        //

        /**
         * This edge's parent node label
         */
        private final N parent;

        /**
         * This edge's child node label
         */
        private final N child;

        /**
         * This edge's label
         */
        private final E label;

        /**
         * @param parent A label for parent node
         * @param child A label for child node
         * @param label A label for this Edge
         * @spec.requires parent, child, label cannot be null
         * @spec.effects Constructs a new Edge label(parent, child)
         */
        private Edge(N parent, N child, E label) {
            this.parent = parent;
            this.child = child;
            this.label = label;
            checkRep();
        }

        /**
         * Returns the label of this Edge
         *
         * @return the label of this Edge
         */
        public E getLabel() {
            checkRep();
            return this.label;
        }

        /**
         * Returns the parent node label
         *
         * @return label for this edge's parent node
         */
        public N getParent() {
            checkRep();
            return this.parent;
        }

        /**
         * Returns the child node label
         *
         * @return label for this edge's child node
         */
        public N getChild() {
            checkRep();
            return this.child;
        }

        /**
         * Standard hashCode function
         *
         * @return an int that all objects equal to this will also return
         */
        @Override
        public int hashCode() {
            return Objects.hash(new Object[]{this.parent, this.child, this.label});
        }

        /**
         * Standard equality operation.
         *
         * @param obj the object to be compared for equality
         * @return true if and only if 'obj' is an instance of an Edge and 'this' and 'obj' represent the
         * same Edge with same label, parent, and child node.
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DirGraph<?, ?>.Edge)) {
                return false;
            }
            DirGraph<?, ?>.Edge e = (DirGraph<?, ?>.Edge) obj;
            return e.label.equals(this.label) && e.child.equals(this.child)
                    && e.parent.equals(this.parent);
        }

        /**
         * Throws an AssertionError if the representation invariant of Edge is violated.
         */
        private void checkRep() {
            assert (parent != null)
                    : "parent node cannot be null";
            assert (child != null)
                    : "child node cannot be null";
        }
    }

    /**
     * This class represents a node in a directed graph with incoming and
     * outgoing edges.
     */
    private class Node {
        // AF(this) = a node with outgoing edges stored in this.out
        // and with incoming edges stored in this.in
        //
        // Rep Inv: out != null, in != null and all element stored in
        // both Set must not be null

        /**
         * A collection of labels of outgoing edges for this node
         */
        private final Set<Edge> out;

        /**
         * A collection of labels of incoming edges for this node
         */
        private final Set<Edge> in;

        /**
         * @spec.effects Constructs a new Node
         */
        public Node() {
            out = new HashSet<>();
            in = new HashSet<>();
            checkRep();
        }

        /**
         * Adds edge 'e' as an incoming edge to Node
         *
         * @param e an incoming Edge to the node
         * @spec.requires e != null
         * @spec.modifies this
         * @spec.effects adds Edge 'e' to this Node's incoming edges,
         * if 'e' already exist as incoming edge, nothing happens
         */
        public void addIn(Edge e) {
            checkRep();
            in.add(e);
            checkRep();
        }

        /**
         * Adds edge 'e' as an outgoing edge from Node
         *
         * @param e an outgoing Edge to the node
         * @spec.requires e != null
         * @spec.modifies this
         * @spec.effects adds Edge 'e' to this Node's outgoing edges,
         * if 'e' already exist as outgoing edge, nothing happens
         */
        public void addOut(Edge e) {
            checkRep();
            out.add(e);
            checkRep();
        }

        /**
         * Removes incoming edge 'e' of this Node
         *
         * @param e an incoming Edge to the node
         * @spec.requires e != null
         * @spec.modifies this
         * @spec.effects removes this node's incoming edge 'e'. If 'e'
         * does not exist as an incoming edge, nothing changes
         */
        public void removeIn(Edge e) {
            checkRep();
            in.remove(e);
            checkRep();
        }

        /**
         * Removes outgoing edge 'e' of this Node
         *
         * @param e an outgoing Edge to the node
         * @spec.requires e != null
         * @spec.modifies this
         * @spec.effects removes this node's outgoing edge 'e'. If 'e'
         * does not exist as an outgoing edge, nothing changes
         */
        public void removeOut(Edge e) {
            checkRep();
            out.remove(e);
            checkRep();
        }

        /**
         * Returns true if this Node has an incoming node 'e'
         *
         * @param e an incoming Edge to the node to be checked
         * @spec.requires e != null
         * @return true if this nodes has incoming node 'e', false otherwise
         */
        public boolean containsIn(Edge e) {
            checkRep();
            return in.contains(e);
        }

        /**
         * Returns true if this Node has an outgoing node 'e'
         *
         * @param e an outgoing Edge to the node to be checked
         * @spec.requires e != null
         * @return true if this nodes has outgoing node 'e', false otherwise
         */
        public boolean containsOut(Edge e) {
            checkRep();
            return out.contains(e);
        }

        /**
         * Returns an iterator that iterates over all incoming edges
         *
         * @return Unmodifiable Iterator of Edge for all incoming edges
         */
        public Iterator<Edge> inIterator() {
            checkRep();
            return Collections.unmodifiableSet(in).iterator();
        }

        /**
         * Returns an iterator that iterates over all outgoing edges
         *
         * @return Unmodifiable Iterator of Edge for all outgoing edges
         */
        public Iterator<Edge> outIterator() {
            checkRep();
            return Collections.unmodifiableSet(out).iterator();
        }

        /**
         * Returns true if the nodes have no edges connected
         *
         * @return true if the nodes have no connected edges, false otherwise
         */
        public boolean isEmpty() {
            checkRep();
            return out.isEmpty() && in.isEmpty();
        }

        /**
         * Removes all connected edges from this node
         *
         * @spec.modifies this
         * @spec.effects this_post will have no edges connected
         */
        public void clear() {
            checkRep();
            in.clear();
            out.clear();
            checkRep();
        }

        /**
         * Throws an AssertionError if the representation invariant of Node is violated.
         */
        private void checkRep() {
            assert (in != null) : "in edge Set cannot be null";
            assert (out != null) : "out edge Set cannot be null";
            assert (!in.contains(null))
                    : "in edge cannot be null";
            assert (!in.contains(null))
                    : "out edge cannot be null";
        }
    }
}