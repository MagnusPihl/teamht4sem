/*
 * FieldSpriteRenderer.java
 *
 * Created on 10. februar 2007, 10:06
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.2
 *
 * This class should use sprite to draw the field. A list should be loaded on
 * startup.
 *
 * ******VERSION HISTORY******
 * LMK @ 12. februar 2007 (v 1.2)
 * TileSize is now determined by the width of the baseTile;
 * LMK @ 11. februar 2007 (v 1.1)
 * Fixed Field.getSize() bug
 * LMK @ 10. februar 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package field;

import java.awt.*;
import java.io.IOException;
import java.io.File;
import javax.swing.ImageIcon;
import java.util.Iterator;
/**
 *
 * @author LMK
 */
public class FieldTileRenderer extends FieldPanel {
    
    protected Image[] pathTiles;
    protected Image baseTile;
    
    /**
     * Tiles should be equal in size  and png.
     *
     * Path tiles should be named using 1 when a path  is present and 0 when not.
     * 1s and 0s should be put in the order UP, RIGHT, DOWN, LEFT.
     * Thus a path tile for a horisontally connected node should be named 0101.png
     * and a vertically connected node should be named 1010.png
     *
     * The base or empty tile should be called base.png
     * 
     * Point tiles should be named p_ followed by the amount of points. 
     * Thus a 100 point marker should be called p_100.png     
     */
    
    /** Creates a new instance of FieldSpriteRenderer */
    public FieldTileRenderer(Field field, String directory) {
        super(field, 30);
        this.loadTile(directory);
        super.setBackground(Color.WHITE);
        super.setGridColor(Color.LIGHT_GRAY);
        
    }
    
    public void loadTile(String path) {
        this.loadTile(new File(path));
    }
    
    public void loadTile(File file) {    
        if (file.isDirectory()) {
            this.pathTiles = new Image[16];

            for (int i = 0; i < this.pathTiles.length; i++) {          
                //System.out.println(file.getAbsolutePath() + file.separator + this.zeroPad(Integer.toBinaryString(i),4));
                this.pathTiles[i] = (new ImageIcon(file.getAbsolutePath() + file.separator + this.zeroPad(Integer.toBinaryString(i),4) + ".png")).getImage();
            }

            this.baseTile = (new ImageIcon(file.getAbsolutePath() + file.separator +  "base.png")).getImage();
            super.tileSize = this.baseTile.getWidth(null);
        }        
    }
    
    public void paint(Graphics g) {    
        super.paint(g);  
        this.drawBaseTile(g);
        super.drawGrid(g);
        
        Point position = null;
        Node current = null;
        int tileNumber = 0; 
        g.setColor(Color.BLACK);
        
        for (Iterator i = this.field.getNodeList().keySet().iterator(); i.hasNext();) {
            position = (Point)i.next();
            current = (Node)this.field.getNodeList().get(position);
            tileNumber = 0;
            
            if (current.getNodeAt(Node.UP) != null) {
                tileNumber += 8;
            }            
            
            if (current.getNodeAt(Node.RIGHT) != null) {
                tileNumber += 4;
            }            
            
            if (current.getNodeAt(Node.DOWN) != null) {
                tileNumber += 2;
            }            
            
            if (current.getNodeAt(Node.LEFT) != null) {
                tileNumber += 1;
            }            
            
            //g.drawOval(position.x*tileSize, position.y*tileSize, 5,5);
            g.drawImage(this.pathTiles[tileNumber], position.x*tileSize, position.y*tileSize, null);
        }        
    }
    
    private void drawBaseTile(Graphics g) {
        Dimension size = this.field.getSize();
        
        for (int x = 0; x < size.width; x++) {            
            for (int y = 0; y < size.height; y++) {
                g.drawImage(this.baseTile, x*tileSize, y*tileSize, null);
            }
        }
    }        
    
    public static String zeroPad(String string, int length) {
        StringBuilder builder = new StringBuilder(length);
        
        for (int i = string.length(); i < length; i++) {
            builder.append('0');
        }
        
        builder.append(string);
        
        return builder.toString();
    }
}