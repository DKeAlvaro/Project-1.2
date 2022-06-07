package project12.group19.api.adt.graph;

import java.util.Optional;

public interface MutableGraph<T, E> extends InspectableGraph<T, E> {
    Node<T, E> addNode(T value);
    Optional<Node<T, E>> removeNode();
    Edge<T, E> addEdge(Node<T, E> left, Node<T, E> right, E value);
    Optional<Edge<T, E>> removeEdge(Node<T, E> left, Node<T, E> right);
}
