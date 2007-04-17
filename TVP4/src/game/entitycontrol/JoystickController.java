/*
 * JoystickController.java
 *
 * Created on 14. marts 2007, 11:33
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.1
 *
 * *************NOTE!!**************
 * JoystickController will ONLY work if the JXInput library is included in the project, and Windows has access to "jxinput.dll".
 * Get both at http://www.hardcode.de/jxinput/
 *
 * ******VERSION HISTORY******
 *
 * Magnus Hemmer Pihl @ 17. april 2007 (v 1.1)
 * Added alternative constructor to specify a joystick ID to use.
 * Altered input code so that direction is only updated when the entity is about to move.
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
    
    int desiredDir;
    int joyID;
    
    /** Creates a new instance of KeyboardController */
    public JoystickController(Entity _entity)
    {
        super(_entity);
        
        this.desiredDir = entity.getDirection();
        this.joyID = 0;
    }
    
    public JoystickController(Entity _entity, int _joyID)
    {
        super(_entity);
        
        this.desiredDir = entity.getDirection();
        this.joyID = _joyID;
    }

    public int move()
    {
        entity.setDirection(this.desiredDir);
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
            this.desiredDir = Node.UP;
        if(joystick.getAxis(0).getValue() > 0.5 && node.getNodeAt(Node.RIGHT)!=null)
            this.desiredDir = Node.RIGHT;
        if(joystick.getAxis(1).getValue() > 0.5 && node.getNodeAt(Node.DOWN)!=null)
            this.desiredDir = Node.DOWN;
        if(joystick.getAxis(0).getValue() < -0.5 && node.getNodeAt(Node.LEFT)!=null)
            this.desiredDir = Node.LEFT;
    }
    
    public void init(InputManager _input)
    {
        this.joystick = JXInputManager.getJXInputDevice(this.joyID);
    }
    
    public void deinit(InputManager _input)
    {
        this.joystick = null;
    }
}