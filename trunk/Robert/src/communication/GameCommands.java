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
    public static byte UP = 0x08;
    public static byte DOWN = 0x02;
    public static byte LEFT = 0x01;
    public static byte RIGHT = 0x04;
    public static byte MOVE_UP = 0x00;
    public static byte MOVE_RIGHT = 0x01;
    public static byte MOVE_DOWN = 0x02;
    public static byte MOVE_LEFT = 0x03;
    public static byte MOVE_UP_DISCOVER = 0x00;
    public static byte MOVE_RIGHT_DISCOVER = 0x01;
    public static byte MOVE_DOWN_DISCOVER = 0x02;
    public static byte MOVE_LEFT_DISCOVER = 0x03;
    public static byte DISCOVER = 0x40;
    public static byte MOVE_DONE = 0x10;
    public static byte LIGHT_ON = 0x11;
    public static byte LIGHT_OFF = 0x12;
    public static byte BEEP = 0x13;
    public static byte CALIBRATE = 0x30;
    public static byte NOP = -0x01;
    
    /** Creates a new instance of GameCommands */
    public GameCommands() {
    }
    
    public static GameCommands getInstance() {
        return instance;
    }
    
}
