package othello.ai;

import java.awt.Point;
import java.util.Random;

import othello.model.Board;

// very simple AI - return the board reflecting the move that captures the most pieces

public class GreedyAI extends ReversiAI {
	private Random r = new Random(SEED);

	public GreedyAI() {
		this(true);
	}

	public GreedyAI(boolean deterministic) {
		if (deterministic)
			r = new Random(SEED);
	}

	@Override
	public Point nextMove(Board prev) {

		Board b = new Board(prev);
		Point best = null;

		int maxScore = MIN_SCORE;

		for (int j = 0; j < size; j++) {
			for (int i = 0; i < size; i++) {
				if (b.move(i, j)) // valid move
				{
					int score = b.getScore();
					if (score > maxScore
							|| (score == maxScore && r.nextDouble() < OVERRIDE)) {
						maxScore = score;
						best = new Point(i, j);
					}
					b = new Board(prev);
				}
			}
		}

		return best;
	}

	@Override
	public String getName() {
		return new String("Greedy  ");
	}
}
