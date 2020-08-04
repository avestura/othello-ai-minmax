package othello.controller;

import java.awt.Point;

import othello.ai.ReversiAI;
import othello.model.Board;
import othello.model.Listener;
import othello.view.BoardGUI;
import othello.view.TestFrameAIVSAI;

// a controller, where both players are AI threads

public class AIControllerSinglePlay extends Controller {

	final public int DEFAULT_WAIT_TIME = 500; 
	private ReversiAI aiWhite, aiBlack;
	public int waitTime = DEFAULT_WAIT_TIME;

	public static void main(String args[]) {
		new TestFrameAIVSAI(); 
	}

	public AIControllerSinglePlay(Listener l, ReversiAI aiWhite,
			ReversiAI aiBlack) {
		this.l = l;

		this.aiWhite = aiWhite;
		this.aiBlack = aiBlack;
	}

	public void move(int x, int y){}
	
	public void setWaitTime(int time){
		waitTime = time;
	}

	public Board getBoard() {
		return b;
	}
	
	private void waitForInterrupt(){
		try{
			Thread.sleep(waitTime);
		}catch(InterruptedException e){}
	}

	@Override
	public void newGame(){
		active = true;
		b = new Board(BoardGUI.ROWS);
		l.setMessage("New game");
		update();
		waitForInterrupt();
		
		while(active){
			//Player Black turn
			if (b.canMove()){
				Point AImove = aiBlack.nextMove(new Board(b));
				if(!b.move(AImove.x, AImove.y)){
					playerLog("Player black's illegal move: Player white wins.");
					update();
					gameOver();
					break;
				}
				b.turn();
				waitForInterrupt();
				update();
				playerLog("Next player's turn.");
				
				//Player White turn
				if(!b.canMove()){
					b.turn();
					update();
					playerLog("Player white can't move so yeilding.");
					continue;
				}
				AImove = aiWhite.nextMove(new Board(b));
				if(!b.move(AImove.x, AImove.y)){
					playerLog("Player white's illegal move: Player black wins.");
					update();
					gameOver();
					break;
				}
				b.turn();
				waitForInterrupt();
				update();
				playerLog("Next player's turn.");
				continue;
			}else{
				b.turn();
				waitForInterrupt();
				playerLog("Player Black can't move so yeilding.");
				update();
				playerLog("Next player's turn.");
				//Player White turn
				if(!b.canMove()){
					update();
					gameOver();
					String winner;
					if(b.getWinning() == Board.WHITE)
						winner = "White";
					else if(b.getWinning() == Board.BLACK)
						winner = "Black";
					else
						winner = "Draw";
					playerLog("No more possible moves so ending. winner: " + winner);
					break;
				}
				Point AImove = aiWhite.nextMove(new Board(b));
				if(!b.move(AImove.x, AImove.y)){
					playerLog("Player white's illegal move: Player black wins.");
					update();
					gameOver();
					break;
				}
				b.turn();
				waitForInterrupt();
				update();
				playerLog("Next player's turn.");
			}
		}
	}

	public void playerLog(String msg) {
		if (LOG_ENABLED)
			System.out.println(b.getActiveName() + "-" + msg);
		l.setMessage(msg);
	}
}
