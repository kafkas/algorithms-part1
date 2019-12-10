import java.util.ArrayList;
import java.util.TreeSet;
import java.util.NoSuchElementException;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class PointSET {
    private final TreeSet<Point2D> treeSet;

    /**
     * Constructs an empty set of points.
     */

    public PointSET() {
        treeSet = new TreeSet<>();
    }

    public boolean isEmpty() {
        return treeSet.isEmpty();
    }

    public int size() {
        return treeSet.size();
    }

    /**
     * Adds the specified point to the set, if it is not already in the set.
     */

    public void insert(Point2D p) {
        validateNonNullArgument(p);
        if (!contains(p))
            treeSet.add(p);
    }

    public boolean contains(Point2D p) {
        validateNonNullArgument(p);
        return treeSet.contains(p);
    }

    /**
     * Draws all points to standard draw.
     */

    public void draw() {
        for (Point2D point : treeSet)
            point.draw();
    }

    /**
     * @return An iterable containing all the points that are inside or on the boundary of
     *         {@code rect}.
     */

    public Iterable<Point2D> range(RectHV rect) {
        validateNonNullArgument(rect);
        ArrayList<Point2D> rangePoints = new ArrayList<>();
        Point2D bottomLeft = new Point2D(rect.xmin(), rect.ymin());
        for (Point2D p : treeSet.tailSet(bottomLeft, true))
            if (p.x() <= rect.xmax() && p.y() <= rect.ymax() && p.x() >= rect.xmin()
                    && p.y() >= rect.ymin())
                rangePoints.add(p);
        return rangePoints;
    }

    /**
     * @return A nearest point in the set to the specified point or null if the set is empty.
     */

    public Point2D nearest(Point2D p) {
        validateNonNullArgument(p);
        Point2D nearest = null;
        for (Point2D point : treeSet)
            if (nearest == null || point.distanceSquaredTo(p) < nearest.distanceSquaredTo(p))
                nearest = point;
        return nearest;
    }

    private <T> void validateNonNullArgument(T arg) {
        if (arg == null)
            throw new IllegalArgumentException();
    }

    public static void main(String[] args) {
        PointSET set = new PointSET();
        for (int i = 1; i <= 100; i++) {
            double x = StdRandom.uniform(0.0, 1.0);
            double y = StdRandom.uniform(0.0, 1.0);
            Point2D point = new Point2D(x, y);
            set.insert(point);
        }
        set.draw();
        Point2D testPoint = new Point2D(0.52, 0.65);
        StdOut.println("set.isEmpty() == " + set.isEmpty());
        StdOut.println("set.size() == " + set.size());
        StdOut.println("set.contains(" + testPoint + ") == " + set.contains(testPoint));
        RectHV testRange = new RectHV(0.3, 0.3, 0.6, 0.4);
        testRange.draw();
        StdOut.println("Points in test range: ");
        for (Point2D point : set.range(testRange))
            StdOut.println(point);
        StdOut.println("set.nearest(" + testPoint + ") == " + set.nearest(testPoint));
    }
}
