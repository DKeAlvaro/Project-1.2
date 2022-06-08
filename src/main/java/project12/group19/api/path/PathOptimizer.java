package project12.group19.api.path;

import java.util.List;

/**
 * An interface for a function that can analyze path and produce a new
 * one with reduced amount of steps.
 *
 * @param <V>
 */
public interface PathOptimizer<V> {
    <T, E> List<Segment<T, E, V>> optimize(List<Segment<T, E, V>> path);
}
