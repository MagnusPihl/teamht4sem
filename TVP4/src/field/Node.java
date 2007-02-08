/*
 * Node.java
 *
 * Created on 8. februar 2007, 20:10
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 8. februar 2007 (v 1.0)
 * Created
 *
 * Questions:
 * Should exceptions be thrown when directions are invalid?
 * or should i check beforehand?
 * 
 */

package field;

import java.awt.Point;
import java.io.Serializable;

public class Node implements Serializable {
    
    public static final int UP = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;
    public static final int INVALID_DIRECTION = -1;
    
    private Point position;
    private Node[] connectedNodes;
    private boolean pointsTaken;
    private int points;
    
    /** 
     * Create new node with specified position, no connections and 0 points held
     *
     * @param position on the playing field.     
     */
    public Node(Point position) {       
        this(position, null, null, null, null, 0);
    }
    
    /**
     * Create new node with specified position, connected nodes, and points
     *
     * @param position on the playing field.
     * @param leftNode node on the left of the node to be created
     * @param rightNode node on the right of the node to be created
     * @param upNode node on top of the node to be created
     * @param downNode node on the bottom of the node to be created
     * @param points points to be held by the node     
     */
    public Node(Point position, Node leftNode, Node rightNode, Node upNode, Node downNode, int points) {
        this.position = position;
        this.connectedNodes = new Node[4];
        this.setNodeAt(leftNode, LEFT);
        this.setNodeAt(rightNode, RIGHT);
        this.setNodeAt(upNode, UP);
        this.setNodeAt(downNode, DOWN);
        this.pointsTaken = false;
        this.points = points;
    }
        
    /**
     * Check whether the node is a straight path (green marker) or crossroad
     * or turning point(yellow marker).
     * 
     * @return true if the path is straight.
     */
    public boolean isStraightPath() {
        return (((this.connectedNodes[LEFT] != null) && (this.connectedNodes[RIGHT] != null)) 
                ^ ((this.connectedNodes[UP] != null) && (this.connectedNodes[DOWN] != null)));
    }
    
    /**
     * Get the position on the playing field.
     *
     * @return The position on the playing field.
     */
    public Point getPosition() {
        return this.position;
    }
         
    /**
     * Set the position on the playing field.
     *
     * @param position
     */
    public void setPosition(Point position) {
        this.position = position;
    }
    
    /**
     * Set the number of points held by the node.
     *
     * @param points that should be held by the node.
     */
    public void setPoints(int points) {
        this.points = points;
    }
    
    /**
     * Get the number of points held by the node.
     *
     * @return number of points held by the node.
     */
    public int getPoints() {
        return this.points;
    }
    
    /**
     * Get the number of points allocated to the node and mark them as haven
     * been taken.
     *
     * @return number of points held by the node.
     */
    public int takePoints() {
        this.pointsTaken = true;
        return this.points;
    }
    
    /**
     * Check whether the points allocated to the node have been taken.
     * @return true if the points allocated to the node have been taken. False 
     * otherwise.
     */
    public boolean pointsTaken() {
        return this.pointsTaken;
    }
    
    /**
     * Set whether the points allocated to the node has been taken.
     *
     * @param isTaken Should be true if the points are taken. False otherwise.
     */
    public void setPointTaken(boolean isTaken) {
        this.pointsTaken = isTaken;
    }
    
    /**
     * Set connection to node in the supplied direction. If NULL is supplied
     * the current connection will be severed. If the node is valid the
     * connection will be severed to the old node and be added to the new node.
     * If an invalid direction is supplied no action will be taken.
     * 
     * @param node the node to connect to. If NULL the connection will be 
     * removed.
     * @param direction the relative direction where the node should be 
     * connected. No action will be taken if an invalid direction is passed
     * no exception will be thrown either (should one be thrown?).
     * 
     */
    public void setNodeAt(Node node, int direction) {        
        if (this.isValidDirection(direction)) {
            if (this.connectedNodes[direction] != null) {
                this.connectedNodes[direction].connectedNodes[this.getOpposite(direction)] = null;
            }
            
            this.connectedNodes[direction] = node;
            
            if (node != null) {
                node.connectedNodes[this.getOpposite(direction)] = this;
            }
        }
    }
    
    /**
     * Get the whole list of connected nodes.
     *
     * @return Node[] containing all the connected nodes.
     * @see getNodeAt(int) for an alternate means of accessing the list.
     * (Should probably be removed or restricted for better encapsulation)
     */
    public Node[] getConnectedNodes() {
        return this.connectedNodes;
    }
        
