/*
 * FieldExplorerDialog.java
 *
 * Created on 11. april 2007, 09:37
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 11. april 2007 (v 1.0)
 * __________ Changes ____________
 *
 * Opdater direction i entityrenderer.
 * Tilføj er du nu sikker på du vil stoppe dialog før man stopper med at indlæse.
 */

package editor;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 *
 * @author LMK
 */
public class FieldExplorerDialog extends JFrame implements ActionListener {
    
    private JTextPane log;
    private JButton startBtn, cancelBtn;   
    private JScrollPane scrollPane;
    private FieldExplorer explorer;
    private Thread explorerThread;
    private JComboBox portList;
    
    /** Creates a new instance of FieldExplorerDialog */
    public FieldExplorerDialog() {
        Container pane = this.getContentPane();
        JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        this.log = new JTextPane();  
        this.log.setPreferredSize(new Dimension(300,200));
        this.log.setEditable(false);
        
        this.scrollPane = new JScrollPane(this.log, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);        
        pane.add(this.scrollPane, BorderLayout.CENTER);
        
        buttonPane.add(new JLabel("Port:"));
        this.portList = new JComboBox(new Object[] {"USB", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7"});
        buttonPane.add(this.portList);
        this.startBtn = new JButton("Scan");
        this.startBtn.setMnemonic('S');
        this.startBtn.addActionListener(this);
        buttonPane.add(this.startBtn);
        this.cancelBtn = new JButton("Cancel");       
        this.cancelBtn.setMnemonic('C');        
        this.cancelBtn.addActionListener(this);
        buttonPane.add(this.cancelBtn);
        pane.add(buttonPane, BorderLayout.SOUTH);
        
        this.explorer = new FieldExplorer(this);        
        this.pack();
    }    
    
    public void reset() {                 
        /*JOptionPane.showMessageDialog(this, "Please turn the Robot marked 1 on by " +
                "pressing the red \"on/off\" button once.\nIf the appropriate software " +
                "has been downloaded to the unit press the green button marked \"run\". \n" +
                "For help on software downloading please refer to the help file.");*/
        this.log.setText("Before you start please position the Robot " +
                "currently blinking on the playing field. When ready click scan " +
                "to start reading field. If you should wish to stop, press cancel" +
                "at any time. You cannot continue scanning a once you've canceled.");
        this.scrollPane.validate();
        this.startBtn.setEnabled(true);
    }
    
    public void explorationDone() {    
        this.portList.setEnabled(true);
        this.startBtn.setEnabled(true);
    }
    
    public void actionPerformed(ActionEvent evt) {             
        if (evt.getActionCommand().equals("Scan")) {
            this.portList.setEnabled(false);
            this.startBtn.setEnabled(false);
            this.explorer.setPort((String)this.portList.getSelectedItem());
            this.explorerThread = new Thread(this.explorer);
            this.explorerThread.start();
        } else if (evt.getActionCommand().equals("Cancel")) {
            this.portList.setEnabled(true);
            this.setVisible(false);
            this.explorer.stop();
        }
    }
    
    public void addToLog(String text) {
        this.log.setText(this.log.getText() + '\n' + text);        
        this.scrollPane.getVerticalScrollBar().setValue(this.scrollPane.getVerticalScrollBar().getMaximum());
    }
    
    public void clearLog() {
        this.log.setText(null);
    }
}
