import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import josx.platform.rcx.*;
import josx.rcxcomm.*;
import josx.util.*;

/*
 * kiss.java
 *
 * Created on 1. marts 2007, 09:27
 *
 *Very simpel test of communication between RCX and PC
 */

/**
 *
 * @author Christian Holm, 5601
 */
public class kiss implements TimerListener, ButtonListener{
    
    Timer t;
    int S1startSensorValue;
    int S2startSensorValue;
    int S3startSensorValue;
    
    RCXPort port;
    DataInputStream in;
    DataOutputStream out;
    
    byte[] buffer = new byte[4];
    
    /** Creates a new instance of kiss */
    public kiss() {
        Button.PRGM.addButtonListener(this);
        
        Sensor.S1.setTypeAndMode(3, 0x00);
        Sensor.S1.activate();
        
        Sensor.S2.setTypeAndMode(3, 0x00);
        Sensor.S2.activate();
        
        Sensor.S3.setTypeAndMode(3, 0x00);
        Sensor.S3.activate();
        
        Motor.A.setPower(7);
        Motor.C.setPower(7);
        
        t = new Timer(100, this);
        
        try {
            port = new RCXPort();
            in = new DataInputStream(port.getInputStream());
            out = new DataOutputStream(port.getOutputStream());
        } catch (IOException ex) {
            Sound.buzz();
            LCD.showNumber(8888);
        }
    }
    
    public void timedOut() {
        int S1value = 2000 + Sensor.S1.readValue();
        int S2value = 3000 + Sensor.S2.readRawValue();
        int S3value = 4000 + Sensor.S3.readRawValue();
        
        try {
            //port.getInputStream() must not replaced by in.
            //I realy dont know why, but that just is...
            if (port.getInputStream().available() != 0) {
                int bytesRead = in.read(buffer);
                if (bytesRead==4) {
                    Sound.twoBeeps();
                    int recvValue = ByteArray.toInt(buffer);
                    if (recvValue== 1000)
                        forward();
                    else if (recvValue== 2000)
                        right();
                    else if (recvValue== 3000)
                        left();
                    else if (recvValue== 4000)
                        backward();
                    else if (recvValue== 5000)
                        stop();
                }
            }
        } catch (IOException ex) {
            Sound.buzz();
            LCD.showNumber(8888);
        }
        
        
        try {
            byte[] transmitBytes = ByteArray.fromInt(S1value);
            port.getOutputStream().write(transmitBytes);
            transmitBytes = ByteArray.fromInt(S2value);
            port.getOutputStream().write(transmitBytes);
            transmitBytes = ByteArray.fromInt(S3value);
            port.getOutputStream().write(transmitBytes);
        } catch (IOException ex) {
            LCD.showNumber(8888);
            Sound.buzz();
        }
    }
    
    private void forward() {
        Motor.A.forward();
        Motor.C.backward();
    }
    
    private void backward() {
        Motor.A.backward();
        Motor.C.forward();
    }
    
    private void left() {
        Motor.A.backward();
        Motor.C.backward();
    }
    
    private void right() {
        Motor.A.forward();
        Motor.C.forward();
    }
    
    private void stop() {
        Motor.A.stop();
        Motor.C.stop();
    }
    
    public void buttonPressed(Button button) {
        ;
    }
    
    public void buttonReleased(Button button) {
        Sound.beep();
        t.stop();
       t.start();
 
    }
}
