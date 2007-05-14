/*
 * BitmapFont.java
 *
 * Created on 1. marts 2007, 09:09
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.4
 *
 * Ideas: 
 * Add support for direction
 * Only break on space or hyphen (-).
 *
 * ******VERSION HISTORY******
 * LMK @ 11. maj 2007 (v 1.4)
 * Added methods for drawing string rects.
 * LMK @ 23. april 2007 (v 1.3)
 * Fixed 0 width and height errors when using renderString.
 * LMK @ 17. april 2007 (v 1.2)
 * Removed offset parameters for getStringBounds
 * Fixed render errors.
 * LMK @ 3. marts 2007 (v 1.1)
 * Added getStringBounds
 * Added getIndexes
 * Added setHeight
 *
 *
 * Fonts should be placed in a seperate directory. Every character should be
 * named according to its Unicode number. Fx. the picture for the letter A 
 * should be named 65.png.
 */

package game.visual;

import game.*;
import java.io.*;
import java.awt.*;
import javax.swing.ImageIcon;
import java.awt.image.*;

public class BitmapFont {
    
    public static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzæøåABCDEFGHIJKLMNOPQRSTUVWXYZÆØÅ1234567890-_., \\/\"'()[]&%#;?!{}=:+©";
                                             
    private Image[] images;
    private int[] charWidths;
    private int[] charHeights;
    private int fontHeight;
    private int lineSpace;
    private int dotIndex;
    private int dotWidth;
        
    /**
     * Creates a new instance of BitmapFont using the files located in the
     * supplied directory
     *
     * @param directory
     */
    public BitmapFont(File directory, int lineSpace) { //throws FileNotFoundException { 
        if (directory.isDirectory()) {
            this.images = new Image[CHARACTERS.length()];
            this.charWidths = new int[CHARACTERS.length()];
            this.charHeights = new int[CHARACTERS.length()];
            this.fontHeight = 0;
            
            for (int i = 0; i < this.images.length; i++) {                    
                /*} else {
                    this.images[i] = new ImageIcon(directory.getAbsolutePath() + File.separator + CHARACTERS.charAt(i) + ".png").getImage();
                }*/
                this.images[i] = new ImageIcon(directory.getAbsolutePath() + File.separator + Integer.toString(CHARACTERS.charAt(i)) + ".png").getImage();
                
                if (this.images[i] != null) {
                    this.charHeights[i] = this.images[i].getHeight(null);
                    this.charWidths[i] = this.images[i].getWidth(null);
                    
                    if (this.charHeights[i] > this.fontHeight) {
                        this.fontHeight = this.charHeights[i];
                    }
                } else {
                    //throw new FileNotFoundException("Could not find character " + this.characters.charAt(i) + " in file " + currentFile.getPath());
                }
            }
            
            this.dotIndex = CHARACTERS.indexOf(".");
            if (this.dotIndex != -1) {
                this.dotWidth = this.charWidths[this.dotIndex] * 3;
            } else {
                this.dotWidth = 0;
            }
        } else {
            //throw new FileNotFoundException("The directory from which to load font doesn't exist: " + directory.getPath());
        }
        
        this.lineSpace = lineSpace;
    }            
    
    /**
     * Draw string on graphic width defined width.
     * 
     * @param graphic to draw on
     * @param string to draw
     * @param x start position. To the left of text.
     * @param y start position. Above text.
     * @param width allowed to draw on. If 0 the string will be drawn vertically.
     * @return height in pixels used to draw the text
     */
    public int drawString(Graphics g, String string, int[] indexes, int startX, int startY, int maxLineWidth) {
        int lineWidth = 0;
        int currentX = startX;
        int currentY = startY + this.fontHeight;
        
        for (int i = 0; i < string.length(); i++) {
            if (indexes[i] != -1) {            
                if (this.images[indexes[i]] != null) {
                    
                    if ((lineWidth != 0) && ((lineWidth + this.charWidths[indexes[i]]) > maxLineWidth)) {
                        currentY += this.fontHeight + this.lineSpace;                        
                        currentX = startX;
                        lineWidth = 0;
                    } 
                    g.drawImage(this.images[indexes[i]], currentX, currentY - this.charHeights[indexes[i]], null);    
                    
                    lineWidth += this.charWidths[indexes[i]];                        
                    currentX += this.charWidths[indexes[i]];
                }
                
            } else if (string.charAt(i) == '\n') {
                currentY += this.fontHeight + lineSpace;
                currentX = startX;
                lineWidth = 0;
            }
        }
        
        return currentY - startY;
    }
    
