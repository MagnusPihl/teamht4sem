/*
 * Sound.java
 *
 * Taken from "Devoloping Games in Java" [Brackeen]
 * by Brackeen, David
 * www.brackeen.com
 * Listing 4.9 Sound.java, page 197
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 6. marts 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package game.audio;

/**
    The Sound class is a container for sound samples. The sound
    samples are format-agnostic and are stored as a byte array.
*/
public class Sound {

    private byte[] samples;

    /**
        Create a new Sound object with the specified byte array.
        The array is not copied.
    */
    public Sound(byte[] samples) {
        this.samples = samples;
    }


    /**
        Returns this Sound's objects samples as a byte array.
    */
    public byte[] getSamples() {
        return samples;
    }

}
