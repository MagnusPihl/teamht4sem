/*
 * ReplayController.java
 *
 * Created on 7. marts 2007, 09:35
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * Magnus Hemmer Pihl @ 7. marts 2007 (v 1.0)
 * Initial.
 *
 */

package game.entitycontrol;

import field.Entity;
import field.Node;
import game.PacmanApp;
import game.input.InputManager;
import java.awt.Point;
import java.util.LinkedList;

public class ReplayController extends EntityController
{
    private LinkedList list;
    
    /** Creates a new instance of ReplayController */
    public ReplayController(Entity _entity, LinkedList _list)
    {
        super(_entity);
        this.list = _list;
    }

    public int move()
    {
        if(this.list.size() > 0)
        {
            int dir = (Integer)this.list.remove();
            if(Node.isValidDirection(dir))
            {
                this.entity.setDirection(dir);
                    Point pos = this.entity.getPosition();
                    Node current_node = PacmanApp.getInstance().getGameScene().getField().getNodeAt(pos);
                    Node next_node = current_node.getNodeAt(this.entity.getDirection());
                    if(next_node != null)
                        if(next_node.getEntity() == null)
                        {
                            current_node.setEntity(null);
                            next_node.setEntity(this.entity);
                            this.entity.setPosition(next_node.getPosition());
                            return this.entity.getDirection();
                        }
            }
        }
        return -1;
    }

    public void calculateNextMove()
    {
    }

    public void init(InputManager _input)
    {
    }

    public void deinit(InputManager _input)
    {
    }
}
