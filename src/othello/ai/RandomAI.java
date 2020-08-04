package othello.ai;

import java.awt.Point;
import java.util.Random;

import othello.model.Board;

// very simple AI - make a random move
public class RandomAI extends ReversiAI {
	private Random r = new Random(SEED);

	private static final int MAX_TRIES = 128;

	public RandomAI() {
		this(true);
	}

	public RandomAI(boolean deterministic) {
		if (deterministic)
			r = new Random(SEED);
	}

	@Override
	public Point nextMove(Board prev) {
		Board b = new Board(prev);
		int i, j, c = 0;

		// try random moves, until one works, or we exhaust MAX_TRIES attempts
		do {
			i = r.nextInt(size);
			j = r.nextInt(size);
			c++;
		} while (!b.move(i, j) && c < MAX_TRIES);

		if (!b.equals(prev))
			return new Point(i, j);
		// if no moves succeeded, attempt iteratively to find a possible move
		else {
			for (j = 0; j < size; j++) {
				for (i = 0; i < size; i++) {
					if (b.move(i, j)) {
						return new Point(i, j);
					}
				}
			}
		}
		return null;
	}

	@Override
	public String getName() {
		return new String("Random  ");
	}
}
