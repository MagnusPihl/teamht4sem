/*
 * GhostBrush.java
 *
 * Created on 18. februar 2007, 16:02
 *
 * Company: HT++
 *
 * @author Mikkel Brøndsholm Nielsen
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * Administrator @ 18. februar 2007 (v 1.1)
 * __________ Changes ____________
 *
 * Administrator @ 18. februar 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package editor;
import java.awt.event.*;
import java.awt.*;

public class GhostBrush extends Brush {
    
    private boolean mouseDown;
    private boolean addGhost;
    
    /** Creates a new instance of GhostBrush */
    public GhostBrush(EditorPanel panel) {
        super(panel);
    }        
    
    public void mouseClicked(MouseEvent e) {
        Point position = super.panel.translate(e.getPoint());
        if (e.getButton() == e.BUTTON1)
        {
            if(super.panel.getField().getNodeAt(position) != null)
                super.panel.placeGhost(position);
        } 
        else if (e.getButton() == e.BUTTON3)
        {
            if(super.panel.getField().getEntityStartPosition().equals(position))
                super.panel.getField().placeGhost(null);
        }
        super.panel.checkSize();
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
