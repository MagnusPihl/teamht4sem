/*
 * FieldTest.java
 *
 * Created on 9. februar 2007, 10:05
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.1
 *
 *
 * ******VERSION HISTORY******
 * Magnus Hemmer Pihl @ 11. februar 2007 (v 1.2)
 * Corrected expected return value from getSize().
 *
 * Magnus Hemmer Pihl @ 11. februar 2007 (v 1.1)
 * Updated to match Field class version 1.3
 *
 * Magnus Hemmer Pihl @ 9. februar 2007 (v 1.0)
 * Created
 *
 */

package field;

import java.awt.Dimension;
import junit.framework.*;
import java.awt.Point;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.Serializable;

public class FieldTest extends TestCase {
    
    private Field instance;
    
    public FieldTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        this.instance = new Field();
        this.instance.addNodeAt(0,0);
        this.instance.addNodeAt(1,0);
        this.instance.addNodeAt(2,0);
        this.instance.addNodeAt(2,1);
        this.instance.addNodeAt(2,2);
        this.instance.addNodeAt(1,2);
        this.instance.addNodeAt(0,2);
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of addNodeAt method, of class field.Field.
     */
    public void testAddNodeAt() {
        System.out.println("addNodeAt");
        
        this.instance.addNodeAt(0,1,2);
        Node node = this.instance.getNodeAt(0,1);
        
        assertEquals(node.getUpNode(), this.instance.getNodeAt(0,0));
        assertEquals(node.getPoints(), 2);
    }

    /**
     * Test of removeNodeAt method, of class field.Field.
     */
    public void testRemoveNodeAt() {
        System.out.println("removeNodeAt");
        
        this.instance.removeNodeAt(1,0);
        Node node1 = this.instance.getNodeAt(0,0);
        Node node2 = this.instance.getNodeAt(2,0);
        
        assertEquals(node1.getRightNode(), null);
        assertEquals(node2.getLeftNode(), null);
        assertEquals(this.instance.getNodeAt(1,0), null);
    }

    /**
     * Test of getNodeAt method, of class field.Field.
     */
    public void testGetNodeAt() {
        System.out.println("getNodeAt");
        
        assertEquals(this.instance.getNodeAt(1,2).getRightNode(), this.instance.getNodeAt(2,2));
    }
    
    /**
     * Test of getSize method, of class field.Field.
     */
    public void testGetSize() {
        System.out.println("getSize");
        
        assertEquals(this.instance.getSize(), new Dimension(3,3));
    }
}