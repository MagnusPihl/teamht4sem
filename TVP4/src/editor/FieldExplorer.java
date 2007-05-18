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
    private Stack open;
    private int[] moves;
    private String port;
    
    /** Creates a new instance of FieldExplorer */
    public FieldExplorer(FieldExplorerDialog dialog) {
        this.dialog = dialog;
        this.algorithm = new BreadthFirstAlgorithm();
        this.synchronizer = new Semaphore(1);
        this.robot = new RobotProxy(1, this.synchronizer);
        this.open = new Stack();
        this.closed = new TreeSet();
        this.port = "USB";
    }
    
    public void setPort(String port) {
        this.port = port;
    }
    
    public void run() {
        Entity pacman;
        Map parents;
        LevelEditor.getInstance().getEditorPanel().setEditable(false);
        
        this.dialog.clearLog();
        this.dialog.addToLog("Contacting robot...");
        //this.robot.init((byte)0);
        this.robot.open(this.port);
        this.robot.setActive(true);
        try {
            this.robot.lights(true);
        } catch (Exception ioe) {
            this.dialog.addToLog("Unable to contact robot! Make sure it is turned, " +
                    "software has been downloaded and started.");
            
            this.stop();
            return;
        }
        
        this.dialog.addToLog("Connection established...");
        JOptionPane.showMessageDialog(this.dialog, "Please place the " +
                "blinking/beeping robot with it's center lightsensor on a node " +
                "on the field and press okay.");
        
        try {
            this.robot.lights(false);
            this.open.clear();
            this.closed.clear();
            this.moves = null;
            
            field.Field field = LevelEditor.getInstance().getEditorPanel().getField();
            field.clearField();
            this.currentNode = field.addNodeAt(0,0);
            field.placePacman(new Point(0,0));
            pacman = this.currentNode.getEntity();
            LevelEditor.getInstance().getEditorPanel().checkSize();
            
            System.out.println("Før node");
            this.robot.search((byte)Node.INVALID_DIRECTION);
            System.out.println("Efter node");
            this.waitForRobot();
            this.addDirections();
            
            Node tempNode, lastNode;
            int move;
            
            while(!scanDone && !this.open.empty()) {
                if (this.open.size() != 0) {
                    this.nextNode = (Node)this.open.pop();
                    this.closed.add(this.currentNode);
                    parents = this.algorithm.fullSearch(this.currentNode, this.nextNode);
                            
                    this.dialog.addToLog("Fra (" + this.currentNode.getPosition().x + "," +  this.currentNode.getPosition().y + ") - (" + this.nextNode.getPosition().x + "," + this.nextNode.getPosition().y + ")");
                    
                    if (parents != null) {
                        tempNode = this.currentNode;
                            
                        do {
                            lastNode = tempNode;
                            tempNode = (Node)parents.get(tempNode);
                            move = lastNode.connectedAt(tempNode);
                            if (!tempNode.equals(this.nextNode)) {
                                switch(move) {
                                    case Node.UP: this.dialog.addToLog("Moving upwards..."); break;
                                    case Node.DOWN: this.dialog.addToLog("Moving downwards..."); break;
                                    case Node.LEFT: this.dialog.addToLog("Moving to the left..."); break;
                                    case Node.RIGHT: this.dialog.addToLog("Moving to the right..."); break;
                                }
                                this.robot.move((byte)move, (byte)tempNode.getBinaryDirections());
                            } else {
                                switch(move) {
                                    case Node.UP: this.dialog.addToLog("Searching upwards..."); break;
                                    case Node.DOWN: this.dialog.addToLog("Searching downwards..."); break;
                                    case Node.LEFT: this.dialog.addToLog("Searching to the left..."); break;
                                    case Node.RIGHT: this.dialog.addToLog("Searching to the right..."); break;
                                }
                                this.robot.search((byte)move);
                            }
                            
                            this.waitForRobot();
                            //System.out.println("nodex:" + this.currentNode.getPosition().x);
                            this.currentNode = tempNode;
                            //System.out.println("nodex:" + this.currentNode.getPosition().x);
                            pacman.setNode(this.currentNode);                            
                            pacman.setDirection(move);
                            LevelEditor.getInstance().getEditorPanel().checkSize();

                        } while (!tempNode.equals(this.nextNode));
                        
                        this.addDirections();
                        LevelEditor.getInstance().getEditorPanel().checkSize();
                            
                            /*} else {
                                this.dialog.addToLog("Robot tried to move to a node that doesn't exist!");
                                switch(moves[i]) {
                                    case Node.UP: this.dialog.addToLog("Failed to move upwards..."); break;
                                    case Node.DOWN: this.dialog.addToLog("Failed to move downwards..."); break;
                                    case Node.LEFT: this.dialog.addToLog("Failed to move to the left..."); break;
                                    case Node.RIGHT: this.dialog.addToLog("Failed to move to the right..."); break;
                                }
                            }*/                        
                    }
                }
            }
            this.dialog.addToLog("Scan done");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } finally {
            this.stop();
        }
    }
    
    private void addDirections() {
        this.availableDirections = this.robot.getAvaibleDirections();
        //System.out.println("Directions: " + Integer.toBinaryString(this.availableDirections));
        if (this.availableDirections != Node.INVALID_DIRECTION) {
            field.Field field = LevelEditor.getInstance().getEditorPanel().getField();
            Node addedNode = null;
            Point position = this.currentNode.getPosition();
            if ((this.availableDirections & GameCommands.LEFT) == GameCommands.LEFT) {
                if (position.x == 0) {
                    field.offsetNodes(1, 0);
                }
                addedNode = field.addNodeAt(position.x-1, position.y);
                if (addedNode != null) {
                    this.dialog.addToLog("Found node - LEFT");
                    this.open.push(addedNode);
                }
            }
            if ((this.availableDirections & GameCommands.DOWN) == GameCommands.DOWN) {
                addedNode = field.addNodeAt(position.x, position.y+1);
                if (addedNode != null) {
                    this.dialog.addToLog("Found node - DOWN");
                    this.open.push(addedNode);
                }
            }
            if ((this.availableDirections & GameCommands.RIGHT) == GameCommands.RIGHT) {
                addedNode = field.addNodeAt(position.x+1, position.y);
                if (addedNode != null) {
                    this.dialog.addToLog("Found node - RIGHT");
                    this.open.push(addedNode);
                }
            }
            if ((this.availableDirections & GameCommands.UP) == GameCommands.UP) {
                if (position.y == 0) {
                    field.offsetNodes(0, 1);
                }
                addedNode = field.addNodeAt(position.x, position.y-1);
                if (addedNode != null) {
                    this.dialog.addToLog("Found node - UP");
                    this.open.push(addedNode);
                }
            }
        }
    }
    
    public void stop() {
        this.scanDone = true;
        this.robot.close();
        LevelEditor.getInstance().getEditorPanel().setEditable(true);
        this.dialog.explorationDone();
    }
    
    private void waitForRobot() throws InterruptedException {
        while (!this.robot.isDoneMoving()) {
            Thread.sleep(100);
        }
    }
}
