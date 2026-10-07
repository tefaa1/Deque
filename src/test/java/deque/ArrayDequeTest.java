package deque;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Runs the shared {@link DequeContractTest} against {@link ArrayDeque}, plus resizing behaviour. */
class ArrayDequeTest extends DequeContractTest {

    @Override
    protected <T> Deque<T> newDeque() {
        return new ArrayDeque<>();
    }

    @Test
    void startsWithCapacityEight() {
        assertEquals(8, new ArrayDeque<Integer>().capacity());
    }

    @Test
    void doublesCapacityWhenFull() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        for (int i = 0; i < 8; i++) {
            deque.addLast(i);
        }
        assertEquals(8, deque.capacity());

        deque.addFirst(-1);
        assertEquals(16, deque.capacity());
        for (int i = -1; i < 8; i++) {
            assertEquals(i, deque.get(i + 1));
        }
    }

    @Test
    void resizePreservesOrderWhenItemsWrapAroundTheArray() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        // Front items land at the end of the array and back items at its start.
        for (int i = 0; i < 4; i++) {
            deque.addFirst(3 - i);
            deque.addLast(4 + i);
        }
        assertEquals(8, deque.capacity());

        deque.addLast(8);
        assertEquals(16, deque.capacity());
        for (int i = 0; i <= 8; i++) {
            assertEquals(i, deque.get(i));
        }
    }

    @Test
    void keepsUsageAboveTwentyFivePercentWhileShrinking() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        for (int i = 0; i < 10_000; i++) {
            deque.addLast(i);
        }

        for (int i = 0; i < 10_000; i++) {
            if (i % 2 == 0) {
                deque.removeFirst();
            } else {
                deque.removeLast();
            }
            int capacity = deque.capacity();
            assertTrue(capacity < 16 || deque.size() * 4 >= capacity,
                    "size " + deque.size() + " in capacity " + capacity);
        }
        assertEquals(8, deque.capacity());
    }

    @Test
    void capacityIsAlwaysAPowerOfTwo() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        for (int i = 0; i < 5_000; i++) {
            deque.addFirst(i);
            assertEquals(1, Integer.bitCount(deque.capacity()));
        }
        while (!deque.isEmpty()) {
            deque.removeLast();
            assertEquals(1, Integer.bitCount(deque.capacity()));
        }
    }

    @Test
    void doesNotResizeBackAndForthAroundAResizeBoundary() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        for (int i = 0; i < 16; i++) {
            deque.addLast(i);
        }
        assertEquals(16, deque.capacity());
        deque.addLast(16);
        assertEquals(32, deque.capacity());

        // Hovering just below the size that triggered growth must not shrink the array again;
        // otherwise every few operations would pay for an O(n) copy.
        for (int round = 0; round < 100; round++) {
            for (int i = 0; i < 4; i++) {
                deque.removeFirst();
                assertEquals(32, deque.capacity());
            }
            for (int i = 0; i < 4; i++) {
                deque.addLast(i);
                assertEquals(32, deque.capacity());
            }
        }
    }
}
