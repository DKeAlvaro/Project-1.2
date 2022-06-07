package project12.group19.api.adt.graph;

import java.util.Set;

public interface InspectableGraph<T, E> {
    Set<Node<T, E>> getNodes();

    interface Edge<T, E> {
        Node<T, E> getSourceNode();
        Node<T, E> getDestinationNode();
        E getAssociatedValue();
    }

    interface Node<T, E> {
        Set<Edge<T, E>> getEdges();
        T getAssociatedValue();
    }
}
