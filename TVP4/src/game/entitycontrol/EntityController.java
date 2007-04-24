/*
 * EntityController.java
 *
 * Created on 5. marts 2007, 11:28
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.1
 *
 *
 * ******VERSION HISTORY******
 * Magnus Hemmer Pihl @ 7. marts 2007 (v 1.1)
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
        this.lastDirection = Node.INVALID_DIRECTION;
        this.nextDirection = Node.INVALID_DIRECTION;
    }
    
    public abstract int move();
    public abstract void calculateNextMove();
    public abstract void init(InputManager _input);
    public abstract void deinit(InputManager _input);
    
    /**
     * Simple method that continues movement in the current direction.
     * If the that direction is blocked, a 90 degree turn is attempted.
     * If that is not possible either no backwards movement will be attempted.
     * If no direction has been chosen yet, a random available direction is chosen.
     * If no moves are possible an invalid directions
     */
    public int getNextDirection() {        
        if (this.lastDirection != Node.INVALID_DIRECTION) {
            if (this.entity.getNode().getNodeAt(this.lastDirection) != null) {
                return this.lastDirection;
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
            Node current = this.entity.getNode().getNodeAt(direction);

            if (current != null) {
                if (current.getEntity() == null) {
                    return direction;
                }
            } 
            
            current = this.entity.getNode().getNodeAt(Node.getOpposite(direction));
            if (current != null) {
                if (current.getEntity() == null) {
                    return Node.getOpposite(direction);
                }
            }

            current = this.entity.getNode().getNodeAt(Node.getOpposite(this.lastDirection));
            if (current != null) {
                if (current.getEntity() == null) {
                    return this.lastDirection;
                }
            }
        } else {
            for (int i = 0; i < Node.DIRECTION_COUNT; i++) {
                if (this.entity.getNode().getNodeAt(i) != null) {
                    return i;
                }
            }
        }
        
        return Node.INVALID_DIRECTION;
    }
}
