package game.com;

import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Menu extends JPanel {

  private Logger logger = Logger.getLogger(Menu.class.getName());
  
  private JPanel screen = new JPanel();
  
  private int width;
  private int height;
  
  private BufferedImage background;
  private BufferedImage startButton;
  
  public Menu(boolean isSplash) throws IOException {
    
    initResources();
    
    if (isSplash == true)
      screen.repaint();

  }
  
  public void paintComponent(Graphics g) {
    
    super.paintComponent(g);

    //draw BG
    g.drawImage(background, 0, 0, width, height, null);

    //draw start button
    int buttonWidth = 273;
    int buttonHeight = 108;
    int buttonXAxis = width / 2 - (buttonWidth / 2);
    int buttonYAxis = height / 2 - buttonHeight + 30;
    
    g.drawImage(startButton, buttonXAxis, buttonYAxis, buttonWidth, buttonHeight, null);

    repaint();
}
  
  public void initResources() throws IOException {
    
    Double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    Double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    this.width = screenWidth.intValue();
    this.height = screenHeight.intValue();
    
    try {
      
      background = ImageIO.read(new File(getClass().getResource("/resources/Menu/rockCave.png").getPath()));
      startButton = ImageIO.read(new File(getClass().getResource("/resources/Menu/startButton.png").getPath()));
    }
    catch(Exception e) {
      
      logger.info("Failure setting up resources in Menu component : " + e.getMessage());
      System.exit(1);
    }
    
  }
}
