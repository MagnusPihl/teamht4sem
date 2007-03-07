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
        this.list = new LinkedList();
        for(int i=0; i<_list.size(); i++)
            this.list.add(_list.get(i));
    }

    public int move()
    {
        if(this.list.size() > 0)
        {
            int dir = (Integer)this.list.remove();
            if(Node.isValidDirection(dir))
            {
                this.entity.setDirection(dir);
                Node current_node = this.entity.getNode();
                Node next_node = current_node.getNodeAt(this.entity.getDirection());
                if(next_node != null)
                    if(next_node.getEntity() == null)
                    {
                        current_node.setEntity(null);
                        next_node.setEntity(this.entity);
                        this.entity.setNode(next_node);
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
