/*
 * PacmanBrush.java
 *
 * Created on 16. februar 2007, 10:22
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * Magnus Hemmer Pihl @ 16. februar 2007 (v 1.0)
 * Created.
 *
 */

package editor;

import java.awt.event.*;
import java.awt.*;

public class PacmanBrush extends Brush
{
    private boolean mouseDown;
    private boolean addNode;
    
    public PacmanBrush(EditorPanel panel)
    {
        super(panel);
    }
    
    public void mouseClicked(MouseEvent e)
    {
        Point curpos = super.panel.translate(e.getPoint());
        if (e.getButton() == e.BUTTON1)
        {
            if(super.panel.getField().getNodeAt(curpos) != null)
                super.panel.placePacman(curpos);
        }
        else if (e.getButton() == e.BUTTON3)
        {
            if(super.panel.getField().getEntityStartPosition(0).equals(curpos))
                super.panel.getField().removeEntityAt(curpos);
        }
    }
    
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}  
}