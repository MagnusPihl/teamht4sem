/*
 * HighScoreList.java
 *
 * Created on 6. marts 2007, 09:51
 *
 * Company: HT++
 *
 * @author Mikkel Brøndsholm Nielsen
 * @version 1.0
 *
 *
 * ******VERSION HISTORY****** 
 * LMK @ 13. april 2007 (v 1.0)
 * Made HighScoreList a sorted list
 * Removed nameToString and scoreToString methods
 * Administrator @ 6. marts 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package game;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Iterator;


public class HighScoreList extends LinkedList implements Serializable {
    
    public static final int MAXIMUM_SIZE = 10;
    
    /** Creates a new instance of HighScoreList */
    public HighScoreList() {
        super();
        reset();
    }
    
    /**
     * Reset list.
     */
    public void reset(){
        this.clear();
        for (int i = 0; i < MAXIMUM_SIZE; i++) {
            this.add(i, new HighScore("NewPlayer", 0));
        }
    }

    /**
     * Check whether score is a high enough to be a high score.
     *
     * @param score to check.
     * @return boolean true if score is a highscore.
     */
    public boolean isHighScore(int score){        
        HighScore current;   
        if (this.size() < MAXIMUM_SIZE) {
            return true;
        }
        for(Iterator iter = this.iterator(); iter.hasNext();){
            current = (HighScore) iter.next();            
            //System.out.println(new HighScore("",score).compareTo(current));
            if(current.getScore() < score){
                return true;
            }
        }
        return false;
    }    
     
    /**
     * Add object to highscore to list. Only the first 10 highest scores will be
     * recorded.
     * 
     * @param Comparable object, the highscore to be added.
     * @return true if the score has been added.
     */
    public boolean add(Comparable object) {        
        for (int i = 0; i < this.size(); i++) {
            //System.out.println(object.compareTo(this.get(i)));
            if (object.compareTo(this.get(i)) > 0) {
                this.add(i, object);
                if (this.size() > MAXIMUM_SIZE) {
                    this.removeLast();
                }
                return true;
            }
        }               
        if (this.size() < MAXIMUM_SIZE) {
            this.addLast(object);
            return true;
        }
        return false;
    }    
}
