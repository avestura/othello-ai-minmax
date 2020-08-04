package othello.ai;
// Aryan Ebrahimpour
import java.awt.*;
import java.util.concurrent.TimeUnit;

import othello.utils.TableInfo;
import othello.model.Board;

// your AI here. currently will choose first possible move
public class MyPlayerAI extends ReversiAI {

    /**
     * The best move that we can choose
     */
    Point bestAlphaBetaMove = null;

    /**
     * Store untouched main board here
     */
    Board globalBoard = null;

    /**
     * Maximum depth we can iterate. Depth of 6 is also a good option.
     * But it can go over 1 second limit in rare situations.
     */
    public final int MAX_DEPTH = 5;

    /**
     * Enable timer log for debugging purpose
     */
    public boolean ENABLE_TIMER = false;

    /**
     * Round that we are playing
     */
    public int CurrentRound = 0;

    /**
     * Is game just initialized?
     */
    public boolean Initialize = false;

	@Override
	public Point nextMove(Board b) {

        // Store a version of untouched board
        globalBoard = b;

        if(!Initialize) {
            CurrentRound = (globalBoard.getActive() == Board.BLACK) ? 1 : 2;
            Initialize = true;
        }
        else
            CurrentRound += 2;

        // Create a copy of board to pass to alpha-beta function
        Board boardCopy = new Board(b);

        // Timer debug actions :: before alpha-beta
        long time = 0;
        if(ENABLE_TIMER){
            time = System.nanoTime();
            System.out.println("Thinking...");
        }

        /*
        Run alpha-beta to find best move in depth of MAX_DEPTH.
        Value of best evaluation is not needed here.
        But we can print it for debugging purposes.
         */
        if(b.getActive() == Board.BLACK){
            double bestEvaluation = AlphaBeta(boardCopy, MAX_DEPTH, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true);
        }
        else{
            double bestEvaluation = AlphaBeta(boardCopy, MAX_DEPTH, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false);
        }


        // Time debug actions :: after alpha-beta
        if(ENABLE_TIMER){
            long endTime = System.nanoTime();
            long duration = TimeUnit.MILLISECONDS.convert(endTime - time, TimeUnit.NANOSECONDS);
            if(duration > 1000)
                System.out.println("========== EXCEEDED :: NOT ACCEPTABLE =========");
            System.out.println("> End of think in " + duration + " milli seconds");
        }


        return bestAlphaBetaMove;

		/*{
			b.isCapturedByMe(x, y);					// square (x, y) is mine
			b.isCapturedByMyOppoenet(x, y);			// square (x, y) is for my opponent
			b.isEmptySquare(x, y);					// square (x, y) is empty
			b.move(x, y);							// attempt to place a piece at specified coordinates, and update
													// the board appropriately, or return false if not possible
			b.turn();								// end current player's turn
			b.print();								// ASCII printout of the current board
			if(b.getActive() == Board.WHITE)		//I am White
			if(b.getActive() == Board.BLACK)		//I am Black

			b.getMoveCount(true);					//number of possible moves for me
			b.getMoveCount(false);					//number of possible moves for my opponent
			b.getTotal(true);						//number of cells captured by me
			b.getTotal(false);						//number of cells captured by my opponent
			this.size;								//board size (always 8)
		}*/
	}

    /**
     * Calculates heuristic function of AI
     * We have a combination of five functions here.
     * Function 1: Mobility of the scene (Weight: 40%)
     * Function 2: Number of our disks vs Opponent (Weight 30%)
     * Function 3: X Squares as good strategic places that should be selected (Weight: 25%)
     * Function 5: X Blockers that block X squares that should not be selected (Weight: -10%)
     *
     * Function 4: Border of game (Not X or X Blocker) has one less direction to be flipped (Weight: 5%)
     *
     * @param board
     * @return
     */
    public double evaluation(Board board){

        boolean isMax = (board.getActive() == Board.BLACK) ? true : false;


        TableInfo currentStrategicInfo = getStrategicInfo(isMax);
        TableInfo opponentStrategicInfo = getStrategicInfo(!isMax);

        double func1 = board.getMoveCount(isMax) - board.getMoveCount(!isMax);
        double func2 = board.getTotal(isMax) - board.getTotal(!isMax);
        double func3 = currentStrategicInfo.XSquares - opponentStrategicInfo.XSquares;
        double func4 = currentStrategicInfo.NonXOrXblockerSideSquares - opponentStrategicInfo.NonXOrXblockerSideSquares;

        double func5 = currentStrategicInfo.XBlockerSquares - opponentStrategicInfo.XBlockerSquares;


        double func1Weight = 0.4;
        double func2Weight = 0.3;
        double func3Weight = 0.25;
        double func4Weight = 0.05;

        double func5Weight = -0.1;

        if(CurrentRound >= 40){
            func3Weight = 0.3;
            func4Weight = 0;
        }



        return ((func1Weight * func1) +
                (func2Weight * func2) +
                (func3Weight * func3) +
                (func4Weight * func4) +
                (func5Weight * func5));

    }


