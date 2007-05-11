/*
 * FieldSpriteRenderer.java
 *
 * Created on 10. februar 2007, 10:06
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.6
 *
 * ******VERSION HISTORY******
 * LMK @ 11. maj 2007 (v 1.6)
 * Added repaintNode method.
 * Magnus Hemmer Pihl @ 8. marts 2007 (v 1.5)
 * Removed dimension argument from render()-method. The method will now calculate it, itself.
 * LMK @ 05. marts 2007 (v 1.4)
 * Added drawPoints
 * Added render method to speedup rendering by prerending the none changing field.
 * Changed to support refactoring of Node/Field. 
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
import java.awt.image.*;
import game.*;
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
        Node current = null;
        int tileNumber = 0; 
        int tileSize = TileSet.getInstance().getTileSize();
        
        for (Iterator i = this.field.getNodeList().iterator(); i.hasNext();) {
            current = (Node)(i.next());
            tileNumber = current.getBinaryDirections();                  
                        
            g.drawImage(
                    TileSet.getInstance().getPathTile(tileNumber), 
                    current.getPosition().x * tileSize + offsetX, 
                    current.getPosition().y * tileSize + offsetY, 
                    null);
        }        
    }
    
    /**
     * Draw base tile on the graphics
     *
     * @param g canvas to draw on.
     * @param offsetX
     * @param offsetY
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
     * Draw the points of the field on the graphics
     *
     * @param g canvas to draw on.
     * @param offset x
     * @param offset y
     */
    public void drawPoints(Graphics g, int offsetX, int offsetY) {            
        Image image = null;
        Node current = null;
        int tileSize = TileSet.getInstance().getTileSize();
        
        for (Iterator i = this.field.getNodeList().iterator(); i.hasNext();) {                        
            current = (Node)(i.next());
            
            if (!current.pointsTaken()) {            
                g.drawImage(
                    TileSet.getInstance().getPointsTile(current.getPoints()), 
                    current.getPosition().x * tileSize + offsetX, 
                    current.getPosition().y * tileSize + offsetY, 
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
     * Repaint supplied node.
     * 
     * @param graphic to draw on.
     * @param node to repaint
     */
    public void repaintNode(Graphics g, Node node) {
        TileSet tileset = TileSet.getInstance();
        int tileSize = tileset.getTileSize();
        
        g.drawImage(
                tileset.getBaseTile(), 
                node.getPosition().x * tileSize, 
                node.getPosition().y * tileSize, 
                null);
        g.drawImage(
                TileSet.getInstance().getPathTile(node.getBinaryDirections()), 
                node.getPosition().x * tileSize, 
                node.getPosition().y * tileSize, 
                null);        
        if (!node.pointsTaken()) {            
            g.drawImage(
                tileset.getPointsTile(node.getPoints()), 
                node.getPosition().x * tileSize, 
                node.getPosition().y * tileSize, 
                null);            
        }      
    }
    
    /**
     * Prerender basetile, nodes and points for later drawing.
     * 
     * @return prerendered basetile and nodes.
     */
    public BufferedImage render() {
        Dimension size = new Dimension(field.getSize().width*TileSet.getInstance().getTileSize(), field.getSize().height*TileSet.getInstance().getTileSize());
        BufferedImage image = PacmanApp.getInstance().getCore().getScreenManager().createCompatibleImage(size.width, size.height, Transparency.TRANSLUCENT);
        this.drawBaseTile(image.getGraphics(), 0, 0, size);
        this.drawNodes(image.getGraphics(), 0, 0);
        this.drawPoints(image.getGraphics(), 0, 0);
        return image;
    }
}