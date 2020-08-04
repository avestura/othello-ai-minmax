package othello.controller;

import java.awt.Point;

import javax.management.RuntimeErrorException;

import othello.ai.MyPlayerAI;
import othello.ai.ReversiAI;
import othello.model.Board;
import othello.model.Listener;
import othello.view.BoardGUI;
import othello.view.ReversiGUI;

// a controller, where one player (BLACK) is a human, and the other (WHITE) is an AI

public class OnePlayerController extends Controller {
	ReversiAI r; // AI to control the non-human player

	public static void main(String args[]) {
		ReversiGUI gui = new ReversiGUI(true);
		OnePlayerController c = new OnePlayerController(gui);
		gui.setController(c);
		c.update();
	}

	public OnePlayerController(Listener l) {
		this.l = l;
		newGame();
	}

	public Board getBoard() {
		return b;
	}

	public void newGame() {
		active = true;
		b = new Board(BoardGUI.ROWS);
		r = new MyPlayerAI();
		update();
		l.setMessage("New game");
	}

	public void move(int x, int y) {
		if (!active)
			return;
		// invalid move by human
		if (!b.move(x, y)){
			l.setMessage("Not a legal move");
			update();
			return;
		}else{
			b.turn(); // AI's turn
			update();
		}

		// AI can move
		while (b.canMove()){
			Point AImove = r.nextMove(new Board(b));
			if(!b.move(AImove.x, AImove.y)){
				l.setMessage("Inavlid move by AI: " + AImove);
				update();
				gameOver();
				throw new RuntimeErrorException(null, "Invalid move by AI");
			}
			b.turn();
			update();
			playerLog("Next player's turn.");
			
			if (b.canMove()){
				update();
				return;
			}else{
				playerLog("Can't move so yielding.");
				b.turn();
				update();
			}
		}

		// AI can't move, so human's turn
		playerLog("Can't move so yielding.");
		b.turn();
		update();

		if (b.canMove()){ // human can move
			update();
			return;
		}

		// no more moves possible, game over
		System.out.println("No more possible moves so ending.");
		update();
		gameOver();
		return;
	}

	public void playerLog(String msg) {
		if (LOG_ENABLED)
			System.out.println(b.getActiveName() + "-" + msg);
	}
}
