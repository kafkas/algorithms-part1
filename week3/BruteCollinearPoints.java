import java.util.ArrayList;
import java.util.Arrays;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.In;

public class BruteCollinearPoints {
    private LineSegment[] segments;

    public BruteCollinearPoints(Point[] points) {
        validatePointsArray(points);
        validateEachPoint(points);
        validateDistinctPoints(points);
        if (cannotHave4CollinearPoints(points)) {
            segments = new LineSegment[0];
            return;
        }
        findCollinearPointsAndCreateLineSegments(points);
    }

    private void validatePointsArray(Point[] points) {
        if (points == null)
            throw new IllegalArgumentException("Points array cannot be null.");
    }

    private void validateEachPoint(Point[] points) {
        for (int i = 0; i < points.length; i++)
            if (points[i] == null)
                throw new IllegalArgumentException("No null points.");
    }

    private void validateDistinctPoints(Point[] points) {
        for (int i = 0; i < points.length; i++)
            for (int j = 0; j < points.length && j != i; j++)
                if (points[i].compareTo(points[j]) == 0)
                    throw new IllegalArgumentException("No duplicate points.");
    }

    private boolean cannotHave4CollinearPoints(Point[] points) {
        return points.length < 4;
    }

    private void findCollinearPointsAndCreateLineSegments(Point[] points) {
        Point[] sortedPoints = getSortedClone(points);
        ArrayList<LineSegment> segmentList = new ArrayList<>();
        int pointCount = points.length;
        for (int i = 0; i < pointCount; i++) {
            Point p = sortedPoints[i];
            for (int j = i + 1; j < pointCount; j++) {
                Point q = sortedPoints[j];
                double pqSlope = p.slopeTo(q);
                for (int k = j + 1; k < pointCount; k++) {
                    Point r = sortedPoints[k];
                    if (pqSlope == p.slopeTo(r))
                        // p, q, r are collinear
                        for (int h = k + 1; h < pointCount; h++) {
                            Point s = sortedPoints[h];
                            if (pqSlope == p.slopeTo(s))
                                // p, q, r, s are collinear
                                segmentList.add(new LineSegment(p, s));
                        }
                }
            }
        }
        segments = new LineSegment[segmentList.size()];
        segments = segmentList.toArray(segments);
    }

    private Point[] getSortedClone(Point[] points) {
        Point[] sortedPoints = Arrays.copyOf(points, points.length);
        Arrays.sort(sortedPoints);
        return sortedPoints;
    }

    public int numberOfSegments() {
        return segments.length;
    }

    public LineSegment[] segments() {
        return Arrays.copyOf(segments, segments.length);
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = getPointsFromFile(in, n);
        drawPoints(points);
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        printAndDrawLineSegments(collinear.segments());
    }

    private static Point[] getPointsFromFile(In in, int n) {
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }
        return points;
    }

    private static void drawPoints(Point[] points) {
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 10);
        StdDraw.setYscale(0, 10);
        for (Point p : points)
            p.draw();
        StdDraw.show();
    }

    private static void printAndDrawLineSegments(LineSegment[] segments) {
        for (LineSegment segment : segments) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
