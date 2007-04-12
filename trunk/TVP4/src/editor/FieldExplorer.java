/*
 * FieldExplorer.java
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
 */

package editor;

import com.sun.org.apache.bcel.internal.classfile.Field;
import communication.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author LMK
 */
public class FieldExplorer implements Runnable {
    
    private RobotSocket robot;
    private FieldExplorerDialog dialog;
    private boolean scanDone;
    private List open, closed;
    private int availableDirections;
    
    /** Creates a new instance of FieldExplorer */
    public FieldExplorer(FieldExplorerDialog dialog) {
        this.robot = new RobotSocket();        
        this.dialog = dialog;
    }
    
    public void run() {
        LinkedList open = new LinkedList();
        LinkedList closed = new LinkedList();
        boolean scanDone = false;
        
        Field field = LevelEditor.getInstance().getEditorPanel().getField();
        Node current = field.addNodeAt(0,0);
        //this.robot.setMode(set to calibrate);
        availableDirection = Node.INVALID_DIRECTION; //this.robot.getAvaibleDirections();
        
        while (!scanDone || (availableDirection = Node.INVALID_DIRECTION)) {
            if (this.robot.isAvailable) {
                availableDirection = this.robot.getAvaibleDirections();
            } else {
                Thread.yield();
            }
        }
        
        while(!scanDone) {
            //this.robot.blink();
            this.dialog.addToLog("Hahahamama");
            this.robot.getAvaibleDirections();
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
        }
    }
    
    private void addDirections() {
        if (this.availableDirections != Node.INVALID_DIRECTION) {
            Field field = LevelEditor.getInstance().getEditorPanel().getField();
            Node current = null;
            
            if (this.availableDirections & Node.UP != 0) {
                current = field.addNodeAt()
            }
        }
    }
    
    public void stop() {        
        this.scanDone = true;
    }        
}