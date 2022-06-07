package project12.group19.api.path;

import project12.group19.api.adt.graph.InspectableGraph;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;

public interface PathFinder<V> {
    /**
     * Finds and returns a path from source node to a node for which
     * {@code destinationMatcher} will return true. Since for on-demand
     * built graphs the target node is not necessarily known at the time
     * of start, it is not expressed directly, but rather through a
     * matcher.
     *
     * @param source Source node to start from.
     * @param destinationMatcher Matcher to use to find whether target
     * node has been reached.
     * @param distanceEvaluator Function to evaluate distance between
     * two nodes.
     * @param fitnessPredictor Function that returns score for
     * particular node regarding it's expected benefit. <b>Please note
     * that this function gives smaller values for more prospective
     * nodes</b>, resulting in zero for the best (final) one and grater
     * values for less beneficial.
     *
     * @return An optional with list of nodes forming a path from source
     * to end
     * @param <T> Type of value associated with nodes.
     * @param <E> Type of value associated with edges.
     */
    <T, E> Optional<List<Segment<T, E, V>>> find(
            InspectableGraph.Node<T, E> source,
            Predicate<InspectableGraph.Node<T, E>> destinationMatcher,
            ToDoubleBiFunction<InspectableGraph.Node<T, E>, InspectableGraph.Node<T, E>> distanceEvaluator,
            ToDoubleFunction<InspectableGraph.Node<T, E>> fitnessPredictor
    );
}
