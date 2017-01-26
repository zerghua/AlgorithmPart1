import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import edu.princeton.cs.algs4.Stopwatch;

/*

naive implementation, check each openedLastRow with each openedFirstRow for percolation, will incur o(n^2) for such check


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

faster implementation(100x faster)
 > java PercolationStats 200 20
mean                     = 0.59133375
stddev                   = 0.0072390855912448406
95% confidence interval  = 0.5903304639238128, 0.5923370360761872
Time                     = 0.09


0 1 2 3
4 5 6 7
8 9 10 11
12 13 14 15


public class Percolation {
    private boolean[] openedSite;
    private WeightedQuickUnionUF site;
    private int numberOfOpenSites=0;
    private int size;

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
    }

    public void open(int row, int col)    // open site (row, col) if it is not open already
    {
        if(!isValidSite(row, col)) throw new IndexOutOfBoundsException("row or col is out of bound, row="+row + " col="+col);
        int index = getSiteIndex(row, col);
        if(openedSite[index] == false) { // if not open
            openedSite[index] = true;
            numberOfOpenSites++;

            // union with neighbours if they are open
            if(isValidSite(row-1, col) && isOpen(row-1, col)) site.union(index, getSiteIndex(row-1, col));
            if(isValidSite(row+1, col) && isOpen(row+1, col)) site.union(index, getSiteIndex(row+1, col));
            if(isValidSite(row, col+1) && isOpen(row, col+1)) site.union(index, getSiteIndex(row, col+1));
            if(isValidSite(row, col-1) && isOpen(row, col-1)) site.union(index, getSiteIndex(row, col-1));
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
            for(int i=1;i<=size;i++){
                if(isOpen(1, i) && site.connected(index, i-1)) return true; // top row should open and they should connect
            }

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
        for(int i=1;i<=size;i++){
            if(isFull(size, i)) return true;
        }
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

 */


// smart implementation
// use sentinel variable top/bottom for faster check. this is effectively like have a list of elements.
// running from above 1s to 0.038s for n=200
// however this solution has a bug named backwash, which means once top and bottom are connected,
// open another bottom row cell which is not full will cause isFull() give false full result.
// to solve this bug, use two UF objects, an extra one doesn't union last row when opened which used to check isFull().
/*
ASSESSMENT SUMMARY
Compilation: PASSED
API: PASSED
Findbugs: PASSED
Checkstyle: FAILED (92 warnings)
Correctness: 22/26 tests passed
Memory: 8/8 tests passed
Timing: 9/9 tests passed
Aggregate score: 90.77% [Compilation: 5%, API: 5%, Findbugs: 0%, Checkstyle: 0%, Correctness: 60%, Memory: 10%, Timing: 20%]

public class Percolation {
    private boolean[][] openedSite; //
    private WeightedQuickUnionUF site;
    private int numberOfOpenSites, size, top, bottom;

    public Percolation(int n)                // create n-by-n grid, with all sites blocked
    {
        if(n<=0) throw new IllegalArgumentException("n is less than 1");
        size=n;
        numberOfOpenSites=0;
        openedSite = new boolean[size+1][size+1];
        site = new WeightedQuickUnionUF(size*size+2);
        top = 0;
        bottom = size*size+1;
    }

    public void open(int row, int col)    // open site (row, col) if it is not open already
    {
        if(!isValidSite(row, col)) throw new IndexOutOfBoundsException("row or col is out of bound, row="+row + " col="+col);
        if(openedSite[row][col] == true) return;  //we don't do anything if it's opened already

        int index = getSiteIndex(row, col);
        openedSite[row][col] = true;
        numberOfOpenSites++;

        // key to faster implementation
        if(row == 1) site.union(top, getSiteIndex(row, col));
        if(row == size) site.union(bottom, getSiteIndex(row, col));

        // union with neighbours if they are open
        if(isValidSite(row-1, col) && isOpen(row-1, col)) site.union(index, getSiteIndex(row-1, col));
        if(isValidSite(row+1, col) && isOpen(row+1, col)) site.union(index, getSiteIndex(row+1, col));
        if(isValidSite(row, col+1) && isOpen(row, col+1)) site.union(index, getSiteIndex(row, col+1));
        if(isValidSite(row, col-1) && isOpen(row, col-1)) site.union(index, getSiteIndex(row, col-1));

    }

    public boolean isOpen(int row, int col)  // is site (row, col) open?
    {
        if(!isValidSite(row, col)) throw new IndexOutOfBoundsException("row or col is out of bound, row="+row + " col="+col);
        return openedSite[row][col];
    }

    public boolean isFull(int row, int col)  // is site (row, col) full?
    {
        if(!isValidSite(row, col)) throw new IndexOutOfBoundsException("row or col is out of bound, row="+row + " col="+col);
        return site.connected(getSiteIndex(row, col), top);
    }

    public int numberOfOpenSites()       // number of open sites
    {
        return numberOfOpenSites;
    }

    public boolean percolates()              // does the system percolate?
    {
        return site.connected(top, bottom);
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
                return numberOfOpenSites*1.0/total;
            }
        }
        return 0; //should never reach here
    }

    private boolean isValidSite(int row, int col){
        if(row <1 || row >size|| col <1 || col >size) return false;
        return true;
    }

    // index of cell is between [1, n*n], 0 and n*n + 1 are reserved as sentinels.
    private int getSiteIndex(int row, int col){
        return size * (row - 1) + col;
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
*/


