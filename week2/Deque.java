import java.util.Iterator;
import java.util.NoSuchElementException;
import edu.princeton.cs.algs4.StdOut;

/**
 * Doubly linked list implementation of double-ended queue (deque) data structure.
 */

public class Deque<Item> implements Iterable<Item> {
    private int itemCount;
    private Node firstNode;
    private Node lastNode;

    private class Node {
        public Item item;
        public Node next;
        public Node prev;

        public Node(Item item) {
            this.item = item;
        }
    }

    /**
     * Constructs an empty deque.
     */

    public Deque() {
    }

    /**
     * Adds the specified item to the front of the queue.
     */

    public void addFirst(Item item) {
        validateItemToBeAdded(item);
        Node oldFirstNode = firstNode;
        firstNode = new Node(item);
        if (isEmpty())
            lastNode = firstNode;
        else
            linkNewFirstNodeToOld(oldFirstNode);
        itemCount++;
    }

    private void linkNewFirstNodeToOld(Node oldFirstNode) {
        firstNode.next = oldFirstNode;
        oldFirstNode.prev = firstNode;
    }

    /**
     * Adds the specified item to the back of the queue.
     */

    public void addLast(Item item) {
        validateItemToBeAdded(item);
        Node oldLastNode = lastNode;
        lastNode = new Node(item);
        if (isEmpty())
            firstNode = lastNode;
        else
            linkNewLastNodeToOld(oldLastNode);
        itemCount++;
    }

    private void validateItemToBeAdded(Item item) {
        if (item == null)
            throw new IllegalArgumentException("Cannot add null item.");
    }

    private void linkNewLastNodeToOld(Node oldLastNode) {
        oldLastNode.next = lastNode;
        lastNode.prev = oldLastNode;
    }

    /**
     * Removes the item from the front and returns it.
     */

    public Item removeFirst() {
        validateRemoveOperation();
        Item item = firstNode.item;
        if (size() == 1)
            nullifyNodePointers();
        else
            removeFirstNode();
        itemCount--;
        return item;
    }

    private void removeFirstNode() {
        firstNode = firstNode.next;
        firstNode.prev = null;
    }

    /**
     * Removes the item from the back and returns it.
     */

    public Item removeLast() {
        validateRemoveOperation();
        Item item = lastNode.item;
        if (size() == 1)
            nullifyNodePointers();
        else
            removeLastNode();
        itemCount--;
        return item;
    }

    private void validateRemoveOperation() {
        if (isEmpty())
            throw new NoSuchElementException("The deque is already empty.");
    }

    public int size() {
        return itemCount;
    }

    public boolean isEmpty() {
        return itemCount == 0;
    }

    private void nullifyNodePointers() {
        firstNode = null;
        lastNode = null;
    }

    private void removeLastNode() {
        lastNode = lastNode.prev;
        lastNode.next = null;
    }

    /**
     * @return An iterator over items in order from front to back.
     */

    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<Item> {
        private Node current = firstNode;

        public boolean hasNext() {
            return current != null;
        }

        public Item next() {
            if (!hasNext())
                throw new NoSuchElementException("No more items to return.");
            Item item = current.item;
            current = current.next;
            return item;
        }

        public void remove() {
            throw new UnsupportedOperationException(
                    "Remove operation in iterator is forbidden to the client.");
        }
    }

    public static void main(String[] args) {
        Deque<Integer> deque = new Deque<>();
        deque.addLast(3);
        deque.addLast(4);
        deque.addFirst(2);
        deque.addFirst(1);
        deque.addLast(5);
        deque.removeFirst();
        deque.removeLast();
        for (Integer i : deque)
            StdOut.println("op. " + i);
    }
}
