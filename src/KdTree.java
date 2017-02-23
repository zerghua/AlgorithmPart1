/**
 * Created by Hua on 2/22/2017.


 2d-tree implementation.
 Write a mutable data type KdTree.java that uses a 2d-tree to implement the same API (but replace PointSET with KdTree).
 A 2d-tree is a generalization of a BST to two-dimensional keys.
 The idea is to build a BST with points in the nodes, using the x- and y-coordinates of the points as keys
 in strictly alternating sequence.


 Search and insert.
 The algorithms for search and insert are similar to those for BSTs, but at the root we use the x-coordinate
 (if the point to be inserted has a smaller x-coordinate than the point at the root, go left; otherwise go right);
 then at the next level, we use the y-coordinate (if the point to be inserted has a smaller y-coordinate than
 the point in the node, go left; otherwise go right); then at the next level the x-coordinate, and so forth.


 Draw.
 A 2d-tree divides the unit square in a simple way: all the points to the left of the root go in the left subtree;
 all those to the right go in the right subtree; and so forth, recursively. Your draw() method should draw all of the
 points to standard draw in black and the subdivisions in red (for vertical splits) and blue (for horizontal splits).
 This method need not be efficient—it is primarily for debugging.



 The prime advantage of a 2d-tree over a BST is that it supports efficient implementation of range search and
 nearest neighbor search. Each node corresponds to an axis-aligned rectangle in the unit square,
 which encloses all of the points in its subtree. The root corresponds to the unit square;
 the left and right children of the root corresponds to the two rectangles split by the
 x-coordinate of the point at the root; and so forth.



 Range search.
 To find all points contained in a given query rectangle, start at the root and recursively search for points in both
 subtrees using the following pruning rule: if the query rectangle does not intersect the rectangle corresponding to
 a node, there is no need to explore that node (or its subtrees).
 A subtree is searched only if it might contain a point contained in the query rectangle.



 Nearest neighbor search.
 To find a closest point to a given query point, start at the root and recursively search in both subtrees using
 the following pruning rule: if the closest point discovered so far is closer than the distance between the query point
 and the rectangle corresponding to a node, there is no need to explore that node (or its subtrees).
 That is, a node is searched only if it might contain a point that is closer than the best one found so far.
 The effectiveness of the pruning rule depends on quickly finding a nearby point.
 To do this, organize your recursive method so that when there are two possible subtrees to go down,
 you always choose the subtree that is on the same side of the splitting line as the query point as the first subtree
 to explore—the closest point found while exploring the first subtree may enable pruning of the second subtree.



 Clients.
 You may use the following interactive client programs to test and debug your code.

 KdTreeVisualizer.java
 computes and draws the 2d-tree that results from the sequence of points clicked by the user in the standard drawing window.


 RangeSearchVisualizer.java
 reads a sequence of points from a file (specified as a command-line argument) and inserts those points into a 2d-tree.
 Then, it performs range searches on the axis-aligned rectangles dragged by the user in the standard drawing window.


 NearestNeighborVisualizer.java
 reads a sequence of points from a file (specified as a command-line argument) and inserts those points into a 2d-tree.
 Then, it performs nearest neighbor queries on the point corresponding to the location of the mouse in the standard drawing window.



 Analysis of running time and memory usage (optional and not graded).

 Give the total memory usage in bytes (using tilde notation) of your 2d-tree data structure as a function of the number
 of points n, using the memory-cost model from lecture and Section 1.4 of the textbook.
 Count all memory that is used by your 2d-tree, including memory for the nodes, points, and rectangles.

 Give the expected running time in seconds (using tilde notation) to build a 2d-tree on n random points
 in the unit square. (Do not count the time to read in the points from standard input.)

 How many nearest neighbor calculations can your 2d-tree implementation perform per second for
 input100K.txt (100,000 points) and input1M.txt (1 million points), where the query points are random points
 in the unit square? (Do not count the time to read in the points or to build the 2d-tree.)
 Repeat this question but with the brute-force implementation.



 These are many ways to improve performance of your 2d-tree. Here are some ideas.
 Squared distances.
 Whenever you need to compare two Euclidean distances, it is often more efficient to compare the squares of the
 two distances to avoid the expensive operation of taking square roots.
 Everyone should implement this optimization because it is both easy to do and likely a bottleneck.


 Range search.
 Instead of checking whether the query rectangle intersects the rectangle corresponding to a node,
 it suffices to check only whether the query rectangle intersects the splitting line segment:
 if it does, then recursively search both subtrees;
 otherwise, recursively search the one subtree where points intersecting the query rectangle could be.



 Save memory.
 You are not required to explicitly store a RectHV in each 2d-tree node (though it is probably wise in your first version).

 */


import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import java.util.LinkedList;

public class KdTree {
    private Node root;
    private int size;

    private static class Node {
        public Point2D p;      // the point
        public RectHV rect;    // the axis-aligned rectangle corresponding to this node
        public Node lb;        // the left/bottom subtree
        public Node rt;        // the right/top subtree
        public boolean isVertical;

        public Node(Point2D p, RectHV rect, boolean isVertical){
            this.p = p;
            this.rect = rect;
            this.lb = null;
            this.rt = null;
            this.isVertical = isVertical;
        }
    }

    // construct an empty set of points
    public KdTree(){
        size = 0;
        root = null;
    }

    // is the set empty?
    public boolean isEmpty(){
        return size() == 0;
    }

