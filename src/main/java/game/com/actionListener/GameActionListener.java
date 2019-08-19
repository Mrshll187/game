package game.com.actionListener;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import game.com.Board;
import game.com.Collidable;
import game.com.Enemy;
import game.com.Player;

public class GameActionListener implements ActionListener {

  private Board board;
  
  public GameActionListener(Board board) {
    this.board = board;
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {

    if (board.isPlayingGame()) {

      Player player = board.getPlayer();
      
      board.getTerrains().stream().forEach(t -> t.nextPos());
      
      player.nextFrame();
      player.updatePos();

      if (board.getSpawnInterval() == board.getEnemySpawnInterval()) {
        
        board.spawnEnemies();
        board.setSpawnInterval(-1);
      }

      player.getWeapons().stream().forEach(w -> w.incrementX(30));
      
      List<Enemy> enemies = board.getEnemies();
      
      enemies.removeIf(Enemy::isDead);
      enemies.removeIf(Enemy::hasExceededBounds);
      enemies.forEach(enemy -> enemy.updateFrameAndPosition());

      player.checkInvulnerability();
      player.checkFiringDuration();
    }

    collisionDetector(board);
    
    board.repaint();
    board.incrementSpawnInterval();
  }
  
  public static void collisionDetector(Board board) {
    
    List<Enemy> enemies = board.getEnemies();
    Player player = board.getPlayer();
    
    enemies.iterator().forEachRemaining(enemy -> {
    
      ArrayList<Collidable> collidables = new ArrayList<Collidable>();
      collidables.add(player);
      
      player.getWeapons().stream().forEach(w -> collidables.add(w));
      
      Rectangle enemyRectangle = enemy.getBounds();
      BufferedImage enemyBufferedImage = enemy.getBufferedImage();
  
      collidables.forEach(collidable -> {
      
        Rectangle collidableRectangle = collidable.getBounds();
        BufferedImage collidableBufferedImage = collidable.getBufferedImage();
        
        if (collidableRectangle.intersects(enemyRectangle)) {
    
          Rectangle intersectionRectangle = collidableRectangle.intersection(enemyRectangle);
    
          int firstI = (int) (intersectionRectangle.getMinX() - collidableRectangle.getMinX());
          int firstJ = (int) (intersectionRectangle.getMinY() - collidableRectangle.getMinY());
          int bp1XHelper = (int) (collidableRectangle.getMinX() - enemyRectangle.getMinX());
          int bp1YHelper = (int) (collidableRectangle.getMinY() - enemyRectangle.getMinY());
    
          for (int i = firstI; i < intersectionRectangle.getWidth() + firstI; i++) {
            for (int j = firstJ; j < intersectionRectangle.getHeight() + firstJ; j++) {
    
              int color = collidableBufferedImage.getRGB(i, j) & 0xFF000000;
              int enemyColor = enemyBufferedImage.getRGB(i + bp1XHelper, j + bp1YHelper) & 0xFF000000;
              
              if (color != 0 && enemyColor != 0) {
    
                if(collidable instanceof Player)
                  board.getPlayer().playHurtSound();
                
                enemy.die();
    
                if (!enemy.isMarkedDead()) {
    
                  enemy.markDead(true);
                  board.incrementScore();
                }
                
                if (!player.isInvicible() && collidable.isDamageable()) {
    
                  player.changeLives(-1);
                  
                  if (!player.isGodMode()) 
                    player.setInvulnDur(board.getInvalnerableDuration());
                  
                  break;
                }
                
                player.getWeapons().remove(collidable);
              }
            }
          }
        }
      });
    });
  }
}