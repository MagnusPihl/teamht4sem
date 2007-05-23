/*
 * Node.java
 *
 * Created on 8. februar 2007, 20:10
 *
 * Company: HT++
 *
 * @author Lau Maack-Krommes
 * @version 1.6
 *
 * 
 * ******VERSION HISTORY******
 * LMK @ 16. april 2007 (v 1.6)
 * Added binary directions, and conversion methods.
 * LMK @ 7. marts 2007 (v 1.5)
 * Added connectedAt() method
 * Added static final DIRECTION_COUNT
 * LMK @ 6. marts 2007 (v 1.5)
 * Fixed takePoints bug returning points even when points are taken
 * LMK @ 5. marts 2007 (v 1.4)
 * Reinstated position :)
 * LMK @ 10. februar 2007 (v 1.3)
 * Fixed isStraightPath() so that it doesn't return true if 3 connections
 * are present.
 * Entity is now stored in node too.
 * LMK @ 9. februar 2007 (v 1.2)
 * Moved position attribute to Field.
 * LMK @ 9. februar 2007 (v 1.1)
 * Added method removeAllConnections()
 * LMK @ 8. februar 2007 (v 1.0)
 * Created
 *
 * Questions:
 * Should exceptions be thrown when directions are invalid?
 * or should i check beforehand?
 * 
 */

package field;

import java.io.Serializable;
import java.awt.*;

public class Node implements Serializable, Comparable {
    
    public static final int UP = 3;
    public static final int RIGHT = 2;
    public static final int DOWN = 1;
    public static final int LEFT = 0;
    public static final int UP_BIN = 1 << UP;
    public static final int RIGHT_BIN = 1 << RIGHT;
    public static final int DOWN_BIN = 1 << DOWN;
    public static final int LEFT_BIN = 1 << LEFT;
    public static final int DIRECTION_COUNT = 4;
    public static final int INVALID_DIRECTION = -1;
        
    private Node[] connectedNodes;
    private boolean pointsTaken;
    private int points;
    private Point position;
    private Entity entity;
    
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
        this.connectedNodes = new Node[DIRECTION_COUNT];
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
        boolean isHorizontalStraight = ((this.connectedNodes[LEFT] != null) && (this.connectedNodes[RIGHT] != null));
        boolean isVerticalStraight = ((this.connectedNodes[UP] != null) && (this.connectedNodes[DOWN] != null));
        
        if (isHorizontalStraight) {
            return isHorizontalStraight ^ ((this.connectedNodes[UP] != null) | (this.connectedNodes[DOWN] != null));
        } else if (isVerticalStraight) {
            return isVerticalStraight ^ ((this.connectedNodes[RIGHT] != null) | (this.connectedNodes[LEFT] != null));
        }
        
        return false;
    }
    
    /**
     * Set position on field.
     *
     * @param position on field.
     */
    public void setPosition(Point position) {
        this.position = position;
    }
    
    /**
     * Get position on field
     *
     * @return point on field
     */
    public Point getPosition() {
        return this.position;
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
     * been taken. If points have already been taken 0 is returned.
     *
     * @return number of points held by the node.
     */
    public int takePoints() {
        if (!this.pointsTaken) {
            this.pointsTaken = true;
            return this.points;
        } else {
            return 0;
        }
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
    public void setPointsTaken(boolean isTaken) {
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
                if (node.connectedNodes[this.getOpposite(direction)] != null) {
                    node.connectedNodes[this.getOpposite(direction)].connectedNodes[direction] = null;
                }
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
     * Remove all connections to surrounding nodes
     */
    public void removeAllConnections() {
        for (int i = 0; i < DIRECTION_COUNT; i++) {
            this.setNodeAt(null, i);
        }
    }
        
    /**
     * Get entity placed on node.
     *
     * @result entity on node.
     */
    public Entity getEntity() {
        return this.entity;
    }
        
    /**
     * Place entity on node.
     *
     * @param entity to place on node.
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
    }
    
    /**
     * Check whether an entity is placed on the node.
     *
     * @result true if an entity is placed on the node.
     */
    public boolean holdsEntity() {
        return (this.entity != null);
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
    
    /**
     * Get the direction in which the supplied node is connected at.
     *
     * @result direction.
     */
    public int connectedAt(Node node) {        
        for (int i = 0; i < DIRECTION_COUNT; i ++) {
            if ((this.connectedNodes[i] != null) && (this.connectedNodes[i].equals(node))) {
                return i;
            }
        }
        
        return INVALID_DIRECTION;
    }
    
    public int compareTo(Object obj) {
        if (obj instanceof Node) {
            Node node = (Node)obj;
            int result = 0;
            
            if ((this.position.x - node.position.x) < 0) {
                result = 1;
            } else if ((this.position.x - node.position.x) > 0) {
                result = 3;
            }
            
            if ((this.position.x - node.position.x) < 0) {
                result -= 2;
            } else if ((this.position.x - node.position.x) > 0) {
                result -= 4;
            }            
            return result;
        }        
        return -1;
    }
    
    public int getBinaryDirections() {
        int directions = 0;
        
        for (int i = 0; i < DIRECTION_COUNT; i++) {
            if (this.connectedNodes[i] != null) {
                directions |= 1 << i;
            }
        }
        
        return directions;
    }        
    
    public int binDirectionToNum(int direction) {
        switch (direction) {
            case DOWN_BIN: return DOWN;
            case UP_BIN: return UP;
            case LEFT_BIN: return LEFT;
            case RIGHT_BIN: return RIGHT;
        }
        return INVALID_DIRECTION;
    } 
    
    public int numToBinDirection(int direction) {
        switch (direction) {
            case DOWN: return DOWN_BIN;
            case UP: return UP_BIN;
            case LEFT: return LEFT_BIN;
            case RIGHT: return RIGHT_BIN;
        }
        return INVALID_DIRECTION;
    }
    
    public boolean equals(Object o) {
        if (o instanceof Node) {
            Node n = (Node)o;
            if ((this.position != null)&&(n.position != null)) {
                if (!this.position.equals(n.position)) {
                    return false;
                }
            } 
            return super.equals(o);
        } else {
            return false;
        }
    }
}