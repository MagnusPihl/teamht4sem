/*
 * JoystickController.java
 *
 * Created on 14. marts 2007, 11:33
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.2
 *
 * *************NOTE!!**************
 * JoystickController will ONLY work if the JXInput library is included in the project, and Windows has access to "jxinput.dll".
 * Get both at http://www.hardcode.de/jxinput/
 *
 * ******VERSION HISTORY******
 * LMK @ 25. April 2007 (v 1.2)
 * Changed nextDirection to nextDirection to match other controller classes
 * Removed the move function and moved it to EntityController
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
    
    int joyID;
    
    /** Creates a new instance of KeyboardController */
    public JoystickController(Entity _entity)
    {
        super(_entity);
        
        this.joyID = 0;
    }
    
    public JoystickController(Entity _entity, int _joyID)
    {
        super(_entity);
        
        this.joyID = _joyID;
    }
    
    public void calculateNextMove()
    {
        Node node = PacmanApp.getInstance().getGameScene().getField().getNodeAt(this.entity.getPosition());
        JXInputManager.updateFeatures();
        if(joystick.getAxis(1).getValue() < -0.5 && node.getNodeAt(Node.UP)!=null)
            this.nextDirection = Node.UP;
        if(joystick.getAxis(0).getValue() > 0.5 && node.getNodeAt(Node.RIGHT)!=null)
            this.nextDirection = Node.RIGHT;
        if(joystick.getAxis(1).getValue() > 0.5 && node.getNodeAt(Node.DOWN)!=null)
            this.nextDirection = Node.DOWN;
        if(joystick.getAxis(0).getValue() < -0.5 && node.getNodeAt(Node.LEFT)!=null)
            this.nextDirection = Node.LEFT;
    }
    
    public void init(InputManager _input) {
        super.init(_input);
        this.joystick = JXInputManager.getJXInputDevice(this.joyID);
    }    
}