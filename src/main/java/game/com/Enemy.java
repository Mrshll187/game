package game.com;

import java.awt.Image;
import java.util.logging.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import game.com.util.ResourceManager;

public class Enemy extends Character {

  private Logger logger = Logger.getLogger(Enemy.class.getName());
  
  private int speed;
  private Clip damageClip;
  private Image image;
  private Image explodeImage;
  private boolean dead = false;
  private boolean markedDead = false;

  public Enemy(int x, int y, int speed) {
    
    loadResources();
    
    super.x = x;
    super.y = y;
    setFrames(1);
    this.speed = speed;
    
    setSprite(new Image[getFrames()]);
    setSpriteImageIndex(0, image);
    setCurrentFrame(0);
  }
  
  public void loadResources() {
    
    try {
      
      Image imageX = ResourceManager.getImage("cactus.png");
      image = ResourceManager.resize(ResourceManager.toBufferedImage(imageX), 150, 150);
      
      explodeImage = ResourceManager.getImage("explode.gif");
      
      damageClip = AudioSystem.getClip();
      damageClip.open(AudioSystem.getAudioInputStream(ResourceManager.getResourceByName("explode.wav")));
    }
    catch(Exception e) {
      
      logger.severe("Failure loading enemy sound : " + e.getMessage());
      System.exit(1);
    }
  }
  
  public Image getCurrentSpriteImage() {
    return image;
  }

  public void setImage(Image image) {
    this.image = image;
  }

  public void setDead() {
    this.dead = true;
  }
  
  public boolean isDead() {
    return dead;
  }
  
  public boolean hasExceededBounds() {
    return y < -150;
  }
  
  public void updatePos() {
    incrementX(speed);
  }

  public void updateFrameAndPosition() {
    updatePos();
    nextFrame();
  }
  
  public void die() {    
    explode();
    playDamageSound();
    this.dead = true;
  }
  
  private void explode() {
    setImage(explodeImage);
  }
  
  private void playDamageSound() {

    if (damageClip.isActive()) 
      return;
    
    damageClip.start();
    damageClip.setFramePosition(0);
  }

  public boolean isMarkedDead() {
    return markedDead;
  }

  public void markDead(boolean value) {
    this.markedDead = value;
  }
}
