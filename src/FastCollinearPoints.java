import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by HuaZ on 2/9/2017.
 * <p/>
 * A faster, sorting-based solution. Remarkably,
 * it is possible to solve the problem much faster than the brute-force solution described above.
 * Given a point p, the following method determines whether p participates in a set of 4 or more
 * collinear points.
 * <p/>
 * 1. Think of p as the origin.
 * 2. For each other point q, determine the slope it makes with p.
 * 3. Sort the points according to the slopes they makes with p.
 * 4. Check if any 3 (or more) adjacent points in the sorted order have equal slopes with respect to p.
 * If so, these points, together with p, are collinear.
 * <p/>
 * Applying this method for each of the n points in turn yields an efficient algorithm to the problem.
 * The algorithm solves the problem because points that have equal slopes with respect to p are collinear,
 * and sorting brings such points together.
 * The algorithm is fast because the bottleneck operation is sorting.
 * <p/>
 * <p/>
 * The method segments() should include each maximal line segment containing 4 (or more) points exactly once.
 * For example, if 5 points appear on a line segment in the order p→q→r→s→t,
 * then do not include the subsegments p→s or q→t.
 * <p/>
 * Corner cases.
 * Throw a java.lang.NullPointerException
 * either the argument to the constructor is null or if any point in the array is null.
 * <p/>
 * Throw a java.lang.IllegalArgumentException
 * if the argument to the constructor contains a repeated point.
 * <p/>
 * Performance requirement.
 * The order of growth of the running time of your program should be n2 log n in the worst case
 * and it should use space proportional to n plus the number of line segments returned.
 * FastCollinearPoints should work properly even if the input has 5 or more collinear points.
 * <p/>
 * <p/>
 * Notes:
 * bug fixed:
 * 1. should be sortedPoints[j-1] rather than sortedPoints[j] when found more than 3.
 * 2. sort needs to be stable in slopeOrder() comparator.
 * <p/>
 * passed:
 * input6.txt
 * input8.txt
 * rs1423.txt
 * input40.txt
 * horizontal5.txt
 *
 * ASSESSMENT SUMMARY
 * Compilation: PASSED
 * API:PASSED
 * Findbugs: PASSED
 * Checkstyle: FAILED (5 warnings)
 * Correctness: 41/41 tests passed
 * Memory: 1/1 tests passed
 * Timing: 41/41 tests passed
 *
 * Aggregate score: 100.00% [Compilation: 5%, API: 5%, Findbugs: 0%, Checkstyle: 0%, Correctness: 60%, Memory: 10%, Timing: 20%]
 *
 */
public class FastCollinearPoints {
    private ArrayList<LineSegment> ret;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        if (points == null || points.length == 0) throw new NullPointerException("null is not allowed as input");
        Point[] in = points.clone();
        int n = in.length;
        ret = new ArrayList<>();

        // check null. o(n)
        for (int i = 0; i < n; i++) {
            if (in[i] == null) throw new NullPointerException("null item found in input");
        }

        // check repeat elements   o(nlogn)
        Arrays.sort(in);
        for (int i = 1; i < n; i++) {
            if (in[i].compareTo(in[i - 1]) == 0) {
                throw new IllegalArgumentException("Repeated point is not allowed");
            }
        }

        for (int i = 0; i < n; i++) {
            Arrays.sort(in);   //important
            Point a = in[i];
            Arrays.sort(in, a.slopeOrder());  //important

            // need to find at least 3 points with the same slope after a in sortedPoints
            // concise code
            for (int first = 1, last = 2; last < n; last++) {
                while (last < n && Double.compare(a.slopeTo(in[first]), a.slopeTo(in[last])) == 0) last++;

                // smart way to make sure only on add line segment starting from this point
                // but the order must be sorted first or this way won't work
                if (last - first >= 3 && a.compareTo(in[first]) < 0) {
                    ret.add(new LineSegment(a, in[last - 1]));
                }

                first = last;
            }
        }

    }


    // the number of line segments
    public int numberOfSegments() {
        return ret.size();
    }

    // the line segments
    // mutate Point[]
    // mutate LineSegment[]
    // segments() should be immutable, multiple call should return the same result of the same input
    public LineSegment[] segments() {
        return ret.toArray(new LineSegment[ret.size()]);
    }

    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();


    }
}