    /**
     * Draw string on graphic width defined width and height.
     * 
     * @param graphic to draw on
     * @param string to draw
     * @param x start position. To the left of text.
     * @param y start position. Above text.
     * @param width allowed to draw on. If 0 the string will be drawn vertically.
     * @param height allowed to draw on. If 0 a single line will be written.
     * @return height in pixels used to draw the text
     */
    public int drawStringRect(Graphics g, String string, int[] indexes, int startX, int startY, int maxLineWidth, int maxHeight) {
        int lineWidth = 0;
        int textHeight = this.fontHeight;
        int currentX = startX;
        int currentY = startY + this.fontHeight;
        maxHeight -= (this.fontHeight + this.lineSpace);
        
        for (int i = 0; i < string.length(); i++) {
            if (indexes[i] != -1) {            
                if (this.images[indexes[i]] != null) {
                        
                    if ((lineWidth != 0) && ((lineWidth + this.charWidths[indexes[i]]) > maxLineWidth)) {
                        currentY += this.fontHeight + this.lineSpace;                        
                        currentX = startX;
                        lineWidth = 0;
                        textHeight += this.fontHeight + this.lineSpace;
                    } 
                                        
                    if (textHeight > maxHeight) {
                        if (lineWidth + this.charWidths[indexes[i]] >= maxLineWidth - this.dotWidth) {   
                            for (int j = 0; j < 3; j++) {
                                g.drawImage(this.images[this.dotIndex], currentX, currentY - this.charHeights[this.dotIndex], null);                                    
                                currentX += this.charWidths[dotIndex];
                            }
                            lineWidth += this.dotWidth;
                            break;
                        }                        
                    }
                    
                    g.drawImage(this.images[indexes[i]], currentX, currentY - this.charHeights[indexes[i]], null);    
                    
                    lineWidth += this.charWidths[indexes[i]];                        
                    currentX += this.charWidths[indexes[i]];
                }
                
            } else if (string.charAt(i) == '\n') {
                if (textHeight > maxHeight) {                    
                    for (int j = 0; j < 3; j++) {
                        g.drawImage(this.images[this.dotIndex], currentX, currentY - this.charHeights[this.dotIndex], null);                                    
                        currentX += this.charWidths[dotIndex];
                    }
                    lineWidth += this.dotWidth;
                    break;                
                }
                
                currentY += this.fontHeight + lineSpace;
                currentX = startX;
                textHeight += this.fontHeight + this.lineSpace;
                lineWidth = 0;
            }
        }
        
        return currentY - startY;
    }
    
    /**
     * Prerender string so that i may be reused. An image of the appropriate
     * size created during prerender.
     *
     * @param String to render.
     * @param Maximum width.
     * @param Color to paint background with.
     */
    public BufferedImage renderString(String string, int maxLineWidth,Color backgroundColor) {        
        int[] indexes = this.getIndexes(string);
        Dimension size = this.getStringBounds(string, indexes, maxLineWidth);
        
        if ((size.width != 0)&&(size.height != 0)) {
            BufferedImage image = PacmanApp.getInstance().getCore().getScreenManager().createCompatibleImage(size.width, size.height, Transparency.OPAQUE);                    
            Graphics g = image.getGraphics();
            g.setColor(backgroundColor);
            g.fillRect(0,0, size.width, size.height);
            this.drawString(g, string, indexes, 0, 0, maxLineWidth);
            return image;
        }
        
        return null;
    }
    
    /**
     * Prerender string so that i may be reused. An image of the appropriate
     * size created during prerender.
     *
     * @param String to render.
     * @param Maximum width.
     */
    public BufferedImage renderString(String string, int maxLineWidth) {        
        int[] indexes = this.getIndexes(string);
        Dimension size = this.getStringBounds(string, indexes, maxLineWidth);
        
        if ((size.width != 0)&&(size.height != 0)) {
            BufferedImage image = PacmanApp.getInstance().getCore().getScreenManager().createCompatibleImage(size.width, size.height, Transparency.TRANSLUCENT);                    
            this.drawString(image.getGraphics(), string, indexes, 0, 0, maxLineWidth);
            return image;
        }
        
        return null;
    }
    
    /**
     * Prerender string so that i may be reused. An image of the appropriate
     * size created during prerender.
     *
     * @param String to render.
     * @param Maximum width.
     * @param Maximum height.
     */
    public BufferedImage renderStringRect(String string, int maxLineWidth, int maxHeight) {
        int[] indexes = this.getIndexes(string);
        Dimension size = this.getStringRectBounds(string, indexes, maxLineWidth, maxHeight);
        
        if ((size.width != 0)&&(size.height != 0)) {
            BufferedImage image = PacmanApp.getInstance().getCore().getScreenManager().createCompatibleImage(size.width, size.height, Transparency.TRANSLUCENT);        
            this.drawStringRect(image.getGraphics(), string, indexes, 0, 0, maxLineWidth,maxHeight);
            return image;
        }
        
        return null;
    }
    
    /**
     * Draw string on graphic width defined width.
     * 
     * @param graphic to draw on
     * @param string to draw
     * @param x start position. To the left of text.
     * @param y start position. Above text.
     * @param width allowed to draw on. If 0 the string will be drawn vertically.
     * @return height in pixels used to draw the text
     */
    public int drawString(Graphics g, String string, int startX, int startY, int maxLineWidth) {        
        //return drawStringRect(g, string, this.getIndexes(string), startX, startY, 100, 0);
        return drawString(g, string, this.getIndexes(string), startX, startY, maxLineWidth);
    }
    
