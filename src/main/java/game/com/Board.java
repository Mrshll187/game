package game.com;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import game.com.actionListener.GameActionListener;
import game.com.util.ResourceManager;

@SuppressWarnings("serial")
public class Board extends JPanel implements ComponentListener {

  private Timer timer;
  private Random random = new Random();

  private final int enemySpawnInterval = 35;
  private final int InvalnerableDuration = 30;

  private boolean playingGame;
  private int frameWidth;
  private int frameHeight;

  private int LAND_HEIGHT = (int) (0.8 * frameHeight);
  private int WATER_HEIGHT = (int) (0.95 * frameHeight);
  private int MOUNTAIN_HEIGHT = (int) (0.82 * frameWidth);

  private int enemySpeed;
  private int numEnemies;
  private int spawnInterval;
  private int score;
  private int scoreWidth;

  private Font scoreFont;
  private FontMetrics metric;

  private Clip backgroundMusicClip;
  
  private List<Terrain> terrains = new ArrayList<>();

  private Terrain cloud;
  private Terrain ground;
  private Terrain ground2;
  private Terrain water;
  private Terrain water2;
  private Terrain mountain;
  private Terrain sun;

  private Player player;

  private ArrayList<Enemy> enemies;

  private Image lifeIndicator;

  public Timer getTimer() {
    return timer;
  }

  public Board() {

    initialize();
    
    addComponentListener(this);
    setDoubleBuffered(true);
    setLayout(null);

    scoreFont = new Font("Calibri", Font.BOLD, 45);
    score = 0;
    scoreWidth = 0;
    
    frameWidth = getWidth();
    frameHeight = getHeight();

    spawnInterval = 0;
    enemySpeed = -7;
    numEnemies = 500;
    enemies = new ArrayList<>();

    terrains.add(cloud);
    terrains.add(ground);
    terrains.add(ground2);
    terrains.add(water);
    terrains.add(water2);
    terrains.add(mountain);
    terrains.add(sun);

    player.setLandYAxis(385);

    lifeIndicator = ResourceManager.getImage("hud_x.png");

    timer = new Timer(25, new GameActionListener(this));
    timer.start();

    playingGame = true;
    startGameMusic();
  }

  private void initialize() {

    try {

      player = new Player();
      
      cloud = new Terrain(-2, "Cloud_1.png", 0.2f);
      ground = new Terrain(-5, "grassMid.png");
      ground2 = new Terrain(-5, "grassCenter.png");
      water = new Terrain(-15, "liquidWaterTop_mid.png");
      water2 = new Terrain(-15, "liquidWater.png");
      mountain = new Terrain(-1, "Mountains.png");
      sun = new Terrain(0, "screamingSun.gif");
      
      backgroundMusicClip = AudioSystem.getClip();
      backgroundMusicClip.open(AudioSystem.getAudioInputStream(ResourceManager.getResourceByName("bgm.wav")));
    }
    catch (Exception e) {

      System.out.println("Failure initializing board resources");
      System.exit(1);
    }
  }
  
  @Override
  public void paintComponent(Graphics g) {

    super.paintComponent(g);

    if (playingGame) {

      drawSky(g);
      drawSun(g);
      drawCloud(g);
      drawMountain(g);
      drawLand(g);
      drawWater(g);
      drawPlayer(g);
      drawEnemies(g);
      drawWeapons(g);
      drawHUD(g);
    }
    else {

      gameOver(g);
    }
  }

  private void drawSky(Graphics g) {
    g.setColor(new Color(181, 229, 216));
    g.fillRect(0, 0, frameWidth, (int) (frameHeight * 0.8));
  }

  private void drawSun(Graphics g) {
    g.drawImage(sun.getSprite(), (int) (frameHeight * 0.1), (int) (frameHeight * 0.1), null);
  }

  private void drawCloud(Graphics g) {
    for (int x = cloud.getX(); x < frameWidth; x += cloud.getW())
      g.drawImage(cloud.getSprite(), x, (int) (frameHeight * 0.1), null);
  }

  private void drawMountain(Graphics g) {
    for (int x = mountain.getX(); x < frameWidth; x += mountain.getW())
      g.drawImage(mountain.getSprite(), x, MOUNTAIN_HEIGHT, null);
  }

  private void drawLand(Graphics g) {

    for (int y = LAND_HEIGHT; y < frameHeight; y += ground.getH()) {

      if (y == LAND_HEIGHT) {

        for (int x = ground.getX(); x < frameWidth; x += ground.getW()) {
          g.drawImage(ground.getSprite(), x, y, null);
        }
      }
      else {

        for (int x = ground.getX(); x < frameWidth; x += ground2.getW()) {
          g.drawImage(ground2.getSprite(), x, y, null);
        }
      }
    }
  }

  private void drawWater(Graphics g) {

    for (int y = WATER_HEIGHT; y < frameHeight; y += water.getH()) {

      if (y == WATER_HEIGHT) {

        for (int x = water.getX(); x < frameWidth; x += water.getW())
          g.drawImage(water.getSprite(), x, y, null);
      }
      else {

        for (int x = water.getX(); x < frameWidth; x += water2.getW())
          g.drawImage(water2.getSprite(), x, y, null);
      }
    }
  }

  private void drawPlayer(Graphics g) {

    if (player.isGodMode() && player.getInvulnDur() % 2 == 0) 
      return;

    g.drawImage(player.getCurrentSpriteImage(), player.getX(), player.getY(), this);
  }

  private void drawWeapons(Graphics g) {
    player.getWeapons().stream().forEach(w -> g.drawImage(w.getImage(), w.getX(), w.getY(), null));
  }

  private void drawEnemies(Graphics g) {
    enemies.stream().forEach(e -> g.drawImage(e.getCurrentSpriteImage(), e.getX(), e.getY(), null));
  }

