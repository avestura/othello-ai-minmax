package othello.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import othello.ai.ReversiAI;
import othello.controller.AIControllerSinglePlay;
import othello.controller.AIList;
import othello.model.Board;
import othello.model.Listener;

@SuppressWarnings("serial")
public class TestFrameAIVSAI extends JFrame implements ActionListener, Logger,
		Listener {
	private static Border THIN_BORDER = new EmptyBorder(4, 4, 4, 4);

	public JComboBox<Object> leftAICombo;
	public JComboBox<Object> rightAICombo;
	private JButton startTest;
	private JButton pauseTest;
	private JButton viewGame;
	private BoardGUI boardGUI;
	private AIControllerSinglePlay aiControllerSinglePlay;
	private Thread thread;

	public TestFrameAIVSAI() {
		super();
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		startTest = new JButton("Start");
		startTest.addActionListener(this);
		pauseTest = new JButton("Pause");
		pauseTest.addActionListener(this);
		viewGame = new JButton("View game->");
		viewGame.addActionListener(this);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(startTest);
		buttonPanel.add(pauseTest);
		leftAICombo = new JComboBox<Object>(AIList.getAINameList());
		leftAICombo.setBorder(BorderFactory.createTitledBorder("Black"));

		rightAICombo = new JComboBox<Object>(AIList.getAINameList());
		rightAICombo.setBorder(BorderFactory.createTitledBorder("White"));

		JLabel vsLabel = new JLabel("vs");

		JPanel aiPanel = new JPanel();
		aiPanel.setLayout(new BoxLayout(aiPanel, BoxLayout.LINE_AXIS));
		aiPanel.add(leftAICombo);
		aiPanel.add(vsLabel);
		aiPanel.add(rightAICombo);
		aiPanel.setBorder(THIN_BORDER);

		JPanel controlPanel = new JPanel();
		controlPanel
				.setLayout(new BoxLayout(controlPanel, BoxLayout.PAGE_AXIS));
		controlPanel.add(aiPanel);
		controlPanel.add(buttonPanel);

		logln("Ready");

		boardGUI = new BoardGUI();

		contentPane.add(boardGUI, BorderLayout.PAGE_START);
		contentPane.add(controlPanel, BorderLayout.CENTER);

		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		if (event.getSource() == startTest) {
			startTest.setEnabled(false);
			runTests();
		}

		if (event.getSource() == pauseTest) {
			if(aiControllerSinglePlay == null)
				return;
			if(aiControllerSinglePlay.waitTime != Integer.MAX_VALUE){
				aiControllerSinglePlay.setWaitTime(Integer.MAX_VALUE);
				pauseTest.setText("Resume");
			}else{
				aiControllerSinglePlay.setWaitTime(aiControllerSinglePlay.DEFAULT_WAIT_TIME);
				pauseTest.setText("pause");
				thread.interrupt();
			}
		}
	}

	private void runTests() {
		
		String s_leftAI = (String) leftAICombo.getSelectedItem();
		String s_rightAI = (String) rightAICombo.getSelectedItem();

		final ReversiAI blackAI = AIList.getAIByName(s_leftAI);
		final ReversiAI whiteAI = AIList.getAIByName(s_rightAI);

		aiControllerSinglePlay = new AIControllerSinglePlay(
				TestFrameAIVSAI.this, whiteAI, blackAI);

		thread = new Thread() {
			@Override
			public void run() {
				aiControllerSinglePlay.newGame();
				startTest.setEnabled(true);
			}
		};
		thread.start();
	}

	@Override
	public void log(String msg) {
	}

	@Override
	public void logln(String msg) {
		log(msg + "\n");
	}

	public void setBoard(Board b) {
		boardGUI.setModel(b);
	}

	public void setMessage(String m) {
	}

	public void setTurn(String m) {
	}

	public void setScore(String m) {
	}

	public void repaint() {
		boardGUI.validate();
		boardGUI.repaint();
	}

}
