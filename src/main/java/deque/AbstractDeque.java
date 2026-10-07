package deque;

import java.util.Iterator;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Skeletal {@link Deque} implementation that defines value-based
 * {@code equals}, {@code hashCode} and {@code toString} in terms of iteration,
 * so every implementation compares and prints consistently.
 *
 * <p>Two deques are equal when they hold equal items in the same order,
 * regardless of their implementation: an {@link ArrayDeque} and a
 * {@link LinkedListDeque} with the same contents are equal.
 *
 * @param <T> the type of elements held in this deque
 * @author Mohamed Abdellatif
 */
public abstract class AbstractDeque<T> implements Deque<T> {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Deque<?> other) || size() != other.size()) {
            return false;
        }
        Iterator<?> otherItems = other.iterator();
        for (T item : this) {
            if (!Objects.equals(item, otherItems.next())) {
                return false;
            }
        }
        return true;
    }

    /** Same formula as {@link java.util.List#hashCode()}, consistent with {@link #equals}. */
    @Override
    public int hashCode() {
        int hash = 1;
        for (T item : this) {
            hash = 31 * hash + Objects.hashCode(item);
        }
        return hash;
    }

    /** Returns the items from front to back, e.g. {@code [1, 2, 3]}. */
    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        for (T item : this) {
            joiner.add(String.valueOf(item));
        }
        return joiner.toString();
    }
}