  private void drawHUD(Graphics g) {

    g.setColor(Color.WHITE);
    g.setFont(scoreFont);

    metric = g.getFontMetrics(scoreFont);
    scoreWidth = metric.stringWidth(String.format("%d", score));
    g.drawString(String.format("%d", score), frameWidth - 80, 55);

    int curLives = player.getLives();

    for (int i = 0, x = 25; i < curLives; i++, x += 50)
      g.drawImage(lifeIndicator, x, 25, null);

    if (curLives == 0) {

      playingGame = false;
      timer.stop();
      repaint();

      if (JOptionPane.showConfirmDialog(null, "Exit Game?", "Notice",
          JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
        System.exit(0);
      else
        restartGame();
    }
  }

  public void stopGameMusic() {
    
    backgroundMusicClip.stop();
  }

  public void startGameMusic() {
    
    backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
    backgroundMusicClip.start();
  }
  
  private void gameOver(Graphics g) {

    stopGameMusic();
    
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, frameWidth, frameHeight);
    
//    Image gameOver = new ImageIcon(this.getClass().getResource("Logo.png")).getImage();
//    
//    g.drawImage(gameOver, (frameWidth / 2) - (gameOver.getWidth(null) / 2),
//        (frameHeight / 2) - (gameOver.getHeight(null) / 2), null);
    
    Font largeScoreFont = new Font("Calibri", Font.BOLD, 100);
    metric = g.getFontMetrics(scoreFont);
    
    FontMetrics metric2 = g.getFontMetrics(scoreFont);
    scoreWidth = metric2.stringWidth(String.format("%d", score));
    
    String message1 = "You're fucking dead!";
    String message2 = "Your Score " + String.format("%d", score);
    
    g.setColor(Color.BLACK);
    g.setFont(largeScoreFont);
    g.setFont(scoreFont);
    g.drawString(message1, frameWidth / 2 - metric.stringWidth(message1) / 2, 100);
    g.drawString(message2, frameWidth / 2 - metric.stringWidth(message2) / 2, frameHeight - 100);
  }

  @Override
  public void componentResized(ComponentEvent e) {

    timer.stop();
    frameHeight = getHeight();
    frameWidth = getWidth();

    LAND_HEIGHT = (int) (0.85 * frameHeight);
    WATER_HEIGHT = (int) (0.92 * frameHeight);
    MOUNTAIN_HEIGHT = (LAND_HEIGHT - 500);

    enemies.iterator().forEachRemaining(enemy -> enemy.setY(LAND_HEIGHT - 81 + 5));

    timer.start();
  }

  @Override
  public void componentMoved(ComponentEvent e) {}

  @Override
  public void componentShown(ComponentEvent e) {}

  @Override
  public void componentHidden(ComponentEvent e) {}

  public void keyPressed(KeyEvent e) throws Exception {

    int key = e.getKeyCode();

    if (key == KeyEvent.VK_UP) {

      if (playingGame)
        player.jump(true);
      else
        restartGame();
    }
    else if (key == KeyEvent.VK_LEFT) {
      player.setBackPeddling(true);
      player.setDx(-9);
    }
    else if (key == KeyEvent.VK_SPACE) {
      player.fire();
    }
    else if (key == KeyEvent.VK_DOWN) {
      timer.stop();
    }
    else if (key == KeyEvent.VK_RIGHT) {

      player.setBackPeddling(false);
      player.setDx(9); 
    }
    else if (key == KeyEvent.VK_ESCAPE) {

      timer.stop();
      player.setStaticImage();
      stopGameMusic();
      
      int x = JOptionPane.showConfirmDialog(null, "Exit Game?", "Welp...", JOptionPane.YES_NO_OPTION);
      
      if (x == JOptionPane.YES_OPTION)
        System.exit(0);
      else {
        timer.start();
        startGameMusic();
      }
    }
  }

  public void keyReleased(KeyEvent e) throws Exception {

    if (e.getKeyCode() == KeyEvent.VK_UP)
      player.jump(false);
    else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
      player.setBackPeddling(false);
      player.setDx(0);
    }
    else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
      timer.start();
    }
    else if (e.getKeyCode() == KeyEvent.VK_RIGHT) player.setDx(0);
  }

  public void spawnEnemies() {

    if (enemies.size() < numEnemies) {

      if (genEnemyChance() > 7)
        enemies.add(new Enemy(frameWidth + 250, LAND_HEIGHT - 150, enemySpeed));
    }
  }

  private int genEnemyChance() {
    return random.nextInt(10) + 1;
  }

  public void restartGame() {

    startGameMusic();
    
    player.setX((int) (0.15 * frameWidth));
    player.setLives(3);
    enemies.clear();
    score = 0;
    playingGame = true;
    timer.start();
  }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public ArrayList<Enemy> getEnemies() {
    return enemies;
  }

  public void setEnemies(ArrayList<Enemy> enemies) {
    this.enemies = enemies;
  }

  public int getSpawnInterval() {
    return spawnInterval;
  }

  public void incrementSpawnInterval() {
    spawnInterval++;
  }

  public void decrementSpawnInterval() {
    spawnInterval--;
  }

  public void setSpawnInterval(int value) {
    this.spawnInterval = value;
  }

  public void incrementScore() {
    score++;
  }

  public int getScore() {
    return score;
  }

  public boolean isPlayingGame() {
    return playingGame;
  }

  public void setPlayingGame(boolean playingGame) {
    this.playingGame = playingGame;
  }

  public int getEnemySpawnInterval() {
    return enemySpawnInterval;
  }

  public int getInvalnerableDuration() {
    return InvalnerableDuration;
  }

  public List<Terrain> getTerrains() {
    return terrains;
  }
}
