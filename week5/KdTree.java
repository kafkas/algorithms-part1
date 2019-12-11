import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Queue;

/**
 * A mutable data structure that uses a 2-d tree to implement the same API as PointSET.
 */

public class KdTree {
    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = !VERTICAL;
    private Node root;

    public void insert(Point2D p) {
        validateNonNullArgument(p);
        if (!contains(p))
            root = insert(root, p, VERTICAL, 0.0, 0.0, 1.0, 1.0);
    }

    /**
     * Assumes tree doesn't contain p.
     */

    private Node insert(Node node, Point2D p, boolean direction, double xmin, double ymin,
            double xmax, double ymax) {
        if (node == null)
            return new Node(p, direction, 1, new RectHV(xmin, ymin, xmax, ymax));
        if (node.isDirectionallyLessThan(p)) {
            if (node.isVertical())
                xmin = node.point.x();
            else if (node.isHorizontal())
                ymin = node.point.y();
            node.right = insert(node.right, p, !direction, xmin, ymin, xmax, ymax);
        } else {
            if (node.isVertical())
                xmax = node.point.x();
            else if (node.isHorizontal())
                ymax = node.point.y();
            node.left = insert(node.left, p, !direction, xmin, ymin, xmax, ymax);
        }
        node.count = 1 + size(node.left) + size(node.right);
        return node;
    }

    public boolean contains(Point2D p) {
        validateNonNullArgument(p);
        return get(p, root) != null;
    }

    private Point2D get(Point2D p, Node node) {
        if (node == null)
            return null;
        if (node.point.equals(p))
            return node.point;
        if (node.isDirectionallyLessThan(p))
            return get(p, node.right);
        return get(p, node.left);
    }

    public void draw() {
        draw(root);
    }

    private void draw(Node node) {
        if (node == null)
            return;
        node.draw();
        draw(node.left);
        draw(node.right);
    }

    public Iterable<Point2D> range(RectHV rect) {
        validateNonNullArgument(rect);
        Queue<Point2D> q = new Queue<>();
        checkRange(root, rect, q);
        return q;
    }

    private void checkRange(Node x, RectHV rect, Queue<Point2D> q) {
        if (x == null)
            return;
        if (!x.rect.intersects(rect))
            return;
        checkRange(x.left, rect, q);
        if (rect.contains(x.point))
            q.enqueue(x.point);
        checkRange(x.right, rect, q);
    }

    public Point2D nearest(Point2D point) {
        validateNonNullArgument(point);
        return isEmpty() ? null : nearest(root, root, point).point;
    }

    private Node nearest(Node x, Node nearestNode, Point2D p) {
        if (subtreeMayContainANearerNode(x, nearestNode, p)) {
            if (x.point.equals(p))
                return x;
            if (isCurrentNodeNearerToPointThanNearest(x, nearestNode, p))
                nearestNode = x;
            if (x.isDirectionallyLessThan(p)) {
                nearestNode = nearest(x.right, nearestNode, p);
                nearestNode = nearest(x.left, nearestNode, p);
            } else {
                nearestNode = nearest(x.left, nearestNode, p);
                nearestNode = nearest(x.right, nearestNode, p);
            }
        }
        return nearestNode;
    }

    private boolean subtreeMayContainANearerNode(Node x, Node nearestNode, Point2D p) {
        return (x != null && nearestNode.point.distanceSquaredTo(p) > x.rect.distanceSquaredTo(p));
    }

    private boolean isCurrentNodeNearerToPointThanNearest(Node x, Node nearestNode, Point2D p) {
        return x.point.distanceSquaredTo(p) < nearestNode.point.distanceSquaredTo(p);
    }

    private <T> void validateNonNullArgument(T arg) {
        if (arg == null)
            throw new IllegalArgumentException();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return size(root);
    }

    private int size(Node node) {
        if (node == null)
            return 0;
        return node.count;
    }

    private class Node {
        public Point2D point;
        public boolean direction;
        public Node left, right;
        public RectHV rect;
        public int count;

        public Node(Point2D point, boolean direction, int count, RectHV rect) {
            this.point = point;
            this.direction = direction;
            this.count = count;
            this.rect = rect;
        }

        public boolean isDirectionallyLessThan(Point2D thatPoint) {
            return directionalDiff(thatPoint) < 0;
        }

        private int directionalDiff(Point2D thatPoint) {
            if (isVertical())
                return point.x() < thatPoint.x() ? -1 : 1;
            return point.y() < thatPoint.y() ? -1 : 1;
        }

        public void draw() {
            StdDraw.setPenColor(StdDraw.BLACK);
            point.draw();
            double x0, y0, x1, y1;
            if (isVertical()) {
                StdDraw.setPenColor(StdDraw.RED);
                x0 = x1 = point.x();
                y0 = rect.ymin();
                y1 = rect.ymax();
            } else {
                StdDraw.setPenColor(StdDraw.BLUE);
                x0 = rect.xmin();
                x1 = rect.xmax();
                y0 = y1 = point.y();
            }
            StdDraw.line(x0, y0, x1, y1);
        }

        private boolean isVertical() {
            return direction == VERTICAL;
        }

        private boolean isHorizontal() {
            return direction == HORIZONTAL;
        }
    }

    public static void main(String[] args) {
        KdTree set = new KdTree();
        insertRandomPointsToSet(set, 100);
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

    private static void insertRandomPointsToSet(KdTree set, int pointCount) {
        for (int i = 1; i <= pointCount; i++)
            set.insert(new Point2D(StdRandom.uniform(0.0, 1.0), StdRandom.uniform(0.0, 1.0)));
    }
}
