/*
 * BitmapFont.java
 *
 * Created on 1. marts 2007, 09:09
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 * Ideas: Add support for direction
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 1. marts 2007 (v 1.0)
 * __________ Changes ____________
 *
 *
 * Fonts should be placed in a seperate directory. Every character should be
 * named according to its Unicode number. Fx. the picture for the letter A 
 * should be named 65.png.
 */

package game.visual;

import java.io.*;
import java.awt.*;
import javax.swing.ImageIcon;

public class BitmapFont {
    
    public static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzæøåABCDEFGHIJKLMNOPQRSTUVWXYZÆØÅ1234567890-_., ?!";
                                             
    private Image[] images;
    private int fontHeight;
        
    /**
     * Creates a new instance of BitmapFont using the files located in the
     * supplied directory
     *
     * @param directory
     */
    public BitmapFont(File directory) { //throws FileNotFoundException { 
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
    public int drawString(Graphics2D g, String string, int startX, int startY, int maxLineWidth, int lineSpace) {
        int lineWidth = 0;
        int currentX = startX;
        int currentY = startY;
        int charHeight = 0;
        int charWidth = 0;
        int index = -1;
        Image currentChar = null;
        
        for (int i = 0; i < string.length(); i++) {
            index = CHARACTERS.indexOf(string.charAt(i));
            
            if (index != -1) {            
                currentChar = this.images[index];
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
     * Draw horizontal string on graphic
     * 
     * @param graphic to draw on
     * @param string to draw
     * @param x start position. To the left of text.
     * @param y start position. Above text.
     * @param distance between lines.
     * @return height in pixels used to draw the text
     */
    public int drawString(Graphics2D g, String string, int startX, int startY, int lineSpace) {        
        return this.drawString(g, string, startX, startY, Integer.MAX_VALUE, lineSpace);
    }    
    
    /**
     * Get heigt of the font, based on the largest typeface.
     *
     * @return height of font. 
     */
    public int getHeight() {
        return this.fontHeight;
    }
}
