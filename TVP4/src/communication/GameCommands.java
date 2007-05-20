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
    public static final byte UP = 0x08;
    public static final byte RIGHT = 0x04;
    public static final byte DOWN = 0x02;
    public static final byte LEFT = 0x01;
    //***
    public static final byte TURN_RIGHT = 0x01;
    public static final byte TURN_LEFT = 0x02;
    public static final byte FORWARD = 0x04;
    public static final byte TURN_NUMBER = 0x08;
    //***
    public static final byte DISCOVER = 0x40;
    public static final byte MOVE_DONE = 0x10;
    public static final byte LIGHT_ON = 0x11;
    public static final byte LIGHT_OFF = 0x12;
    public static final byte BEEP = 0x13;
    public static final byte CALIBRATE = 0x30;
    public static final byte NOP = 0x00;
    public static final byte SEARCH_NODE = 0x20;        
}