// two UF objects to fix backwash problem. works.
/*
public class Percolation {
    private boolean[][] openedSite; //
    private WeightedQuickUnionUF site, siteNoBottom;
    private int numberOfOpenSites, size, top, bottom;

    public Percolation(int n)                // create n-by-n grid, with all sites blocked
    {
        if(n<=0) throw new IllegalArgumentException("n is less than 1");
        size=n;
        numberOfOpenSites=0;
        openedSite = new boolean[size+1][size+1];
        site = new WeightedQuickUnionUF(size*size+2);
        siteNoBottom = new WeightedQuickUnionUF(size*size+1);
        top = 0;
        bottom = size*size+1;
    }

    public void open(int row, int col)    // open site (row, col) if it is not open already
    {
        if(!isValidSite(row, col)) throw new IndexOutOfBoundsException("row or col is out of bound, row="+row + " col="+col);
        if(openedSite[row][col] == true) return;  //we don't do anything if it's opened already

        int index = getSiteIndex(row, col);
        openedSite[row][col] = true;
        numberOfOpenSites++;

        // key to faster implementation
        if(row == 1) {
            site.union(top, getSiteIndex(row, col));
            siteNoBottom.union(top, getSiteIndex(row, col));
        }
        if(row == size) site.union(bottom, getSiteIndex(row, col));

        // union with neighbours if they are open
        if(isValidSite(row-1, col) && isOpen(row-1, col)) {
            site.union(index, getSiteIndex(row-1, col));
            siteNoBottom.union(index, getSiteIndex(row-1, col));
        }
        if(isValidSite(row+1, col) && isOpen(row+1, col)) {
            site.union(index, getSiteIndex(row+1, col));
            siteNoBottom.union(index, getSiteIndex(row+1, col));
        }
        if(isValidSite(row, col+1) && isOpen(row, col+1)) {
            site.union(index, getSiteIndex(row, col+1));
            siteNoBottom.union(index, getSiteIndex(row, col+1));
        }
        if(isValidSite(row, col-1) && isOpen(row, col-1)) {
            site.union(index, getSiteIndex(row, col-1));
            siteNoBottom.union(index, getSiteIndex(row, col-1));
        }

    }

    public boolean isOpen(int row, int col)  // is site (row, col) open?
    {
        if(!isValidSite(row, col)) throw new IndexOutOfBoundsException("row or col is out of bound, row="+row + " col="+col);
        return openedSite[row][col];
    }

    public boolean isFull(int row, int col)  // is site (row, col) full?
    {
        if(!isValidSite(row, col)) throw new IndexOutOfBoundsException("row or col is out of bound, row="+row + " col="+col);
        return siteNoBottom.connected(getSiteIndex(row, col), top);
    }

    public int numberOfOpenSites()       // number of open sites
    {
        return numberOfOpenSites;
    }

    public boolean percolates()              // does the system percolate?
    {
        return site.connected(top, bottom);
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
                return numberOfOpenSites*1.0/total;
            }
        }
        return 0; //should never reach here
    }

    private boolean isValidSite(int row, int col){
        if(row <1 || row >size|| col <1 || col >size) return false;
        return true;
    }

    // index of cell is between [1, n*n], 0 and n*n + 1 are reserved as sentinels.
    private int getSiteIndex(int row, int col){
        return size * (row - 1) + col;
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

*/


