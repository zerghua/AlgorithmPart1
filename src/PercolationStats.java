import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.algs4.StdRandom;

public class PercolationStats {
    private double[] ret ;
    private int size ;
    private int T;

    private double getThreshold(int n){
        Percolation p = new Percolation(n);
        int total = n*n;
        int[] blockedSites= new int[total];
        for(int i=0;i<total;i++) blockedSites[i] = i;
        StdRandom.shuffle(blockedSites);

        for(int i=0;i<total;i++) {
            int row = 1 + blockedSites[i] / n, col = 1 + blockedSites[i] % n;
            p.open(row, col);
            if(p.percolates()){
                return p.numberOfOpenSites()*1.0/total;
            }
        }
        return 0; //should never reach here
    }

    public PercolationStats(int n, int trials)    // perform trials independent experiments on an n-by-n grid
    {
        if(n<=0 || trials <=0) throw new IllegalArgumentException("n and trails should >0");
        ret = new double[trials];
        size = n;
        T = trials;
        for(int i=0;i<trials;i++){
            ret[i] = getThreshold(n);
        }
    }

    public double mean()                          // sample mean of percolation threshold
    {
        return StdStats.mean(ret);
    }
    public double stddev()                        // sample standard deviation of percolation threshold
    {
        return StdStats.stddev(ret);
    }
    public double confidenceLo()                  // low  endpoint of 95% confidence interval
    {
        return mean() - 1.96*stddev() / Math.sqrt(T);
    }
    public double confidenceHi()                  // high endpoint of 95% confidence interval
    {
        return mean() + 1.96*stddev() / Math.sqrt(T);
    }

    public static void main(String[] args)        // test client (described below)
    {
        if(args.length != 2) {
            throw new IllegalArgumentException("usage: java PercolationStats 20 10 ");
        }

        Stopwatch time = new Stopwatch();
        PercolationStats ps = new PercolationStats(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        System.out.println(String.format("%-25s= ","mean") + ps.mean());
        System.out.println(String.format("%-25s= ","stddev") + ps.stddev());
        System.out.println(String.format("%-25s= ","95% confidence interval") + ps.confidenceLo() + ", "+ ps.confidenceHi());

        System.out.println(String.format("%-25s= ","Time") + time.elapsedTime());


    }
}