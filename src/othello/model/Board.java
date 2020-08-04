package othello.model;

/* Basic Reversi Board abstraction

 important methods are:
 - Board(int) - create a new n x n board
 - Board(Board) - duplicate an existing n x n board
 - move(int, int) - attempt to put a piece on coordinate x, y for the active player
 -if successful, the board is updated with to include newly captured pieces
 -if unsuccessful, the board is not modified and false is returned
 -turn() - end the turn of the current player
 -canMove() - return true if the active (current) player can make a move
 -gameOver() - return true if neither player can move

 */

import java.awt.Point;
import java.io.*;

public class Board {
	public static final int BLACK = 0;
	public static final int WHITE = 1;
	public static final int EMPTY = 2;
	public static final int BLOCK = 3;

	private long active_board = 0; // bitboard - 1 means active player's piece
									// occupies the position
	private long inactive_board = 0; // bitboard - 1 means a inactive player's
										// piece occupies the position

	private int active_count = 0; // number of pieces active player has on the
									// board
	private int inactive_count = 0; // number of pieces inactive player has on
									// the board

	private int size; // dimensions of the board

	private boolean active = true; // true if it's black's turn, false if it's
									// white's turn

	private int blast = -1; // coordinates of last black move
	private int wlast = -1; // coordinates of last white move

	// build a new board, with dimensions size x size
	public Board(int boardsize) {
		size = boardsize;
		int mid = boardsize / 2 - 1;

		setSquare(mid + 1, mid); // set initial board - 4 center squares
									// occupied
		setSquare(mid, mid + 1);
		turn();
		setSquare(mid, mid);
		setSquare(mid + 1, mid + 1);
		turn();
	}

	// copy constructor
	public Board(Board b) {
		active_board = b.active_board;
		inactive_board = b.inactive_board;

		active_count = b.active_count;
		inactive_count = b.inactive_count;

		size = b.size;

		active = b.active;
	}

	public boolean equals(Board right) {
		return (active_board == right.active_board
				&& inactive_board == right.inactive_board && active == right.active);
	}

	// place a piece at position x, y for the current player
	private void setSquare(int x, int y) {
		active_board |= (1L << (y * size + x));
		active_count++;
	}

	// flip the piece at position x, y from the inactive player to the active
	// one
	private void flipSquare(int x, int y) {
		long val = (1L << (y * size + x));
		active_board |= val;
		inactive_board &= ~val;
	}

	// returns true if the current player occupies the given square
	private boolean getSquare(int x, int y, boolean current) {
		if (x < 0 || y < 0 || x >= size || y >= size)
			return false;
		if(x == 0 && y == 0)
			return false;
		if(x == 7 && y == 0)
			return false;
		if(x == 0 && y == 7)
			return false;
		if(x == 7 && y == 7)
			return false;

		if (current)
			return (active_board & (1L << (y * size + x))) != 0;
		else
			return (inactive_board & (1L << (y * size + x))) != 0;
	}
	
	public boolean isCapturedByMe(int x, int y){
		return getSquare(x, y, true);
	}
	
	public boolean isCapturedByMyOppoenet(int x, int y){
		return getSquare(x, y, false);
	}

	public boolean isEmptySquare(int x, int y) {
		if (x < 0 || y < 0 || x >= size || y >= size)
			return false;
		if(x == 0 && y == 0)
			return false;
		if(x == 7 && y == 0)
			return false;
		if(x == 0 && y == 7)
			return false;
		if(x == 7 && y == 7)
			return false;
		long tmp = active_board | inactive_board;
		return ((tmp >>> (y * size + x)) & 1L) == 0;
	}

	// getter methods
	public int getSize() {
		return size;
	}

	public int getActive() {
		return active ? BLACK : WHITE;
	}

	public String getActiveName() {
		return active ? "Black" : "White";
	}

	// return the total number of squares - for current player or opponent
	public int getTotal(boolean current) {
		return current ? active_count : inactive_count;
	}

//	public int getCornerCount(boolean current) {
//		long tmp;
//		if (current)
//			tmp = active_board;
//		else
//			tmp = inactive_board;
//
//		return (int) ((tmp >>> 63) + ((tmp >>> 56) & 1L) + ((tmp >>> 8) & 1L) + (tmp & 1L));
//	}

	public int getFrontierCount(boolean current) {
		int count = 0;
		for (int j = 0; j < size; j++) {
			for (int i = 0; i < size; i++) {
				if (getSquare(i, j, current)) {
					if (isEmptySquare(i - 1, j - 1) || isEmptySquare(i - 1, j)
							|| isEmptySquare(i - 1, j + 1)
							|| isEmptySquare(i, j - 1)
							|| isEmptySquare(i, j + 1)
							|| isEmptySquare(i + 1, j - 1)
							|| isEmptySquare(i + 1, j)
							|| isEmptySquare(i + 1, j + 1))
						count++;
				}
			}
		}
		return count;
	}

