import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.ResizingArrayStack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {
    private boolean isInitialBoardSolvable;
    private int minMoves = -1;
    private ResizingArrayStack<Board> boardStack;

    /**
     * Finds a solution to the initial board using the A* algorithm
     */

    public Solver(Board initialBoard) {
        validateInitialBoard(initialBoard);
        solveBoardWithAStarAlgo(initialBoard);
    }

    private void validateInitialBoard(Board board) {
        if (board == null)
            throw new IllegalArgumentException("Initial board may not be null.");
    }

    private void solveBoardWithAStarAlgo(Board board) {
        MinPQ<SearchNode> pq1 = new MinPQ<>();
        SearchNode node1 = new SearchNode(board, null, 0);
        boolean isNode1Goal = node1.board.isGoal();
        MinPQ<SearchNode> pq2 = new MinPQ<>();
        SearchNode node2 = new SearchNode(board.twin(), null, 0);
        boolean isNode2Goal = node2.board.isGoal();
        while (!isNode1Goal && !isNode2Goal) {
            addNeighborNodesToPQ(node1, pq1);
            node1 = pq1.delMin();
            isNode1Goal = node1.board.isGoal();
            if (isNode1Goal)
                break;
            addNeighborNodesToPQ(node2, pq2);
            node2 = pq2.delMin();
            isNode2Goal = node2.board.isGoal();
            if (isNode2Goal)
                break;
        }
        isInitialBoardSolvable = isNode1Goal;
        if (isInitialBoardSolvable) {
            minMoves = node1.moveCount;
            createSolutionBoardStack(node1);
        }
    }

    private void addNeighborNodesToPQ(SearchNode node, MinPQ<SearchNode> pq) {
        for (Board neighbor : node.board.neighbors())
            if (node.prevNode == null || !neighbor.equals(node.prevNode.board))
                pq.insert(new SearchNode(neighbor, node, node.moveCount + 1));
    }

    private void createSolutionBoardStack(SearchNode node1) {
        boardStack = new ResizingArrayStack<Board>();
        SearchNode cur = node1;
        while (true) {
            if (cur.moveCount == 0) {
                boardStack.push(cur.board);
                break;
            }
            boardStack.push(cur.board);
            cur = cur.prevNode;
        }
    }

    public boolean isSolvable() {
        return isInitialBoardSolvable;
    }

    /**
     * @return Minimum number of moves to solve initial board
     */

    public int moves() {
        return minMoves;
    }

    /**
     * @return Sequence of boards in a shortest solution
     */

    public Iterable<Board> solution() {
        return boardStack;
    }

    private final class SearchNode implements Comparable<SearchNode> {
        public Board board;
        public SearchNode prevNode;
        public int moveCount, manhattanDistance;

        public SearchNode(Board board, SearchNode prevNode, int moveCount) {
            this.board = board;
            this.prevNode = prevNode;
            this.moveCount = moveCount;
            this.manhattanDistance = board.manhattan();
        }

        public int compareTo(SearchNode that) {
            int p1 = manhattanDistance + moveCount;
            int p2 = that.manhattanDistance + that.moveCount;
            if (p1 == p2)
                return manhattanDistance - that.manhattanDistance;
            return p1 - p2;
        }
    }

    public static void main(String[] args) {
        int[][] tiles1 = {{1, 2, 7}, {4, 5, 6}, {8, 3, 0}};
        Board board = new Board(tiles1);
        Solver solver = new Solver(board);
        ResizingArrayStack<Board> solution = (ResizingArrayStack<Board>) solver.solution();
        StdOut.println("Initial board is solvable: " + solver.isSolvable());
        StdOut.println("Moves: " + solver.moves());
        if (solver.isSolvable())
            for (Board b : solution)
                StdOut.println("Board: " + b);
    }
}
