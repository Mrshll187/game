package game.com;

import java.awt.Image;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import game.com.util.ResourceUtil;

public class Enemy extends Character {

  private int speed;
  private Clip damageClip;
  private Image image;
  private Image explodeImage;
  private boolean dead = false;
  private boolean markedDead = false;

  public Enemy(int x, int y, int speed) {
    
    setX(x);
    setY(y);
    setFrames(1);
    this.speed = speed;

    this.image = ResourceUtil.getImage("smallZombieLeft.gif");
    this.explodeImage = ResourceUtil.getImage("explode.gif");
    
    setSprite(new Image[getFrames()]);
    setSpriteImageIndex(0, image);
    setCurrentFrame(0);

    try {
      
      damageClip = AudioSystem.getClip();
      damageClip.open(AudioSystem.getAudioInputStream(ResourceUtil.getResourceByName("explode.wav")));
    }
    catch (Exception e) {
      
      System.out.println("Failure playing damage sound : " + e.getMessage());
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
  
  public void updatePos() {
    incrementX(speed);
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
