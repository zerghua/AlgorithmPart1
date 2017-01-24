// some pseudo code

test client:
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