/*
 * Options.java
 *
 * Created on 15. maj 2007, 12:16
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 0.1
 *
 *
 * ******VERSION HISTORY******
 *
 * Magnus Hemmer Pihl @ 14. marts 2007 (v 1.0)
 * Initial.
 *
 */

package game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Options
{
    private static final Options instance = new Options();
    
    private File file;
    
    private String[] entity;
    private String skin;
    private int speed;
    private int sound;
    private int online;
    private int towerPort;
    
    private Options()
    {
        this.file = new File("options.cfg");
        this.entity = new String[3];
    }
    
    public static Options getInstance()
    {
        return instance;
    }
    
    public void save()
    {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(this.file));
            out.println(this.entity[0]);
            out.println(this.entity[1]);
            out.println(this.entity[2]);
            out.println(this.skin);
            out.println(this.speed);
            out.println(this.sound);
            out.println(this.online);
            out.println(this.towerPort);
            out.flush();
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void load()
    {
        try {
            BufferedReader in = new BufferedReader(new FileReader(this.file));
            this.entity[0] = in.readLine();
            this.entity[1] = in.readLine();
            this.entity[2] = in.readLine();
            this.skin = in.readLine();
            this.speed = Integer.parseInt(in.readLine());
            this.sound = Integer.parseInt(in.readLine());
            this.online = Integer.parseInt(in.readLine());
            this.towerPort = Integer.parseInt(in.readLine());
            in.close();
        } catch (Exception ex) {    //Set defaults:
            this.entity[0] = "";     //Keyboard arrows controller
            this.entity[1] = "";     //Figure out what to do here
            this.entity[2] = "";     //^- Yeah.
            this.skin = "pacman";   //Skin "pacman"
            this.speed = 1;         //Normal speed
            this.sound = 0;         //Sound on
            this.online = 0;        //Offline mode
            this.towerPort = 0;     //USB interface
        }
    }
    
    public void setEntity(int entNum, String value) { this.entity[entNum] = value; }
    public void setSkin(String skin) { this.skin = skin; }
    public void setSpeed(int speed) { this.speed = speed; }
    public void setSound(int sound) { this.sound = sound; }
    public void setOnline(int online) { this.online = online; }
    public void setInterface(int port) { this.towerPort = port; }
    
    public String getEntity(int entNum) { return this.entity[entNum]; }
    public String getSkin() { return this.skin; }
    public int getSpeed() { return this.speed; }
    public int getSound() { return this.sound; }
    public int getOnline() { return this.online; }
    public int getInterface() { return this.towerPort; }
}