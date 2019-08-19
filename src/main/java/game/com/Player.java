package game.com;

import java.awt.Image;
import java.util.ArrayList;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import game.com.util.ResourceManager;

public class Player extends Character {
  
  private int dx;
  private int velocity;
  private int lives;
  private int invulnDur;
  private int fireDuration = 10;
  
  private ArrayList<Weapon> weapons = new ArrayList<>();
  
  private boolean jumping;
  private boolean firing;
  
  private boolean peaked;
  private boolean godMode;
  private int landYAxis;
  
  private Image jumpSprite;
  private Image fireSprite;
  private Image reverseWalking;
  private Image forwardWalking;
  private boolean backPeddling;
  
  private Clip jumpSound;
  private Clip hurtSound;
  
  private boolean invincible;
  
  private int xAxisWeaponOffset = 230;
  private int yAxisWeaponOffset = 130;

  public Player() throws Exception {
    this(0, 0, false);
  }

  public void setGodMode() {
    this.godMode = true;
  }

  public boolean isGodMode() {
    return godMode;
  }
  
  public void setInvicible(boolean value) {
    this.invincible = value;
  }

  public boolean isInvicible() {
    return invincible;
  }

  public Player(int x, int y, boolean invicible) throws Exception {

    setInvicible(invicible);

    setFrames(1);
    lives = 3;
    setX(x);
    setY(y);
    
    setSprite(new Image[getFrames()]);
    
    forwardWalking = ResourceManager.getImage("mechWalking.gif");
    
    setSpriteImageIndex(0, forwardWalking);
    
    jumpSprite = ResourceManager.getImage("mechJump.gif");
    fireSprite = ResourceManager.getImage("mechFire.png");
    reverseWalking = ResourceManager.getImage("reverseWalking.gif");
    
    jumpSound = AudioSystem.getClip();
    jumpSound.open(AudioSystem.getAudioInputStream(ResourceManager.getResourceByName("jump.wav")));
    
    hurtSound = AudioSystem.getClip();
    hurtSound.open(AudioSystem.getAudioInputStream(ResourceManager.getResourceByName("hurt.wav")));
    
    setCurrentFrame(0);
    
    jumping = false;
    firing = false;
    backPeddling = false;
    godMode = false;
    invulnDur = 0;
    velocity = 40;
  }

  public void setStaticImage() {
    setSpriteImageIndex(0, jumpSprite);
  }
  
  public void setWalkingImage() {
    setSpriteImageIndex(0, forwardWalking);
  }
  
  public void setDx(int dx) {
    this.dx = dx;
  }

  public void setLandYAxis(int LAND_Y) {
    this.landYAxis = LAND_Y;
  }

  public void setBackPeddling(boolean value) {
    this.backPeddling = value;
  }
  
  public Image getCurrentSpriteImage() {
    
    if (jumping)
      return jumpSprite;
    else if(backPeddling)
      return reverseWalking;
    else if(firing)
      return fireSprite;
    else
      return forwardWalking;
  }

  public int getLives() {
    return lives;
  }

  public void setLives(int n) {
    lives = n;
  }

  public void changeLives(int n) {
    if (!godMode) lives += (n);
  }

  public void updatePos() {
    
    incrementX(dx);
    
    if (jumping && !peaked) {
      if (getY() == landYAxis) {
        jumpSound.start();
        jumpSound.setFramePosition(0);
      }
      if (velocity > 0) {
        decrementY(velocity);
        velocity *= 0.8;
      }
      else {
        peaked = true;
        velocity = 0;
      }
    }
    else {
      if ((getY() + velocity) < landYAxis) {
        if (!peaked) {
          velocity = 0;
          peaked = true;
        }
        incrementY(velocity);
        velocity += 3;
      }
      else {
        setY(landYAxis);
        peaked = false;
        velocity = 40;
      }
    }
  }

  public void checkInvulnerability() {
    
    if (invulnDur > 0)
      invulnDur--;
    else if (invulnDur == 0) 
      godMode = false;
  }

  public void fire() {
    
    this.firing = true;
    playFiringSound();

    Weapon weapon = new Weapon();
    weapon.setX(x + getxAxisWeaponOffset());
    weapon.setY(y + getyAxisWeaponOffset());
    
    weapons.add(weapon);
  }
  
  public ArrayList<Weapon> getWeapons(){
    return weapons;
  }
  
  public int getFireDuation() {
    return fireDuration;
  }
  
  public void setFireDuration(int value) {
    this.fireDuration = value;
  }
  
  public int getInvulnDur() {
    return invulnDur;
  }

  public void setInvulnDur(int n) {
    godMode = true;
    invulnDur = n;
  }
  
  public void checkFiringDuration() {
    
    if (fireDuration > 0) {
      fireDuration--;
    }
    else if (fireDuration == 0) { 
      firing = false;
      fireDuration = 10;
    }
  }
  
  private void playFiringSound() {

    try {
      
      Clip x = AudioSystem.getClip();
      x.open(AudioSystem.getAudioInputStream(ResourceManager.getResourceByName("rocket.wav")));
      x.start();
    }
    catch(Exception e) {
      
      System.out.println("Failure playing gun sounds" + e.getMessage());
      System.exit(1);
    } 
  }
  
  public void playHurtSound() {

    try {
      
      Clip x = AudioSystem.getClip();
      x.open(AudioSystem.getAudioInputStream(ResourceManager.getResourceByName("hurt.wav")));
      x.start();
    }
    catch(Exception e) {
      
      System.out.println("Failure playing gun sounds" + e.getMessage());
      System.exit(1);
    } 
  }
  
  public int getxAxisWeaponOffset() {
    return xAxisWeaponOffset;
  }

  public int getyAxisWeaponOffset() {
    return yAxisWeaponOffset;
  }

  public void jump(boolean value) throws Exception {
    
    if (value) {
      
      if (!jumping && !peaked) 
        landYAxis = getY();
      
      jumping = true;
    }
    else
      jumping = false;
  }
}
