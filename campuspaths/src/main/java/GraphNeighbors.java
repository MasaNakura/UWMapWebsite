//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package shortestPath;

import java.util.Objects;
import java.util.Set;

public interface GraphNeighbors<Node, Label> {
    Set<GraphNeighbors.Edge<Node, Label>> outgoingEdges(Node var1);

    public static class Edge<Node, Label> {
        private final Node source;
        private final Node dest;
        private final Label label;

        public Edge(Node source, Node dest, Label label) {
            this.source = source;
            this.dest = dest;
            this.label = label;
        }

        public Node getSource() {
            return this.source;
        }

        public Node getDest() {
            return this.dest;
        }

        public Label getLabel() {
            return this.label;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.source, this.dest, this.label});
        }
    }
}
