/*
 * FieldPanel.java
 *
 * Created on 10. februar 2007, 09:47
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.3
 * 
 * FieldPanel is an abstract from wich to derive classes to render the contents
 * of a Field. 
 *
 *
 * ******VERSION HISTORY******
 * LMK @ 11. februar 2007 (v 1.3)
 * Added javadoc to newest functions
 * LMK @ 11. februar 2007 (v 1.2)
 * Added isGridVisible and setGridVisible
 * Tweaked drawGrid so that it only draws when gridVisible is set
 * LMK @ 11. februar 2007 (v 1.1)
 * Fixed Field.getSize() bug
 * LMK @ 10. februar 2007 (v 1.0)_
 *
 */

package obsolete;

import field.*;
import java.awt.*;
import javax.swing.*;
/**
 *
 * @author LMK
 */
public abstract class FieldPanel extends JPanel {
    
    protected Field field;
    protected int tileSize;
    protected boolean gridVisible; //bør fjernes
    protected Color gridColor;
    
    /** Creates a new instance of FieldRenderer */
    public FieldPanel(Field field, int tileSize) {
        this.field = field;
        this.tileSize = tileSize;
        this.gridVisible = false;
        this.gridColor = Color.LIGHT_GRAY;
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
    
    /**
     * Draw the grid on graphic
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
