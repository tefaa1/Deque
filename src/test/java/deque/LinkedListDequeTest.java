package deque;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Runs the shared {@link DequeContractTest} against {@link LinkedListDeque}, plus {@code getRecursive}. */
class LinkedListDequeTest extends DequeContractTest {

    @Override
    protected <T> Deque<T> newDeque() {
        return new LinkedListDeque<>();
    }

    @Test
    void getRecursiveMatchesGet() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        for (int i = 0; i < 1_000; i++) {
            deque.addFirst(i);
        }
        for (int i = 0; i < deque.size(); i++) {
            assertEquals(deque.get(i), deque.getRecursive(i));
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 3, Integer.MAX_VALUE})
    void getRecursiveOutOfBoundsReturnsNull(int index) {
        LinkedListDeque<String> deque = new LinkedListDeque<>();
        deque.addLast("a");
        deque.addLast("b");
        deque.addLast("c");

        assertNull(deque.getRecursive(index));
    }

    @Test
    void getWalksFromTheNearerEnd() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        for (int i = 0; i < 9; i++) {
            deque.addLast(i);
        }
        for (int i = 0; i < 9; i++) {
            assertEquals(i, deque.get(i));
        }
    }
}
