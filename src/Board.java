import edu.princeton.cs.algs4.In;

import java.util.Stack;

/**
 * Created by Hua on 2/15/2017.

 The problem.
 The 8-puzzle problem is a puzzle invented and popularized by Noyes Palmer Chapman in the 1870s.
 It is played on a 3-by-3 grid with 8 square blocks labeled 1 through 8 and a blank square.
 Your goal is to rearrange the blocks so that they are in order, using as few moves as possible.
 You are permitted to slide blocks horizontally or vertically into the blank square.
 The following shows a sequence of legal moves from an initial board (left) to the goal board (right).


 1  3           1     3        1  2  3        1  2  3        1  2  3
 4  2  5   =>   4  2  5   =>   4     5   =>   4  5      =>   4  5  6
 7  8  6        7  8  6        7  8  6        7  8  6        7  8

 initial        1 left          2 up          5 left          goal


 Best-first search.
 Now, we describe a solution to the problem that illustrates a general artificial intelligence methodology
 known as the A* search algorithm. We define a search node of the game to be a board, the number of moves
 made to reach the board, and the previous search node.

 First, insert the initial search node (the initial board, 0 moves, and a null previous search node) into a priority queue.

 Then, delete from the priority queue the search node with the minimum priority, and insert onto the priority queue
 all neighboring search nodes (those that can be reached in one move from the dequeued search node).

 Repeat this procedure until the search node dequeued corresponds to a goal board.
 The success of this approach hinges on the choice of priority function for a search node.

 We consider two priority functions:

 Hamming priority function.
 The number of blocks in the wrong position, plus the number of moves made so far to get to the search node.
 Intuitively, a search node with a small number of blocks in the wrong position is close to the goal,
 and we prefer a search node that have been reached using a small number of moves.

 Manhattan priority function.
 The sum of the Manhattan distances (sum of the vertical and horizontal distance) from the blocks to
 their goal positions, plus the number of moves made so far to get to the search node.


 For example, the Hamming and Manhattan priorities of the initial search node below are 5 and 10, respectively.


 8  1  3        1  2  3     1  2  3  4  5  6  7  8    1  2  3  4  5  6  7  8
 4     2        4  5  6     ----------------------    ----------------------
 7  6  5        7  8        1  1  0  0  1  1  0  1    1  2  0  0  2  2  0  3

 initial          goal         Hamming = 5 + 0          Manhattan = 10 + 0


 We make a key observation: To solve the puzzle from a given search node on the priority queue,
 the total number of moves we need to make (including those already made) is at least its priority,
 using either the Hamming or Manhattan priority function.

 (For Hamming priority, this is true because each block that is out of place must move at least once to reach its
 goal position.

 For Manhattan priority, this is true because each block must move its Manhattan distance from its goal position.
 Note that we do not count the blank square when computing the Hamming or Manhattan priorities.)

 Consequently,
 when the goal board is dequeued, we have discovered not only a sequence of moves from the initial board to the
 goal board, but one that makes the fewest number of moves.(Challenge for the mathematically inclined: prove this fact.)


 A critical optimization.
 Best-first search has one annoying feature: search nodes corresponding to the same board are enqueued
 on the priority queue many times. To reduce unnecessary exploration of useless search nodes,
 when considering the neighbors of a search node, don't enqueue a neighbor if its board is the same as the board
 of the previous search node.


 8  1  3       8  1  3       8  1       8  1  3     8  1  3
 4     2       4  2          4  2  3    4     2     4  2  5
 7  6  5       7  6  5       7  6  5    7  6  5     7  6

 previous    search node    neighbor   neighbor    neighbor
                                       (disallow)



 Game tree.
 One way to view the computation is as a game tree, where each search node is a node in the game tree and the children
 of a node correspond to its neighboring search nodes. The root of the game tree is the initial search node;
 the internal nodes have already been processed; the leaf nodes are maintained in a priority queue; at each step,
 the A* algorithm removes the node with the smallest priority from the priority queue and processes it
 (by adding its children to both the game tree and the priority queue).



 Detecting unsolvable puzzles.
 Not all initial boards can lead to the goal board by a sequence of legal moves, including the two below:

 1  2  3         1  2  3  4
 4  5  6         5  6  7  8
 8  7            9 10 11 12
                13 15 14
 unsolvable
                unsolvable


 To detect such situations, use the fact that boards are divided into two equivalence classes with respect to reachability:
 (i) those that lead to the goal board and
 (ii) those that lead to the goal board if we modify the initial board by swapping any pair of blocks
 (the blank square is not a block). (Difficult challenge for the mathematically inclined: prove this fact.)
 To apply the fact, run the A* algorithm on two puzzle instances—one with the initial board and one with
 the initial board modified by swapping a pair of blocks—in lockstep (alternating back and forth between
 exploring search nodes in each of the two game trees). Exactly one of the two will lead to the goal board.


 Corner cases.
 You may assume that the constructor receives an n-by-n array containing the n2 integers between 0 and n2 − 1,
 where 0 represents the blank square.


 Performance requirements.
 Your implementation should support all Board methods in time proportional to n2 (or better) in the worst case.


 How can I reduce the amount of memory a Board uses?
 For starters, recall that an n-by-n int[][] array in Java uses about 24 + 32n + 4n^2 bytes; when n equals 3,
 this is 156 bytes. To save memory, consider using an n-by-n char[][] array or a length n^2 char[] array.
 You could use a more elaborate representation: since each board is a permutation of length n^2, in principle,
 you need only about lg ((n^2)!) bits to represent it; when n equals 3, this is only 19 bits.



 Any ways to speed up the algorithm? Yes there are many opportunities for optimization here.
 1. Use a 1d array instead of a 2d array (as suggested above).

 2. Cache either the Manhattan distance of a board (or Manhattan priority of a search node). It is waste to
 recompute the same quantity over and over again.

 3. Exploit the fact that the difference in Manhattan distance between a board and a neighbor is either −1 or +1.

 4. Use only one PQ to run the A* algorithm on the initial board and its twin.

 5. When two search nodes have the same Manhattan priority, you can break ties however you want, e.g., by comparing
 either the Hamming or Manhattan distances of the two boards.

 Use a parity argument to determine whether a puzzle is unsolvable (instead of two synchronous A* searches).
 However, this will either break the API or will require a fragile dependence on the toString() method, so don't do it.


 puzzle47.txt needs speed up to solve.
 puzzle49.txt removed goal[][] array solved it.
 puzzle4x4-49.txt is solved in 3 mins with 2-5 optimization.
 puzzle50.txt not solved in 5 mins. with 1-5 optimization.


 */


