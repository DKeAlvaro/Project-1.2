package project12.group19.api.adt.collection;

import java.util.Optional;

public interface Heap<T> extends Iterable<T> {
    int size();
    default boolean isEmpty() {
        return size() == 0;
    }
    Optional<T> remove();
    Heap<T> add(T value);
}