// a better solution: less memory.  one UF object with status of isConnectedToTop and isConnectedToBottom
// each open, check isConnectedToTop[index] && isConnectedToBottom[index];
// the key is that isConnectedToTop and isConnectedToBottom maintains the status of each groups' root.
/*

ASSESSMENT SUMMARY
Compilation: PASSED
API: PASSED
Findbugs: PASSED
Checkstyle: FAILED (109 warnings)
Correctness: 26/26 tests passed
Memory: 9/8 tests passed
Timing: 9/9 tests passed
Aggregate score: 101.25% [Compilation: 5%, API: 5%, Findbugs: 0%, Checkstyle: 0%, Correctness: 60%, Memory: 10%, Timing: 20%]

*/
public class Percolation {
    private boolean[] isOpened, isConnectedToTop, isConnectedToBottom;
    private WeightedQuickUnionUF uf;
    private int numberOfOpenSites, size;
    private boolean isPercolate;

    public Percolation(int n)                // create n-by-n grid, with all sites blocked
    {
        if(n<=0) throw new IllegalArgumentException("n is less than 1");
        size=n;
        numberOfOpenSites=0;
        isOpened =new boolean[size*size];
        isConnectedToTop = new boolean[size*size];
        isConnectedToBottom = new boolean[size*size];
        uf = new WeightedQuickUnionUF(size*size);
        isPercolate = false;
    }

    public void open(int row, int col)    // open site (row, col) if it is not open already
    {
        if(!isValidSite(row, col)) throw new IndexOutOfBoundsException("row or col is out of bound, row="+row + " col="+col);
        if(isOpened[getSiteIndex(row, col)] == true) return;  //we don't do anything if it's opened already

        int index = getSiteIndex(row, col);
        isOpened[index] = true;
        numberOfOpenSites++;
        boolean top = false, bottom = false;

        // union with neighbours if they are open, and get status
        if(isValidSite(row-1, col) && isOpen(row-1, col)) { // top
            int neighbour_index = index - size ;
            if(isConnectedToTop[uf.find(neighbour_index)]  || isConnectedToTop[uf.find(index)]) top = true;
            if(isConnectedToBottom[uf.find(neighbour_index)]  || isConnectedToBottom[uf.find(index)]) bottom = true;
            uf.union(index, neighbour_index);
        }
        if(isValidSite(row+1, col) && isOpen(row+1, col)) { // bottom
            int neighbour_index = index + size ;
            if(isConnectedToTop[uf.find(neighbour_index)]  || isConnectedToTop[uf.find(index)]) top = true;
            if(isConnectedToBottom[uf.find(neighbour_index)]  || isConnectedToBottom[uf.find(index)]) bottom = true;
            uf.union(index, neighbour_index);
        }
        if(isValidSite(row, col+1) && isOpen(row, col+1)) { // right
            int neighbour_index = index + 1 ;
            if(isConnectedToTop[uf.find(neighbour_index)]  || isConnectedToTop[uf.find(index)]) top = true;
            if(isConnectedToBottom[uf.find(neighbour_index)]  || isConnectedToBottom[uf.find(index)]) bottom = true;
            uf.union(index, neighbour_index);
        }
        if(isValidSite(row, col-1) && isOpen(row, col-1)) { // left
            int neighbour_index = index - 1 ;
            if(isConnectedToTop[uf.find(neighbour_index)]  || isConnectedToTop[uf.find(index)]) top = true;
            if(isConnectedToBottom[uf.find(neighbour_index)]  || isConnectedToBottom[uf.find(index)]) bottom = true;
            uf.union(index, neighbour_index);
        }

        if(row == 1) top = true;
        if(row == size) bottom = true;

        // update status
        isConnectedToTop[uf.find(index)] = top;
        isConnectedToBottom[uf.find(index)] = bottom;

        if(top && bottom) isPercolate = true;
    }

    public boolean isOpen(int row, int col)  // is site (row, col) open?
    {
        if(!isValidSite(row, col)) throw new IndexOutOfBoundsException("row or col is out of bound, row="+row + " col="+col);
        return isOpened[getSiteIndex(row, col)];
    }

    public boolean isFull(int row, int col)  // is site (row, col) full?
    {
        if(!isValidSite(row, col)) throw new IndexOutOfBoundsException("row or col is out of bound, row="+row + " col="+col);
        return isConnectedToTop[uf.find(getSiteIndex(row, col))];
    }

    public int numberOfOpenSites()       // number of open sites
    {
        return numberOfOpenSites;
    }

    public boolean percolates()              // does the system percolate?
    {
        return isPercolate;
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
                return numberOfOpenSites*1.0/total;
            }
        }
        return 0; //should never reach here
    }

    private boolean isValidSite(int row, int col){
        if(row <1 || row >size|| col <1 || col >size) return false;
        return true;
    }

    // index of cell is between [0, n*n-1]
    private int getSiteIndex(int row, int col){
        return size * (row - 1) + col - 1;
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

