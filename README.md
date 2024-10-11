# Deque

## Overview
The Deque implementation provides an efficient way to store and manipulate a collection of elements from both ends. This project includes two implementations: `ArrayDeque` and `LinkedListDeque`. 

- **ArrayDeque**: Utilizes a dynamic array that automatically resizes as needed, offering constant-time performance for adding and removing elements, with amortized constant-time complexity for resizing operations.
- **LinkedListDeque**: Uses a doubly linked list, allowing for efficient insertions and deletions at both ends without the need for resizing.

## Method Signatures
- `public void addFirst(T item)`: Adds an item to the front of the deque.
- `public void addLast(T item)`: Adds an item to the back of the deque.
- `public boolean isEmpty()`: Returns true if the deque is empty.
- `public int size()`: Returns the number of items in the deque.
- `public void printDeque()`: Prints items from front to back.
- `public T removeFirst()`: Removes and returns the item at the front of the deque.
- `public T removeLast()`: Removes and returns the item at the back of the deque.
- `public T get(int index)`: Gets the item at the specified index.
- `public T getRecursive(int index)`: Gets the item at the specified index using recursion (for `LinkedListDeque`).
- `public Iterator<T> iterator()`: Returns an iterator over the items in the deque.
- `public boolean equals(Object o)`: Checks if this deque is equal to another.
