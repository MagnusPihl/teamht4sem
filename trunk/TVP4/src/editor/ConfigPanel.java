/*
 * ConfigPanel.java
 *
 * Created on 10. februar 2007, 10:17
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 10. februar 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package editor;

import field.*; 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ConfigPanel extends JPanel implements MouseMotionListener {
    
    private EditorPanel editor;
    private JLabel positionLabel;
    private JLabel pointLabel;
    private JTextField pointField;
    
    /** Creates a new instance of ConfigPanel */
    public ConfigPanel(EditorPanel editor) {                
        this.pointField = new JTextField("100");
        this.pointLabel = new JLabel("100");
        this.positionLabel = new JLabel("(0,0)");
        
        this.setLayout(new GridLayout(0,1));
        this.add(new JLabel("Point:"), 0);
        this.add(this.pointField, 1);
        this.add(new JLabel("Position (x,y):"), 2);
        this.add(this.positionLabel, 3);
        this.add(new JLabel("Point:"), 4);
        this.add(this.pointLabel, 5);
        
        this.editor = editor;
        editor.addMouseMotionListener(this);        
    }
    
    public void mouseMoved(MouseEvent e) {
        Point position = this.editor.translate(e.getPoint());
        Node node = this.editor.getField().getNodeAt(position);
        
        this.positionLabel.setText("(" + position.x + "," + position.y + ")");
        if (node != null) {
            this.pointLabel.setText(String.valueOf(node.getPoints()));
        }
    }
    
    public void mouseDragged(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}                    
}
