/*
 * ConfigPanel.java
 *
 * Created on 10. februar 2007, 10:17
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.1
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 6. marts 2007 (v 1.1)
 * Total points on field are now also shown.
 *
 */

package editor;

import field.*; 
import field.Node;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StatusBar extends JPanel implements MouseMotionListener, MouseListener {
    
    private EditorPanel editor;
    private JLabel positionLabel;
    private JLabel pointLabel;
    private JLabel totalPointsLabel;
    
    /** Creates a new instance of ConfigPanel */
    public StatusBar(EditorPanel editor) {       
        this.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        this.pointLabel = new JLabel("-");
        this.positionLabel = new JLabel("(0,0)");
        this.totalPointsLabel = new JLabel("0");
        
        JLabel pointText = new JLabel("Points:");
        pointText.setPreferredSize(new Dimension(45,22));
        this.pointLabel.setPreferredSize(new Dimension(50,22));        
        this.pointLabel.setHorizontalAlignment(JTextField.LEFT);
        
        JLabel positionText = new JLabel("Position (x,y):");
        positionText.setPreferredSize(new Dimension(80,22));
        this.positionLabel.setPreferredSize(new Dimension(40,22));
                
        JLabel totalPointText = new JLabel("Total points:");
        pointText.setPreferredSize(new Dimension(65,22));
        this.totalPointsLabel.setPreferredSize(new Dimension(50,22));        
        this.totalPointsLabel.setHorizontalAlignment(JTextField.LEFT);
               
        this.add(totalPointText);
        this.add(this.totalPointsLabel);
        this.add(new JSeparator(JSeparator.VERTICAL));         
        this.add(positionText);
        this.add(this.positionLabel);
        this.add(new JSeparator(JSeparator.VERTICAL));         
        this.add(pointText);
        this.add(this.pointLabel);
                
        this.editor = editor;
        editor.addMouseListener(this);
        editor.addMouseMotionListener(this);        
    }
    
    /**
     * When the mouse is moved, fetch the node under the cursor and print its
     * position and points held.
     */
    public void mouseMoved(MouseEvent e) {
        Point position = this.editor.translate(e.getPoint());
        Node node = this.editor.getField().getNodeAt(position);
        
        this.positionLabel.setText("(" + position.x + "," + position.y + ")");
        
        if (node != null) {
            this.pointLabel.setText(String.valueOf(node.getPoints()));
        } else {
            this.pointLabel.setText("-");        
        }
    }
    
    public void mouseReleased(MouseEvent e) {
        this.totalPointsLabel.setText(Integer.toString(this.editor.getField().getPointsLeft()));
    }
        
    public void mouseClicked(MouseEvent e) {}    
    public void mousePressed(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}                    
}
