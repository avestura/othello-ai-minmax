package othello.controller;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import othello.ai.ReversiAI;
import othello.model.Board;
import othello.view.BoardGUI;

public class LadderController {

	private static class Score implements Comparable<Score> {
		private String aiName;
		private Integer score = new Integer(0);
		private ReversiAI ai;

		public Score(ReversiAI ai) {
			try{
				ai = ai.getClass().newInstance();
			}catch(Exception e){}
			this.aiName = ai.getName();
			this.ai = ai;
		}

		@Override
		public int compareTo(Score score) {
			return this.score.compareTo(score.score);
		}
	}

	public static void main(String[] args) {
		LadderController ladder = new LadderController();
		ladder.runLadder();
	}

	List<Score> scores;
	private int percentComplete;

	public LadderController() {
		scores = new ArrayList<Score>();
		for (int i = 0; i < AIList.AI.length; i++) {
			scores.add(new Score(AIList.AI[i]));
		}
	}

	public void runLadder() {

		Score left;
		Score right;

		int n = scores.size();
		int current;
		int total = ((n * n) - n);

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i == j) {
					continue;
				}
				left = scores.get(i);
				right = scores.get(j);

				ReversiAI black = left.ai;
				ReversiAI white = right.ai;

				int result = play(black, white);
				String winner = "Draw"; 
				if(result == Board.BLACK){
					left.score++;
					winner = black.getName();
				}if(result == Board.WHITE){
					right.score++;
					winner = white.getName();
				}

				current = (i * n) + j - 1;

				percentComplete = (current * 100) / (total);
				System.out.println("Complete: " + percentComplete + "% - "
						+ left.aiName + " vs. " + right.aiName + ", Winner: "
						+ winner);
			}
		}

		Collections.sort(scores);

		System.out.println("AI name" + "\t\t" + "AI score");
		for (Score score : scores) {
			System.out.println(score.aiName + "\t" + score.score);
		}
	}
	
	int play(ReversiAI black, ReversiAI white) {
		
		ReversiAI activeAI = black, inactiveAI = white;
		ReversiAI aiTemp;
		Board b = new Board(BoardGUI.ROWS);
//		b.turn();
		int countNoMove = 0;
		
		while (true) {
			if(!b.canMove()){
				// next player's turn
				b.turn();
				aiTemp = activeAI;
				activeAI = inactiveAI;
				inactiveAI = aiTemp;
				
				countNoMove++;
				if(countNoMove == 2)
					break; // neither player could move
				continue;
			}
			countNoMove = 0;
			
			Point AImove = activeAI.nextMove(new Board(b));
			
			if (AImove == null || !b.move(AImove.x, AImove.y)){
				//Illegal move by active player
				if(activeAI == black)
					return Board.WHITE;		//black will lose
				else
					return Board.BLACK;		//white will lose
			}else{
				// switch AIs
				b.turn();
				aiTemp = activeAI;
				activeAI = inactiveAI;
				inactiveAI = aiTemp;
			}
		}
		return b.getWinning();
	}

}
