/*
 * Main.java
 *
 * Created on 14. februar 2007, 11:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package robert;

import josx.rcxcomm.*;

/**
 *
 * @author bamsi
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Hejsa");
        Tower t = new Tower();
        t.open();
        t.write(new byte[] {(byte) 0x10},1);
        t.close();
    }
    
}