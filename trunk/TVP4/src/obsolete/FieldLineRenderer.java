/*
 * FieldRenderer.java
 *
 * Created on 9. februar 2007, 10:22
 *
 * Company: HT++
 *
 * @author Lau Maack-Krommes
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 9. februar 2007 (v 1.0)
 * Created
 *
 */

package obsolete;

import field.*;
import java.awt.*;
import javax.swing.*;
import java.util.Iterator;

public class FieldLineRenderer extends FieldPanel {
    
    /** 
     *
     */
    public FieldLineRenderer(Field field) {
        super(field, 15);
        super.setBackground(Color.WHITE);
    }
    
    public void setField(Field field) {
        this.field = field;
    }
    
    public Field getField() {
        return this.field;
    }
    
    public void paint(Graphics g) {        
        super.paint(g);
                
        if (this.field != null) {
            this.drawGrid(g);
            this.drawPaths(g);
            this.drawPoints(g);
        }
    }
    
    private void drawPaths(Graphics g) {    
        g.setColor(Color.BLACK);
        Point position = null;
        Node current = null;
        
        for (Iterator i = this.field.getNodeList().iterator(); i.hasNext();) {
            current = (Node)(i.next());
            position = current.getPosition();
            
            if (current.getNodeAt(Node.UP) != null) {
                g.fillRect(
                        position.x * tileSize + tileSize / 2 - 1,
                        position.y * tileSize + tileSize / 2 + 2,
                        3, -(tileSize + 3));
            }            
            
            if (current.getNodeAt(Node.DOWN) != null) {
                g.fillRect(
                        position.x * tileSize + tileSize / 2 - 1,
                        position.y * tileSize + tileSize / 2 - 1,
                        3, tileSize + 3);
            }            
            
            if (current.getNodeAt(Node.LEFT) != null) {
                g.fillRect(
                        position.x * tileSize + tileSize / 2 + 2,
                        position.y * tileSize + tileSize / 2 - 1,
                        -(tileSize + 3), 3);
            }            
            
            if (current.getNodeAt(Node.RIGHT) != null) {
                g.fillRect(
                        position.x * tileSize + tileSize / 2 - 1,
                        position.y * tileSize + tileSize / 2 - 1,
                        tileSize + 3, 3);
            }
        }        
    }
    
    private void drawPoints(Graphics g) {    
        Point position = null;
        Node current = null;
        
        for (Iterator i = this.field.getNodeList().iterator(); i.hasNext();) {
            current = (Node)(i.next());
            position = current.getPosition();
            
            if (current.isStraightPath()) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.YELLOW);
            }
            
            g.fillOval(
                    position.x * tileSize + tileSize / 2 - 4,
                    position.y * tileSize + tileSize / 2 - 4,
                    9, 9);
        }
    }    
    
    private void drawPlayers(Graphics g) {    
        /*for (Iterator i = this.field.getNodeList().iterator(); i.hasNext();) {
        
        } */       
    }    
}
