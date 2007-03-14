/*
 * JoystickController.java
 *
 * Created on 14. marts 2007, 11:33
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.0
 *
 * *************NOTE!!**************
 * JoystickController will ONLY work if the JXInput library is included in the project, and Windows has access to "jxinput.dll".
 * Get both at http://www.hardcode.de/jxinput/
 *
 * ******VERSION HISTORY******
 *
 * Magnus Hemmer Pihl @ 14. marts 2007 (v 1.0)
 * Initial creation.
 *
 */

package game.entitycontrol;

import de.hardcode.jxinput.JXInputDevice;
import de.hardcode.jxinput.JXInputManager;
import field.Entity;
import field.Node;
import game.PacmanApp;
import game.input.InputManager;

public class JoystickController extends EntityController
{
    JXInputDevice joystick;
    
    /** Creates a new instance of KeyboardController */
    public JoystickController(Entity _entity)
    {
        super(_entity);
    }

    public int move()
    {
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
        return -1;
    }

    public void calculateNextMove()
    {
        Node node = PacmanApp.getInstance().getGameScene().getField().getNodeAt(this.entity.getPosition());
        JXInputManager.updateFeatures();
        if(joystick.getAxis(1).getValue() < -0.5 && node.getNodeAt(Node.UP)!=null)
            this.entity.setDirection(Node.UP);
        if(joystick.getAxis(0).getValue() > 0.5 && node.getNodeAt(Node.RIGHT)!=null)
            this.entity.setDirection(Node.RIGHT);
        if(joystick.getAxis(1).getValue() > 0.5 && node.getNodeAt(Node.DOWN)!=null)
            this.entity.setDirection(Node.DOWN);
        if(joystick.getAxis(0).getValue() < -0.5 && node.getNodeAt(Node.LEFT)!=null)
            this.entity.setDirection(Node.LEFT);
    }
    
    public void init(InputManager _input)
    {
        this.joystick = JXInputManager.getJXInputDevice(0);
    }
    
    public void deinit(InputManager _input)
    {
        this.joystick = null;
    }
}
