import edu.princeton.cs.algs4.*;
import edu.princeton.cs.algs4.StdDraw;

/**
 * Created by HuaZ on 2/9/2017.
 * <p/>
 * Brute force. Write a program BruteCollinearPoints.java that examines 4 points at a time and checks whether
 * they all lie on the same line segment, returning all such line segments.
 * To check whether the 4 points p, q, r, and s are collinear, check whether the three slopes between p and q,
 * between p and r, and between p and s are all equal.
 * <p/>
 * The method segments() should include each line segment containing 4 points exactly once.
 * If 4 points appear on a line segment in the order p→q→r→s, then you should include either
 * the line segment p→s or s→p (but not both) and you should not include subsegments such as p→r or q→r.
 * For simplicity, we will not supply any input to BruteCollinearPoints that has 5 or more collinear points.
 * <p/>
 * Corner cases. Throw a java.lang.NullPointerException either the argument to the constructor is null or
 * if any point in the array is null. Throw a java.lang.IllegalArgumentException if the argument to the
 * constructor contains a repeated point.
 * <p/>
 * Performance requirement.
 * The order of growth of the running time of your program should be n4 in the worst case and it should use
 * space proportional to n plus the number of line segments returned.
 */
public class BruteCollinearPoints {
    private int numOfSegments = 0;
    private LineSegment[] seg;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        if (points == null || points.length == 0) throw new NullPointerException("null is not allowed as input");
        int n = points.length;
        seg = new LineSegment[n];

        // sanity check
        for (int i = 0; i < n; i++) {
            if (points[i] == null) throw new NullPointerException("null item found in input");
            for (int j = i - 1; j >= 0; j--) {
                if (points[i].compareTo(points[j]) == 0) {
                    throw new IllegalArgumentException("Repeated point is not allowed");
                }
            }
        }

        //compute segment
        boolean isFound = false;
        for (int i = 0; i < n; i++) {
            Point a = points[i];
            for (int j = 0; j < n; j++) {
                if (j == i) continue;
                Point b = points[j];
                if (a.compareTo(b) > 0) continue;
                for (int k = 0; k < n; k++) {
                    if (k == i || k == j) continue;
                    Point c = points[k];
                    if (b.compareTo(c) > 0) continue;
                    for (int l = 0; l < n; l++) {
                        if (l == i || l == j || l == k) continue;
                        Point d = points[l];
                        if (c.compareTo(d) > 0) continue;
                        if (Double.compare(a.slopeTo(b), a.slopeTo(c)) == 0 &&
                                Double.compare(a.slopeTo(b), a.slopeTo(d)) == 0) {
                            seg[numOfSegments++] = new LineSegment(a, d);
                            isFound = true;
                            break;
                        }
                    }
                    if (isFound) break;
                }
                isFound = false;
            }

        }
    }


    // the number of line segments
    public int numberOfSegments() {
        return numOfSegments;
    }

    // the line segments
    public LineSegment[] segments() {
        LineSegment[] ret = new LineSegment[numOfSegments];
        for (int i = 0; i < numOfSegments; i++) {
            ret[i] = seg[i];
        }
        return ret;
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
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }


}
