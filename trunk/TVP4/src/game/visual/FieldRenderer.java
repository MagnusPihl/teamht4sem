/*
 * FieldSpriteRenderer.java
 *
 * Created on 10. februar 2007, 10:06
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.4
 *
 * ******VERSION HISTORY******  
 * LMK @ 05. marts 2007 (v 1.4)
 * Added offset coordinates to drawNodes()
 * Added offset coordinates to drawBaseTile()
 * drawNodes now caches tileSize
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

package game.visual;

import field.*;
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
    
    /** 
     * Creates a new instance of FieldRenderer with a specified Field to render
     * and a TileSet containing the images to paint with.
     *
     * @param field to draw.
     * @param tileSet containing images. 
     */
    public FieldRenderer(Field field) {
        this.field = field;   
    }        
    
    /**
     * Draw the contents of the field on the graphics
     *
     * @param g canvas to draw on.
     * @param offset x
     * @param offset y
     */
    public void drawNodes(Graphics g, int offsetX, int offsetY) {            
        Point position = null;
        Node current = null;
        int tileNumber = 0; 
        int tileSize = TileSet.getInstance().getTileSize();
        
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
                        
            g.drawImage(
                    TileSet.getInstance().getPathTile(tileNumber), 
                    position.x * tileSize + offsetX, 
                    position.y * tileSize + offsetY, 
                    null);
        }        
    }
    
    /**
     * Draw base tile on the graphics
     *
     * @param g canvas to draw on.
     * @param offset x
     * @param offset y
     * @param area to cover with base tile.
     */
    public void drawBaseTile(Graphics g, int offSetX, int offSetY, Dimension size) {
        int tileSize = TileSet.getInstance().getTileSize();
        Image baseTile = TileSet.getInstance().getBaseTile();
        
        size.setSize(
                size.getWidth() / tileSize,
                size.getHeight() / tileSize);
        
        
        /*Dimension size = this.field.getSize(); */
        
        for (int x = 0; x < size.width; x++) {            
            for (int y = 0; y < size.height; y++) {
                g.drawImage(
                        baseTile, 
                        x * tileSize + offSetX, 
                        y * tileSize + offSetY, 
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
}