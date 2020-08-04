package othello.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import othello.model.Board;

public class BoardGUI extends JPanel
{
	private static final long serialVersionUID = 1L; /* needed for Eclipse */
	
	public static final int ROWS = 8;
  public static final int SIZE = 400;
  public static final int CELLSIZE = SIZE / ROWS;
  public static final double OFFSET = 0.1;

  private Board model; // the board that will be drawn next
  private BufferedImage backgroundImage;
  private BufferedImage white, black, block;
  
  public BoardGUI()
  {
	try{
		backgroundImage = ImageIO.read(new File("resources/bg.jpg"));
		white = ImageIO.read(new File("resources/white.png"));
		black = ImageIO.read(new File("resources/black.png"));
		block = ImageIO.read(new File("resources/block.png"));
	}catch(IOException e){
		e.printStackTrace();
	}
    setBackground(Color.GRAY);
    setLayout(null);
  }
  
  // external interface to redraw the board
  public void setModel(Board b) { model = b; repaint(); }
  
  public Dimension getPreferredSize() { return new Dimension(SIZE+1, SIZE+1); }
  
  // redraw the board
  public void paintComponent(Graphics g)
  {
	super.paintComponent(g);
	g.drawImage(backgroundImage, 0, 0, SIZE, SIZE, 200, 0, 1400, 1200, null);
	
	g.setColor(new Color(255, 255, 255, 100));
    for(int i = 0; i <= ROWS; i++){ // divide the board into squares
      g.drawLine(i * CELLSIZE, 0, i * CELLSIZE, SIZE);
      g.drawLine(0, i * CELLSIZE, SIZE, i * CELLSIZE);
    }
    
    g.fillOval(2*CELLSIZE - 2, 2*CELLSIZE - 2, 5, 5); // mark the inner quadrant
    g.fillOval(SIZE-2*CELLSIZE - 2, 2*CELLSIZE - 2, 5, 5);
    g.fillOval(2*CELLSIZE - 2, SIZE-2*CELLSIZE - 2, 5, 5);
    g.fillOval(SIZE-2*CELLSIZE - 2, SIZE-2*CELLSIZE - 2, 5, 5);

    // draw all pieces currently on the board
    for(int i = 0; i < ROWS; i++){
      for(int j = 0; j < ROWS; j++){

    	if(model != null){
    		int state = model.getState(i, j);
			if(state == Board.BLACK)
				g.drawImage(black, (int)(i * CELLSIZE + OFFSET * CELLSIZE), (int)(j * CELLSIZE + OFFSET * CELLSIZE),
    					(int)(CELLSIZE - 2 * OFFSET * CELLSIZE), (int)(CELLSIZE - 2 * OFFSET * CELLSIZE), null);
			if(state == Board.WHITE)
    				g.drawImage(white, (int)(i * CELLSIZE + OFFSET * CELLSIZE), (int)(j * CELLSIZE + OFFSET * CELLSIZE),
        					(int)(CELLSIZE - 2 * OFFSET * CELLSIZE), (int)(CELLSIZE - 2 * OFFSET * CELLSIZE), null);
			if(state == Board.BLOCK)
				g.drawImage(block, (int)(i * CELLSIZE + OFFSET * CELLSIZE), (int)(j * CELLSIZE + OFFSET * CELLSIZE),
    					(int)(CELLSIZE - 2 * OFFSET * CELLSIZE), (int)(CELLSIZE - 2 * OFFSET * CELLSIZE), null);
    	}
      }
    }
  }
}
