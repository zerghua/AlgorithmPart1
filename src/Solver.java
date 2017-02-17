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
    /*
    private class Node{
        Board board;
        Node preNode;
        int moves;
        Node(Board cur, Node pre, int moves){
            this.board = cur;
            this.preNode = pre;
            this.moves = moves;
        }
    }
    */

    // additional variable to differentiate from twins
    private class Node{
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
    }


    private boolean isSolvable =false;
    private int moves = -1;
    private Node lastNode;


    private MinPQ<Node> initQueue(Board b){
        return new MinPQ<>(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                // break tie by using manhattan
                if(o1.board.manhattan() + o1.moves == o2.board.manhattan() + o2.moves){
                    return o1.board.manhattan() - o2.board.manhattan();
                }
                return o1.board.manhattan() + o1.moves - o2.board.manhattan() - o2.moves;
            }
        });
    }

    private void addNeighboursToQueue(Node cur, MinPQ<Node> q){
        for(Board neighbour: cur.board.neighbors()){
            if(cur.preNode != null && cur.preNode.board.equals(neighbour) ) continue;

            Node newNode = new Node(neighbour, cur, cur.moves + 1, cur.isTwin);
            q.insert(newNode);
        }
    }

/*
    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial){
        Board twin = initial.twin();
        boolean isRunningInitial = true;

        Node a = new Node(initial, null, 0);
        MinPQ<Node> q = initQueue(initial);
        q.insert(a);

        Node b = new Node(twin, null, 0);
        MinPQ<Node> twin_q = initQueue(twin);
        twin_q.insert(b);

        while(true){
            if(isRunningInitial){
                Node cur = q.delMin();
                if(cur.board.isGoal()){
                    isSolvable = true;
                    lastNode = cur;
                    moves = cur.moves;
                    break;
                }
                addNeighboursToQueue(cur, q);
                isRunningInitial = false;
            }
            else{
                Node cur = twin_q.delMin();
                if(cur.board.isGoal()){
                    isSolvable = false;
                    break;
                }
                addNeighboursToQueue(cur, twin_q);
                isRunningInitial = true;
            }
        }
    }
*/

    // find a solution to the initial board (using the A* algorithm)
    // using only one queue
    public Solver(Board initial){
        Board twin = initial.twin();
        Node a = new Node(initial, null, 0, false);
        Node b = new Node(twin, null, 0, true);
        MinPQ<Node> q = initQueue(initial);
        q.insert(a);
        q.insert(b);

        while(true){
            Node cur = q.delMin();
            if(cur.board.isGoal()){
                if(cur.isTwin == true){
                    isSolvable = false;
                }else{
                    isSolvable = true;
                }
                lastNode = cur;
                moves = cur.moves;
                break;
            }
            addNeighboursToQueue(cur, q);
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
        while(lastNode != null){
            ret.addFirst(lastNode.board);
            lastNode = lastNode.preNode;
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

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}
