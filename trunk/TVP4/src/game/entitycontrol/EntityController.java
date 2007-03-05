/*
 * EntityController.java
 *
 * Created on 5. marts 2007, 11:28
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * Magnus Hemmer Pihl @ 5. marts 2007 (v 1.0)
 * Initial.
 *
 */

package game.entitycontrol;

import field.Entity;

public abstract class EntityController
{
    Entity entity;
    
    /** Creates a new instance of EntityController */
    public EntityController(Entity _entity)
    {
        this.entity = _entity;
    }
    
    public abstract void move();
    public abstract void calculateNextMove();
    public abstract void init();
    public abstract void deinit();
}