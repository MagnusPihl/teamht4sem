/*
 * EditorPanel.java
 *
 * Created on 9. februar 2007, 22:57
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.1
 *
 *
 * ******VERSION HISTORY******
 * LMK @ 11. februar 2007 (v 1.1)
 * Fixed Field.getSize() bug
 * LMK @ 9. februar 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package editor;

import field.*;
import java.awt.event.*;
import java.io.File;
import java.awt.Dimension;

public class EditorPanel extends FieldTileRenderer implements MouseMotionListener, MouseListener {
    
    private boolean mouseDown;
    private boolean addNode;
        
    /** Creates a new instance of EditorPanel */
    public EditorPanel(Field field) {        
        super(field, "skins/nodes/", 30);
        //System.out.println((new File("skins/lines/")).getAbsolutePath());
        super.addMouseListener(this);
        super.addMouseMotionListener(this);        
    }
    
    public void checkSize() {
        Dimension size = super.field.getSize();
        size.setSize(size.getWidth()*super.tileSize, size.getHeight()*super.tileSize);
        this.setPreferredSize(size);
        this.revalidate();
        this.repaint();
    }
    
    public void mouseClicked(MouseEvent e) { 
        super.field.invertNodeAt(super.translate(e.getPoint()));        
        this.checkSize();
    }
    
    public void mouseDragged(MouseEvent e) {
        
        if (this.mouseDown) {            
            if (this.addNode) {
                super.field.addNodeAt(super.translate(e.getPoint()),100);
            } else {
                super.field.removeNodeAt(super.translate(e.getPoint()));            
            }
            this.checkSize();
        }        
    }
    
    public void mousePressed(MouseEvent e) {
        this.mouseDown = true;
        this.addNode = (super.field.getNodeAt(super.translate(e.getPoint())) == null);
    }
    
    public void mouseReleased(MouseEvent e) {               
        this.mouseDown = false;
        this.checkSize();
    }
    
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}            
}
