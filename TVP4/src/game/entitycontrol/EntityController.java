/*
 * EntityController.java
 *
 * Created on 5. marts 2007, 11:28
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.3
 *
 *
 * ******VERSION HISTORY******
 * LMK @ 11. april 2007 (v 1.3)
 * init and deinit are no longer abstract.
 * init now resets lastDirection and nextDirection.
 *
 * Magnus Hemmer Pihl @ 7. marts 2007 (v 1.2)
 * Added getRandomDirection
 * Added field lastDirection
 * Added field nextDirection
 *
 * Magnus Hemmer Pihl @ 7. marts 2007 (v 1.1)
 * move() must now return an integer, denoting the direction of movement, or -1 if no movement is made.
 * 
 * Magnus Hemmer Pihl @ 5. marts 2007 (v 1.0)
 * Initial.
 *
 */

package game.entitycontrol;

import field.*;
import game.input.InputManager;
import java.util.Random;

public abstract class EntityController
{
    protected Entity entity;
    protected int lastDirection;
    protected int nextDirection;
    public static final Random RANDOM = new Random();
    
    /** Creates a new instance of EntityController */
    public EntityController(Entity _entity)
    {
        this.entity = _entity;
    }
    
    public abstract void calculateNextMove();
        
    /**
     * Register keys or other resources needed.
     * Resets directions.
     * @param input manager to register keys with
     */
    public void init(InputManager _input) {        
        this.lastDirection = Node.INVALID_DIRECTION;
        this.nextDirection = Node.INVALID_DIRECTION;
    }
        
    /**
     * Does any releasing of resource of needed.
     * @param input manager to unregister keys with
     */
    public void deinit(InputManager _input) {
    
    }
            
    /**
     * Execute move
     */
    public int move() {
        if (this.nextDirection != Node.INVALID_DIRECTION) {  
            if (this.entity.getNode().getNodeAt(this.nextDirection) != null) {
                if (this.entity.getNode().getNodeAt(this.nextDirection).getEntity() == null) {
                    this.entity.setNode(this.entity.getNode().getNodeAt(this.nextDirection));
                    this.entity.setDirection(this.nextDirection);            
                    this.lastDirection = this.nextDirection;
                    return this.nextDirection;
                }
            }
        }
        
        return Node.INVALID_DIRECTION;
    }
    
    /**
     * Simple method that continues movement in the current direction.
     * If the that direction is blocked, a 90 degree turn is attempted.
     * If that is not possible either no backwards movement will be attempted.
     * If no direction has been chosen yet, a random available direction is chosen.
     * If no moves are possible an invalid directions
     */
    public int getNextDirection() {        
        Node[] nodes = this.entity.getNode().getConnectedNodes();
        
        if (this.lastDirection != Node.INVALID_DIRECTION) {            
            if (nodes[this.lastDirection] != null) {
                if (!nodes[this.lastDirection].holdsEntity()) {
                    return this.lastDirection;
                }
            }

            int direction = Node.INVALID_DIRECTION;

            if ((this.lastDirection == Node.UP) || (this.lastDirection == Node.DOWN)) {
                if (RANDOM.nextBoolean()) {
                    direction = Node.LEFT;                 
                } else {
                    direction = Node.RIGHT;
                }                        
            } else {
                if (RANDOM.nextBoolean()) {
                    direction = Node.UP;                 
                } else {
                    direction = Node.DOWN;
                }                        
            }            

            if (nodes[direction] != null) {
                if (!nodes[direction].holdsEntity()) {
                    return direction;
                }
            } 
            
            direction = Node.getOpposite(direction);
            if (nodes[direction] != null) {
                if (!nodes[direction].holdsEntity()) {
                    return direction;
                }
            }

            direction = Node.getOpposite(this.lastDirection);
            if (nodes[direction] != null) {
                if (!nodes[direction].holdsEntity()) {
                    return direction;
                }
            }
        } else {
            for (int i = 0; i < Node.DIRECTION_COUNT; i++) {
                if (nodes[i] != null) {
                    if (!nodes[i].holdsEntity()) {
                        return i;
                    }
                }
            }
        }
        
        return Node.INVALID_DIRECTION;
    }
}
