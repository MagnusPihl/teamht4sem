/*
 * EntityTest.java
 * JUnit based test
 *
 * Created on 23. februar 2007, 09:05
 *
 */

package communication;

import java.util.*;
import junit.framework.*;

/**
 *
 * @author Mikkel Nielsen
 */
public class NetworkDatagramTest extends TestCase {    
    private Random rand;
    public static final int NUMBER_OF_TESTS = 1000;
    
    public NetworkDatagramTest(String testName) {
        super(testName);
        rand = new Random();
    }

    protected void setUp() throws Exception {        
    }

    protected void tearDown() throws Exception {
    }
                    
    /**
     * Check that the correct sender id is retreive from an adressHeader
     * when method getSender is run
     */
    public void testGetSender() {
        int expected;
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            expected = (rand.nextInt() & 0x0F);
            assertEquals(expected, NetworkDatagram.getSender(expected << 4));            
        }
    }
    
    /**
     * Check that the correct receiver id is retreive from an adressHeader
     * when method getReceiver is run
     *
     * @param addressHeader
     * @result int with 4 least significant bits containing receiver ID.
     */
    public void testGetReceiver() {
        int expected;
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            expected = (rand.nextInt() & 0x0F);
            assertEquals(expected, NetworkDatagram.getReceiver(expected));            
        }
    }
    
    /**
     * Check that a correct addressHeader is constructed from a receiver and
     * sender id.
     */
    public void testGetAdressHeader() {
        int sender;
        int receiver;
        int expected;
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            sender = rand.nextInt();
            receiver = rand.nextInt();
            expected = ((sender & 0x0F) << 4) | (receiver & 0x0F);
            assertEquals(expected, NetworkDatagram.getAdressHeader(sender, receiver));            
        }
    }
}