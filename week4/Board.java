import java.util.ArrayList;
import edu.princeton.cs.algs4.StdOut;

public class Board {
    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private final int dimension;
    private int[] tiles;
    private int gapIndex;

    public Board(int[][] tiles2D) {
        dimension = tiles2D.length;
        initialiseTilesAndSaveGapIndex(tiles2D);
    }

    private void initialiseTilesAndSaveGapIndex(int[][] tiles2D) {
        tiles = new int[dimension * dimension];
        for (int row = 1; row <= dimension; row++) {
            for (int col = 1; col <= dimension; col++) {
                int val = tiles2D[row - 1][col - 1];
                setTileValue(row, col, val);
                if (val == 0)
                    gapIndex = convertTo1DIndex(row, col);
            }
        }
    }

    private void setTileValue(int row, int col, int val) {
        setTileValue(convertTo1DIndex(row, col), val);
    }

    private void setTileValue(int index, int val) {
        tiles[index] = val;
    }

    private int getGoalTileValue(int row, int col) {
        return 1 + convertTo1DIndex(row, col);
    }

    public String toString() {
        String str = "" + dimension + "\n";
        for (int row = 1; row <= dimension; row++) {
            for (int col = 1; col <= dimension; col++)
                str += getTileValue(row, col) + (col == dimension ? "" : " ");
            str += (row == dimension ? "" : "\n");
        }
        return str;
    }

    public int dimension() {
        return dimension;
    }

    /**
     * Returns the Manhattan distance between the board and the goal board, i.e. the sum of the
     * Manhattan distances (sum of the vertical and horizontal distance) from the tiles to their
     * goal positions.
     */

    public int manhattan() {
        int distance = 0;
        for (int row = 1; row <= dimension; row++)
            for (int col = 1; col <= dimension; col++)
                if (!isTileBlank(row, col))
                    distance += manhattanDistanceToGoalCoords(row, col);
        return distance;
    }

    private int manhattanDistanceToGoalCoords(int row, int col) {
        int[] goalCoords = getGoalTileCoords(row, col);
        return Math.abs(row - goalCoords[0]) + Math.abs(col - goalCoords[1]);
    }

    private int[] getGoalTileCoords(int row, int col) {
        return convertTo2DCoords(getGoalTileIndex(row, col));
    }

    private int getGoalTileIndex(int row, int col) {
        int index = getTileValue(row, col) - 1;
        // Handle the blank tile
        if (index == -1)
            index = dimension * dimension - 1;
        return index;
    }

    public boolean isGoal() {
        return hamming() == 0;
    }

    /**
     * Returns the Hamming distance between the board and the goal board, i.e. the number of tiles
     * out of place.
     */

    public int hamming() {
        int distance = 0;
        for (int row = 1; row <= dimension; row++)
            for (int col = 1; col <= dimension; col++)
                if (!isTileBlank(row, col) && !isTileGoalTile(row, col))
                    distance++;
        return distance;
    }

    private boolean isTileBlank(int row, int col) {
        return convertTo1DIndex(row, col) == gapIndex;
    }

    private boolean isTileGoalTile(int row, int col) {
        return getTileValue(row, col) == getGoalTileValue(row, col);
    }

    public boolean equals(Object y) {
        if (y == this)
            return true;
        if (y == null)
            return false;
        if (y.getClass() != this.getClass())
            return false;
        Board that = (Board) y;
        if (that.dimension() != dimension)
            return false;
        for (int row = 1; row <= dimension; row++)
            for (int col = 1; col <= dimension; col++)
                if (getTileValue(row, col) != that.getTileValue(row, col))
                    return false;
        return true;
    }

    /**
     * Returns an iterable containing the neighbors of the board. Two boards are said to be
     * neighbors iff a single tile move from one board can make the two boards equal. Depending on
     * the location of the blank square, a board can have 2, 3, or 4 neighbors.
     */

