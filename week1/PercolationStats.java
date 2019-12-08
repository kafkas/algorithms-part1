import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private final static double CONFIDENCE_95 = 1.96;
    private double[] results;
    private double resultsSampleMean;
    private double resultsSampleStdDev;
    private double resultsConfidenceLo;
    private double resultsConfidenceHi;

    public PercolationStats(int n, int trials) {
        validateGridSize(n);
        validateTrialCount(trials);
        performTrialsAndSaveResults(n, trials);
        computeAndSaveResultStats();
    }

    private void validateGridSize(int gridSize) {
        if (gridSize < 1)
            throw new IllegalArgumentException("Grid size must be at least 1.");
    }

    private void validateTrialCount(int trialCount) {
        if (trialCount < 1)
            throw new IllegalArgumentException("Trial count must be at least 1.");
    }

    private void performTrialsAndSaveResults(int gridSize, int trialCount) {
        results = new double[trialCount];
        for (int i = 0; i < trialCount; i++) {
            Percolation percolation = new Percolation(gridSize);
            while (!percolation.percolates())
                openARandomBlockedSite(percolation, gridSize);
            results[i] = getOpenSiteFraction(percolation, gridSize);
        }
    }

    private void openARandomBlockedSite(Percolation percolation, int gridSize) {
        int row;
        int col;
        do {
            row = StdRandom.uniform(1, gridSize + 1);
            col = StdRandom.uniform(1, gridSize + 1);
        } while (percolation.isOpen(row, col));
        percolation.open(row, col);
    }

    private double getOpenSiteFraction(Percolation percolation, int gridSize) {
        return (double) percolation.numberOfOpenSites() / (double) (gridSize * gridSize);
    }

    private void computeAndSaveResultStats() {
        resultsSampleMean = StdStats.mean(results);
        resultsSampleStdDev = StdStats.stddev(results);
        resultsConfidenceLo = resultsSampleMean
                - (CONFIDENCE_95 * resultsSampleStdDev) / Math.sqrt(results.length);
        resultsConfidenceHi = resultsSampleMean
                + (CONFIDENCE_95 * resultsSampleStdDev) / Math.sqrt(results.length);
    }

    public double mean() {
        return resultsSampleMean;
    }

    public double stddev() {
        return resultsSampleStdDev;
    }

    public double confidenceLo() {
        return resultsConfidenceLo;
    }

    public double confidenceHi() {
        return resultsConfidenceHi;
    }

    public static void main(String[] args) {
        int gridSize = Integer.parseInt(args[0]);
        int trialCount = Integer.parseInt(args[1]);
        PercolationStats stats = new PercolationStats(gridSize, trialCount);
        printStats(stats);
    }

    private static void printStats(PercolationStats stats) {
        StdOut.println("mean = " + stats.mean());
        StdOut.println("stddev = " + stats.stddev());
        StdOut.println("95% confidence interval = [" + stats.confidenceLo() + ", "
                + stats.confidenceHi() + "]");
    }
}
