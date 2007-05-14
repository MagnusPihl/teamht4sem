/*
 * GameDialog.java
 *
 * Created on 24. april 2007, 09:24
 *
 * Company: HT++
 *
 * @author Mikkel Brøndsholm Nielsen
 * @version 1.1
 *
 *
 * ******VERSION HISTORY******
 *
 * MHP @ 26. april 2007 (v 1.1)
 * Changed font to use BitMapFont.
 * Added options for centering the text and the dialog.
 *
 * MBN @ 24. april 2007 (v 1.0)
 * Created
 *
 */

package game.visual;

import game.*;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import javax.swing.ImageIcon;


public class GameDialog {

    public static final int TOP_LEFT = 0;
    public static final int TOP_RIGHT = 1;
    public static final int BOTTOM_LEFT = 2;
    public static final int BOTTOM_RIGHT = 3;
    public static final int VERTICAL = 4;
    public static final int HORIZONTAL = 5;
    public static final int BACKGROUND = 6;
    public static final int BACKGROUND_OFFSET = 18;
    public static final int TEXT_OFFSET = 25;
    public static final int VERTICAL_OFFSET = 30;
    public static final int HORIZONTAL_OFFSET = 32;
    public static final int HORIZONTAL_TILE_HEIGHT = 27;
    public static final int VERTICAL_TILE_WIDTH = 28;
    public static final int SIDE_TILE_LENGTH = 20;
                
    private static Image tiles[] = {
        new ImageIcon("images/dialog_tl.png").getImage(),
        new ImageIcon("images/dialog_tr.png").getImage(),
        new ImageIcon("images/dialog_bl.png").getImage(),
        new ImageIcon("images/dialog_br.png").getImage(),
        new ImageIcon("images/dialog_v.png").getImage(),
        new ImageIcon("images/dialog_h.png").getImage(),
        new ImageIcon("images/dialog_b.png").getImage(),
    };
    
    private Image dialog;
    private String text;
    private int height = 100;
    private int width = 100;    
    private boolean isOpaque;
    
    /** Creates a new instance of GameDialog */
    public GameDialog(String text, int width, int height, boolean isOpaque) {
        this.text = text;  
        this.isOpaque = isOpaque;
        Image image = null;
        
        if (height > 100) {
            this.height = height;
        }
        if (width > 100) {
            this.width = width;        
        }
        
        if (this.text != null) {
            image = PacmanApp.getInstance().getFont().renderStringRect(
                    this.text, 
                    this.width - 2*TEXT_OFFSET,
                    this.height - 2*TEXT_OFFSET);
        }
        
        this.renderDialog(image);
    }
    
    public GameDialog(String text, boolean isOpaque) {        
        this.text = text;        
        Image image = null;
        this.isOpaque = isOpaque;
        
        if (this.text != null) {
            image = PacmanApp.getInstance().getFont().renderString(text, 529);
            this.height = image.getHeight(null) + TEXT_OFFSET * 2;
            this.width = image.getWidth(null) + TEXT_OFFSET * 2;
        }
        
        if (this.height < 100) {
            this.height = 100;
        }
        if (this.width < 100) {
            this.width = 100;        
        }
        
        this.renderDialog(image);
    }
    
    public GameDialog(int width, int height, boolean isOpaque) {
        this(null, width, height, isOpaque);
    } 
    
    private void renderDialog(Image text) {
        this.dialog = PacmanApp.getInstance().getCore().getScreenManager().createCompatibleImage(this.width, this.height, Transparency.TRANSLUCENT);
        Graphics g = this.dialog.getGraphics();
                
        System.out.println(this.width + " " + this.height);
        
        drawDialog(g, this.isOpaque, 0,0, this.width, this.height);
                
        if (text != null) {
            //g.setClip(HORIZONTAL_OFFSET, VERTICAL_OFFSET, this.width - 2*HORIZONTAL_OFFSET, this.height - 2*VERTICAL_OFFSET);
            g.drawImage(text, TEXT_OFFSET, TEXT_OFFSET, null);
        }
    }
    
    /**
     * Draw dialog in a specific position.
     */
    public void draw(Graphics2D _g, int offX, int offY){
        _g.drawImage(dialog, offX, offY, null);        
    }
    
    /**
     * Draw dialog in the center of the screen;
     */
    public void draw(Graphics2D g){
        g.drawImage(dialog, 400 - this.width / 2, 300 - this.height / 2, null);        
    }
    
    public static void drawDialog(Graphics g, boolean isOpaque, int offsetX, int offsetY, int width, int height) {
        Shape clip = g.getClip();
        
        g.setClip(offsetX + BACKGROUND_OFFSET, offsetY + BACKGROUND_OFFSET, width - BACKGROUND_OFFSET*2, height - BACKGROUND_OFFSET*2);
        if (isOpaque) {
            g.setColor(Color.BLACK);
            g.fillRect(BACKGROUND_OFFSET + offsetX, 
                    BACKGROUND_OFFSET + offsetY, 
                    width - BACKGROUND_OFFSET*2, 
                    height - BACKGROUND_OFFSET*2);
        } else {        
            for (int x = BACKGROUND_OFFSET + offsetX; x < (width - BACKGROUND_OFFSET + offsetX); x += SIDE_TILE_LENGTH) {
                for (int y = BACKGROUND_OFFSET + offsetY; y < (width - BACKGROUND_OFFSET + offsetY); y += SIDE_TILE_LENGTH) {
                    g.drawImage(tiles[BACKGROUND], x, y, null);
                }
            }
        }
        
        g.setClip(HORIZONTAL_OFFSET + offsetX, offsetY, width - HORIZONTAL_OFFSET*2, height);        
        for (int x = HORIZONTAL_OFFSET + offsetX; x < (width - HORIZONTAL_OFFSET + offsetX); x += SIDE_TILE_LENGTH) {
            g.drawImage(tiles[HORIZONTAL], x, offsetY, null);
            g.drawImage(tiles[HORIZONTAL], x, height - HORIZONTAL_TILE_HEIGHT + offsetY, null);
        }
        
        g.setClip(offsetX, VERTICAL_OFFSET + offsetY, width, height - VERTICAL_OFFSET*2);
        for (int y = VERTICAL_OFFSET + offsetY; y < (width - VERTICAL_OFFSET + offsetY); y += SIDE_TILE_LENGTH) {
            g.drawImage(tiles[VERTICAL], offsetX, y, null);
            g.drawImage(tiles[VERTICAL], width - VERTICAL_TILE_WIDTH + offsetX, y, null);
        }
        
        g.setClip(offsetX, offsetY, width, height);
        g.drawImage(tiles[TOP_LEFT], offsetX, offsetY, null);
        g.drawImage(tiles[TOP_RIGHT], offsetX + width - HORIZONTAL_OFFSET, offsetY, null);
        g.drawImage(tiles[BOTTOM_LEFT], offsetX, height - VERTICAL_OFFSET + offsetY, null);
        g.drawImage(tiles[BOTTOM_RIGHT], offsetX + width - HORIZONTAL_OFFSET, height - VERTICAL_OFFSET + offsetY, null);        
        g.setClip(clip);
    }       
}
