/**
 @Author mohamed abdellatif
 */
package deque;

import java.util.Iterator;


public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node {
        Node() {
        }

        Node(T val, Node nex, Node pre) {
            value = val;
            next = nex;
            prev = pre;
        }

        T value;
        Node next;
        Node prev;
    }

    private Node sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new Node();
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    @Override
    public void addFirst(T val) {
        Node first = new Node(val, sentinel.next, sentinel);
        sentinel.next.prev = first;
        sentinel.next = first;
        size++;
    }

    @Override
    public void addLast(T val) {
        Node last = new Node(val, sentinel, sentinel.prev);
        sentinel.prev.next = last;
        sentinel.prev = last;
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node temp = sentinel.next;
        while (temp != sentinel) {
            System.out.print(temp.value + " ");
            temp = temp.next;
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T temp = sentinel.next.value;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size--;
        return temp;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T temp = sentinel.prev.value;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size--;
        return temp;
    }

    @Override
    public T get(int index) {
        if (index >= size) {
            return null;
        }
        Node temp = sentinel.next;
        while (index > 0) {
            temp = temp.next;
            index--;
        }
        return temp.value;
    }

    /**
     * helper method
     */
    private T rec(int index, Node temp) {
        if (index == 0) {
            return temp.value;
        }
        return rec(index - 1, temp.next);
    }

    public T getRecursive(int index) {
        if (index >= size) {
            return null;
        }
        return rec(index, sentinel.next);
    }

    private class LinkedListDequeIterator<T> implements Iterator<T> {
        int pos;

        LinkedListDequeIterator() {
            pos = 0;
        }

        public boolean hasNext() {
            return pos < size();
        }

        public T next() {
            T x = (T) get(pos);
            pos++;
            return x;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator<>();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Deque) {
            Deque<T> anotherDeque = (Deque) o;
            if (anotherDeque.size() != size()) {
                return false;
            }
            for (int i = 0; i < size(); i++) {
                if (!get(i).equals(anotherDeque.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

//    @Override
//    public String toString() {
//        if (size() == 0) return "{}";
//        StringBuilder s = new StringBuilder("{");
//        int cnt = 1;
//        for (T it : this) {
//            s.append(it);
//            if (cnt < size())
//                s.append(", ");
//            cnt++;
//        }
//        s.append("}");
//        return s.toString();
//    }
}
