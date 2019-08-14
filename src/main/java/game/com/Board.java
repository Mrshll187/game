package game.com;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import game.com.util.ResourceManager;

@SuppressWarnings("serial")
public class Board extends JPanel implements ComponentListener {

  private Timer timer;
  private Random random;

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
  private int i = 0;
  private int score;
  private int scoreWidth;

  private Font scoreFont;
  private FontMetrics metric;

  private Terrain cloud, ground, ground2, water, water2, mountain, sun;
  private Player player;
  private ArrayList<Enemy> enemies;
  private Image life;

  public Timer getTimer() {
    return timer;
  }

  public Board() throws Exception {

    addComponentListener(this);
    setDoubleBuffered(true);

    this.frameWidth = getWidth();
    this.frameHeight = getHeight();
    score = 0;
    random = new Random();
    setLayout(null);
    scoreWidth = 0;

    enemySpeed = -7;
    numEnemies = 500;
    enemies = new ArrayList<>();
    scoreFont = new Font("Calibri", Font.BOLD, 45);

    cloud = new Terrain(-2, "Cloud_1.png");
    cloud.scaleSprite(0.2f);
    ground = new Terrain(-5, "grassMid.png");
    ground2 = new Terrain(-5, "grassCenter.png");
    water = new Terrain(-15, "liquidWaterTop_mid.png");
    water2 = new Terrain(-15, "liquidWater.png");
    mountain = new Terrain(-1, "Mountains.png");
    sun = new Terrain(0, "screamingSun.gif");

    life = ResourceManager.getImage("hud_x.png");

    player = new Player();
    player.setLandYAxis(385);

    timer = new Timer(25, new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        if (playingGame) {

          cloud.nextPos();
          ground.nextPos();
          ground2.nextPos();
          water.nextPos();
          water2.nextPos();
          mountain.nextPos();
          player.nextFrame();
          player.updatePos();

          if (i == enemySpawnInterval) {
            spawnEnemies();
            i = -1;
          }

          player
          .getWeapons()
          .stream()
          .forEach(w -> {
            
            w.incrementX(30);
            
          });
          
          enemies.removeIf(Enemy::isDead);
          
          for(Enemy enemy : enemies) {
            
            if(enemy.isDead()) {
              enemies.remove(enemy);
              continue;
            }
            
            if (enemy.getX() < -150) {
              
              enemies.remove(enemy);
              break;
            }

            enemy.nextFrame();
            enemy.updatePos();
          }

          player.checkInvulnerability();
          player.checkFiringDuration();
        }

        checkCollisions();
        repaint();
        i++;
      }
    });

    timer.start();
    playingGame = true;
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
    for (int x = cloud.getInitX(); x < frameWidth; x += cloud.getW())
      g.drawImage(cloud.getSprite(), x, (int) (frameHeight * 0.1), null);
  }

  private void drawMountain(Graphics g) {
    for (int x = mountain.getInitX(); x < frameWidth; x += mountain.getW())
      g.drawImage(mountain.getSprite(), x, MOUNTAIN_HEIGHT, null);
  }

  private void drawLand(Graphics g) {
    
    for (int y = LAND_HEIGHT; y < frameHeight; y += ground.getH()) {
      
      if (y == LAND_HEIGHT) {
        
        for (int x = ground.getInitX(); x < frameWidth; x += ground.getW()) {
          g.drawImage(ground.getSprite(), x, y, null);
        }
      }
      else {
        
        for (int x = ground.getInitX(); x < frameWidth; x += ground2.getW()) {
          g.drawImage(ground2.getSprite(), x, y, null);
        }
      }
    }
  }

  private void drawWater(Graphics g) {

    for (int y = WATER_HEIGHT; y < frameHeight; y += water.getH()) {

      if (y == WATER_HEIGHT) {
        
        for (int x = water.getInitX(); x < frameWidth; x += water.getW())
          g.drawImage(water.getSprite(), x, y, null);
      }
      else {
        
        for (int x = water.getInitX(); x < frameWidth; x += water2.getW())
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
    
    player
    .getWeapons()
    .stream()
    .forEach(w -> {
      
      g.drawImage(w.getImage(), w.getX(), w.getY(), null);
    });
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
      g.drawImage(life, x, 25, null);

    if (curLives == 0) {

      playingGame = false;
      timer.stop();
      repaint();
    }
  }

  private void gameOver(Graphics g) {

    g.setColor(Color.WHITE);
    g.fillRect(0, 0, frameWidth, frameHeight);
    Image gameOver = new ImageIcon(this.getClass().getResource("Logo.png")).getImage();
    g.drawImage(gameOver, (frameWidth / 2) - (gameOver.getWidth(null) / 2),
        (frameHeight / 2) - (gameOver.getHeight(null) / 2), null);
    Font largeScoreFont = new Font("Calibri", Font.BOLD, 100);
    metric = g.getFontMetrics(scoreFont);
    FontMetrics metric2 = g.getFontMetrics(scoreFont);
    scoreWidth = metric2.stringWidth(String.format("%d", score));
    String message1 = "You fucked up!";
    String message2 = "Press space to restart";
    g.setColor(Color.BLACK);
    g.setFont(largeScoreFont);
    g.drawString(String.format("%d", score), frameWidth / 2 - scoreWidth, 200);
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
    else if (key == KeyEvent.VK_RIGHT) {
      {

        player.setBackPeddling(false);
        player.setDx(9);
      }
    }
    else if (key == KeyEvent.VK_ESCAPE) {

      if (JOptionPane.showConfirmDialog(null, "Are you sure you want to exit game?", "Notice",
          JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
        System.exit(0);
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
    else if (e.getKeyCode() == KeyEvent.VK_RIGHT) player.setDx(0);
  }

  private void spawnEnemies() {

    if (enemies.size() < numEnemies) {

      if (genEnemyChance() > 7) {
        Enemy enemy = new Enemy(frameWidth + 150, LAND_HEIGHT - 100 + 5, enemySpeed);
        enemies.add(enemy);
      }
    }
  }

  private int genEnemyChance() {
    return random.nextInt(10) + 1;
  }

  public void checkCollisions() {
    enemies.iterator().forEachRemaining(en -> collisionHelper(player, en));
  }

  private void collisionHelper(Player player, Enemy enemy) {
    
    ArrayList<Collidable> collidables = new ArrayList<Collidable>();
    collidables.add(player);
    
    player.getWeapons().stream().forEach(w -> collidables.add(w));
    
    Rectangle enemyRectangle = enemy.getBounds();
    BufferedImage enemyBufferedImage = enemy.getBufferedImage();

    collidables.forEach(c -> {
    
      Rectangle collidableRectangle = c.getBounds();
      BufferedImage collidableBufferedImage = c.getBufferedImage();
      
      if (collidableRectangle.intersects(enemyRectangle)) {
  
        Rectangle intersectionRectangle = collidableRectangle.intersection(enemyRectangle);
  
        int firstI = (int) (intersectionRectangle.getMinX() - collidableRectangle.getMinX());
        int firstJ = (int) (intersectionRectangle.getMinY() - collidableRectangle.getMinY());
        int bp1XHelper = (int) (collidableRectangle.getMinX() - enemyRectangle.getMinX());
        int bp1YHelper = (int) (collidableRectangle.getMinY() - enemyRectangle.getMinY());
  
        for (int i = firstI; i < intersectionRectangle.getWidth() + firstI; i++) {
          for (int j = firstJ; j < intersectionRectangle.getHeight() + firstJ; j++) {
  
            if ((collidableBufferedImage.getRGB(i, j) & 0xFF000000) != 0x00 && (enemyBufferedImage.getRGB(i + bp1XHelper, j + bp1YHelper) & 0xFF000000) != 0x00) {
  
              enemy.die();
  
              if (!enemy.isMarkedDead()) {
  
                enemy.markDead(true);
                score++;
              }
              
              if (!player.isInvicible() && c.isDamageable()) {
  
                player.changeLives(-1);
                if (!player.isGodMode()) player.setInvulnDur(InvalnerableDuration);
                break;
              }
            }
          }
        }
      }
    });
  }

  public void restartGame() {
    
    
    player.setX((int) (0.15 * frameWidth));
    player.setLives(3);
    enemies.clear();
    score = 0;
    playingGame = true;
    timer.start();
  }
}
