import java.util.ArrayList;
import java.util.Arrays;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;

public class FastCollinearPoints {
    private LineSegment[] segments;

    /**
     * Finds all line segments containing 4 or more points
     */

    public FastCollinearPoints(Point[] points) {
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
        ArrayList<LineSegment> segmentList = new ArrayList<>();
        for (int i = 0; i < points.length; i++) {
            Point currentPoint = points[i];
            Point[] otherPoints = copyPointsExcludingOne(points, i);
            sortPointsWRTSlopeWithPivot(otherPoints, currentPoint);
            createSegmentsFromSortedPoints(segmentList, otherPoints, currentPoint);
        }
        segments = new LineSegment[segmentList.size()];
        segments = segmentList.toArray(segments);
    }

    private Point[] copyPointsExcludingOne(Point[] points, int excludedIndex) {
        Point[] rest = new Point[points.length - 1];
        for (int i = 0; i < points.length; i++) {
            if (i < excludedIndex)
                rest[i] = points[i];
            if (i > excludedIndex)
                rest[i - 1] = points[i];
        }
        return rest;
    }

    private void sortPointsWRTSlopeWithPivot(Point[] points, Point pivotPoint) {
        Arrays.sort(points, pivotPoint.slopeOrder());
    }

    private void createSegmentsFromSortedPoints(ArrayList<LineSegment> segmentList,
            Point[] sortedOtherPoints, Point pivotPoint) {
        int lo = 0;
        double lastSlope = pivotPoint.slopeTo(sortedOtherPoints[0]);
        for (int hi = 1; hi < sortedOtherPoints.length; hi++) {
            double currentSlope = pivotPoint.slopeTo(sortedOtherPoints[hi]);
            // TODO: Rewrite to reduce parameter count of handleSegmentCreation (5 is too many)
            if (lastSlope == currentSlope) {
                if (hi == sortedOtherPoints.length - 1 && hi - lo >= 2)
                    // There are hi - lo + 1 adjacent slopes (from lo to hi = last index) at the end
                    // of the array, which represent a line segment.
                    handleSegmentCreation(segmentList, sortedOtherPoints, pivotPoint, lo, hi);
            } else {
                if (hi - lo >= 3)
                    // There are hi - lo adjacent slopes (from lo to hi - 1) that are equal, i.e.
                    // they represent a line segment.
                    handleSegmentCreation(segmentList, sortedOtherPoints, pivotPoint, lo, hi - 1);
                lo = hi;
            }
            lastSlope = currentSlope;
        }
    }

    private void handleSegmentCreation(ArrayList<LineSegment> segmentList, Point[] otherPoints,
            Point pivotPoint, int lo, int hi) {
        Point minPoint = findMinPointInSubarray(otherPoints, lo, hi);
        Point maxPoint = findMaxPointInSubarray(otherPoints, lo, hi);
        if (pivotPoint.compareTo(minPoint) < 0)
            segmentList.add(new LineSegment(pivotPoint, maxPoint));
        // Otherwise the segment has already been added to the list.
    }

    private Point findMaxPointInSubarray(Point[] points, int lo, int hi) {
        Point max = points[lo];
        for (int i = lo + 1; i <= hi; i++)
            if (points[i].compareTo(max) > 0)
                max = points[i];
        return max;
    }

    private Point findMinPointInSubarray(Point[] points, int lo, int hi) {
        Point min = points[lo];
        for (int i = lo + 1; i <= hi; i++)
            if (points[i].compareTo(min) < 0)
                min = points[i];
        return min;
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
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
