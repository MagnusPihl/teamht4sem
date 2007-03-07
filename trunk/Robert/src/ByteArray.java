//package common;
/*
 * ByteArray.java
 *
 * Created on 13. september 2006, 16:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 * @desc ByteArray converts any primitive or String to and from byte[].
 * Should run on micro edition. Uses only bitwise operations.
 * @author LMK
 */
public class ByteArray {

//    public static final int BYTES_PER_LONG = 8;
    public static final int BYTES_PER_INT = 4;
    public static final int BYTES_PER_CHAR = 2;
    public static final int BITS_PER_BYTE = 8;
       
    public static final char[] CHAR_MASKS = new char[] {
        0x00FF,
        0xFF00,
    };
    
    public static final int[] INT_MASKS = new int[] {
        0x000000FF,
        0x0000FF00,
        0x00FF0000,  
        0xFF000000,
    };
        
/*    public static final long[] LONG_MASKS = new long[] {
        0x00000000000000FFL,
        0x000000000000FF00L,
        0x0000000000FF0000L,  
        0x00000000FF000000L,
        0x000000FF00000000L,
        0x0000FF0000000000L,
        0x00FF000000000000L,  
        0xFF00000000000000L
    };
*/    
    public static byte[] resize(byte[] oldArray, int size) {
	byte[] newArray = new byte[size];
	for (int i = 0; (i < size) && (i < oldArray.length); i++) {
	    newArray[i] = oldArray[i];
	}
	return newArray;
    }
    
    /**
     *Convert String to byte[].
     *Can be reverted by using toString
     */
    public static byte[] fromString(String string) {
        return fromCharArray(string.toCharArray());
    }
    
    /**
     * Convert byte[] to String
     * Input array must be 4 bytes (an int with the number of chars)
     * and 2 bytes for each character in the string
     */
    public static String toString(byte[] array) {
        return String.valueOf(toCharArray(array));
    }
    
    /**
     * Convert char to byte[2]
     */
    public static byte[] fromChar(char number) {
        byte[] output = new byte[BYTES_PER_CHAR];
        output[0] = (byte) number;
        output[1] = (byte) (number >>> BITS_PER_BYTE);
        return output;
    }
    
    /**
     *	Converts byte[2] to char
     * */
    public static char toChar(byte[] array) {        
        char output = 0;
        output |= (((char)array[1]) << BITS_PER_BYTE) & CHAR_MASKS[1];
        output |= ((char)array[0]) & CHAR_MASKS[0];
        return output;    
    }
    
    /**
     * Converts a char[] to a byte[]
     * The length of byte[] will be two times that of the char[]
     */
    public static byte[] fromCharArray(char[] array) {
        byte[] output = new byte[array.length*BYTES_PER_CHAR];
        for (int i = 0; i < array.length; i++) {                        
            output[i*2] = (byte) array[i];
            output[i*2+1] = (byte) (array[i] >>> BITS_PER_BYTE);            
        }
        return output;
    }
    
    /**
     * Converts byte[] of even length to char[]. Output array will be half as 
     * long as input array
     */
    public static char[] toCharArray(byte[] array) {
        char[] output = new char[array.length/BYTES_PER_CHAR];
        for (int i = 0; i < output.length; i++) {
            output[i] = 0;
            output[i] |= (((char)array[i*2+1]) << BITS_PER_BYTE) & CHAR_MASKS[1];
            output[i] |= ((char)array[i*2]) & CHAR_MASKS[0];
        }
        return output;    
    }
    
    /**
     *Converts integer to byte[4]
     */
    public static byte[] fromInt(int number) {
        byte[] output = new byte[BYTES_PER_INT];
        for (int i = 0; i < BYTES_PER_INT; i++) {
            output[i] = (byte) (number);
            number >>>= BITS_PER_BYTE;
        }
        return output;
    }
    
    /**
     *Converts byte[4] to integer
     */
    public static int toInt(byte[] array) {
        int output = 0;
        for (int i = (BYTES_PER_INT - 1); i >= 0; i--) {
            output |= (((int)array[i]) << (BITS_PER_BYTE * i)) & INT_MASKS[i];
        }
        return output;    
    }
    
    /**
     *Converts byte[8] to long
     */
/*    public static long toLong(byte[] array) {
        long output = 0;
        for (int i = (BYTES_PER_LONG - 1); i >= 0; i--) {
            output |= (((long)array[i]) << (BITS_PER_BYTE * i)) & LONG_MASKS[i];
        }
        return output;
    }
*/        
    /**
     *Converts long to byte[8]
     */
/*    public static byte[] fromLong(long number) {
        byte[] output = new byte[BYTES_PER_LONG];
        for (int i = 0; i < BYTES_PER_LONG; i++) {
            output[i] = (byte) (number);
            number >>>= BITS_PER_BYTE;
        }
        return output;
    }    
*/ 
    /**
     * Converts byte[4] to float
     */
    public static float toFloat(byte[] array) {        
        return Float.intBitsToFloat(toInt(array));
    }

    /**
     * Converts float to byte[4]
     */
    public static byte[] fromFloat(float number) {
        return fromInt(Float.floatToIntBits(number));
    }    
       
    /**
     * Converts byte[8] to double
     */
/*    public static double toDouble(byte[] array) {           
        return Double.longBitsToDouble(toLong(array));
    }
*/        
    /**
     *Converts double to byte[8]
     */
/*    public static byte[] fromDouble(double number) {
        return fromLong(Double.doubleToLongBits(number));
    }*/    
}
