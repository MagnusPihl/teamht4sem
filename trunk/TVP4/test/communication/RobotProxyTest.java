/*
 * RobotProxyTest.java
 *
 * Created on 9. maj 2007, 17:14
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 9. maj 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package communication;

import field.*;
import java.util.*;
import junit.framework.*;

/**
 *
 * @author LMK
 */
public class RobotProxyTest extends TestCase{    
    
    private Random rand;
    public static final int NUMBER_OF_TESTS = 1000;
    
    public RobotProxyTest(String testName) {
        super(testName);
        rand = new Random();
    }

    protected void setUp() throws Exception {        
    }

    protected void tearDown() throws Exception {
    }
     
    /**
     * Check that the correct receiver id is retreive from an adressHeader
     * when method getReceiver is run
     *
     * @param addressHeader
     * @result int with 4 least significant bits containing receiver ID.
     */
    public void testGetReceiver() {
        byte posibleDirections;
        byte turn;
        byte output;        
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            turn = (byte)this.rand.nextInt(Node.DIRECTION_COUNT);
            posibleDirections = (byte)(this.rand.nextInt() & 0x0F);
            output = RobotProxy.rotatePosibleDirections(turn, posibleDirections);
            switch (turn) {
                case Node.UP :                
                    //System.out.println("UP");
                    assertEquals(posibleDirections, output);                    
                    break;
                    
                case Node.LEFT:
                    //System.out.println("LEFT");
                    if ((posibleDirections & GameCommands.UP) == GameCommands.UP) {
                        assertEquals(output & GameCommands.RIGHT, GameCommands.RIGHT);
                    }                    
                    if ((posibleDirections & GameCommands.RIGHT) == GameCommands.RIGHT) {
                        assertEquals(output & GameCommands.DOWN, GameCommands.DOWN);
                    }
                    if ((posibleDirections & GameCommands.DOWN) == GameCommands.DOWN) {
                        assertEquals(output & GameCommands.LEFT, GameCommands.LEFT);
                    }
                    if ((posibleDirections & GameCommands.LEFT) == GameCommands.LEFT) {
                        assertEquals(output & GameCommands.UP, GameCommands.UP);
                    }
                    break;
                    
                case Node.RIGHT:
                    //System.out.println("RIGHT");
                    if ((posibleDirections & GameCommands.UP) == GameCommands.UP) {
                        assertEquals(output & GameCommands.LEFT, GameCommands.LEFT);
                    }                    
                    if ((posibleDirections & GameCommands.LEFT) == GameCommands.LEFT) {
                        assertEquals(output & GameCommands.DOWN, GameCommands.DOWN);
                    }
                    if ((posibleDirections & GameCommands.DOWN) == GameCommands.DOWN) {
                        assertEquals(output & GameCommands.RIGHT, GameCommands.RIGHT);
                    }
                    if ((posibleDirections & GameCommands.RIGHT) == GameCommands.RIGHT) {
                        assertEquals(output & GameCommands.UP, GameCommands.UP);
                    }
                    break;
                    
                case Node.DOWN:
                    //System.out.println("DOWN");
                    if ((posibleDirections & GameCommands.UP) == GameCommands.UP) {
                        assertEquals(output & GameCommands.DOWN, GameCommands.DOWN);
                    }                    
                    if ((posibleDirections & GameCommands.LEFT) == GameCommands.LEFT) {
                        assertEquals(output & GameCommands.RIGHT, GameCommands.RIGHT);
                    }
                    if ((posibleDirections & GameCommands.DOWN) == GameCommands.DOWN) {
                        assertEquals(output & GameCommands.UP, GameCommands.UP);
                    }
                    if ((posibleDirections & GameCommands.RIGHT) == GameCommands.RIGHT) {
                        assertEquals(output & GameCommands.LEFT, GameCommands.LEFT);
                    }
                    break;            
            }
        }
    }
}
