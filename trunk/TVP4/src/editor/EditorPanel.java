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
 * LMK @ 13. februar 2007 (v 1.1)
 * Points are now alloted to each node according to the amount of points 
 * specified by the user.
 * LMK @ 9. februar 2007 (v 1.0)
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
        this.font = new Font("HT", Font.PLAIN, 5);
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
    
    public void paint(Graphics g) {
        this.field.drawField(g);
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
            Dimension size = this.field.getSize();
            g.setColor(this.gridColor);       
            int tileSize = this.tileSet.getTileSize();
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
        for (Iterator i = this.field.getNodeList().keySet().iterator(); i.hasNext();) {
            position = (Point)i.next();
            current = (Node)this.field.getNodeList().get(position);
            g.setFont(this.font);
            g.setColor(Color.BLACK);
            g.drawRect(position.x * this.tileSet.getTileSize(), 
                    position.y * this.tileSet.getTileSize(), 
                    this.tileSet.getTileSize(),
                    this.tileSet.getTileSize());
            g.setColor(Color.WHITE);
            g.drawString(current.getPoints() + "", 
                    position.x * this.tileSet.getTileSize(), 
                    position.y * this.tileSet.getTileSize());
            
        }
        }//...
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
    
    public void placePacman(Point point){
        this.field.placePacman(point);
    }
    
    public void placeGhost(Point point){
        this.field.placeGhost(point);
    }
}
