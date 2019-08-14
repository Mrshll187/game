package game.com;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import game.com.util.ResourceManager;

public class Weapon implements Collidable {

  public int x;
  public int y;
  private Image image;
  
  public Weapon() {
    image = ResourceManager.getImage("fireball.gif");
  }
  
  public Image getImage() {
    return image;
  }
  
  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }
  
  public void incrementX(int value) {
    this.x += value;
  }

  public void decrementX(int value) {
    this.x -= value;
  }
  
  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }
  
  public void incrementY(int value) {
    this.y += value;
  }

  public void decrementY(int value) {
    this.y -= value;
  }
  
  @Override
  public Rectangle getBounds() {
    return new Rectangle(x, y, image.getWidth(null), image.getHeight(null));
  }

  @Override
  public BufferedImage getBufferedImage() {
    
    BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
    
    Graphics g = bi.getGraphics();
    g.drawImage(image, 0, 0, null);
    g.dispose();
    
    return bi;
  }

  @Override
  public boolean isDamageable() {
    return false;
  }
}
