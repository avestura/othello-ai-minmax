package othello.view;

// GUI for the game - View in the MVC model

import javax.swing.*;

import othello.controller.Controller;
import othello.model.Board;
import othello.model.Listener;

import java.awt.*;
import java.awt.event.*;

public class ReversiGUI extends JFrame implements Listener {

	private static final long serialVersionUID = 1L; // needed for Eclipse
	private JLabel gameMessage = new JLabel(" "); // display game messages
	private JLabel gameTurn = new JLabel(" "); // display turn information
	private JLabel gameScore = new JLabel(" "); // display current score
	private JButton newGame = new JButton("New Game"); // start a new game
	private JButton quitGame = new JButton("Quit"); // quit
	private BoardGUI gameBoard = new BoardGUI(); // board
	private Thread thread;

	private Controller c; // mediate between model and view

	public ReversiGUI(boolean userInput) // build the GUI - status info, a
											// board, and two buttons
	{
		super("Reversi");
		JComponent content = (JComponent) getContentPane();
		content.setLayout(new BorderLayout());

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
		infoPanel.add(gameMessage);
		infoPanel.add(gameTurn);
		infoPanel.add(gameScore);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		newGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newGame.setEnabled(false);
				if(thread != null)
					thread.stop();
				thread = new Thread(new Runnable() {
					@Override
					public void run() {
						c.newGame();
						newGame.setEnabled(true);
					}
				});
				thread.start();
			}
		});

		quitGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		buttonPanel.add(newGame);
		buttonPanel.add(quitGame);

		if (userInput)
			gameBoard.addMouseListener(new BoardActionListener());

		content.add(gameBoard, BorderLayout.CENTER);
		content.add(infoPanel, BorderLayout.PAGE_START);
		content.add(buttonPanel, BorderLayout.PAGE_END);

		pack();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void setBoard(Board b) {
		gameBoard.setModel(b);
	}

	public void setMessage(String m) {
		gameMessage.setText(m);
	}

	public void setTurn(String m) {
		gameTurn.setText(m);
	}

	public void setScore(String m) {
		gameScore.setText(m);
	}

	public void setController(Controller c) {
		this.c = c;
	}

	@Override
	public void repaint() {
		gameBoard.validate();
		gameBoard.repaint();
	}

	public class BoardActionListener implements MouseListener {
		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseClicked(MouseEvent e) // translate clicks to squares on
												// the board
		{
			int x = e.getX();
			int y = e.getY();
			if (x < BoardGUI.SIZE && y < BoardGUI.SIZE) {
				int row = x / BoardGUI.CELLSIZE;
				int col = y / BoardGUI.CELLSIZE;
				if (c != null)
					c.move(row, col); // if there is a controller, notify it
			}
		}
	}
}
