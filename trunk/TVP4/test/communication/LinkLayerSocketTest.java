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
public class LinkLayerSocketTest extends TestCase {    
    private Random rand;
    public static final int NUMBER_OF_TESTS = 1000;
    
    public LinkLayerSocketTest(String testName) {
        super(testName);
        rand = new Random();
    }

    protected void setUp() throws Exception {        
    }

    protected void tearDown() throws Exception {
    }
                    
    /**
     * Check that a correct acknowledge header is created.
     */
    public void testChecksum() {
        byte[] buffer = new byte[LinkLayerSocket.PACKET_SIZE];
        int expected;
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            rand.nextBytes(buffer);
            LinkLayerSocket.addChecksum(buffer);            
            assertTrue(LinkLayerSocket.checksumIsValid(buffer));            
        }        
    }    
}