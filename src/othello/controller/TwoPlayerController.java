package othello.controller;

import othello.model.Board;
import othello.model.Listener;
import othello.view.BoardGUI;
import othello.view.ReversiGUI;

// controller for a two player reversi game

public class TwoPlayerController extends Controller {

	public static void main(String args[]) {
		ReversiGUI gui = new ReversiGUI(true);
		TwoPlayerController c = new TwoPlayerController(gui);
		gui.setController(c);
		c.update();
	}

	public TwoPlayerController(Listener l) {
		this.l = l;
		newGame();
	}

	public void move(int x, int y) {
		if (!active)
			return;

		/* invalid move */
		if (!b.move(x, y)){
			l.setMessage("Not a legal move");
			return;
		}

		b.turn();

		/* if player can move, let them */
		if (b.canMove()){
			update();
			return;
		}

		/* next player can't move */
		l.setMessage("No possible moves for " + sideToString(b));
		b.turn();

		/* but original player has a possible move */
		if (b.canMove()){
			update();
			return;
		}

		/* no more moves possible, game over */
		gameOver();
		return;
	}

	public void newGame() {
		active = true;
		b = new Board(BoardGUI.ROWS);
		update();
		l.setMessage("New game");
	}

	@Override
	public Board getBoard() {
		return b;
	}
}
