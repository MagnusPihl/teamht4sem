/*
 * Replay.java
 *
 * Created on 7. marts 2007, 08:59
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * Magnus Hemmer Pihl @ 7. marts 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package game;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;

public class Replay implements Serializable
{
    public LinkedList[] list;
    
    public Replay()
    {
        this.list = new LinkedList[3];
        this.list[0] = new LinkedList();
        this.list[1] = new LinkedList();
        this.list[2] = new LinkedList();
    }
    
    public void save(File file)
    {
        try
        {
            FileOutputStream out = new FileOutputStream(file);
            BufferedOutputStream buf = new BufferedOutputStream(out);
            ObjectOutputStream s = new ObjectOutputStream(out);
            if (!file.isFile()) {
                new File(file.getParent()).mkdirs();
            }

            int arrays = this.list.length;
            s.writeInt(arrays);
            for(int i=0; i<arrays; i++)
            {
                s.writeInt(this.list[i].size());
                for(int j=0; j<this.list[i].size(); j++)
                {
                    s.writeInt((Integer)this.list[i].get(j));
                }
            }
            s.flush();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    public void load(File file)
    {
        try
        {
            FileInputStream in = new FileInputStream(file);
            BufferedInputStream buf = new BufferedInputStream(in);
            ObjectInputStream s = new ObjectInputStream(buf);

            int arrays = s.readInt();
            this.list = new LinkedList[arrays];
            for(int i=0; i<arrays; i++)
            {
                this.list[i] = new LinkedList();
                int size = s.readInt();
                for(int j=0; j<size; j++)
                {
                    System.out.println(i+":"+j);
                    this.list[i].add(s.readInt());
                }
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}