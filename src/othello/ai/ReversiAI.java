package othello.ai;

import java.awt.Point;

import othello.model.Board;

// simple interface for an AI - given the board, and the last
// move of the human player, return a board with the next move of the AI

public abstract class ReversiAI {

//	public interface Types {
//		String RANDOM = new RandomAI().getName();
//		String GREEDY = new GreedyAI().getName();
//		String PLAYER = new MyPlayerAI().getName();
//	}

//	public static ReversiAI getAIByName(String aiName) {
//		if (ReversiAI.Types.GREEDY.equals(aiName)) {
//			return new GreedyAI();
//		} else if (ReversiAI.Types.RANDOM.equals(aiName)) {
//			return new RandomAI();
//		} else if (ReversiAI.Types.PLAYER.equals(aiName)) {
//			return new MyPlayerAI();
//		} else {
//			throw new IllegalArgumentException("Unknown AI");
//		}
//	}

	public static final int MAX_SCORE = 1000000;
	public static final int MIN_SCORE = -1000000;

	protected static final double OVERRIDE = 0.3; // probability that we break
													// tie with new candidate
	protected static final int SEED = 1000;

	protected int size = 8;

	abstract public String getName();

	public abstract Point nextMove(Board prev);
}
