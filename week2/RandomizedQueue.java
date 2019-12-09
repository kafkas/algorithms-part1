import java.util.Iterator;
import java.util.NoSuchElementException;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

/**
 * Resizing array implementation of randomized queue data structure.
 */

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] itemsArray;
    private int itemCount;

    /**
     * Constructs an empty randomized queue
     */

    public RandomizedQueue() {
        itemsArray = (Item[]) new Object[1];
    }

    public boolean isEmpty() {
        return itemCount == 0;
    }

    public int size() {
        return itemCount;
    }

    /**
     * Adds the item to the queue.
     */

    public void enqueue(Item item) {
        validateItemToBeEnqueued(item);
        if (isItemCountAtMaxThreshold())
            resize(2 * itemCount);
        putItemIntoFirstAvailableIndex(item);
        itemCount++;
        swapLastItemWithARandomItem();
    }

    private void validateItemToBeEnqueued(Item item) {
        if (item == null)
            throw new IllegalArgumentException("enqueue() argument cannot be null.");
    }

    private void resize(int newSize) {
        Item[] temp = (Item[]) new Object[newSize];
        for (int i = 0; i < itemCount; i++)
            temp[i] = itemsArray[i];
        itemsArray = temp;
    }

    private void putItemIntoFirstAvailableIndex(Item item) {
        itemsArray[itemCount] = item;
    }

    private void swapLastItemWithARandomItem() {
        swap(itemCount - 1, getARandomItemIndex());
    }

    private void swap(int i, int j) {
        Item temp = itemsArray[i];
        itemsArray[i] = itemsArray[j];
        itemsArray[j] = temp;
    }

    /**
     * Removes and returns a random item from the queue.
     */

    public Item dequeue() {
        validateDequeueOperation();
        Item item = deleteAndReturnLastItem();
        itemCount--;
        if (isItemCountAtMinThreshold())
            resize(itemsArray.length / 2);
        return item;
    }

    private void validateDequeueOperation() {
        if (isEmpty())
            throw new NoSuchElementException("The randomized queue is already empty.");
    }

    private Item deleteAndReturnLastItem() {
        Item lastItem = itemsArray[itemCount - 1];
        itemsArray[itemCount - 1] = null;
        return lastItem;
    }

    private boolean isItemCountAtMaxThreshold() {
        return itemCount == itemsArray.length;
    }

    private boolean isItemCountAtMinThreshold() {
        return itemCount > 0 && itemCount == itemsArray.length / 4;
    }

    /**
     * @return A random item in the queue.
     */

    public Item sample() {
        validateSampleOperation();
        return itemsArray[getARandomItemIndex()];
    }

    private void validateSampleOperation() {
        if (isEmpty())
            throw new NoSuchElementException("The randomized queue is empty.");
    }

    private int getARandomItemIndex() {
        return StdRandom.uniform(0, itemCount);
    }

    /**
     * @return An independent iterator over items in random order.
     */

    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }

    private class RandomizedQueueIterator implements Iterator<Item> {
        private int currentIndex;
        private int[] order;

        RandomizedQueueIterator() {
            order = StdRandom.permutation(itemCount);
        }

        public boolean hasNext() {
            return currentIndex < itemCount;
        }

        public Item next() {
            if (!hasNext())
                throw new NoSuchElementException("No more items to return.");
            Item item = itemsArray[order[currentIndex++]];
            return item;
        }

        public void remove() {
            throw new UnsupportedOperationException(
                    "Remove operation in iterator is forbidden to the client.");
        }
    }

    public static void main(String[] args) {
        RandomizedQueue<Integer> q = new RandomizedQueue<>();
        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        q.enqueue(4);
        q.enqueue(5);
        q.enqueue(5);
        q.enqueue(6);
        q.dequeue();
        for (int n : q)
            StdOut.println("n == " + n);
    }
}
