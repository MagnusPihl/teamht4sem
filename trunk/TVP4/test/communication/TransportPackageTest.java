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
public class TransportPackageTest extends TestCase {    
    private Random rand;
    public static final int NUMBER_OF_TESTS = 1000;
    
    public TransportPackageTest(String testName) {
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
    public void testCreateAcknowledgeHeader() {
        int header;
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            header = rand.nextInt();
            assertEquals(header | 0x80, TransportPackage.createAcknowledgeHeader(header));
        }
    }
    
    /**
     * Check that a correct sequence number is returned.     
     */
    public void testGetReceiver() {
        int header;
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            header = rand.nextInt();
            assertEquals(header & 0x7F, TransportPackage.getSequenceNumber(header));            
        }
    }
    
    /**
     * Check that the correct type is returned from getType;
     */
    public void testGetType() {        
        int header;
        int expected;
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            header = rand.nextInt();
            if ((header & 0x80) == 0x80) {
                expected = TransportSocket.RECEIPT;
            } else {                
                expected = TransportSocket.DATA;
            }
            assertEquals(expected, TransportPackage.getType(header));            
        }
    }
}