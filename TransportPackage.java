/*
 * TransportPackage.java
 *
 * Created on 24. marts 2007, 12:06
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * Magnus Hemmer Pihl @ 13. april 2007 (v 1.1)
 * Added static methods for getType and getSequenceNumber.
 *
 * Magnus Hemmer Pihl @ 24. marts 2007 (v 1.0)
 * Initial.
 *
 */

package communication;

public class TransportPackage
{
    private int type;
    private int sequence;
    private int data;    
    
    public TransportPackage(int header, int data)
    {
        this.type = (header & 0x80)>>7;
        this.sequence = (header & 0x7F);
        this.data = data;
    }
    
    /** Creates a new instance of IRDatagram */
    public TransportPackage(int _type, int _sequence, int data)
    {
        this.type = _type;
        if(_type!=0)
            this.type = 1;
        this.sequence = _sequence;
        this.data = data;
    }
    
    public int getType()
    {
        return this.type;
    }
    
    public int getSequenceNumber()
    {
        return this.sequence;
    }
    
    public int getHeader()
    {
        return ((this.type << 7) | this.sequence);
    }
    
    public int getData() {
        return this.data;
    }
    
    public static int getType(int _header)
    {
        return (_header & 0x80)>>7;
    }
    
    public static int getSequenceNumber(int _header)
    {
        return (_header & 0x7F);
    }
    
    public static int createAcknowledgeHeader(int _header)
    {
        return 0x80 | _header;
    }
}