    /**
     * Draw string on graphic width defined width.
     * 
     * @param graphic to draw on
     * @param string to draw
     * @param x start position. To the left of text.
     * @param y start position. Above text.
     * @param width allowed to draw on. If 0 the string will be drawn vertically.
     * @param h height allowed to draw on. If 0 a single line will be drawn.
     * @return height in pixels used to draw the text
     */
    public int drawStringRect(Graphics g, String string, int startX, int startY, int maxLineWidth, int maxHeight) {        
        return drawStringRect(g, string, this.getIndexes(string), startX, startY, maxLineWidth, maxHeight);
    }    
    
    /**
     * Draw horizontal string on graphic
     * 
     * @param graphic to draw on
     * @param string to draw
     * @param x start position. To the left of text.
     * @param y start position. Above text.
     * @return height in pixels used to draw the text
     */
    public int drawString(Graphics g, String string, int startX, int startY) {        
        return this.drawString(g, string, this.getIndexes(string), startX, startY, Integer.MAX_VALUE);
    }    
    
    private int[] getIndexes(String string) {
        int[] indexes = new int[string.length()];
        
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = CHARACTERS.indexOf(string.charAt(i));
        }
        
        return indexes;
    }
    
    /**
     * Get the pixel dimensions needed to draw the supplied string.
     *
     * @param string to calculate dimensions of.
     * @param string indexes.
     * @param width allowed to draw on. If 0 the string will be drawn vertically.
     * @return Dimension.
     */
    public Dimension getStringBounds(String string, int[] indexes, int maxLineWidth) {        
        int currentWidth = 0;
        int maxX = 0;
        int maxY = this.fontHeight;
        
        for (int i = 0; i < string.length(); i++) {                        
            if (indexes[i] != -1) {            
                if (this.images[indexes[i]] != null) {
                    
                    if ((currentWidth != 0) && ((currentWidth + this.charWidths[indexes[i]]) > maxLineWidth)) {
                        maxY += this.fontHeight + this.lineSpace;
                        if (currentWidth > maxX) {
                            maxX = currentWidth;
                        }
                        currentWidth = 0;
                    } 
                    
                    currentWidth += this.charWidths[indexes[i]];
                }
            } else if (string.charAt(i) == '\n') {
                if (currentWidth > maxX) {
                    maxX = currentWidth;
                }
                maxY += this.fontHeight + lineSpace;
                currentWidth = 0;
            }
        }
        
        if (currentWidth > maxX) {
            maxX = currentWidth;
        }
        currentWidth = 0;
        
        return new Dimension(maxX, maxY);
    }
        
    /**
     * Get the pixel dimensions needed to draw the supplied string.
     *
     * @param string to calculate dimensions of.
     * @param string indexes.
     * @param width allowed to draw on. If 0 the string will be drawn vertically.
     * @param height allowed to draw on. If 0 the string will be singleline.
     * @return Dimension.
     */
    public Dimension getStringRectBounds(String string, int[] indexes, int maxLineWidth, int maxHeight) {        
        int currentWidth = 0;        
        int maxX = 0;
        int maxY = this.fontHeight;        
        maxHeight -= (this.fontHeight + this.lineSpace);
        
        for (int i = 0; i < string.length(); i++) {                        
            if (indexes[i] != -1) {            
                if (this.images[indexes[i]] != null) {
                    
                    if ((currentWidth != 0) && ((currentWidth + this.charWidths[indexes[i]]) > maxLineWidth)) {                        
                        maxY += this.fontHeight + this.lineSpace;
                        if (currentWidth > maxX) {
                            maxX = currentWidth;
                        }
                        currentWidth = 0;                        
                    } 
                    
                    if (maxY > maxHeight) {
                        if (currentWidth + this.charWidths[indexes[i]] >= maxLineWidth - this.dotWidth) {                               
                            currentWidth += this.dotWidth;
                            break;
                        }                        
                    }
                    
                    currentWidth += this.charWidths[indexes[i]];
                }
            } else if (string.charAt(i) == '\n') {
                if (maxY > maxHeight) {
                    currentWidth += this.dotWidth;
                    break;                
                }
                if (currentWidth > maxX) {
                    maxX = currentWidth;
                }
                maxY += this.fontHeight + lineSpace;
                currentWidth = 0;
            }
        }
        
        if (currentWidth > maxX) {
            maxX = currentWidth;
        }
        
        return new Dimension(maxX, maxY);
    }
    
    /**
     * Get heigt of the font, based on the largest typeface.
     *
     * @return height of font. 
     */
    public int getHeight() {
        return this.fontHeight;
    }
    
    /**
     * Set the height of the font
     *
     * @param height in pixels. Must be greater than zero.
     */
    public void setHeight(int height) {
        if (height > 0) {
            this.fontHeight = height;
        }
    }
    
    /**
     * Set the space between lines.
     *
     * @param space in pixels. Must be none negative.
     */
    public void setLineSpace(int lineSpace) {
        if (lineSpace >= 0) {
            this.lineSpace = lineSpace;
        }
    }
}
