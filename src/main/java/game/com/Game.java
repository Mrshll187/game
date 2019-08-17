package game.com;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;
import javax.swing.Timer;
import game.com.adapter.ClosingWindowAdapter;
import game.com.util.ResourceManager;

public class Game implements ActionListener, KeyListener {

  private static final int START_BUTTON_W = 273;
  private static final int START_BUTTON_H = 108;

  private JFrame mainFrame;
  private JPanel topPanel;
  private JPanel contentPanel;

  private boolean inGameMode;

  private JButton pauseButton;
  private JButton startButton;
  private Board board;
  private Menu menu;
  private Timer timer;
  private Clip backgroundMusicClip;

  public Game() {

    inGameMode = false;

    initialize();

    mainFrame = new JFrame("Shitty Game");
    mainFrame.setMinimumSize(ResourceManager.getFractionalScreenDimension(4, 4));
    mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    mainFrame.setResizable(true);
    mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    mainFrame.addWindowListener(new ClosingWindowAdapter(board));
    mainFrame.setAlwaysOnTop(false);
    mainFrame.addKeyListener(this);

    timer = new Timer(25, new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent e) {
        topPanel.revalidate();
        topPanel.repaint();
      }
    });

    mainFrame.setContentPane(contentPanel);

    backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
    backgroundMusicClip.start();
    
    mainFrame.setVisible(true);
  }

  private void initialize() {
    
    try {
    
      backgroundMusicClip = AudioSystem.getClip();
      backgroundMusicClip.open(AudioSystem.getAudioInputStream(ResourceManager.getResourceByName("bgm.wav")));
      
      contentPanel = createContentPane();
    }
    catch(Exception e) {
      
      System.out.println("Failure initializing game resources : " + e.getMessage());
      System.exit(1);
    }
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
  public void keyTyped(KeyEvent e) {
    //System.out.println("User is pressing " + e.getKeyChar() + " for some reason");
  }

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

        board = new Board();
        
        topPanel.add(board);
        topPanel.revalidate();
        inGameMode = true;
      }
    }
  }
}
