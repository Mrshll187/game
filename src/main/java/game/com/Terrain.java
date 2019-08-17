package game.com;

import java.awt.Image;
import java.io.IOException;
import game.com.util.ResourceManager;

public class Terrain {
  
  private int initX;
  private int w;
  private int h;
  private int pace;
  private Image sprite;

  public Terrain(int pace, String resourceName) throws IOException {
    
    this.initX = 0;
    this.pace = pace;
    
    sprite = ResourceManager.getImage(resourceName);

    w = sprite.getWidth(null);
    h = sprite.getHeight(null);
  }
  
  public Terrain(int pace, String resourceName, float scaleSprite) throws IOException {
    
    this(pace, resourceName);
    this.scaleSprite(scaleSprite);
  }

  public Image getSprite() {
    return sprite;
  }

  public int getInitX() {
    return initX;
  }

  public void nextPos() {
    initX += (pace);
    if (initX < (-w)) {
      initX += w;
    }
  }

  public int getH() {
    return h;
  }

  public int getW() {
    return w;
  }

  public void scaleSprite(float factor) {
    
    int newW = (int) (w * factor);
    int newH = (int) (h * factor);
    
    sprite = sprite.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
  }
}
