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
import field.Node;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StatusBar extends JPanel implements MouseMotionListener {
    
    private EditorPanel editor;
    private JLabel positionLabel;
    private JLabel pointLabel;
    
    /** Creates a new instance of ConfigPanel */
    public StatusBar(EditorPanel editor) {       
        this.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        this.pointLabel = new JLabel("-");
        this.positionLabel = new JLabel("(0,0)");
        JLabel pointText = new JLabel("Points:");
        pointText.setPreferredSize(new Dimension(45,22));
        pointLabel.setPreferredSize(new Dimension(50,22));        
        pointLabel.setHorizontalAlignment(JTextField.LEFT);
        
        JLabel positionText = new JLabel("Position (x,y):");
        positionText.setPreferredSize(new Dimension(80,22));
        positionLabel.setPreferredSize(new Dimension(40,22));
                
        this.add(positionText);
        this.add(positionLabel);
        this.add(new JSeparator(JSeparator.VERTICAL));         
        this.add(pointText);
        this.add(pointLabel);
        
        /*JPanel pointPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));        
        pointPanel.setSize(new Dimension(100,22));
        pointPanel.add(new JLabel("Position (x,y):"));
        pointPanel.add(this.positionLabel);*/
                
        /*JPanel positionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));  
        positionPanel.setSize(new Dimension(100,22));
        positionPanel.add(new JLabel("Point:"));
        positionPanel.add(this.pointLabel);*/
                
        //this.setLayout(new GridLayout(0,1));
        /*this.add(positionPanel);
        this.add(new JSeparator(JSeparator.VERTICAL));        
        this.add(pointPanel);*/
                
        this.editor = editor;
        editor.addMouseMotionListener(this);        
    }
    
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
    
    public void mouseDragged(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}                    
}
