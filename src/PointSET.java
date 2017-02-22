/**
 * Created by Hua on 2/22/2017.


 Brute-force implementation.
 Write a mutable data type PointSET.java that represents a set of points in the unit square.
 Implement the following API by using a red-black BST (using either SET from algs4.jar or java.util.TreeSet).


 Corner cases.
 Throw a java.lang.NullPointerException if any argument is null.

 Performance requirements.
 Your implementation should support insert() and contains() in time proportional to
 the logarithm of the number of points in the set in the worst case;
 it should support nearest() and range() in time proportional to the number of points in the set.


 */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.LinkedList;
import java.util.TreeSet;

public class PointSET {
    private TreeSet<Point2D> set ;

    // construct an empty set of points
    public PointSET(){
        set = new TreeSet<>();
    }

    // is the set empty?
    public boolean isEmpty(){
        return set.isEmpty();
    }

    // number of points in the set
    public int size(){
        return set.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p){
        if(p == null) throw new java.lang.NullPointerException("point should not be null");
        set.add(p);  // Java Treeset will take care of it, will not insert if already exist.
    }

    // does the set contain point p?
    public boolean contains(Point2D p){
        if(p == null) throw new java.lang.NullPointerException("point should not be null");
        return set.contains(p);
    }


    // draw all points to standard draw
    public void draw(){
        for(Point2D p : set){
            p.draw();
        }
    }


    // all points that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect){
        if(rect == null) throw new java.lang.NullPointerException("rect should not be null");

        LinkedList<Point2D> ret = new LinkedList<>();
        for(Point2D p : set){
            if(rect.contains(p)) ret.add(p);
        }

        return ret;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p){
        if(p == null) throw new java.lang.NullPointerException("point should not be null");

        if(set.isEmpty()) return null;

        Point2D ret = null;
        double min = Double.POSITIVE_INFINITY;
        for(Point2D n: set){
            double dist = p.distanceSquaredTo(n);
            if( dist < min){
                min = dist;
                ret = n;
            }
        }
        return ret;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args){

    }
}
