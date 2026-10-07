package deque;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Behaviour every {@link Deque} implementation must satisfy. Each implementation's
 * test class extends this and supplies {@link #newDeque()}, so both are held to
 * exactly the same specification.
 */
abstract class DequeContractTest {

    /** Returns a new, empty instance of the implementation under test. */
    protected abstract <T> Deque<T> newDeque();

    @SafeVarargs
    private <T> Deque<T> dequeOf(T... items) {
        Deque<T> deque = newDeque();
        for (T item : items) {
            deque.addLast(item);
        }
        return deque;
    }

    @Test
    void newDequeIsEmpty() {
        Deque<String> deque = newDeque();
        assertTrue(deque.isEmpty());
        assertEquals(0, deque.size());
    }

    @Test
    void addFirstAndAddLastKeepFrontToBackOrder() {
        Deque<String> deque = newDeque();
        deque.addLast("middle");
        deque.addFirst("front");
        deque.addLast("back");

        assertEquals(3, deque.size());
        assertFalse(deque.isEmpty());
        assertEquals("front", deque.get(0));
        assertEquals("middle", deque.get(1));
        assertEquals("back", deque.get(2));
    }

    @Test
    void removeFirstAndRemoveLastReturnItemsFromTheirEnds() {
        Deque<Integer> deque = dequeOf(1, 2, 3, 4);

        assertEquals(1, deque.removeFirst());
        assertEquals(4, deque.removeLast());
        assertEquals(2, deque.removeFirst());
        assertEquals(3, deque.removeLast());
        assertTrue(deque.isEmpty());
    }

    @Test
    void removingFromEmptyDequeReturnsNullAndKeepsSizeZero() {
        Deque<Integer> deque = dequeOf(3);
        deque.removeLast();

        assertNull(deque.removeFirst());
        assertNull(deque.removeLast());
        assertEquals(0, deque.size());

        deque.addFirst(7);
        assertEquals(1, deque.size());
        assertEquals(7, deque.get(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 3, 4, Integer.MAX_VALUE})
    void getOutOfBoundsReturnsNull(int index) {
        assertNull(dequeOf(10, 20, 30).get(index));
    }

    @Test
    void getOnEmptyDequeReturnsNull() {
        assertNull(newDeque().get(0));
    }

    @Test
    void storesNullItems() {
        Deque<String> deque = dequeOf("a", null, "c");

        assertEquals(3, deque.size());
        assertNull(deque.get(1));
        assertEquals("[a, null, c]", deque.toString());
    }

    @Test
    void worksWithDifferentElementTypes() {
        Deque<String> strings = newDeque();
        Deque<Double> doubles = newDeque();
        Deque<Boolean> booleans = newDeque();

        strings.addFirst("string");
        doubles.addFirst(3.14159);
        booleans.addFirst(true);

        assertEquals("string", strings.removeFirst());
        assertEquals(3.14159, doubles.removeFirst());
        assertEquals(true, booleans.removeFirst());
    }

    @Test
    void handlesOneMillionItemsInOrder() {
        int n = 1_000_000;
        Deque<Integer> deque = newDeque();
        for (int i = 0; i < n; i++) {
            deque.addLast(i);
        }
        assertEquals(n, deque.size());

        for (int i = 0; i < n / 2; i++) {
            assertEquals(i, deque.removeFirst());
        }
        for (int i = n - 1; i >= n / 2; i--) {
            assertEquals(i, deque.removeLast());
        }
        assertTrue(deque.isEmpty());
    }

    @Test
    void iteratorVisitsItemsFrontToBack() {
        Deque<Integer> deque = newDeque();
        for (int i = 0; i < 100; i++) {
            deque.addLast(i);
            deque.addFirst(-i - 1);
        }

        int expected = -100;
        for (int item : deque) {
            assertEquals(expected++, item);
        }
        assertEquals(100, expected);
    }

    @Test
    void exhaustedIteratorThrowsNoSuchElementException() {
        Iterator<Integer> it = dequeOf(1, 2).iterator();
        it.next();
        it.next();

        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);
        assertThrows(NoSuchElementException.class, () -> newDeque().iterator().next());
    }

    @Test
    void iteratorFailsFastOnConcurrentModification() {
        Deque<Integer> deque = dequeOf(1, 2, 3);
        Iterator<Integer> it = deque.iterator();
        it.next();

        deque.addLast(4);
        assertThrows(ConcurrentModificationException.class, it::next);

        Iterator<Integer> again = deque.iterator();
        deque.removeFirst();
        assertThrows(ConcurrentModificationException.class, again::next);
    }

    @Test
    void equalsComparesItemsInOrder() {
        Deque<Integer> deque = dequeOf(1, 2, 3);

        assertEquals(deque, deque);
        assertEquals(dequeOf(1, 2, 3), deque);
        assertNotEquals(dequeOf(3, 2, 1), deque);
        assertNotEquals(dequeOf(1, 2), deque);
        assertNotEquals(dequeOf(1, 2, 3, 4), deque);
        assertNotEquals(null, deque);
        assertNotEquals(List.of(1, 2, 3), deque);
    }

    @Test
    void equalsHandlesNullItems() {
        assertEquals(dequeOf("a", null), dequeOf("a", null));
        assertNotEquals(dequeOf("a", null), dequeOf("a", "b"));
        assertNotEquals(dequeOf("a", "b"), dequeOf("a", null));
    }

    @Test
    void equalsAndHashCodeWorkAcrossImplementations() {
        Deque<Integer> arrayDeque = new ArrayDeque<>();
        Deque<Integer> linkedDeque = new LinkedListDeque<>();
        Deque<Integer> deque = newDeque();
        for (int i = 0; i < 50; i++) {
            arrayDeque.addLast(i);
            linkedDeque.addLast(i);
            deque.addLast(i);
        }

        assertEquals(arrayDeque, deque);
        assertEquals(deque, arrayDeque);
        assertEquals(linkedDeque, deque);
        assertEquals(deque, linkedDeque);
        assertEquals(arrayDeque.hashCode(), deque.hashCode());
        assertEquals(linkedDeque.hashCode(), deque.hashCode());
    }

    @Test
    void equalDequesHaveEqualHashCodes() {
        assertEquals(dequeOf(1, null, 3).hashCode(), dequeOf(1, null, 3).hashCode());
        assertEquals(List.of(1, 2, 3).hashCode(), dequeOf(1, 2, 3).hashCode());
    }

    @Test
    void toStringListsItemsFrontToBack() {
        assertEquals("[]", newDeque().toString());
        assertEquals("[1, 2, 3]", dequeOf(1, 2, 3).toString());
    }

    @Test
    void printDequePrintsItemsSeparatedBySpaces() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out, true));
        try {
            dequeOf("front", "middle", "back").printDeque();
        } finally {
            System.setOut(original);
        }
        assertEquals("front middle back" + System.lineSeparator(), out.toString());
    }

    /**
     * Applies a long random sequence of operations to both the deque under test and
     * {@link java.util.LinkedList}, checking that every result and the size always match.
     * The mix alternates between growing and shrinking phases so the deque repeatedly
     * passes through empty, wraps around, grows and shrinks.
     */
    @Test
    void matchesJavaLinkedListUnderRandomOperations() {
        Random random = new Random(61);
        Deque<Integer> deque = newDeque();
        LinkedList<Integer> reference = new LinkedList<>();

        for (int step = 0; step < 200_000; step++) {
            boolean growing = (step / 20_000) % 2 == 0;
            double roll = random.nextDouble();
            String context = "step " + step;

            if (roll < 0.2) {
                int index = random.nextInt(reference.size() + 2) - 1;
                Integer expected = index >= 0 && index < reference.size() ? reference.get(index) : null;
                assertEquals(expected, deque.get(index), context + ": get(" + index + ")");
            } else if (roll < (growing ? 0.68 : 0.52)) {
                if (random.nextBoolean()) {
                    deque.addFirst(step);
                    reference.addFirst(step);
                } else {
                    deque.addLast(step);
                    reference.addLast(step);
                }
            } else if (random.nextBoolean()) {
                assertEquals(reference.pollFirst(), deque.removeFirst(), context + ": removeFirst");
            } else {
                assertEquals(reference.pollLast(), deque.removeLast(), context + ": removeLast");
            }
            assertEquals(reference.size(), deque.size(), context + ": size");
        }
        assertEquals(reference, toList(deque));
    }

    private static <T> List<T> toList(Deque<T> deque) {
        List<T> list = new LinkedList<>();
        deque.forEach(list::add);
        return list;
    }
}
