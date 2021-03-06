import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private static final boolean OPEN = true;
    private static final boolean BLOCKED = !OPEN;
    private static final int TOP_VIRTUAL_INDEX = 0;
    private final WeightedQuickUnionUF forest1, forest2;
    private boolean[] sites; // sites[index] indicates whether a site is OPEN or BLOCKED
    private int openSiteCount;

    /**
     * Creates an n-by-n grid, with all sites initially blocked.
     * 
     * @param n - Grid size
     */

    public Percolation(int n) {
        validateGridSize(n);
        forest1 = new WeightedQuickUnionUF(1 + n * n); // With top virtual node
        forest2 = new WeightedQuickUnionUF(2 + n * n); // With both virtual nodes
        initialiseGrid(n);
    }

    private void validateGridSize(int n) {
        if (n < 1)
            throw new IllegalArgumentException("Grid size must be at least 1.");
    }

    private void initialiseGrid(int gridSize) {
        sites = new boolean[gridSize * gridSize + 2];
        sites[TOP_VIRTUAL_INDEX] = OPEN;
        for (int i = 1; i < bottomVirtualIndex(); i += 1)
            sites[i] = BLOCKED;
        sites[bottomVirtualIndex()] = OPEN;
    }

    /**
     * Opens the site (row, col) if it is not already open.
     */

    public void open(int row, int col) {
        if (!isOpen(row, col)) {
            int siteIndex = convertTo1DIndex(row, col);
            openSite(siteIndex);
            openSiteCount++;
            if (siteIsInFirstRow(row))
                connectSiteToTopVirtual(siteIndex);
            if (siteIsInLastRow(row))
                connectSiteToBottomVirtual(siteIndex);
            connectSiteToNeighboringOpenSites(row, col, siteIndex);
        }
    }

    private void openSite(int siteIndex) {
        sites[siteIndex] = OPEN;
    }

    private boolean siteIsInFirstRow(int row) {
        return row == 1;
    }

    private boolean siteIsInLastRow(int row) {
        return row == gridSize();
    }

    private void connectSiteToTopVirtual(int siteIndex) {
        forest1.union(TOP_VIRTUAL_INDEX, siteIndex);
        forest2.union(TOP_VIRTUAL_INDEX, siteIndex);
    }

    private void connectSiteToBottomVirtual(int siteIndex) {
        forest2.union(bottomVirtualIndex(), siteIndex);
    }

    private void connectSiteToNeighboringOpenSites(int row, int col, int siteIndex) {
        // Each delta represents the {vertical, horizontal} distance between the site and one of its
        // neighbors (4 max).
        int[][] deltas = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        for (int[] delta : deltas) {
            int i = row + delta[0];
            int j = col + delta[1];
            if (areValidCoordinates(i, j) && isOpen(i, j)) {
                forest1.union(convertTo1DIndex(i, j), siteIndex);
                forest2.union(convertTo1DIndex(i, j), siteIndex);
            }
        }
    }

    public boolean isFull(int row, int col) {
        if (!isOpen(row, col) || openSiteCount < row)
            return false;
        // To prevent backwash, we need to ignore the forest with the bottom virtual.
        return forest1.connected(TOP_VIRTUAL_INDEX, convertTo1DIndex(row, col));
    }

    public boolean isOpen(int row, int col) {
        validateCoordinates(row, col);
        return sites[convertTo1DIndex(row, col)] == OPEN;
    }

    private void validateCoordinates(int row, int col) {
        if (!areValidCoordinates(row, col))
            throw new IllegalArgumentException("One of the arguments is outside the range [1, n].");
    }

    private boolean areValidCoordinates(int row, int col) {
        return (row >= 1 && row <= gridSize() && col >= 1 && col <= gridSize());
    }

    private int convertTo1DIndex(int row, int col) {
        return (gridSize() * (row - 1) + col);
    }

    public int numberOfOpenSites() {
        return openSiteCount;
    }

    public boolean percolates() {
        if (openSiteCount < gridSize())
            return false;
        // System percolates if bottom virtual is connected to top virtual
        return forest2.connected(TOP_VIRTUAL_INDEX, bottomVirtualIndex());
    }

    private int bottomVirtualIndex() {
        return gridSize() * gridSize() + 1;
    }

    private int gridSize() {
        return (int) Math.sqrt(sites.length - 2);
    }
}
