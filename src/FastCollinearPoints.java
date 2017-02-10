import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

/**
 * Created by HuaZ on 2/9/2017.

 A faster, sorting-based solution. Remarkably,
 it is possible to solve the problem much faster than the brute-force solution described above.
 Given a point p, the following method determines whether p participates in a set of 4 or more
 collinear points.

 1. Think of p as the origin.
 2. For each other point q, determine the slope it makes with p.
 3. Sort the points according to the slopes they makes with p.
 4. Check if any 3 (or more) adjacent points in the sorted order have equal slopes with respect to p.
 If so, these points, together with p, are collinear.

 Applying this method for each of the n points in turn yields an efficient algorithm to the problem.
 The algorithm solves the problem because points that have equal slopes with respect to p are collinear,
 and sorting brings such points together.
 The algorithm is fast because the bottleneck operation is sorting.


 The method segments() should include each maximal line segment containing 4 (or more) points exactly once.
 For example, if 5 points appear on a line segment in the order p→q→r→s→t,
 then do not include the subsegments p→s or q→t.

 Corner cases.
 Throw a java.lang.NullPointerException
 either the argument to the constructor is null or if any point in the array is null.

 Throw a java.lang.IllegalArgumentException
 if the argument to the constructor contains a repeated point.

 Performance requirement.
 The order of growth of the running time of your program should be n2 log n in the worst case
 and it should use space proportional to n plus the number of line segments returned.
 FastCollinearPoints should work properly even if the input has 5 or more collinear points.


 Notes:
 bug fixed:
 1. should be sortedPoints[j-1] rather than sortedPoints[j] when found more than 3.
 2. sort needs to be stable in slopeOrder() comparator.

 passed:
 input6.txt
 input8.txt
 rs1423.txt
 input40.txt
 horizontal5.txt


 */
public class FastCollinearPoints {
    private int numOfSegments=0;
    private Point[] sortedPoints;
    private Point[] start;
    private Point[] end;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points){
        if(points == null || points.length==0) throw new NullPointerException("null is not allowed as input");
        int n = points.length;
        if(n<4) return;
        sortedPoints = new Point[n];
        start = new Point[n];
        end = new Point[n];

        // sanity check 1
        for(int i=0;i<n;i++) {
            if(points[i] == null) throw new NullPointerException("null item found in input");
        }

        Arrays.sort(points);

        // sanity check 2
        //System.out.println(points[0]);
        sortedPoints[0] = points[0];
        for(int i=1;i<n;i++){
            if(points[i].compareTo(points[i-1]) == 0) {
                throw new IllegalArgumentException("Repeated point is not allowed");
            }
            sortedPoints[i] = points[i];
            //System.out.println(points[i]);
        }


        for(int i=0;i<n;i++){
            Point a = points[i];
            Arrays.sort(sortedPoints, a.slopeOrder());
            //System.out.println("\n\n Point a="+a);
            //printPoint(sortedPoints);


            // need to find at least 3 points with the same slope after a in sortedPoints
            double slope = a.slopeTo(sortedPoints[1]);
            int count = 1;
            for(int j=2;j<n;j++){
                Point newPoint = sortedPoints[j];
                double newSlope = a.slopeTo(newPoint);
                if(slope == newSlope) count++;
                else {
                    if(count >= 3) {//isSubSegment should be fixed to handle parallel
                        Arrays.sort(sortedPoints, j-count, j);
                        Point left = sortedPoints[j - count];
                        Point right =  sortedPoints[j - 1];
                        if(a.compareTo(left) < 0) left = a;
                        if(a.compareTo(right) > 0) right = a;

                        if(!isDup(left, right)) {
                            //System.out.println("j="+j+" count="+count+" numOfSegments="+numOfSegments);
                            start[numOfSegments] = left;
                            end[numOfSegments] = right;
                            numOfSegments++;
                        }
                    }
                    slope = newSlope;
                    count = 1;
                }
            }
            if(count >= 3) {
                Arrays.sort(sortedPoints, n-count, n);
                Point left = sortedPoints[n - count];
                Point right =  sortedPoints[n - 1];
                if(a.compareTo(left) < 0) left = a;
                if(a.compareTo(right) > 0) right = a;

                if(!isDup(left, right)) {
                    start[numOfSegments] = left;
                    end[numOfSegments] = right;
                    numOfSegments++;
                }
            }
        }
    }

    private void printPoint(Point[] a){
        for(int i=0;i<a.length;i++)System.out.print(a[i] + ":" + a[0].slopeTo(a[i]) + "  ");
        System.out.println();
    }


    private boolean isDup(Point a, Point b){
        for(int i=0;i<numberOfSegments();i++){
            if(start[i].compareTo(a) == 0 && end[i].compareTo(b) == 0) return true;
        }
        return false;
    }

    // the number of line segments
    public int numberOfSegments(){
        return numOfSegments;
    }

    // the line segments
    public LineSegment[] segments(){
        LineSegment[] ret = new LineSegment[numOfSegments];
        for(int i=0;i<numOfSegments;i++){
            ret[i] = new LineSegment(start[i], end[i]);
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
