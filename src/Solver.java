import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.MinPQ;

import java.util.Comparator;
import java.util.LinkedList;


/**
 * Created by Hua on 2/15/2017.

 To implement the A* algorithm, you must use MinPQ from algs4.jar for the priority queue(s).

 Corner cases.  The constructor should throw a java.lang.NullPointerException if passed a null argument.


 challenges:(don't expect you to use set)
 1. How to combine manhattan with moves in priority queue as comparator?
 2. How to trace to original?
 both 1 and 2 need another structure to contain those information.

 3. How to alternate executing two priority queue?
 see code



 */
public class Solver {
    private boolean isSolvable =false;
    private int moves = -1;
    private Node lastNode = null;


    // additional variable to differentiate from twins
    private class Node implements Comparable<Node>{
        Board board;
        Node preNode;
        int moves;
        boolean isTwin;
        Node(Board cur, Node pre, int moves, boolean isTwin){
            this.board = cur;
            this.preNode = pre;
            this.moves = moves;
            this.isTwin = isTwin;
        }

        public int heuristic(){
            return board.manhattan() + moves;
        }

        public int compareTo(Node that){
            if(this.heuristic() == that.heuristic()) return this.board.manhattan() - that.board.manhattan();
            return this.heuristic() - that.heuristic();
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    // using only one queue
    // see git history of 2 queue version
    public Solver(Board initial){
        Board twin = initial.twin();
        Node a = new Node(initial, null, 0, false);
        Node b = new Node(twin, null, 0, true);
        MinPQ<Node> q = new MinPQ<>();
        q.insert(a);
        q.insert(b);

        while(true){
            Node cur = q.delMin();
            if(cur.board.isGoal()){
                if(cur.isTwin == false) isSolvable = true;
                lastNode = cur;
                moves = cur.moves;
                break;
            }
            addNeighboursToQueue(cur, q);
        }
    }

    private void addNeighboursToQueue(Node cur, MinPQ<Node> q){
        for(Board neighbour: cur.board.neighbors()){
            if(cur.preNode != null && cur.preNode.board.equals(neighbour) ) continue;

            Node newNode = new Node(neighbour, cur, cur.moves + 1, cur.isTwin);
            q.insert(newNode);
        }
    }

    // is the initial board solvable?
    public boolean isSolvable(){
        return isSolvable;
    }


    // min number of moves to solve initial board; -1 if unsolvable
    public int moves(){
        if(!isSolvable) return -1;
        return moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution(){
        if(!isSolvable) return null;
        LinkedList<Board> ret = new LinkedList<>();
        Node p = lastNode;  // to make this function immutable
        while(p != null){
            ret.addFirst(p.board);
            p = p.preNode;
        }
        return ret;
    }

    // solve a slider puzzle (given below)
    public static void main(String[] args){
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);
        System.out.println(solver.moves());
        System.out.println(solver.moves());
        for (Board board : solver.solution())
            StdOut.println(board);
        System.out.println(solver.isSolvable());
        System.out.println(solver.moves());
        System.out.println(solver.isSolvable());

        for (Board board : solver.solution())
            StdOut.println(board);

        /*
        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }

        */
    }

}
