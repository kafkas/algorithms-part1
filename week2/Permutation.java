import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdRandom;

public class Permutation {
    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);
        int i = 0;
        RandomizedQueue<String> rq = new RandomizedQueue<>();
        while (!StdIn.isEmpty()) {
            String str = StdIn.readString();
            i++;
            if (rq.size() < k)
                rq.enqueue(str);
            else if (shouldKeepNewItem(k, i)) {
                rq.dequeue();
                rq.enqueue(str);
            }
        }
        for (String str : rq)
            System.out.println(str);
    }

    /**
     * Returns true with probability k / i and false otherwise. This condition comes from reservoir
     * sampling, which we need to use to have k amount of maximum space in the queue.
     * 
     * @see https://www.geeksforgeeks.org/reservoir-sampling/
     */

    private static boolean shouldKeepNewItem(int k, int i) {
        return (StdRandom.uniform() < (double) k / i);
    }
}