// this is 2d int array
public class Board {
    private int[][] tiles;
    private int n;
    private int manhattan=-1;

    // construct a board from an n-by-n array of blocks (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocks){
        if (blocks == null) throw new java.lang.NullPointerException("null input is not allowed");
        if (blocks[0].length != blocks.length) throw new IllegalArgumentException("input is not n by n");
        n = blocks.length;
        tiles = new int[n][n];

        // initialize goal board and copy to tiles
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++) {
                tiles[i][j] = blocks[i][j];
            }
        }
    }

    // board dimension n
    public int dimension(){
        return n;
    }

    // number of blocks out of place
    public int hamming(){
        int ret = 0;
        for(int i=0;i<n;i++) {
            for (int j = 0; j < n; j++) {
                if(i == n-1 && j == n-1) break;  // don't count the last one
                if(tiles[i][j] != i*n + j + 1) ret++;
            }
        }
        return ret;
    }

    // sum of Manhattan distances between blocks and goal
    public int manhattan(){
        if(manhattan != -1) return manhattan; //cache it, significant speed up.
        manhattan = 0;
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                if(tiles[i][j] == 0 || tiles[i][j] == i*n + j + 1) continue;
                int val = tiles[i][j];
                manhattan += Math.abs(i - (val-1)/n) + Math.abs(j - (val-1)%n);
            }
        }
        return manhattan;
    }

    // is this board the goal board?
    public boolean isGoal(){
        return manhattan() == 0;
    }

    private void swap(int[][] m, int i, int j, int new_i, int new_j){
        int tmp = m[i][j];
        m[i][j] = m[new_i][new_j];
        m[new_i][new_j] = tmp;
    }

    // a board that is obtained by exchanging any pair of blocks
    public Board twin(){
        Board ret = new Board(tiles);
        int count = 0, idx_i=0, idx_j=0;
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                if(ret.tiles[i][j] == 0) continue;
                if(count == 0){
                    idx_i = i;
                    idx_j = j;
                    count++;
                }else if(count == 1){
                    swap(ret.tiles, i, j, idx_i, idx_j);
                    return ret;
                }
            }
        }
        return ret;
    }

    // does this board equal y?
    public boolean equals(Object y){
        if(y == this) return true;
        if(y == null) return false;
        if(y.getClass() != this.getClass()) return false;
        Board that = (Board) y;
        if(that.n != this.n) return false;
        for(int i=0; i<n; i++){
            for(int j=0; j<n; j++) if(tiles[i][j] != that.tiles[i][j]) return false;
        }
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors(){
        // get blank index
        int blank_i =0, blank_j = 0;
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                if(tiles[i][j] == 0){
                    blank_i = i;
                    blank_j = j;
                    break;
                }
            }
        }

        // add 4 neighbours
        int[][] dir = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        Stack<Board> ret = new Stack<>();
        for(int i=0;i<dir.length;i++){
            int neighbour_i = blank_i + dir[i][0];
            int neighbour_j = blank_j + dir[i][1];
            if(neighbour_i >=0 && neighbour_i <n && neighbour_j >=0 && neighbour_j < n){
                // exploit neighbour's manhattan distance is either +1 or -1
                // calculate the manhattan difference for that number between new and old position.
                int val = tiles[neighbour_i][neighbour_j];
                int old_manhattan = Math.abs(neighbour_i - (val-1)/n) + Math.abs(neighbour_j - (val-1)%n);
                int new_manhattan = Math.abs(blank_i - (val-1)/n) + Math.abs(blank_j - (val-1)%n);
                int diff = new_manhattan - old_manhattan;

                Board b = new Board(tiles);
                swap(b.tiles, blank_i, blank_j, neighbour_i, neighbour_j);
                b.manhattan = this.manhattan() + diff;
                ret.add(b);
            }
        }
        return ret;
    }

    // string representation of this board (in the output format specified below)
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append(n + "\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                s.append(String.format("%2d ", tiles[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    // unit tests (not graded)
    public static void main(String[] args){
    }
}


