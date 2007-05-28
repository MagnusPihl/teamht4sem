/*
 * PacmanBrush.java
 *
 * Created on 16. februar 2007, 10:22
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.3
 *
 *
 * ******VERSION HISTORY******
 * Magnus Hemmer Pihl @ 19. februar 2007 (v 1.3)
 * Now uses the MouseReleased event, rather than MouseClicked, to ensure action even when the mouse is dragged.
 *
 * Magnus Hemmer Pihl @ 19. februar 2007 (v 1.2)
 * Removed conditional removal from removeEntityAt call. Will now remove any entity at the given position.
 *
 * Magnus Hemmer Pihl @ 19. februar 2007 (v 1.1)
 * Updated removing the Pacman entity to use removeEntityAt method.
 *
 * Magnus Hemmer Pihl @ 16. februar 2007 (v 1.0)
 * Created.
 */

package editor;

import java.awt.event.*;
import java.awt.*;
import field.*;

public class PacmanBrush extends Brush
{
    
    public PacmanBrush(EditorPanel panel)
    {
        super(panel);
    }
    
    public void mouseReleased(MouseEvent e)
    {
        Point curpos = super.panel.translate(e.getPoint());
        
        if (e.getButton() == e.BUTTON1) {
            super.panel.getField().placePacman(curpos);
        } else if (e.getButton() == e.BUTTON3) {
            super.panel.getField().removeEntityAt(curpos);
        }
        super.panel.checkSize();
    }
    
    public void mousePressed(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}  
}