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

import communication.*;
import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;
import java.awt.*;
import field.*;
import game.entitycontrol.*;
import java.util.concurrent.*;

/**
 *
 * @author LMK
 */
public class FieldExplorer implements Runnable {
    
    private RobotProxy robot;
    private FieldExplorerDialog dialog;
    private boolean scanDone;
    private int availableDirections;
    private Node currentNode, nextNode;
    private SearchAlgorithm algorithm;
    private Semaphore synchronizer;
    private Set closed;
    private LinkedList open;
    private int[] moves;
    
    /** Creates a new instance of FieldExplorer */
    public FieldExplorer(FieldExplorerDialog dialog) {
        this.dialog = dialog;
        this.algorithm = new BreadthFirstAlgorithm();
        this.synchronizer = new Semaphore(1);
        this.robot = new RobotProxy(1);        
        this.open = new LinkedList();
        this.closed = new TreeSet();        
    }
        
    public void run() {
        this.dialog.addToLog("Contacting robot...");
        //try {
            this.robot.blink();
            this.robot.beep(true);
        //} catch (IOException ioe) {
            this.dialog.addToLog("Unable to contact robot! Make sure it is turned, " +
                "software has been downloaded and started.");            
            
            this.stop();
            //return;
        //}                
        
        this.dialog.addToLog("Connection established...");
        JOptionPane.showMessageDialog(this.dialog, "Please place the " +
                "blinking/beeping robot with it's center lightsensor on a node " +
                "on the field and press okay.");        
        
        this.robot.lights(false);
        this.robot.beep(false);
        this.open.clear();
        this.closed.clear();
        this.moves = null;
        
        field.Field field = LevelEditor.getInstance().getEditorPanel().getField();
        field.placePacman(new Point(0,0));
        this.currentNode = field.addNodeAt(0,0);        
        
        //this.robot.setMode(set to calibrate);
        this.availableDirections = Node.INVALID_DIRECTION; //this.robot.getAvaibleDirections();
        
        while (!scanDone || (this.availableDirections == Node.INVALID_DIRECTION)) {            
            //this.availableDirection = this.robot.getAvaibleDirections();            
        }
        this.addDirections();
        
        //this.robot.blink();
        
        try {
            while(!scanDone || this.open.size() == 0) {                
                this.dialog.addToLog("Hahahamama");
                this.nextNode = (Node)this.open.remove(0);
                this.closed.add(this.currentNode);
                moves = this.algorithm.fullSearch(this.currentNode, this.nextNode);

                if (moves != null) {
                    for (int i = 0; i < moves.length; i++) {                        
                        while (this.synchronizer.availablePermits() == 0) {
                            Thread.sleep(200);
                        }                

                        if (i == moves.length) {
                            //this.robot.moveDiscover(moves[i]);                                
                            this.availableDirections = this.robot.getAvaibleDirections();
                            this.addDirections();
                        } else {
                            //this.robot.move(moves[i], this.currentNode.getNodeAt(moves[i]).getBinaryDirections());
                        }
                        this.currentNode = this.currentNode.getNodeAt(moves[i]);
                        field.placePacman(this.currentNode.getPosition());
                    }
                }
            }
            
            this.dialog.addToLog("Scan done");
            this.stop();
        //} catch (IOException ioe) {
            
        } catch (InterruptedException ie) {
        
        }
    }
    
    private void addDirections() {
        if (this.availableDirections != Node.INVALID_DIRECTION) {
            field.Field field = LevelEditor.getInstance().getEditorPanel().getField();
            Node addedNode = null;
            Point position = this.currentNode.getPosition();
            
            if ((this.availableDirections & (0x01 << Node.UP)) != 0) {
                addedNode = field.addNodeAt(position.x, position.y-1);
                if (addedNode != null) {
                    this.open.add(0,addedNode);
                }
            }
            if ((this.availableDirections & (0x01 << Node.UP)) != 0) {
                addedNode = field.addNodeAt(position.x, position.y+1);
                if (addedNode != null) {
                    this.open.add(0,addedNode);
                }
            }
            if ((this.availableDirections & (0x01 << Node.UP)) != 0) {
                addedNode = field.addNodeAt(position.x-1, position.y);
                if (addedNode != null) {
                    this.open.add(0,addedNode);
                }
            }
            if ((this.availableDirections & (0x01 << Node.UP)) != 0) {
                addedNode = field.addNodeAt(position.x+1, position.y);
                if (addedNode != null) {
                    this.open.add(0,addedNode);
                }
            }
        }
    }
    
    public void stop() {        
        this.scanDone = true;
        this.dialog.explorationDone();
    }        
}