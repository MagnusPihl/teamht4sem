/*
 * GameCommands.java
 *
 * Created on 23. april 2007, 12:21
 *
 * Company: HT++
 *
 * @author thh
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * thh @ 23. april 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package communication;


public class GameCommands {
    private static GameCommands instance = new GameCommands();
    public static final byte UP = 0x08;
    public final static byte DOWN = 0x02;
    public final static byte LEFT = 0x01;
    public final static byte RIGHT = 0x04;
    public final static byte MOVE_UP = 0x01;
    public final static byte MOVE_RIGHT = 0x02;
    public final static byte MOVE_DOWN = 0x03;
    public final static byte MOVE_LEFT = 0x04;
    public final static byte MOVE_UP_DISCOVER = 0x41;
    public final static byte MOVE_RIGHT_DISCOVER = 0x42;
    public final static byte MOVE_DOWN_DISCOVER = 0x43;
    public final static byte MOVE_LEFT_DISCOVER = 0x44;
    public final static byte DISCOVER = 0x40;
    public final static byte MOVE_DONE = 0x10;
    public final static byte LIGHT_ON = 0x11;
    public final static byte LIGHT_OFF = 0x12;
    public final static byte BEEP = 0x13;
    public final static byte CALIBRATE = 0x30;
    public final static byte NOP = 0x00;
    public final static byte SEARCH_NODE = 0x20;
    
    /** Creates a new instance of GameCommands */
    private GameCommands() {
    }
    
    public static GameCommands getInstance() {
        return instance;
    }
    
}
