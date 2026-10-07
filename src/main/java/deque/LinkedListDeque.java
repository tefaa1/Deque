package deque;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A {@link Deque} backed by a circular doubly linked list with a single sentinel node.
 *
 * <p>The sentinel's {@code next} is the front and its {@code prev} is the back,
 * so an empty deque is just the sentinel pointing to itself and every insertion
 * or removal is the same pointer splice with no null checks or special cases.
 *
 * <p>{@code addFirst}, {@code addLast}, {@code removeFirst}, {@code removeLast}
 * and {@code size} run in O(1) time. {@code get} runs in O(min(i, n - i)) time
 * by walking from whichever end is closer. Iteration is O(n) overall.
 *
 * @param <T> the type of elements held in this deque
 * @author Mohamed Abdellatif
 */
public class LinkedListDeque<T> extends AbstractDeque<T> {

    private static final class Node<T> {
        T item;
        Node<T> prev;
        Node<T> next;

        Node(T item, Node<T> prev, Node<T> next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    private final Node<T> sentinel;
    private int size;
    /** Incremented on every structural change so iterators can fail fast. */
    private int modCount;

    public LinkedListDeque() {
        sentinel = new Node<>(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
    }

    @Override
    public void addFirst(T item) {
        linkBetween(item, sentinel, sentinel.next);
    }

    @Override
    public void addLast(T item) {
        linkBetween(item, sentinel.prev, sentinel);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T removeFirst() {
        return isEmpty() ? null : unlink(sentinel.next);
    }

    @Override
    public T removeLast() {
        return isEmpty() ? null : unlink(sentinel.prev);
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return nodeAt(index).item;
    }

    /** Same as {@link #get(int)}, but implemented recursively. Always walks from the front. */
    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return getRecursive(sentinel.next, index);
    }

    private T getRecursive(Node<T> node, int index) {
        if (index == 0) {
            return node.item;
        }
        return getRecursive(node.next, index - 1);
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private void linkBetween(T item, Node<T> prev, Node<T> next) {
        Node<T> node = new Node<>(item, prev, next);
        prev.next = node;
        next.prev = node;
        size++;
        modCount++;
    }

    private T unlink(Node<T> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        T item = node.item;
        // Clear references so the removed node does not keep other nodes or the item alive.
        node.item = null;
        node.prev = null;
        node.next = null;
        size--;
        modCount++;
        return item;
    }

    /** Returns the node at a valid {@code index}, walking from the nearer end. */
    private Node<T> nodeAt(int index) {
        Node<T> node;
        if (index < size / 2) {
            node = sentinel.next;
            for (int i = 0; i < index; i++) {
                node = node.next;
            }
        } else {
            node = sentinel.prev;
            for (int i = size - 1; i > index; i--) {
                node = node.prev;
            }
        }
        return node;
    }

    /** Walks the nodes directly, so a full iteration is O(n) rather than O(n^2) via {@code get}. */
    private final class LinkedListDequeIterator implements Iterator<T> {
        private Node<T> next = sentinel.next;
        private final int expectedModCount = modCount;

        @Override
        public boolean hasNext() {
            return next != sentinel;
        }

        @Override
        public T next() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T item = next.item;
            next = next.next;
            return item;
        }
    }
}
