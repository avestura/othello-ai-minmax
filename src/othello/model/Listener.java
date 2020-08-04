package othello.model;

// interface for a class that listens to the controller

public interface Listener
{
  public void setBoard(Board b);
  public void setMessage(String m);
  public void setTurn(String m);
  public void setScore(String m);
  public void repaint();
}