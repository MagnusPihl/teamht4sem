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
 *
 * Magnus Hemmer Pihl @ 7. marts 2007 (v 1.1)
 * move() must now return an integer, denoting the direction of movement, or -1 if no movement is made.
 * 
 * Magnus Hemmer Pihl @ 5. marts 2007 (v 1.0)
 * Initial.
 *
 */

package game.entitycontrol;

import field.Entity;
import game.input.InputManager;

public abstract class EntityController
{
    protected Entity entity;
    
    /** Creates a new instance of EntityController */
    public EntityController(Entity _entity)
    {
        this.entity = _entity;
    }
    
    public abstract int move();
    public abstract void calculateNextMove();
    public abstract void init(InputManager _input);
    public abstract void deinit(InputManager _input);
}
