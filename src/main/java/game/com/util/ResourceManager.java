package game.com.util;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

public class ResourceManager {

  private static Map<String, File> fileCache = new HashMap<String, File>();
  private static Map<String, Image> imageCache = new HashMap<String, Image>();
  
  private static Logger logger = Logger.getLogger(ResourceManager.class.getName());
  
  public static final Number SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
  public static final Number SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
 
  public static Dimension getScreenDimension() {
    return new Dimension(SCREEN_WIDTH.intValue(), SCREEN_HEIGHT.intValue());
  }
  
  public static Dimension getFractionalScreenDimension(int width, int height) {
    return new Dimension(SCREEN_WIDTH.intValue()/width, SCREEN_HEIGHT.intValue()/height);
  }
  
  public static File getResourceByName(String name) {
    
    if(fileCache.containsKey(name))
      return fileCache.get(name);
    
    File file = null;
    
    try {
      
      Path base = Paths.get(ResourceManager.class.getResource("/").getPath());
      
      Path path =  Files
      .walk(base)
      .filter(f -> f.getFileName().toFile().getName().equals(name))
      .findFirst()
      .get();
     
      file = new File(path.toString());
      fileCache.put(name, file);
    }
    catch(Exception e) {
      
      logger.severe("Failure finding resource : " + name);
      System.exit(1);
      
    }
    
    return file;
  }
  
  public static Image getImage(String resourceName) {
    
    if(imageCache.containsKey(resourceName))
      return imageCache.get(resourceName);
    
    Image image = null;
    
    try {
      image = new ImageIcon(getResourceByName(resourceName).getAbsolutePath()).getImage();
      imageCache.put(resourceName, image);
    }
    catch(Exception e) {
      
      logger.severe("Failure finding image resource [" + resourceName + "] Problem : " + e.getMessage());
      System.exit(1);
    }
    
    return image;
  }
}
