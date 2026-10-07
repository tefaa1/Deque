package deque;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Comparator;
import org.junit.jupiter.api.Test;

/** Runs the shared {@link DequeContractTest} against {@link MaxArrayDeque}, plus {@code max}. */
class MaxArrayDequeTest extends DequeContractTest {

    @Override
    protected <T> Deque<T> newDeque() {
        return new MaxArrayDeque<T>((a, b) -> 0);
    }

    @Test
    void maxOfEmptyDequeIsNull() {
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(Comparator.naturalOrder());
        assertNull(deque.max());
        assertNull(deque.max(Comparator.reverseOrder()));
    }

    @Test
    void maxUsesTheDefaultComparator() {
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(Comparator.naturalOrder());
        for (int item : new int[] {4, -2, 17, 9, 17, 0}) {
            deque.addLast(item);
        }
        assertEquals(17, deque.max());
    }

    @Test
    void maxWithComparatorOverridesTheDefault() {
        MaxArrayDeque<String> deque = new MaxArrayDeque<>(Comparator.naturalOrder());
        deque.addLast("pear");
        deque.addLast("fig");
        deque.addLast("banana");
        deque.addFirst("kiwi");

        assertEquals("pear", deque.max());
        assertEquals("banana", deque.max(Comparator.comparingInt(String::length)));
        assertEquals("banana", deque.max(Comparator.reverseOrder()));
    }

    @Test
    void maxReturnsTheFrontmostItemWhenSeveralTie() {
        MaxArrayDeque<String> deque = new MaxArrayDeque<>(Comparator.comparingInt(String::length));
        String first = new String("abc");
        deque.addLast("a");
        deque.addLast(first);
        deque.addLast(new String("abc"));

        assertSame(first, deque.max());
    }

    @Test
    void acceptsComparatorOfSupertype() {
        Comparator<Number> byDoubleValue = Comparator.comparingDouble(Number::doubleValue);
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(byDoubleValue);
        deque.addLast(3);
        deque.addLast(11);
        deque.addLast(7);

        assertEquals(11, deque.max());
    }

    @Test
    void rejectsNullComparators() {
        assertThrows(NullPointerException.class, () -> new MaxArrayDeque<Integer>(null));
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(Comparator.naturalOrder());
        assertThrows(NullPointerException.class, () -> deque.max(null));
    }
}
