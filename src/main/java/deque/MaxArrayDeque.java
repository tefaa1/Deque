package deque;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;

/**
 * An {@link ArrayDeque} that can also report its maximum item, according to a
 * default {@link Comparator} supplied at construction or one supplied per call.
 *
 * @param <T> the type of elements held in this deque
 * @author Mohamed Abdellatif
 */
public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private final Comparator<? super T> comparator;

    /**
     * Creates an empty deque whose {@link #max()} uses {@code comparator}.
     *
     * @throws NullPointerException if {@code comparator} is null
     */
    public MaxArrayDeque(Comparator<? super T> comparator) {
        this.comparator = Objects.requireNonNull(comparator, "comparator");
    }

    /** Returns the maximum item by the default comparator, or {@code null} if empty. O(n). */
    public T max() {
        return max(comparator);
    }

    /**
     * Returns the maximum item by {@code comparator}, or {@code null} if empty. When several
     * items tie for the maximum, the one closest to the front is returned. O(n).
     *
     * @throws NullPointerException if {@code comparator} is null
     */
    public T max(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator, "comparator");
        Iterator<T> items = iterator();
        if (!items.hasNext()) {
            return null;
        }
        T max = items.next();
        while (items.hasNext()) {
            T item = items.next();
            if (comparator.compare(item, max) > 0) {
                max = item;
            }
        }
        return max;
    }
}
