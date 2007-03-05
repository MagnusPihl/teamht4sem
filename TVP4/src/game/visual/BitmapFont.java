/*
 * BitmapFont.java
 *
 * Created on 1. marts 2007, 09:09
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.1
 *
 * Ideas: Add support for direction
 *
 * ******VERSION HISTORY******
 *
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
    private int fontHeight;
    private int lineSpace;
        
    /**
     * Creates a new instance of BitmapFont using the files located in the
     * supplied directory
     *
     * @param directory
     */
    public BitmapFont(File directory, int lineSpace) { //throws FileNotFoundException { 
        if (directory.isDirectory()) {
            this.images = new Image[CHARACTERS.length()];
            this.fontHeight = 0;
            
            for (int i = 0; i < this.images.length; i++) {                    
                /*} else {
                    this.images[i] = new ImageIcon(directory.getAbsolutePath() + File.separator + CHARACTERS.charAt(i) + ".png").getImage();
                }*/
                this.images[i] = new ImageIcon(directory.getAbsolutePath() + File.separator + Integer.toString(CHARACTERS.charAt(i)) + ".png").getImage();
                
                if (this.images[i] != null) {
                    if (this.images[i].getHeight(null) > this.fontHeight) {
                        this.fontHeight = this.images[i].getHeight(null);
                    }
                } else {
                    //throw new FileNotFoundException("Could not find character " + this.characters.charAt(i) + " in file " + currentFile.getPath());
                }
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
     * @param distance between lines.
     * @return height in pixels used to draw the text
     */
    public int drawString(Graphics g, String string, int[] indexes, int startX, int startY, int maxLineWidth) {
        int lineWidth = 0;
        int currentX = startX;
        int currentY = startY;
        int charHeight = 0;
        int charWidth = 0;
        Image currentChar = null;
        
        for (int i = 0; i < string.length(); i++) {
            if (indexes[i] != -1) {            
                currentChar = this.images[indexes[i]];
                if (currentChar != null) {
                    charHeight = currentChar.getHeight(null);
                    charWidth = currentChar.getWidth(null);

                    if ((lineWidth != 0) && (lineWidth + charWidth > maxLineWidth)) {
                        currentY += this.fontHeight + lineSpace;
                        currentX = startX;
                        lineWidth = 0;
                    }

                    lineWidth += charWidth;
                    g.drawImage(currentChar, currentX, currentY + (this.fontHeight - charHeight), null);
                    currentX += charWidth;
                }
            } else if (string.charAt(i) == '\n') {
                currentY += this.fontHeight + lineSpace;
                currentX = startX;
                lineWidth = 0;
            }
        }
        
        return currentY - startY + this.fontHeight;
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
        Dimension size = this.getStringBounds(string, indexes, 0, 0, maxLineWidth);
        BufferedImage image = PacmanApp.getInstance().getCore().getScreenManager().createCompatibleImage(size.width, size.height, Transparency.TRANSLUCENT);        
        this.drawString(image.getGraphics(), string, indexes, 0, 0, maxLineWidth);
        return image;
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
        return drawString(g, string, this.getIndexes(string), startX, startY, maxLineWidth);
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
    
    private Dimension getStringBounds(String string, int[] indexes, int startX, int startY, int maxLineWidth) {
        int lineWidth = 0;
        int width = 1;
        int currentX = startX;
        int currentY = startY;
        int charHeight = 0;
        int charWidth = 0;
        Image currentChar = null;
        
        for (int i = 0; i < string.length(); i++) {                        
            if (indexes[i] != -1) {            
                currentChar = this.images[indexes[i]];
                if (currentChar != null) {
                    charHeight = currentChar.getHeight(null);
                    charWidth = currentChar.getWidth(null);

                    if ((lineWidth != 0) && (lineWidth + charWidth > maxLineWidth)) {
                        currentY += this.fontHeight + lineSpace;
                        currentX = startX;
                        lineWidth = 0;
                    }

                    lineWidth += charWidth;
                    currentX += charWidth;
                }
            } else if (string.charAt(i) == '\n') {
                if (lineWidth > width) {
                    width = lineWidth;
                }
                currentY += this.fontHeight + lineSpace;
                currentX = startX;
                lineWidth = 0;
            }
        }
        
        if (lineWidth > width) {
            width = lineWidth;
        }
        
        return new Dimension(width, currentY - startY + this.fontHeight);
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
