package game.com.util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
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
  
  public static BufferedImage resize(BufferedImage img, int height, int width) {
    
    Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    
    BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    
    Graphics2D g2d = resized.createGraphics();
    g2d.drawImage(tmp, 0, 0, null);
    g2d.dispose();
    
    return resized;
  }
  
  public static BufferedImage toBufferedImage(Image img){
    
    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

    // Draw the image on to the buffered image
    Graphics2D bGr = bimage.createGraphics();
    bGr.drawImage(img, 0, 0, null);
    bGr.dispose();

    // Return the buffered image
    return bimage;
  }
  
  public static void writeGifFrameToFile(String resouceName, int frameNumber, String outputPath) throws IOException {
    
    Image image = getGifFrames(resouceName).get(frameNumber);
    BufferedImage bufferedImage = toBufferedImage(image);
    
    ImageIO.write(bufferedImage, "PNG", new File(outputPath));
  }
  
  public static List<BufferedImage> getGifFrames(String resourceName) throws IOException {

    File file = getResourceByName(resourceName);
    
    // grab the output image type from the first image in the sequence 
    ImageReader reader = ImageIO.getImageReadersBySuffix("GIF").next();
    ImageInputStream in = ImageIO.createImageInputStream(file);
    reader.setInput(in);
    
    List<BufferedImage> list = IntStream
    .range(0, reader.getNumImages(true))
    .mapToObj(i -> {
      
      BufferedImage bufferedImage = null;
      
      try {
        bufferedImage = reader.read(i);
      }
      catch(Exception e) {
        System.out.println("failure reading file :" + e.getMessage());
      }
      
      return bufferedImage;
      
    })
    .collect(Collectors.toList());
    
    return list;
  }
}