    // number of points in the set
    public int size(){
        return size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p){
        if ( p == null ) throw new java.lang.NullPointerException("point should not be null");
        if( root == null) {
            root = new Node(p, new RectHV(0,0,1,1), true);
            size++;
        }else{
            insert(root, root, p);
        }
    }

    private Node insert(Node cur, Node parent, Point2D p){ // parent is used to calculate rectHV
        if(cur == null) {
            size++;
            double xmin = parent.rect.xmin();
            double ymin = parent.rect.ymin();
            double xmax = parent.rect.xmax();
            double ymax = parent.rect.ymax();

            if(parent.isVertical){
                if(p.x() < parent.p.x()) xmax = parent.p.x();
                else xmin = parent.p.x();
            }else{
                if(p.y() < parent.p.y()) ymax = parent.p.y();
                else ymin = parent.p.y();
            }

            RectHV rect = new RectHV(xmin, ymin, xmax, ymax);
            //System.out.println("rect = " + rect);

            return new Node(p, rect, !parent.isVertical);
        }

        if(p.equals(cur.p)) return cur; // discard repeat point

        if(cur.isVertical){ // compare x when vertical
            if(p.x() < cur.p.x()) cur.lb = insert(cur.lb, cur, p);
            else cur.rt = insert(cur.rt, cur, p);
        }else{  // compare y when horizontal
            if(p.y() < cur.p.y()) cur.lb = insert(cur.lb, cur, p);
            else cur.rt = insert(cur.rt, cur, p);
        }
        return cur;
    }

    // does the set contain point p?
    public boolean contains(Point2D p){
        if ( p == null ) throw new java.lang.NullPointerException("point should not be null");
        return contains(p, root);
    }

    private boolean contains(Point2D p, Node cur){
        if(cur == null) return false;
        if(p.equals(cur.p)) return true;

        if(cur.isVertical){
            if(p.x() < cur.p.x()) return contains(p, cur.lb);
            else return contains(p, cur.rt);
        }else{
            if(p.y() < cur.p.y()) return contains(p, cur.lb);
            else return contains(p, cur.rt);
        }
    }


    // draw all of the points to standard draw in black
    // and the subdivisions in red (for vertical splits) and blue (for horizontal splits)
    // Use StdDraw.setPenColor(StdDraw.BLACK) and StdDraw.setPenRadius(0.01) before before drawing the points;
    // use StdDraw.setPenColor(StdDraw.RED) or StdDraw.setPenColor(StdDraw.BLUE) and StdDraw.setPenRadius()
    // before drawing the splitting lines.
    public void draw(){
        StdDraw.clear();
        draw(root, true);
    }

    private void draw(Node cur, boolean isVertical){
        if(cur == null) return;

        StdDraw.setPenRadius(0.01);

        // draw line
        double xmin = cur.rect.xmin();
        double ymin = cur.rect.ymin();
        double xmax = cur.rect.xmax();
        double ymax = cur.rect.ymax();
        if(isVertical){
            StdDraw.setPenColor(StdDraw.RED);
            xmin = xmax = cur.p.x();
            StdDraw.line(xmin, ymin, xmax, ymax);
        }else{
            StdDraw.setPenColor(StdDraw.BLUE);
            ymin = ymax = cur.p.y();
            StdDraw.line(xmin, ymin, xmax, ymax);
        }
        //System.out.format("line [%f %f], [%f %f]\n\n", xmin, ymin, xmax, ymax);

        // draw point
        StdDraw.setPenColor(StdDraw.BLACK);
        cur.p.draw();

        // recursively draw
        draw(cur.lb, !isVertical);
        draw(cur.rt, !isVertical);
    }


    // all points that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect){
        if (rect == null) throw new java.lang.NullPointerException("rect should not be null");
        LinkedList<Point2D> ret = new LinkedList<>();
        range(rect, ret, root);
        return ret;
    }

    private void range(RectHV rect, LinkedList<Point2D> ret, Node cur){
        if(cur == null) return;
        if(rect.contains(cur.p)) ret.add(cur.p);
        if(cur.lb != null && rect.intersects(cur.lb.rect)) range(rect, ret, cur.lb);
        if(cur.rt != null && rect.intersects(cur.rt.rect)) range(rect, ret, cur.rt);
    }


    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p){
        if (p == null) throw new java.lang.NullPointerException("point should not be null");
        if (isEmpty()) return null;

        return nearest(p, root, root.p, Double.POSITIVE_INFINITY);
    }

    private Point2D nearest(Point2D p, Node cur, Point2D candidate, double min_dist){
        if(cur != null && cur.rect.distanceSquaredTo(p) < min_dist) {

            double dist = p.distanceSquaredTo(cur.p);
            if (dist < min_dist) {
                min_dist = dist;
                candidate = cur.p;
            }

            if(cur.lb != null && cur.lb.rect.contains(p)){
                candidate = nearest(p, cur.lb, candidate, min_dist);
                candidate = nearest(p, cur.rt, candidate, candidate.distanceSquaredTo(p));
            }else if(cur.rt != null){
                candidate = nearest(p, cur.rt, candidate, min_dist);
                candidate = nearest(p, cur.lb, candidate, candidate.distanceSquaredTo(p));
            }
        }

        return candidate;
    }



    // unit testing of the methods (optional)
    public static void main(String[] args){
        String filename = args[0];
        In in = new In(filename);

        StdDraw.enableDoubleBuffering();

        // initialize the two data structures with point from standard input
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }
        System.out.println("size="+kdtree.size());

        StdDraw.clear();
        StdDraw.setPenRadius(0.01);
        kdtree.draw();
        StdDraw.show();

    }
}