    public Iterable<Board> neighbors() {
        ArrayList<Board> neighBoards = new ArrayList<>();
        // Instead of imagining a tile moving to a position, imagine that the gap is moving in the
        // reverse direction to the tile's original position.
        if (canGapMove(Direction.UP))
            neighBoards.add(neighbor(Direction.UP));
        if (canGapMove(Direction.RIGHT))
            neighBoards.add(neighbor(Direction.RIGHT));
        if (canGapMove(Direction.DOWN))
            neighBoards.add(neighbor(Direction.DOWN));
        if (canGapMove(Direction.LEFT))
            neighBoards.add(neighbor(Direction.LEFT));
        return neighBoards;
    }

    private boolean canGapMove(Direction direction) {
        int[] gapCoords = convertTo2DCoords(gapIndex);
        int gapRow = gapCoords[0];
        int gapCol = gapCoords[1];
        switch (direction) {
            case UP:
                return gapRow - 1 >= 1;
            case RIGHT:
                return gapCol + 1 <= dimension;
            case DOWN:
                return gapRow + 1 <= dimension;
            case LEFT:
                return gapCol - 1 >= 1;
            default:
                return false;
        }
    }

    /**
     * Returns a neighbor board which is obtained from a single move by a tile adjacent to the gap.
     * E.g. right neighbor is the board that we get after swapping the gap with the tile to the
     * right of it. Right neighbor does not exist if such a move is illegal.
     */

    private Board neighbor(Direction dir) {
        Board nb = createClone();
        int[] gapCoords = convertTo2DCoords(gapIndex);
        int gapRow, gapCol, tileRow, tileCol;
        gapRow = tileRow = gapCoords[0];
        gapCol = tileCol = gapCoords[1];
        if (dir == Direction.UP)
            tileRow--;
        else if (dir == Direction.RIGHT)
            tileCol++;
        else if (dir == Direction.DOWN)
            tileRow++;
        else if (dir == Direction.LEFT)
            tileCol--;
        nb.swapTilesAndUpdateGapIndex(tileRow, tileCol, gapRow, gapCol);
        return nb;
    }

    /**
     * @return A board that is obtained by exchanging any pair of tiles
     */

    public Board twin() {
        Board twin = createClone();
        if (gapIndex > 1)
            twin.swapTilesAndUpdateGapIndex(0, 1);
        else
            twin.swapTilesAndUpdateGapIndex(gapIndex + 1, gapIndex + 2);
        return twin;
    }

    private void swapTilesAndUpdateGapIndex(int row1, int col1, int row2, int col2) {
        swapTilesAndUpdateGapIndex(convertTo1DIndex(row1, col1), convertTo1DIndex(row2, col2));
    }

    private void swapTilesAndUpdateGapIndex(int i, int j) {
        int t = tiles[i];
        tiles[i] = tiles[j];
        tiles[j] = t;
        if (i == gapIndex)
            gapIndex = j;
        if (j == gapIndex)
            gapIndex = i;
    }

    /**
     * Creates and returns a new Board which .equals() this board.
     */

    private Board createClone() {
        int[][] tiles2D = new int[dimension][dimension];
        for (int row = 1; row <= dimension; row++)
            for (int col = 1; col <= dimension; col++)
                tiles2D[row - 1][col - 1] = getTileValue(row, col);
        Board clone = new Board(tiles2D);
        return clone;
    }

    private int getTileValue(int row, int col) {
        return tiles[convertTo1DIndex(row, col)];
    }

    /**
     * @param row (between 1 and dimension)
     * @param col (between 1 and dimension)
     * @return 1-d array index (between 0 and dimension ^ 2 - 1)
     */

    private int convertTo1DIndex(int row, int col) {
        return dimension * (row - 1) + (col - 1);
    }

    /**
     * @param index (between 0 and dimension ^ 2 - 1)
     * @return Coordinates array, {row, col}, of 2 ints
     */

    private int[] convertTo2DCoords(int index) {
        return new int[] {1 + index / dimension, 1 + index % dimension};
    }

    public static void main(String[] args) {
        int[][] tiles1 = {{8, 0, 3}, {4, 2, 7}, {6, 5, 1}};
        Board board = new Board(tiles1);
        StdOut.println(board);
        StdOut.println("Hamming distance: " + board.hamming());
        StdOut.println("Manhattan distance: " + board.manhattan());
        StdOut.println("Neighbors:");
        for (Board nb : board.neighbors())
            StdOut.println(nb);
        StdOut.println("A twin:");
        StdOut.println(board.twin());
    }
}
