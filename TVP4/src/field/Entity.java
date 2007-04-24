/*
 * Entity.java
 *
 * Created on 18. februar 2007, 13:19
 *
 * Company: HT++
 *
 * @author Mikkel Brøndsholm Nielsen
 * @version 1.3
 *
 *
 * ******VERSION HISTORY******
 * Magnus Hemmer Pihl @ 24. april 2007 (v 1.4)
 * Corrected null-pointer exception in isMoving().
 *
 * LMK @ 23. marts 2007 (v 1.3)
 * Fixed error when trying to place to entity on a node already holding an entity. 
 * The old entity now has it's node reference removed. 
 * Fixed set direction error. Only valid directions can now be set.
 * Fixed error when a creating an entity with a node already containing another entity. 
 *
 * LMK @ 7. marts 2007 (v 1.2)
 * Position is no longer held by Entity instead a reference to Node is held.
 * Mikkel Nielsen @ 18. februar 2007 (v 1.1)
 * isPacman removed.
 *
 * Mikkel Nielsen @ 18. februar 2007 (v 1.0)
 * Class created
 *
 */

package field;

import game.entitycontrol.EntityController;
import java.awt.Point;
import java.io.*;

public class Entity implements Serializable {
    
    private boolean isMoving;
    private Node node;
    private int direction;
    private int speed;
    private int ID;
    private EntityController controller;
    
    /** Creates a new instance of Entity */
    public Entity(Node _node, int _ID) {
        this.isMoving = false;
        this.setNode(_node);
        this.direction = Node.UP;
        this.speed = 0;
        this.ID = _ID;
    }
    
    
    public void setNode(Node _node) {
        if (this.node != null) {
            this.node.setEntity(null);
        }        
        if (_node != null) {
            if (_node.getEntity() != null) {
                _node.getEntity().node = null;
            }
            _node.setEntity(this);
        }
        
        this.node = _node;
    }
        
    public void setDirection(int dir) {
        if (Node.isValidDirection(dir)) {
            this.direction = dir;
        }    
    }

    public Node getNode(){return this.node;}    
    public void setIsMoving(boolean _isMoving){this.isMoving = _isMoving;}
    public void setSpeed(int _speed){this.speed = _speed;}
    public void setID(int _ID){this.ID = _ID;}
    
    //Accessors
    public boolean isMoving()
    {
        if(node != null)
        {
            if(this.node.getNodeAt(this.direction) == null)
                this.isMoving = false;
            else
                this.isMoving = true;
            return this.isMoving;
        }
        return false;
    }
    public int getDirection(){return this.direction;}
    public Point getPosition(){return this.node.getPosition();}
    public int getSpeed(){return this.speed;}
    public int getID(){return this.ID;}
    public void setController(EntityController _controller){this.controller = _controller;}
    public EntityController getController(){return this.controller;}
}