    /**
     * Set the node connected to the left of the current node. 
     *
     * @param node The node you wish to connect to the left of the callee node. 
     * If null is supplied the current connection is removed.
     *
     * @see #setNodeAt(Node, int) (Can be removed, as long as setNodeAt is left)
     */
    public void setLeftNode(Node node) {
        this.setNodeAt(node, LEFT);
    }
    
    /**
     * Set the node connected to the right of the current node. 
     *
     * @param node The node you wish to connect to the right of the callee node. 
     * If null is supplied the current connection is removed.
     *
     * @see #setNodeAt(Node, int) (Can be removed, as long as setNodeAt is left)
     */
    public void setRightNode(Node node) {
        this.setNodeAt(node, RIGHT);
    }
    
    /**
     * Set the node connected to the top of the current node. 
     *
     * @param node The node you wish to connect to the top of the callee node. 
     * If null is supplied the current connection is removed.
     *
     * @see #setNodeAt(Node, int) (Can be removed, as long as setNodeAt is left)
     */
    public void setUpNode(Node node) {
        this.setNodeAt(node, UP);
    }
    
    /**
     * Set the node connected to the bottom of the current node. 
     *
     * @param node The node you wish to connect at the bottom of the callee node. 
     * If null is supplied the current connection is removed.
     *
     * @see #setNodeAt(Node, int) (Can be removed, as long as setNodeAt is left)
     */
    public void setDownNode(Node node) {
        this.setNodeAt(node, DOWN);
    }
        
    /**
     * Get node associated with the supplied direction. Can be used to cycle
     * through the connected nodes.
     *
     * @param direction the relative direction of node you wish to fetch.
     * @return The node connected in the supplied direction of the callee node. 
     * If no node is connected here, NULL is returned. NULL is also returned
     * if an invalid direction is supplied.
     */
    public Node getNodeAt(int direction) {        
        if (this.isValidDirection(direction)) {
            return this.connectedNodes[direction];
        }
        
        return null;
    }
    
    /**
     * Get the node connected to the left of the current node. 
     *
     * @return The node connected to the left of the callee node. If no node
     * is connected here, NULL is returned.
     *
     * @see #getNodeAt(int) (Can be removed, as long as getNodeAt is left)
     */
    public Node getLeftNode() {
        return this.connectedNodes[LEFT];
    }
    
    /**
     * Get the node connected to the right of the current node. 
     *
     * @return The node connected to the right of the callee node. If no node
     * is connected here, NULL is returned. NULL is also returned
     * if an invalid direction is supplied.
     *
     * @see #getNodeAt(int) (Can be removed, as long as getNodeAt is left)
     */
    public Node getRightNode() {
        return this.connectedNodes[RIGHT];
    }
    
    /**
     * Get the node connected to the top of the current node. 
     *
     * @return The node connected to the top of the callee node. If no node
     * is connected here, NULL is returned. NULL is also returned
     * if an invalid direction is supplied.
     *
     * @see #getNodeAt(int) (Can be removed, as long as getNodeAt is left)
     */
    public Node getUpNode() {
        return this.connectedNodes[UP];
    }
    
    /**
     * Get the node connected to the bottom of the current node. 
     *
     * @return The node connected to the bottom of the callee node. If no node
     * is connected here, NULL is returned. NULL is also returned
     * if an invalid direction is supplied.
     *
     * @see #getNodeAt(int) (Can be removed, as long as getNodeAt is left)
     */
    public Node getDownNode() {
        return this.connectedNodes[DOWN];
    }
    
    /**
     * Retrieve the index associated with the direction opposite of the input 
     *
     * @param direction the direction you wish to invert.
     * @return int containing the index of the opposite direction of the supplied.
     * If the supplied direction is invalid INVALID_DIRECTION is returned.     
     */
    public static int getOpposite(int direction) {
        switch(direction) {
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
            case UP: return DOWN;
            case DOWN: return UP;
            default: return -1;
        }
    }
    
    /**
     * Check whether a direction is valid.
     * 
     * @param direction the direction to be checked.
     * @return true if the direction is valid.
     */
    public static boolean isValidDirection(int direction) {
        // Another possible algorithm.
        // (0 <= direction) && (direction < this.connectedNodes.length)
        switch(direction) {
            case LEFT: return true;
            case RIGHT: return true;
            case UP: return true;
            case DOWN: return true;
            default: return false;
        }
    }
}