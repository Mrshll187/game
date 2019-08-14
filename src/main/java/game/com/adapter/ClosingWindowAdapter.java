package game.com.adapter;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;
import game.com.Board;

public class ClosingWindowAdapter extends WindowAdapter {

  private Board board;

  public ClosingWindowAdapter(Board board) {
    this.board = board;
  }

  @Override
  public void windowClosing(WindowEvent e) {
    
    super.windowClosing(e);
    
    if (board != null && board.getTimer().isRunning()) board.getTimer().stop();
    
    if (JOptionPane.showConfirmDialog(null, "Are you sure you want to exit game?", "Notice",
        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
      System.exit(0);
    }
    
    if (board != null && !board.getTimer().isRunning()) 
      board.getTimer().start();
  }
}
