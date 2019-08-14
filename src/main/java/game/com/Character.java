package game.com;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Character implements Collidable{

  private int frames = 11;
  protected int x;
  protected int y;
  private int currentFrame;
  private Image[] sprite;

  public int getFrames() {
    return frames;
  }

  public void setFrames(int frames) {
    this.frames = frames;
  }

  public int getCurrentFrame() {
    return currentFrame;
  }

  public void setCurrentFrame(int currentFrame) {
    this.currentFrame = currentFrame;
  }

  public void nextFrame() {

    if (currentFrame == (frames - 1))
      currentFrame = 0;
    else
      currentFrame++;
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
  
  public Image[] getSprite() {
    return sprite;
  }

  public void setSprite(Image[] sprite) {
    this.sprite = sprite;
  }

  public void setSpriteImageIndex(int index, Image image) {
    this.sprite[index] = image;
  }

  public Image getCurrentSpriteImage() {
    return sprite[currentFrame];
  }

  public Image getSpriteImage(int index) {
    return sprite[index];
  }
  
  @Override
  public Rectangle getBounds() {
    return (new Rectangle(x, y, sprite[currentFrame].getWidth(null),
        sprite[currentFrame].getHeight(null)));
  }

  @Override
  public BufferedImage getBufferedImage() {
    
    BufferedImage bi = new BufferedImage(sprite[currentFrame].getWidth(null), sprite[currentFrame].getHeight(null), BufferedImage.TYPE_INT_ARGB);
    
    Graphics g = bi.getGraphics();
    g.drawImage(sprite[currentFrame], 0, 0, null);
    g.dispose();
    
    return bi;
  }
}
