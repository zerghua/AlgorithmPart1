// some pseudo code

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import edu.princeton.cs.algs4.Stopwatch;
import java.util.*;  //this is not allowed

/*

 > java PercolationStats 200 10
 mean                     = 0.5961874999999999
 stddev                   = 0.011335987740623034
 95% confidence interval  = 0.5946164122546428, 0.597758587745357
 Time                     = 8.246

 > java PercolationStats 200 20
mean                     = 0.593105
stddev                   = 0.009043118930988348
95% confidence interval  = 0.5918516876590411, 0.5943583123409589
Time                     = 15.674

0 1 2 3
4 5 6 7
8 9 10 11
12 13 14 15

 */

public class Percolation {
    private boolean[] openedSite;
    private WeightedQuickUnionUF site;
    private int numberOfOpenSites=0;
    private int size;
    private ArrayList<Integer> openedFirstRow;
    private ArrayList<Integer> openedLastRow;

    private boolean isValidSite(int row, int col){
        if(row <1 || row >size|| col <1 || col >size) return false;
        return true;
    }

    private int getSiteIndex(int row, int col){
        return size * (row - 1) + col -1;
    }

    public Percolation(int n)                // create n-by-n grid, with all sites blocked
    {
        if(n<=0) throw new IllegalArgumentException("n is less than 1");
        size=n;
        openedSite = new boolean[size*size];
        site = new WeightedQuickUnionUF(size*size);
        openedFirstRow = new ArrayList();
        openedLastRow = new ArrayList();

    }

    public void open(int row, int col)    // open site (row, col) if it is not open already
    {
        if(!isValidSite(row, col)) throw new IndexOutOfBoundsException("row or col is out of bound, row="+row + " col="+col);
        int index = getSiteIndex(row, col);
        if(openedSite[index] == false) { // if not open
            openedSite[index] = true;
            numberOfOpenSites++;

            // union with neighbours if they are open TODO, check is valid site and isOpen
            if(isValidSite(row-1, col) && isOpen(row-1, col)) site.union(index, getSiteIndex(row-1, col));
            if(isValidSite(row+1, col) && isOpen(row+1, col)) site.union(index, getSiteIndex(row+1, col));
            if(isValidSite(row, col+1) && isOpen(row, col+1)) site.union(index, getSiteIndex(row, col+1));
            if(isValidSite(row, col-1) && isOpen(row, col-1)) site.union(index, getSiteIndex(row, col-1));

            if(index < size) openedFirstRow.add(index);
            if(index >= size*(size-1)) openedLastRow.add(index);
        }
    }

    public boolean isOpen(int row, int col)  // is site (row, col) open?
    {
        if(!isValidSite(row, col)) throw new IndexOutOfBoundsException("row or col is out of bound, row="+row + " col="+col);
        return openedSite[getSiteIndex(row, col)];
    }

    public boolean isFull(int row, int col)  // is site (row, col) full?
    {
        if(!isValidSite(row, col)) throw new IndexOutOfBoundsException("row or col is out of bound, row="+row + " col="+col);
        int index = getSiteIndex(row, col);
        // check if it's connected to top row
        if(isOpen(row, col)){ // itself should open
            for(int item: openedFirstRow){
                if(site.connected(item, index))return true;
            }

            /* scan last row
            for(int i=1;i<=size;i++){
                if(isOpen(1, i) && site.connected(index, i-1)) return true; // top row should open and they should connect
            }
            */
        }
        return false;
    }

    public int numberOfOpenSites()       // number of open sites
    {
        return numberOfOpenSites;
    }

    public boolean percolates()              // does the system percolate?
    {
        //check if any of site in last row is full
        for(int item: openedLastRow){
            if(isFull(size, 1 + item%size))return true;
        }
        /*
        for(int i=1;i<=size;i++){
            if(isFull(size, i)) return true;
        }
        */
        return false;
    }

    private double getThreshold(){
        int n = size, total = n*n;
        int[] blockedSites= new int[total];
        for(int i=0;i<total;i++) blockedSites[i] = i;
        StdRandom.shuffle(blockedSites);

        for(int i=0;i<total;i++) {
            int row = 1 + blockedSites[i] / n, col = 1 + blockedSites[i] % n;
            open(row, col);
            if(i>=n && percolates()){
                //System.out.println("open sites=" + p.numberOfOpenSites + " total="+total + " percent="+numberOfOpenSites*1.0/total);
                return numberOfOpenSites*1.0/total;
            }
        }
        return 0; //should never reach here
    }


    public static void main(String[] args)   // test client (optional)
    {
        int n=200;
        Stopwatch time = new Stopwatch();
        Percolation p = new Percolation(n);
        System.out.println(p.getThreshold());
        System.out.println(String.format("%-25s= ","Time") + time.elapsedTime());
    }

}


/*
this solution has a bug, when a middle one is opened which connects top and bottom, but last row is not updated.


private boolean[] isOpen;
private boolean[] isFull;
private boolean isPercolates;

for each unopened cell:
// 1. open(row, col)
// open cell: set isOpen[index] = true;
// union neighbours,  union(p, q) , at most 4* logN

// if first row, set it's full.
        isGroupFull= false;
        if(index <= n) {
        isFull[index] = true;
        isGroupFull = true;
        }

// check with neighbours, if any of them isFull, set all of them(5) to be full.
        if(isFull(left)) isGroupFull = true;
        if(isFull(right)) isGroupFull = true;
        if(isFull(up)) isGroupFull = true;
        if(isFull(down)) isGroupFull = true;

        if(isGroupFull) {
        isFull[left] =true;
        isFull[right] =true;
        isFull[up] =true;
        isFull[down] =true;

        // if the index from any of the 5 are >= n(n-1) and they are full, set isPercolates=true;
        if(left_index >= n*(n-1)) isPercolates=true;
        if(right_index >= n*(n-1)) isPercolates=true;
        if(up_index >= n*(n-1)) isPercolates=true;
        if(down_index >= n*(n-1)) isPercolates=true;
        }


// 2. isPercolates()

*/



/*
public class Percolation {
    public Percolation(int n)                // create n-by-n grid, with all sites blocked
    public void open(int row, int col)    // open site (row, col) if it is not open already
    public boolean isOpen(int row, int col)  // is site (row, col) open?
    public boolean isFull(int row, int col)  // is site (row, col) full?  use connected(p,q) to check itself against first row o(n)
    public int numberOfOpenSites()       // number of open sites
    public boolean percolates()              // does the system percolate? isFull on last row o(n^2)

    public static void main(String[] args)   // test client (optional)


    //solution 1, store openedFirstRow and opendedLastRow.
    // isFull(), use connected(p, each_item_in_openedFirstRow)


    // percolates(),  use connected(each_item_in_openedLastRow, each_item_in_openedFirstRow)
    // call isFull() on each_item_in_openedLastRow.
    // pruning, only check percolates when numberofOpenSites >=n ;





}

*/