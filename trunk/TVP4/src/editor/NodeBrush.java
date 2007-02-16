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
    
    /**
     * When mouse is clicked add node or change node at position if the left
     * button was clicked. If the right button was clicked remove node at 
     * position.
     */
    public void mouseClicked(MouseEvent e) { 
        if (e.getButton() == e.BUTTON1) {
            super.panel.getField().addNodeAt(super.panel.translate(e.getPoint()), LevelEditor.getInstance().getPoints());
        } else if (e.getButton() == e.BUTTON3) {
            super.panel.getField().removeNodeAt(super.panel.translate(e.getPoint()));        
        }
            //super.field.invertNodeAt(super.translate(e.getPoint()));        
        super.panel.checkSize();
    }
    
    /**
     * When mouse is dragged add node if none is available at the current
     * position, if one is available change its points when
     * the left button is down. If the right buttons is down remove nodes.
     */
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
    
    /**
     * Register that the mouse is press down. Used so that you can only start
     * dragging motion within the object that uses the brush. Registers
     * whether nodes should be added or removed based on the button pressed.
     */
    public void mousePressed(MouseEvent e) {
        this.mouseDown = true;
        if (e.getButton() == e.BUTTON1) {
            this.addNode = true;//(super.field.getNodeAt(super.translate(e.getPoint())) == null);
        } else {
            this.addNode = false;
        }
    }
    
    /**
     * Clear mouseDown variable
     */
    public void mouseReleased(MouseEvent e) {               
        this.mouseDown = false;
    }
    
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}  
}
