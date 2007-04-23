/*
 * HighScore.java
 *
 * Created on 6. marts 2007, 10:39
 *
 * Company: HT++
 *
 * @author Mikkel Brøndsholm Nielsen
 * @version 1.1
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 23. April 2007 (v 1.1)
 * Implemented Comparable interface
 * Administrator @ 6. marts 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package game;

import java.io.Serializable;


public class HighScore implements Serializable, Comparable {
    
    private String name;
    private int score;
    
    /** Creates a new HighScore object */
    public HighScore(String _name, int _score) {
        this.name = _name;
        this.score = _score;
    }
    
    /**
     * Get score.
     *
     * @return score.
     */
    public int getScore(){
        return this.score;
    }
    
    /**
     * Get name of the person who got the score
     * 
     * @return String name.
     */
    public String getName(){
        return this.name;
    }    
    
    /**
     * Compare one HighScore to another.
     *
     * @return A positive if the caller object has a greater score than the callee.
     */
    public int compareTo(Object obj) {
        HighScore highscore = (HighScore)obj;
        return this.score - highscore.score;
    }
}