/*
    // 1d int array
public class Board {
    private int[] tiles;
    private int n;
    private int manhattan;

    // construct a board from an n-by-n array of blocks (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocks){
        if (blocks == null) throw new java.lang.NullPointerException("null input is not allowed");
        if (blocks[0].length != blocks.length) throw new IllegalArgumentException("input is not n by n");
        n = blocks.length;
        tiles = new int[n*n];
        manhattan = -1;

        // initialize goal board and copy to tiles
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++) {
                tiles[to1dIndex(i,j)] = blocks[i][j];
            }
        }
    }

    private Board(int[] blocks, int n){
        this.n = n;
        this.tiles = blocks.clone();
        manhattan = -1;
    }

    // board dimension n
    public int dimension(){
        return n;
    }

    // number of blocks out of place
    public int hamming(){
        int ret = 0;
        for(int i=0;i<n*n-1;i++) {
            if(tiles[i] != i+1) ret++;
        }
        return ret;
    }

    // sum of Manhattan distances between blocks and goal
    public int manhattan(){
        if(manhattan != -1) return manhattan; //cache it, significant speed up.
        manhattan = 0;
        for(int i=0;i<n*n;i++){
            int val = tiles[i];
            if(val == 0) continue;

            // get it's 2d index
            int x = i/n;
            int y = i%n;

            // calculate it's manhattan distance by sum up diff of x and y difference between now and supposed position.
            manhattan += Math.abs(x - (val-1)/n) + Math.abs(y - (val-1)%n);
        }
        return manhattan;
    }

    // is this board the goal board?
    public boolean isGoal(){
        return manhattan() == 0;
    }

    private void swap(int[] m, int i, int new_i){
        int tmp = m[i];
        m[i] = m[new_i];
        m[new_i] = tmp;
    }

    // a board that is obtained by exchanging any pair of blocks
    public Board twin(){
        Board ret = new Board(tiles, n);
        int count = 0, idx_i=0;
        for(int i=0;i<n*n;i++){
            if(ret.tiles[i] == 0) continue;
            if(count == 0){
                idx_i = i;
                count++;
            }else if(count == 1){
                swap(ret.tiles, i, idx_i);
                return ret;
            }
        }
        return ret;
    }

    // does this board equal y?
    public boolean equals(Object y){
        if(y == this) return true;
        if(y == null) return false;
        if(y.getClass() != this.getClass()) return false;
        Board that = (Board) y;
        if(that.n != this.n) return false;
        for(int i=0; i<n*n; i++){
            if(tiles[i] != that.tiles[i]) return false;
        }
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors(){
        // get blank index
        int blank_i =0, blank_j = 0;
        for(int i=0;i<n*n;i++){
            if(tiles[i] == 0){
                blank_i = i/n;
                blank_j = i%n;
                break;
            }
        }

        // add 4 neighbours
        int[][] dir = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        Stack<Board> ret = new Stack<>();
        for(int i=0;i<dir.length;i++){
            int neighbour_i = blank_i + dir[i][0];
            int neighbour_j = blank_j + dir[i][1];
            if(neighbour_i >=0 && neighbour_i <n && neighbour_j >=0 && neighbour_j < n){
                // exploit neighbour's manhattan distance is either +1 or -1
                // calculate the manhattan difference for that number between new and old position.
                int val = tiles[to1dIndex(neighbour_i, neighbour_j)];
                int old_manhattan = Math.abs(neighbour_i - (val-1)/n) + Math.abs(neighbour_j - (val-1)%n);
                int new_manhattan = Math.abs(blank_i - (val-1)/n) + Math.abs(blank_j - (val-1)%n);
                int diff = new_manhattan - old_manhattan;

                Board b = new Board(tiles, n);
                swap(b.tiles, to1dIndex(blank_i, blank_j), to1dIndex(neighbour_i, neighbour_j));
                b.manhattan = this.manhattan() + diff;
                ret.add(b);
            }
        }
        return ret;
    }

    private int to1dIndex(int i, int j){
        return i*n + j;
    }

    // string representation of this board (in the output format specified below)
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append(n + "\n");
        for (int i = 0; i < n*n; i++) {
            s.append(String.format("%2d ", tiles[i]));
            if(i != 0 && i%n == n-1) s.append("\n");
        }
        return s.toString();
    }

    // unit tests (not graded)
    public static void main(String[] args){
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);
        System.out.println(initial.manhattan());

    }
}
*/