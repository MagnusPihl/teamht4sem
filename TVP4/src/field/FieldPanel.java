/*
 * FieldPanel.java
 *
 * Created on 10. februar 2007, 09:47
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.1
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 11. februar 2007 (v 1.1)
 * Fixed Field.getSize() bug
 * LMK @ 10. februar 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package field;

import java.awt.*;
import javax.swing.*;
/**
 *
 * @author LMK
 */
public abstract class FieldPanel extends JPanel {
    
    protected Field field;
    protected int tileSize;
    protected Color gridColor;
    
    /** Creates a new instance of FieldRenderer */
    public FieldPanel(Field field, int tileSize) {
        this.field = field;
        this.tileSize = tileSize;
        this.gridColor = Color.LIGHT_GRAY;
    }
    
    public Point translate(Point position) {
        return new Point(
                (int)(position.x / tileSize),
                (int)(position.y / tileSize)
                );
    }
    
    public void setGridColor(Color color) {
        this.gridColor = color;
    }
    
    public Color getGridColor() {
        return this.gridColor;
    }
    
    protected void drawGrid(Graphics g) {
        Dimension size = this.field.getSize();
        g.setColor(this.gridColor);       
        
        for (int x = 1; x < size.width; x++) {
            g.drawLine(x*tileSize, 0, x*tileSize, size.height*tileSize);
        }
        for (int y = 1; y < size.height; y++) {
            g.drawLine(0, y*tileSize, size.width*tileSize, y*tileSize);
        }
    }

    public Field getField() {
        return field;
    }
    //public abstract void paint(Graphics g);
    
    public void setField(Field field) {
        this.field = field;
    }
}
