/*
 * EditorPanel.java
 *
 * Created on 9. februar 2007, 22:57
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.2
 *
 *
 * ******VERSION HISTORY******
 * LMK @ 13. februar 2007 (v 1.2)
 * Points are now alloted to each node according to the amount of points 
 * specified by the user.
 * LMK @ 11. februar 2007 (v 1.1)
 * Fixed Field.getSize() bug
 * LMK @ 9. februar 2007 (v 1.0)
 *
 */

package editor;

import field.*;
import field.Field;
import field.FieldRenderer;
import java.awt.event.*;
import java.io.File;
import java.awt.*;
import javax.swing.*;

public class EditorPanel extends JPanel {    
            
    protected Field field;
    protected FieldRenderer renderer;
    //protected TileSet tileSet;
    protected boolean gridVisible;
    protected Color gridColor;
    protected Brush brush;
    
    /** Creates a new instance of EditorPanel */
    public EditorPanel(Field field) {        
        this.field = field;
        this.renderer = new FieldRenderer(field);
        this.tileSet = new TileSet(SKIN_LIBRARY + "nodes/");
        this.gridColor = Color.LIGHT_GRAY;
        this.gridVisible = false;
        this.setBrush(new NodeBrush());    
    }
    
    /**
     * Set the brush used on to draw on the panel.
     *
     * @param brush to be used on panel.
     */
    public void setBrush(Brush brush) {
        if (this.brush != null) {            
            super.removeMouseListener(this.brush);
            super.removeMouseMotionListener(this.brush);
        }
        super.addMouseListener(brush);
        super.addMouseMotionListener(brush);    
    }
    
    public void checkSize() {
        Dimension size = super.field.getSize();
        size.setSize(size.getWidth()*super.tileSize, size.getHeight()*super.tileSize);
        this.setPreferredSize(size);
        this.revalidate();
//        this.repaint();
    }                         
    
    /**
     * Translate the pixel position on the panel to the coordinates of node
     * at that pixel. No check is made to see if the translated point is valid.
     *
     * @param point the coordinates to translate
     * @return a point containing the translated coordinates. 
     */
    public Point translate(Point position) {
        return new Point(
            (int)(position.x / tileSize),
            (int)(position.y / tileSize)
            );
    }
    
    /**
     * Set the color of the grid on the panel.
     *
     * @param color of the grid.     
     */
    public void setGridColor(Color color) {
        this.gridColor = color;
    }
    
    /**
     * Get the color of the grid.
     * 
     * @return the color of the grid.
     */
    public Color getGridColor() {
        return this.gridColor;
    }
    
    public void paint(Graphics g) {
        this.renderer.aint(g);
    }
    
    /**
     * Draw grid on graphic
     *
     * @param g graphic to draw grid on.
     */
    protected void drawGrid(Graphics g) {
        if (gridVisible) {
            Dimension size = this.field.getSize();
            g.setColor(this.gridColor);       

            for (int x = 1; x < size.width; x++) {
                g.drawLine(x*tileSize, 0, x*tileSize, size.height*tileSize);
            }
            for (int y = 1; y < size.height; y++) {
                g.drawLine(0, y*tileSize, size.width*tileSize, y*tileSize);
            }
        }
    }

    /**
     * Retrive the field held.
     *
     * @return Field.
     */
    public Field getField() {
        return field;
    }
    //public abstract void paint(Graphics g);
    
    public void setField(Field field) {
        this.field = field;
    }
    
    /**
     * Set whether grid should be visible when drawGrid() is  called.
     * 
     * @param isVisible. Should be true if the grid should be drawn
     */
    public void setGridVisible(boolean isVisible) {
        this.gridVisible = isVisible;
    }
    
    /**
     * Get grid visibility.
     * 
     * @return true if the grid is visible.
     */
    public boolean isGridVisible() {
        return this.gridVisible;
    }
}
