// some pseudo code

test client:


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

