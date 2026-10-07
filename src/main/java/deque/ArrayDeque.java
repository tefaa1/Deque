package deque;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A {@link Deque} backed by a resizable circular array.
 *
 * <p>{@code addFirst}, {@code addLast}, {@code removeFirst} and {@code removeLast}
 * run in amortized O(1) time; {@code get} and {@code size} run in O(1) time.
 *
 * <p>Invariants:
 * <ul>
 *   <li>The items occupy {@code size} consecutive slots starting at {@code head},
 *       wrapping around the end of the array.</li>
 *   <li>The capacity is always a power of two (8, 16, 32, ...), so an index wraps
 *       with a bit mask instead of a modulo.</li>
 *   <li>The array doubles when full and halves when fewer than a quarter of its
 *       slots are used (never below 8), so memory stays proportional to
 *       {@code size} without resizing back and forth on alternating add/remove.</li>
 * </ul>
 *
 * @param <T> the type of elements held in this deque
 * @author Mohamed Abdellatif
 */
public class ArrayDeque<T> extends AbstractDeque<T> {

    private static final int MIN_CAPACITY = 8;
    private static final int SHRINK_THRESHOLD = 16;

    private T[] items;
    private int head;
    private int size;
    /** Incremented on every structural change so iterators can fail fast. */
    private int modCount;

    @SuppressWarnings("unchecked")
    public ArrayDeque() {
        items = (T[]) new Object[MIN_CAPACITY];
    }

    @Override
    public void addFirst(T item) {
        growIfFull();
        head = wrap(head - 1);
        items[head] = item;
        size++;
        modCount++;
    }

    @Override
    public void addLast(T item) {
        growIfFull();
        items[slot(size)] = item;
        size++;
        modCount++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T item = items[head];
        items[head] = null; // let the GC reclaim the item
        head = wrap(head + 1);
        size--;
        modCount++;
        shrinkIfSparse();
        return item;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        int last = slot(size - 1);
        T item = items[last];
        items[last] = null;
        size--;
        modCount++;
        shrinkIfSparse();
        return item;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return items[slot(index)];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    /** Current length of the backing array. Exposed for tests. */
    int capacity() {
        return items.length;
    }

    /** Maps a logical index (0 = front) to its slot in the backing array. */
    private int slot(int index) {
        return wrap(head + index);
    }

    /** Wraps {@code i} into {@code [0, capacity)}; also correct for {@code i == -1}. */
    private int wrap(int i) {
        return i & (items.length - 1);
    }

    private void growIfFull() {
        if (size == items.length) {
            resize(items.length * 2);
        }
    }

    private void shrinkIfSparse() {
        if (items.length >= SHRINK_THRESHOLD && size < items.length / 4) {
            resize(items.length / 2);
        }
    }

    /** Copies the items into a new array of {@code capacity} slots, front at index 0. */
    private void resize(int capacity) {
        @SuppressWarnings("unchecked")
        T[] resized = (T[]) new Object[capacity];
        int untilEnd = Math.min(size, items.length - head);
        System.arraycopy(items, head, resized, 0, untilEnd);
        System.arraycopy(items, 0, resized, untilEnd, size - untilEnd);
        items = resized;
        head = 0;
    }

    private final class ArrayDequeIterator implements Iterator<T> {
        private int cursor;
        private final int expectedModCount = modCount;

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        public T next() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return items[slot(cursor++)];
        }
    }
}