	public int getMoveCount(boolean current) {
		int count = 0;
		Board tmp = new Board(this);

		if (!current)
			tmp.turn();

		for (int j = 0; j < size; j++) {
			for (int i = 0; i < size; i++) {
				Board tmp2 = new Board(tmp);
				if (tmp2.move(i, j))
					count++;
			}
		}
		return count;
	}

//	public int getEmptyCornerNeighbors(boolean current) {
//		int count = 0;
//
//		if (isEmptySquare(0, 0)) {
//			if (getSquare(0, 1, current) || getSquare(1, 0, current)
//					|| getSquare(1, 1, current))
//				count++;
//		}
//		if (isEmptySquare(0, size - 1)) {
//			if (getSquare(0, size - 2, current)
//					|| getSquare(1, size - 2, current)
//					|| getSquare(1, size - 1, current))
//				count++;
//		}
//		if (isEmptySquare(size - 1, 0)) {
//			if (getSquare(size - 2, 0, current)
//					|| getSquare(size - 2, 1, current)
//					|| getSquare(size - 1, 1, current))
//				count++;
//		}
//		if (isEmptySquare(size - 1, size - 1)) {
//			if (getSquare(size - 2, size - 2, current)
//					|| getSquare(size - 2, size - 1, current)
//					|| getSquare(size - 1, size - 2, current))
//				count++;
//		}
//
//		return count;
//	}

	public int getScore() {
		return active_count - inactive_count;
	}

	public int getMoves() {
		return size * size - (active_count + inactive_count);
	}

	// get ID of winning player
	public int getWinning() {
		int acount = active_count, icount = inactive_count;
		if (!active) {
			int tmp = acount;
			acount = icount;
			icount = tmp;
		}
		if (acount > icount)
			return BLACK;
		else if (icount > acount)
			return WHITE;
		else
			return EMPTY;
	}

	public int getState(int x, int y) {
		if (getSquare(x, y, true))
			return active ? BLACK : WHITE;
		else if (getSquare(x, y, false))
			return active ? WHITE : BLACK;
		else if (isEmptySquare(x, y))
			return EMPTY;
		else
			return BLOCK;
	}

	// attempt to place a piece at specified coordinates, and update
	// the board appropriately, or return false if not possible
	public boolean move(int x, int y) {
		if (getState(x, y) != EMPTY)
			return false; // current square must be unoccupied

		int before = active_count;

		west(x - 1, y); // checks if can capture in this direction, flipping
						// pieces where necessary
		east(x + 1, y);
		north(x, y - 1);
		south(x, y + 1);
		northwest(x - 1, y - 1);
		southeast(x + 1, y + 1);
		northeast(x + 1, y - 1);
		southwest(x - 1, y + 1);

		if (before == active_count)
			return false; // if no changes, move was unsuccessful

		// place piece at current position
		setSquare(x, y);

		// set last player move.
		if (active)
			blast = x + y * size;
		else
			wlast = x + y * size;

		return true;
	}

	public Point getLastPlayerMove() {
		if (active)
			return new Point(blast % size, blast / size);
		else
			return new Point(wlast % size, wlast / size);
	}

	// end current player's turn
	public void turn() {
		{
			long tmp = active_board;
			active_board = inactive_board;
			inactive_board = tmp;
		}
		{
			int tmp = active_count;
			active_count = inactive_count;
			inactive_count = tmp;
		}
		active = !active;
	}

	// can the current player make a move?
	public boolean canMove() {
		Board tmp = new Board(this); // duplicate board
		for (int j = 0; j < size; j++) {
			for (int i = 0; i < size; i++)
				if (tmp.move(i, j)) {
					tmp = null;
					return true;
				} // try moves
		}
		return false;
	}

	// check if game is over - can either player make a move?
	public boolean gameOver() {
		if (active_count + inactive_count == size * size)
			return true;
		else if (canMove())
			return false;
		else {
			turn();
			return !canMove();
		}
	}

	// update piece counts for current player capturing 'flipped' pieces
	private void updateCount(int flipped) {
		active_count += flipped;
		inactive_count -= flipped;
	}

	// methods to check whether pieces can be capture in any of the 8 directions
	// from the current coordinates, and to capture them if they can
	private void west(int x, int y) {
		if (x <= 0)
			return; // can't capture - no room

		int i;
		for (i = x; getSquare(i, y, false); i--)
			// traverse squares that could be captured
			if (i == 0)
				return; // can't capture if reached the edge of the board

		if (i != x && getSquare(i, y, true)) // if capturable square is followed
												// by captured square
		{
			updateCount(x - i); // number of squares captured

			for (i++; i <= x; i++)
				flipSquare(i, y); // flip them
		}
	}

	private void east(int x, int y) {
		if (x >= size - 1)
			return;

		int i;
		for (i = x; getSquare(i, y, false); i++)
			if (i == size - 1)
				return;

		if (i != x && getSquare(i, y, true)) {
			updateCount(i - x);

			for (i--; i >= x; i--)
				flipSquare(i, y);
		}
	}

	private void north(int x, int y) {
		if (y <= 0)
			return;

		int i;
		for (i = y; getSquare(x, i, false); i--)
			if (i == 0)
				return;

		if (i != y && getSquare(x, i, true)) {
			updateCount(y - i);

			for (i++; i <= y; i++)
				flipSquare(x, i);
		}
	}

