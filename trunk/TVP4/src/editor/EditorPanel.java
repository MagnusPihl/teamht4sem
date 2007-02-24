/*
 * EditorPanel.java
 *
 * Created on 9. februar 2007, 22:57
 *
 * Company: HT++
 *
 * @author Mikkel Nielsen
 * @version 1.2.1
 *
 *
 * ******VERSION HISTORY******
 * Mikkel Nielsen @ 21. februar 2007 (v 1.3)
 * super.paint() kaldes nu i paint-metoden.
 * Magnus Pihl @ 21. februar 2007 (v 1.2.1)
 * Changed background color of drawPoints to dark gray, to work better with all skins
 * Mikkel Nielsen @ 21. februar 2007 (v 1.2)
 * drawPoints refactored and optimated.
 * Mikkel Nielsen @ 13. februar 2007 (v 1.1)
 * Points are now alloted to each node according to the amount of points 
 * specified by the user.
 * Mikkel Nielsen @ 9. februar 2007 (v 1.0)
 *
 */

package editor;

import field.*;
import java.awt.event.*;
import java.io.File;
import java.awt.*;
import java.util.Iterator;
import javax.swing.*;
import java.util.HashMap;

public class EditorPanel extends JPanel {    
            
    protected Field field;
    protected TileSet tileSet;
    protected boolean gridVisible, pointsVisible;
    protected Color gridColor;
    protected Brush brush;
    private Font font;
    private int ID;
    
    /** Creates a new instance of EditorPanel */
    public EditorPanel(Field field) {        
        this.field = field;
        this.tileSet = TileSet.getInstance();
        this.gridColor = Color.LIGHT_GRAY;
        this.gridVisible = false;
        this.pointsVisible = false;
        this.setBrush(new NodeBrush(this));
        this.font = new Font("HT", Font.PLAIN, 10);
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
        this.brush = brush;
        
    }
    
    /**
     * Resizes the frame and repainting
     */
    public void checkSize() {
        Dimension size = this.field.getSize();
        size.setSize(size.getWidth()*this.tileSet.getTileSize(), size.getHeight()*this.tileSet.getTileSize());
        this.setPreferredSize(size);
        this.revalidate();
        this.repaint();
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
            (int)(position.x / this.tileSet.getTileSize()),
            (int)(position.y / this.tileSet.getTileSize())
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
     * Overrides paint-method
     *
     * @param g graphic to draw on.
     */
    public void paint(Graphics g) {
        super.paint(g);
        this.field.drawField(g,  this.getSize());
        this.drawGrid(g);
        this.drawPoints(g);
    }
    
    /**
     * Draw grid on graphic
     *
     * @param g graphic to draw grid on.
     */
    protected void drawGrid(Graphics g) {
        if (gridVisible) {
            int tileSize = this.tileSet.getTileSize();

            Dimension size = new Dimension(
                    (int)(this.getSize().getWidth() / tileSize) + 1,
                    (int)(this.getSize().getHeight() / tileSize) + 1);
                        
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
     * Draw points on graphic
     *
     * @param g graphic to draw points on.
     */
    protected void drawPoints(Graphics g) {    
        if(pointsVisible){
        Point position = null;
        Node current = null;
        g.setFont(this.font);
        int tileSize = this.tileSet.getTileSize();
        for (Iterator i = this.field.getNodeList().keySet().iterator(); i.hasNext();) {
            position = (Point)i.next();
            current = (Node)this.field.getNodeList().get(position);
            g.setColor(Color.DARK_GRAY);
            String points = current.getPoints() + "";
            int width = g.getFontMetrics().stringWidth(points);
            int height = g.getFontMetrics().getHeight();
            g.fillRect(position.x * tileSize, 
                    position.y * tileSize, 
                    width, height);
            g.setColor(Color.WHITE);
            g.drawString(points + "", 
                    position.x * tileSize, 
                    position.y * tileSize + height-3);
            
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
     * Set whether points should be visible when drawPoints() is  called.
     * 
     * @param isVisible. Should be true if the grid should be drawn
     */
    public void setPointsVisible(boolean isVisible) {
        this.pointsVisible = isVisible;
    }
    
    /**
     * Get points visibility.
     * 
     * @return true if the points is visible.
     */
    public boolean isPointsVisible() {
        return this.pointsVisible;
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
