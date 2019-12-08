import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final static boolean OPEN = true;
    private final static boolean BLOCKED = false;
    private final static int TOP_VIRTUAL_INDEX = 0;
    private final int gridSize;
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
        gridSize = n;
        forest1 = new WeightedQuickUnionUF(1 + gridSize * gridSize); // With top virtual node
        forest2 = new WeightedQuickUnionUF(2 + gridSize * gridSize); // With both virtual nodes
        initializeGrid();
    }

    private void validateGridSize(int n) {
        if (n < 1)
            throw new IllegalArgumentException("Grid size must be at least 1.");
    }

    private void initializeGrid() {
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
            incrementOpenSiteCount();
            if (siteIsInFirstRow(row))
                connectSiteToTopVirtual(siteIndex);
            if (siteIsInLastRow(row))
                connectSiteToBottomVirtual(siteIndex);
            connectSiteToNeighboringOpenSites(row, col, siteIndex);
        }
    }

    public boolean isOpen(int row, int col) {
        validateCoordinates(row, col);
        return sites[convertTo1DIndex(row, col)];
    }

    private void validateCoordinates(int row, int col) {
        if (!areValidCoordinates(row, col))
            throw new IllegalArgumentException("One of the arguments is outside the range [1, n].");
    }

    private int convertTo1DIndex(int row, int col) {
        return (gridSize * (row - 1) + col);
    }

    private void openSite(int siteIndex) {
        sites[siteIndex] = OPEN;
    }

    private void incrementOpenSiteCount() {
        openSiteCount++;
    }

    private boolean siteIsInFirstRow(int row) {
        return row == 1;
    }

    private boolean siteIsInLastRow(int row) {
        return row == gridSize;
    }

    private void connectSiteToTopVirtual(int siteIndex) {
        forest1.union(TOP_VIRTUAL_INDEX, siteIndex);
        forest2.union(TOP_VIRTUAL_INDEX, siteIndex);
    }

    private void connectSiteToBottomVirtual(int siteIndex) {
        forest2.union(bottomVirtualIndex(), siteIndex);
    }

    private int bottomVirtualIndex() {
        return gridSize * gridSize + 1;
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

    private boolean areValidCoordinates(int row, int col) {
        return (row >= 1 && row <= gridSize && col >= 1 && col <= gridSize);
    }

    public boolean isFull(int row, int col) {
        validateCoordinates(row, col);
        if (!isOpen(row, col) || openSiteCount < row)
            return false;
        // To prevent backwash, we need to ignore the forest with the bottom virtual.
        return forest1.connected(TOP_VIRTUAL_INDEX, convertTo1DIndex(row, col));
    }

    public int numberOfOpenSites() {
        return openSiteCount;
    }

    public boolean percolates() {
        if (openSiteCount < gridSize)
            return false;
        // System percolates if bottom virtual is connected to top virtual
        return forest2.connected(TOP_VIRTUAL_INDEX, bottomVirtualIndex());
    }
}
