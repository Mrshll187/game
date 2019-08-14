package game.com;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public interface Collidable {

  public Rectangle getBounds();
  
  public BufferedImage getBufferedImage();
}