	private void south(int x, int y) {
		if (y >= size - 1)
			return;

		int i;
		for (i = y; getSquare(x, i, false); i++)
			if (i == size - 1)
				return;

		if (i != y && getSquare(x, i, true)) {
			updateCount(i - y);

			for (i--; i >= y; i--)
				flipSquare(x, i);
		}
	}

	private void northwest(int x, int y) {
		if (x <= 0 || y <= 0)
			return;

		int i, j;
		for (i = x, j = y; getSquare(i, j, false); i--, j--)
			if (i == 0 || j == 0)
				return;

		if (i != x && getSquare(i, j, true)) {
			updateCount(x - i);

			for (i++, j++; i <= x; i++, j++)
				flipSquare(i, j);
		}
	}

	private void southeast(int x, int y) {
		if (x >= size - 1 || y >= size - 1)
			return;

		int i, j;
		for (i = x, j = y; getSquare(i, j, false); i++, j++)
			if (i == size - 1 || j == size - 1)
				return;

		if (i != x && getSquare(i, j, true)) {
			updateCount(i - x);

			for (i--, j--; i >= x; i--, j--)
				flipSquare(i, j);
		}
	}

	private void northeast(int x, int y) {
		if (x >= size - 1 || y <= 0)
			return;

		int i, j;
		for (i = x, j = y; getSquare(i, j, false); i++, j--)
			if (i == size - 1 || j == 0)
				return;

		if (i != x && getSquare(i, j, true)) {
			updateCount(i - x);

			for (i--, j++; i >= x; i--, j++)
				flipSquare(i, j);
		}
	}

	private void southwest(int x, int y) {
		if (x <= 0 || y >= size - 1)
			return;

		int i, j;
		for (i = x, j = y; getSquare(i, j, false); i--, j++)
			if (i == 0 || j == size - 1)
				return;

		if (i != x && getSquare(i, j, true)) {
			updateCount(x - i);

			for (i++, j--; i <= x; i++, j--)
				flipSquare(i, j);
		}
	}

	// ASCII printout of the current board
	public void print() {
		System.out.print("  ");
		for (int i = 0; i < size; i++)
			System.out.print(i);
		System.out.print("\n");

		System.out.print(" --");
		for (int i = 0; i < size; i++)
			System.out.print("-");
		System.out.print("\n");
		for (int j = 0; j < size; j++) {
			System.out.print(j + "|");
			for (int i = 0; i < size; i++) {
				int t = getState(i, j);
				if (t == EMPTY)
					System.out.print(" ");
				else if (t == WHITE)
					System.out.print("o");
				else if (t == BLACK)
					System.out.print("x");
				else
					System.out.print("#");
			}
			System.out.print("|\n");
		}
		System.out.print(" --");
		for (int i = 0; i < size; i++)
			System.out.print("-");
		System.out.print("\n");
		System.out.println(boardStats());
	}

	private String boardStats() {
		if (active)
			return "b: " + active_count + " w: " + inactive_count + " e: "
					+ ((size * size) - (active_count + inactive_count));
		else
			return "b: " + inactive_count + " w: " + active_count + " e: "
					+ ((size * size) - (active_count + inactive_count));
	}

	// get coordinates from the user
	private static boolean getCoords(BufferedReader in, int[] coords, int max)
			throws IOException {
		String line = in.readLine();
		if (line.equals("quit"))
			System.exit(0);

		String input[] = line.split(" ");
		if (input.length != 2) {
			System.out.println("Invalid input format");
			return false;
		}
		int x = Integer.parseInt(input[0]);
		int y = Integer.parseInt(input[1]);

		if (x < 0 || x > max || y < 0 || y > max) {
			System.out.println("Coordinates must be between 0 and " + max);
			return false;
		}
		coords[0] = x;
		coords[1] = y;
		return true;
	}

	private void prompt() {
		if (active)
			System.out.print("x > ");
		else
			System.out.print("o > ");
	}

	// run a text-based Reversi game
	public void game() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			int coords[] = new int[2];

			System.out.println("Reversi!");
			print();
			while (!gameOver()) {
				do {
					prompt();
				} while (!getCoords(in, coords, size - 1));
				while (!move(coords[0], coords[1])) {
					System.out.println("Can't make move to " + coords[0] + ","
							+ coords[1]);
					do {
						prompt();
					} while (!getCoords(in, coords, size - 1));
				}
				print();
				turn();
			}

			System.out.println("GAME OVER");
			int winner = getWinning();
			int bcount = getTotal(true), wcount = getTotal(false);
			if (!active) {
				int tmp = bcount;
				bcount = wcount;
				wcount = tmp;
			}

			if (winner == BLACK)
				System.out.println("Black wins " + bcount + " - " + wcount
						+ ".");
			else if (winner == WHITE)
				System.out.println("White wins " + wcount + " - " + bcount
						+ ".");
			else
				System.out.println("Tie " + bcount + " - " + wcount + ".");
		} catch (IOException ioe) {
		}
	}

	public static void main(String[] args) {
		Board b = new Board(4);
		b.game();

	}
}
