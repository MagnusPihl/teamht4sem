/*
 * Random.java
 *
 * Created on 17. maj 2007, 12:14
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 17. maj 2007 (v 1.0)
 * The random generator in lejos created a new int everytime it was called
 * we've made this version of the lejos random class to prevent overflow.
 *
 */

package communication;

/**
 *
 * @author LMK
 */
/**
 * Pseudo-random number generation. Stolen from Lejos source
 */
public class Random {
    private int previousSeed, seed, newSeed;
    
    public Random() {
        this.previousSeed = 1;
        this.seed = (int)System.currentTimeMillis();
        
    }
    
    /**
     * @return A random positive or negative integer.
     */
    public int nextInt() {
        this.newSeed = (this.seed * 48271) ^ this.previousSeed;
        this.previousSeed = this.seed;
        this.seed = this.newSeed;
        return this.newSeed;
    }
}