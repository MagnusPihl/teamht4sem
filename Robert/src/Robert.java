/*
 * Robert.java
 *
 * Created on 14. februar 2007, 11:10
 *
 */


import java.io.IOException;
import robot.Controller;

/**
 *
 * @author Troels Hagman-Hansen, 5711
 */
public class Robert {
    
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws InterruptedException, IOException{
        /*
         * ops = new blackOps();
         *
         * while(true) {
         * ;
         * }*/
        
        Controller noget = new Controller();
        noget.run();
    }
    
}
