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
     * Check that the binary directions from a Node can be rotated and converted
     * correctly to the format described by GameCommands and the application
     * protocol.     
     */
    public void testRotatePossibleDirections() {
        byte possibleDirections;
        byte turn;
        byte output;        
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            turn = (byte)this.rand.nextInt(Node.DIRECTION_COUNT);
            possibleDirections = (byte)(this.rand.nextInt() & 0x07);
            output = RobotProxy.rotatePossibleDirections(turn, possibleDirections);
            
            assertEquals(output & ~0x07, 0);
            switch (turn) {
                case Node.UP :                
                    //System.out.println("UP");
                    if ((possibleDirections & GameCommands.UP) == GameCommands.UP) {
                        assertEquals(output & GameCommands.FORWARD, GameCommands.FORWARD);
                    }                    
                    if ((possibleDirections & GameCommands.RIGHT) == GameCommands.RIGHT) {
                        assertEquals(output & GameCommands.TURN_RIGHT, GameCommands.TURN_RIGHT);
                    }
                    if ((possibleDirections & GameCommands.LEFT) == GameCommands.LEFT) {
                        assertEquals(output & GameCommands.TURN_LEFT, GameCommands.TURN_LEFT);
                    }
                    break;
                    
                case Node.LEFT:
                    //System.out.println("LEFT");
                    if ((possibleDirections & GameCommands.UP) == GameCommands.UP) {
                        assertEquals(output & GameCommands.TURN_RIGHT, GameCommands.TURN_RIGHT);
                    }                    
                    if ((possibleDirections & GameCommands.DOWN) == GameCommands.DOWN) {
                        assertEquals(output & GameCommands.TURN_LEFT, GameCommands.TURN_LEFT);
                    }
                    if ((possibleDirections & GameCommands.LEFT) == GameCommands.LEFT) {
                        assertEquals(output & GameCommands.FORWARD, GameCommands.FORWARD);
                    }
                    break;
                    
                case Node.RIGHT:
                    //System.out.println("RIGHT");
                    if ((possibleDirections & GameCommands.UP) == GameCommands.UP) {
                        assertEquals(output & GameCommands.TURN_LEFT, GameCommands.TURN_LEFT);
                    }                    
                    if ((possibleDirections & GameCommands.DOWN) == GameCommands.DOWN) {
                        assertEquals(output & GameCommands.TURN_RIGHT, GameCommands.TURN_RIGHT);
                    }
                    if ((possibleDirections & GameCommands.RIGHT) == GameCommands.RIGHT) {
                        assertEquals(output & GameCommands.FORWARD, GameCommands.FORWARD);
                    }
                    break;
                    
                case Node.DOWN:
                    //System.out.println("DOWN");
                    if ((possibleDirections & GameCommands.DOWN) == GameCommands.DOWN) {
                        assertEquals(output & GameCommands.FORWARD, GameCommands.FORWARD);
                    }                    
                    if ((possibleDirections & GameCommands.LEFT) == GameCommands.LEFT) {
                        assertEquals(output & GameCommands.TURN_RIGHT, GameCommands.TURN_RIGHT);
                    }
                    if ((possibleDirections & GameCommands.RIGHT) == GameCommands.RIGHT) {
                        assertEquals(output & GameCommands.TURN_LEFT, GameCommands.TURN_LEFT);
                    }
                    break;            
            }
        }                
    }    
    
    /**
     * Check that the binary directions from a Node can be rotated and converted
     * correctly to the format described by GameCommands and the application
     * protocol.     
     */
    public void testDerotatePossibleDirections() {
        byte possibleDirections;
        byte turn;
        byte output;        
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            turn = (byte)this.rand.nextInt(Node.DIRECTION_COUNT);
            possibleDirections = (byte)(this.rand.nextInt() & 0x0F);
            output = RobotProxy.derotatePossibleDirections(turn, possibleDirections);                        
            
            assertEquals(output & ~0x0F, 0);
            switch (turn) {
                case Node.UP :                
                    //System.out.println("UP");
                    if ((possibleDirections & GameCommands.FORWARD) == GameCommands.FORWARD) {
                        assertEquals(output & GameCommands.UP, GameCommands.UP);
                    }                    
                    if ((possibleDirections & GameCommands.TURN_RIGHT) == GameCommands.TURN_RIGHT) {
                        assertEquals(output & GameCommands.RIGHT, GameCommands.RIGHT);
                    }
                    if ((possibleDirections & GameCommands.TURN_LEFT) == GameCommands.TURN_LEFT) {
                        assertEquals(output & GameCommands.LEFT, GameCommands.LEFT);
                    }                    
                    if ((possibleDirections & GameCommands.TURN_NUMBER) == GameCommands.TURN_NUMBER) {
                        assertEquals(output & GameCommands.DOWN, GameCommands.DOWN);
                    }
                    break;
                    
                case Node.LEFT:
                    //System.out.println("LEFT");
                    if ((possibleDirections & GameCommands.TURN_RIGHT) == GameCommands.TURN_RIGHT) {
                        assertEquals(output & GameCommands.UP, GameCommands.UP);
                    }                    
                    if ((possibleDirections & GameCommands.TURN_LEFT) == GameCommands.TURN_LEFT) {
                        assertEquals(output & GameCommands.DOWN, GameCommands.DOWN);
                    }
                    if ((possibleDirections & GameCommands.FORWARD) == GameCommands.FORWARD) {
                        assertEquals(output & GameCommands.LEFT, GameCommands.LEFT);
                    }                   
                    if ((possibleDirections & GameCommands.TURN_NUMBER) == GameCommands.TURN_NUMBER) {
                        assertEquals(output & GameCommands.RIGHT, GameCommands.RIGHT);
                    }
                    break;
                    
                case Node.RIGHT:
                    //System.out.println("RIGHT");
                    if ((possibleDirections & GameCommands.TURN_LEFT) == GameCommands.TURN_LEFT) {
                        assertEquals(output & GameCommands.UP, GameCommands.UP);
                    }                    
                    if ((possibleDirections & GameCommands.TURN_RIGHT) == GameCommands.TURN_RIGHT) {
                        assertEquals(output & GameCommands.DOWN, GameCommands.DOWN);
                    }
                    if ((possibleDirections & GameCommands.FORWARD) == GameCommands.FORWARD) {
                        assertEquals(output & GameCommands.RIGHT, GameCommands.RIGHT);
                    }                                     
                    if ((possibleDirections & GameCommands.TURN_NUMBER) == GameCommands.TURN_NUMBER) {
                        assertEquals(output & GameCommands.LEFT, GameCommands.LEFT);
                    }
                    break;
                    
                case Node.DOWN:
                    //System.out.println("DOWN");
                    if ((possibleDirections & GameCommands.FORWARD) == GameCommands.FORWARD) {
                        assertEquals(output & GameCommands.DOWN, GameCommands.DOWN);
                    }                    
                    if ((possibleDirections & GameCommands.TURN_RIGHT) == GameCommands.TURN_RIGHT) {
                        assertEquals(output & GameCommands.LEFT, GameCommands.LEFT);
                    }
                    if ((possibleDirections & GameCommands.TURN_LEFT) == GameCommands.TURN_LEFT) {
                        assertEquals(output & GameCommands.RIGHT, GameCommands.RIGHT);
                    }                 
                    if ((possibleDirections & GameCommands.TURN_NUMBER) == GameCommands.TURN_NUMBER) {
                        assertEquals(output & GameCommands.UP, GameCommands.UP);
                    }
                    break;            
            }
        }                
    }    
    
    /**
     * Check that rotate and derotate can inverse each others content except
     * the back direction.
     */
    public void testRotatation() {
        byte possibleDirections;
        byte turn;
        byte output;        
        System.out.println("urdl - bflr");
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            turn = (byte)this.rand.nextInt(Node.DIRECTION_COUNT);
            possibleDirections = (byte)(this.rand.nextInt() & 0x07);
            output = RobotProxy.derotatePossibleDirections(turn, RobotProxy.rotatePossibleDirections(turn, possibleDirections));
            
            System.out.println(turn);      
            System.out.println(Integer.toBinaryString(possibleDirections) + " - " + Integer.toBinaryString(output));
            
            switch (turn) {
                case Node.UP :                
                    //System.out.println("UP");                    
                    assertEquals(possibleDirections & ~GameCommands.DOWN, output);
                    break;
                    
                case Node.LEFT:
                    //System.out.println("LEFT");
                    assertEquals(possibleDirections & ~GameCommands.RIGHT, output);
                    break;
                    
                case Node.RIGHT:
                    //System.out.println("RIGHT");
                    assertEquals(possibleDirections & ~GameCommands.LEFT, output);
                    break;
                    
                case Node.DOWN:
                    //System.out.println("DOWN");
                    assertEquals(possibleDirections & ~GameCommands.UP, output);
                    break;            
            }
        }                
    }    
}
