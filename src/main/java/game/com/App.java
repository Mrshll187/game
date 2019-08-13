package game.com;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;
import javax.swing.Timer;
import game.com.util.ResourceUtil;

public class App implements ActionListener, KeyListener {

  private static final int START_BUTTON_W = 273;
  private static final int START_BUTTON_H = 108;

  private static JFrame mainFrame;
  private JPanel topPanel;

  private boolean inGameMode;

  private JButton pauseButton;
  private JButton startButton;
  private Board board;
  private Menu menu;
  private Timer timer;
  private Clip clip;

  public static void main(String[] args) {

    EventQueue.invokeLater(new Runnable() {

      @Override
      public void run() {

        try {

          new App();
        }
        catch (IOException e) {
          e.printStackTrace();
        }

        mainFrame.setVisible(true);
      }
    });
  }

  private App() throws IOException {

    inGameMode = false;

    try {
      clip = AudioSystem.getClip();
      clip.open(AudioSystem.getAudioInputStream(ResourceUtil.getResourceByName("bgm.wav")));
    }
    catch (Exception e) {
      JOptionPane.showMessageDialog(null, "Failed to load background music: " + e, "Error",
          JOptionPane.ERROR_MESSAGE);
    }
    mainFrame = new JFrame("A Java Game");
    mainFrame.setMinimumSize(ResourceUtil.getFractionalScreenDimension(4, 4));
    mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    mainFrame.setResizable(true);
    mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    mainFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        if (board != null && board.getTimer().isRunning()) board.getTimer().stop();
        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to exit game?", "Notice",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
          System.exit(0);
        }
        if (board != null && !board.getTimer().isRunning()) board.getTimer().start();
      }
    });

    mainFrame.setAlwaysOnTop(false);
    mainFrame.addKeyListener(this);

    timer = new Timer(25, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        topPanel.revalidate();
        topPanel.repaint();
      }
    });

    mainFrame.setContentPane(createContentPane());

    clip.loop(Clip.LOOP_CONTINUOUSLY);
    clip.start();
  }

  private JPanel createContentPane() throws IOException {

    topPanel = new JPanel(); // topmost JPanel in layout hierarchy
    topPanel.setBackground(Color.BLACK);
    topPanel.addKeyListener(this);

    // Allow us to layer the panels
    LayoutManager overlay = new OverlayLayout(topPanel);
    topPanel.setLayout(overlay);

    // start button
    startButton = new JButton("Start");
    startButton.setOpaque(false);
    startButton.setMinimumSize(new Dimension(START_BUTTON_W, START_BUTTON_H));
    startButton.setContentAreaFilled(false);
    startButton.setBorderPainted(false);
    startButton.setFocusable(false); // rather than just setFocusable(false)
    startButton.setAlignmentX(0.5f); // center horizontally on-screen
    startButton.setAlignmentY(0.5f); // center vertically on-screen
    startButton.addActionListener(this);

    // pause button
    pauseButton = new JButton("Resume Playing");
    pauseButton.setFocusable(false); // rather than just setFocusable(false)
    pauseButton.setFont(new Font("Calibri", Font.BOLD, 42));
    pauseButton.setAlignmentX(0.5f); // center horizontally on-screen
    pauseButton.setAlignmentY(0.5f); // center vertically on-screen
    pauseButton.addActionListener(this);
    pauseButton.setVisible(false);
    topPanel.add(pauseButton);

    // Must add last to ensure button's visibility
    menu = new Menu(true);
    topPanel.add(startButton);
    topPanel.add(menu);

    return topPanel;
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    if (e.getSource() == startButton) {
      topPanel.remove(startButton);
      topPanel.remove(menu);
      topPanel.revalidate();
    }
    else if (e.getSource() == pauseButton) {

      if (!board.getTimer().isRunning()) {
        timer.stop();
        board.getTimer().start();
        pauseButton.setVisible(false);
      }
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {}

  @Override
  public void keyPressed(KeyEvent e) {

    if (inGameMode) {
      try {
        board.keyPressed(e);
      }
      catch (Exception e1) {
        e1.printStackTrace();
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {

    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {

      if (board.getTimer().isRunning()) {

        board.getTimer().stop();
        timer.start();
        pauseButton.setVisible(true);
      }
      else {

        timer.stop();
        board.getTimer().start();
        pauseButton.setVisible(false);
      }
    }
    else if (inGameMode) {
      try {
        board.keyReleased(e);
      }
      catch (Exception e1) {
        e1.printStackTrace();
      }
    }
    else if (!inGameMode) {

      if (e.getKeyCode() == KeyEvent.VK_ENTER) {

        try {
          board = new Board();
        }
        catch (Exception e1) {
          e1.printStackTrace();
        }
        topPanel.add(board);
        topPanel.revalidate();
        inGameMode = true;
      }
    }
  }
}
