package project12.group19.api.path;

import project12.group19.api.adt.graph.InspectableGraph;

import java.util.function.Function;

/**
 * A path segment between two nodes. Due to the possibility that path
 * finding algorithm may want to attach some extra data (e.g.
 * instructions) to found segments, it has {@link #getAssociatedValue()}
 * method.
 */
public interface Segment<T, E, V> {
    InspectableGraph.Edge<T, E> getEdge();
    default InspectableGraph.Node<T, E> getSource() {
        return getEdge().getSourceNode();
    }
    default InspectableGraph.Node<T, E> getDestination() {
        return getEdge().getDestinationNode();
    }
    V getAssociatedValue();

    default <U> Segment<T, E, U> map(Function<? super V, ? extends U> transformer) {
        return create(getEdge(), transformer.apply(getAssociatedValue()));
    }

    static <T, E, V> Segment<T, E, V> create(InspectableGraph.Edge<T, E> edge, V associatedValue) {
        return new Standard<>(edge, associatedValue);
    }

    static <T, E, V> Segment<T, E, V> create(InspectableGraph.Edge<T, E> edge) {
        return create(edge, null);
    }

    record Standard<T, E, V>(InspectableGraph.Edge<T, E> edge, V associatedValue) implements Segment<T, E, V> {
        @Override
        public InspectableGraph.Edge<T, E> getEdge() {
            return edge;
        }

        @Override
        public V getAssociatedValue() {
            return associatedValue;
        }
    }
}
