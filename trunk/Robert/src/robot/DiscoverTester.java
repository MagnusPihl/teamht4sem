package robot;

import communication.GameCommands;
import josx.platform.rcx.Button;
import josx.platform.rcx.ButtonListener;
import josx.platform.rcx.LCD;

public class DiscoverTester implements ButtonListener
{
    private NewDrive drive;
    
    private byte paths;
    
    public static void main(String[] args)
    {
        new DiscoverTester();
    }
    
    public DiscoverTester()
    {
        this.drive = new NewDrive();
        Button.RUN.addButtonListener(this);
    }
    
    public void buttonPressed(Button button)
    {
        if(Button.RUN.isPressed())
        {
            LCD.clearSegment(1);
            LCD.clearSegment(2);
            LCD.clearSegment(3);
            LCD.showNumber(9999);
            
            this.paths = this.drive.search();
            if((this.paths & GameCommands.TURN_LEFT) > 0)
                LCD.setSegment(1);
            if((this.paths & GameCommands.FORWARD) > 0)
                LCD.setSegment(2);
            if((this.paths & GameCommands.TURN_RIGHT) > 0)
                LCD.setSegment(3);
            LCD.showNumber(this.paths);
        }
    }
    
    public void buttonReleased(Button button)
    {
    }
}