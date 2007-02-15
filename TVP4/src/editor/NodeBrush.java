/*
 * NodeBrush.java
 *
 * Created on 14. februar 2007, 18:39
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 14. februar 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package editor;

import java.awt.event.*;
import java.awt.*;

/**
 *
 * @author LMK
 */
public class NodeBrush extends Brush {
    
    private boolean mouseDown;
    private boolean addNode;
    
    public NodeBrush(EditorPanel panel) {
        super(panel);
    }        
    
    public void mouseClicked(MouseEvent e) { 
        if (e.getButton() == e.BUTTON1) {
            super.panel.getField().addNodeAt(super.panel.translate(e.getPoint()), LevelEditor.getInstance().getPoints());
        } else if (e.getButton() == e.BUTTON3) {
            super.panel.getField().removeNodeAt(super.panel.translate(e.getPoint()));        
        }
            //super.field.invertNodeAt(super.translate(e.getPoint()));        
        super.panel.checkSize();
    }
    
    public void mouseDragged(MouseEvent e) {
        
        if (this.mouseDown) {            
            if (this.addNode) {
                super.panel.getField().addNodeAt(super.panel.translate(e.getPoint()), LevelEditor.getInstance().getPoints());
            } else {
                super.panel.getField().removeNodeAt(super.panel.translate(e.getPoint()));            
            }
            super.panel.checkSize();
        }        
    }
    
    public void mousePressed(MouseEvent e) {
        this.mouseDown = true;
        if (e.getButton() == e.BUTTON1) {
            this.addNode = true;//(super.field.getNodeAt(super.translate(e.getPoint())) == null);
        } else {
            this.addNode = false;
        }
    }
    
    public void mouseReleased(MouseEvent e) {               
        this.mouseDown = false;
        super.panel.checkSize();
    }
    
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}  
}
