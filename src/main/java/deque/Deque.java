package deque;

import java.util.StringJoiner;

/**
 * A double-ended queue: a linear collection that supports insertion and removal
 * at both the front and the back.
 *
 * <p>Like {@link java.util.Deque#pollFirst()}, removal and lookup methods return
 * {@code null} instead of throwing when the requested item does not exist.
 * {@code null} elements are permitted.
 *
 * @param <T> the type of elements held in this deque
 * @author Mohamed Abdellatif
 */
public interface Deque<T> extends Iterable<T> {

    /** Inserts {@code item} at the front of this deque. */
    void addFirst(T item);

    /** Inserts {@code item} at the back of this deque. */
    void addLast(T item);

    /** Returns {@code true} if this deque contains no items. */
    default boolean isEmpty() {
        return size() == 0;
    }

    /** Returns the number of items in this deque. */
    int size();

    /** Prints the items from front to back, separated by a space, followed by a newline. */
    default void printDeque() {
        StringJoiner line = new StringJoiner(" ");
        for (T item : this) {
            line.add(String.valueOf(item));
        }
        System.out.println(line);
    }

    /** Removes and returns the item at the front, or {@code null} if this deque is empty. */
    T removeFirst();

    /** Removes and returns the item at the back, or {@code null} if this deque is empty. */
    T removeLast();

    /**
     * Returns the item at {@code index}, where 0 is the front,
     * or {@code null} if {@code index} is out of bounds.
     */
    T get(int index);
}