    /**
     * This function traverses the tree and chooses the best move based on MAX_DEPTH
     * @param boardState Current state of the board
     * @param depth Depth limit we can continue
     * @param alpha Current traversal alpha value
     * @param beta Current traversal beta value
     * @param isMax Black or White player is playing
     * @return
     */
    public double AlphaBeta(Board boardState, int depth, double alpha, double beta, boolean isMax){

        // Return evaluate function of board if depth limit arrived or game has ended
        if(depth == 0 || boardState.gameOver())
            return evaluation(boardState);

        // State of board after we select a move
        Board boardAfterMove = new Board(boardState);

        if(isMax){

            // Can't move
            if(boardState.getMoveCount(true) < 1)
                return AlphaBeta(boardAfterMove, depth - 1, alpha, beta, false);

            Point move;
            Point selectedMove = null;

            double bestAlphaYet = Double.NEGATIVE_INFINITY;

            // Iterate children
            for(int i = 0; i < 8; i++){
                for(int j = 0; j < 8; j++){

                    move = new Point(i, j);
                    Board copy = new Board(boardAfterMove);

                    if(copy.move(i, j)){
                        boardAfterMove.move(i, j);
                        alpha = Math.max(alpha,  AlphaBeta(boardAfterMove, depth - 1, alpha, beta, false));
                    }

                    if(alpha > bestAlphaYet){
                        selectedMove = move;
                        bestAlphaYet = alpha;
                    }

                    if(alpha >= beta)
                        break;

                    boardAfterMove = new Board(boardState);

                }
            }

            if(globalBoard.getActive() == Board.BLACK)
                bestAlphaBetaMove = selectedMove;

            return alpha;
        }
        else {
            // Can't move
            if(boardState.getMoveCount(true) < 1)
                return AlphaBeta(boardAfterMove, depth - 1, alpha, beta, true);

            Point move;
            Point selectedMove = null;

            double bestBetaYet = Double.POSITIVE_INFINITY;

            // Iterate children
            for(int i = 0; i < 8; i++){
                for(int j = 0; j < 8; j++){

                    move = new Point(i, j);
                    Board copy = new Board(boardAfterMove);

                    if(copy.move(i, j)){
                        boardAfterMove.move(i, j);
                        beta = Math.min(beta, AlphaBeta(boardAfterMove, depth - 1, alpha, beta, true));
                    }

                    if(beta < bestBetaYet){
                        selectedMove = move;
                        bestBetaYet = beta;
                    }

                    if(alpha >= beta)
                        break;

                    boardAfterMove = new Board(boardState);
                }
            }

            if(globalBoard.getActive() == Board.WHITE)
                bestAlphaBetaMove = selectedMove;

            return beta;
        }

    }

    public TableInfo getStrategicInfo(boolean current) {


        TableInfo tableInfo = new TableInfo();

        Board tmp = new Board(globalBoard);

        if (!current)
            tmp.turn();

        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                Board tmp2 = new Board(tmp);

                boolean xSquare = ((i == 0 && j == 1) || (i == 0 && j == 6) ||
                                    (i == 1 && j == 0) || (i == 1 && j == 7) ||
                                    (i == 6 && j == 0) || (i == 6 && j == 7) ||
                                    (i == 7 && j == 1) || (i == 7 && j == 6));

                boolean xSquareBlocker =
                        ((i == 1 && j == 1) || (i == 6 && j == 1) ||
                        (i == 2 && j == 0) || (i == 0 && j == 2) ||
                        (i == 5 && j == 0) || (i == 0 && j == 5) ||
                        (i == 7 && j == 2) || (i == 2 && j == 7) ||
                        (i == 7 && j == 5) || (i == 5 && j == 7) ||
                        (i == 1 && j == 6) || (i == 6 && j == 6));

                boolean nonXOrXBlockerSquareSide = ((j == 0) && ((i == 3) || (i == 4))) ||
                        ((j == 7) && ((i == 3) || (i == 4))) ||
                        ((i == 0) && ((j == 3) || (j == 4))) ||
                        ((i == 7) && ((j == 3) || (j == 4)));

                if (tmp2.move(i, j)){
                    if(xSquare)
                        tableInfo.XSquares++;
                    if(nonXOrXBlockerSquareSide)
                        tableInfo.NonXOrXblockerSideSquares++;
                    if(xSquareBlocker)
                        tableInfo.XBlockerSquares++;
                }

            }
        }
        return tableInfo;
    }


	@Override
	public String getName() {
		return new String("Aryan Ebrahimpour");
	}
}

