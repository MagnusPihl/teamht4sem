/*
 * HighScoreDialog.java
 *
 * Created on 23. april 2007, 16:56
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 23. april 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import game.*;
import java.util.*;
/**
 *
 * @author LMK
 */
public class HighScoreDialog extends JFrame implements ActionListener {
    
    private static final Object[] coloumnNames = {"#", "Name:", "Score:"};
    private JPanel tablePane;
    
    /** Creates a new instance of HighScoreDialog */
    public HighScoreDialog() {
        super("High Score List");
        JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        this.tablePane = new JPanel(new BorderLayout());
        
        JButton closeButton = new JButton("Close");
        closeButton.setMnemonic('C');
        closeButton.addActionListener(this);
        buttonPane.add(closeButton);
        
        this.getContentPane().add(buttonPane, BorderLayout.SOUTH);        
        this.getContentPane().add(tablePane, BorderLayout.CENTER);                
    }
       
    public void update() {
        
        Object[][] rowData = new Object[11][3];
        rowData[0] = coloumnNames;
        
        HighScoreList list = LevelEditor.getInstance().getEditorPanel().getField().getHighScores();
        int j = 1;
        HighScore current = null;
        for (Iterator i = list.iterator(); i.hasNext(); j++) {
            current = (HighScore)i.next();
            rowData[j][0] = j;
            rowData[j][1] = current.getName();
            rowData[j][2] = current.getScore();
        }
        
        this.tablePane.removeAll();
        JTable scoreTable = new JTable(rowData, coloumnNames);                   
        this.tablePane.add(scoreTable, BorderLayout.CENTER);     
        this.pack();   
    }
    
    public void actionPerformed(ActionEvent evt) {             
        this.setVisible(false);
    }
}
