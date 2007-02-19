/*
 * FieldSpriteRenderer.java
 *
 * Created on 10. februar 2007, 10:06
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.3
 *
 * This class should use a TileSet to draw the field.
 *
 * ******VERSION HISTORY******  
 * LMK @ 15. februar 2007 (v 1.3)
 * Refactored and moved tile loading to the new TileSet class
 * No longer an extension of FieldPanel.
 * LMK @ 12. februar 2007 (v 1.2)
 * TileSize is now determined by the width of the baseTile;
 * LMK @ 11. februar 2007 (v 1.1)
 * Fixed Field.getSize() bug
 * LMK @ 10. februar 2007 (v 1.0)
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
public class FieldRenderer {
    
    protected Field field;
    protected TileSet tileSet;    
    
    /** 
     * Creates a new instance of FieldRenderer with a specified Field to render
     * and a TileSet containing the images to paint with.
     *
     * @param field to draw.
     * @param tileSet containing images. 
     */
    public FieldRenderer(Field field, TileSet tileSet) {
        this.field = field;
        this.tileSet = tileSet;        
    }        
    
    /**
     * Draw the contents of the field on the graphics
     *
     * @param g canvas to draw on.
     */
    public void drawNodes(Graphics g) {            
        Point position = null;
        Node current = null;
        int tileNumber = 0; 
        
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
            g.drawImage(
                    this.tileSet.getNodeTile(tileNumber), 
                    position.x * this.tileSet.getTileSize(), 
                    position.y * this.tileSet.getTileSize(), 
                    null);
        }        
    }
    
    /**
     * Draw base tile on the graphics
     *
     * @param g canvas to draw on.
     */
    public void drawBaseTile(Graphics g) {
        Dimension size = this.field.getSize();
        
        for (int x = 0; x < size.width; x++) {            
            for (int y = 0; y < size.height; y++) {
                g.drawImage(
                        this.tileSet.getBaseTile(), 
                        x * this.tileSet.getTileSize(), 
                        y * this.tileSet.getTileSize(), 
                        null);
            }
        }
    }
    
    /**
     * Return the field that the renderer draws.
     *
     * @return field held by renderer.
     */
    public Field getField() {
        return this.field;
    }
    
    /**
     * Set the TileSet used to render
     *
     * @param tileSet to render field with.
     */
    public void setTileSet(TileSet tileSet) {
        this.tileSet = tileSet;
    }
}