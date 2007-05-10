///*
// * Controller.java
// *
// * Created on 13. april 2007, 11:04
// *
// * Company: HT++
// *
// * @author thh
// * @version 1.0
// *
// *
// * ******VERSION HISTORY******
// *
// * thh @ 13. april 2007 (v 1.0)
// * __________ Changes ____________
// *
// */
//
//package robot;
//
//import communication.GameCommands;
//import communication.GameProxy;
//import josx.platform.rcx.*;
//import java.io.*;
//
//public class Controller implements ButtonListener{
//    //Drive ride = new Drive();
//    LowRider ride = new LowRider();
//    GameProxy tower;
//    private static Controller instance = new Controller();
//    private int command = 0;
//    private int directions = 0;
//    private int address = 0;
//    private boolean addressing = true;
//    
//    /** Creates a new instance of Controller */
//    private Controller() {
//    }
//    
//    public void run(){
//        this.address();
//        tower = new GameProxy(address);
//        while(true){
//            command = tower.getcommand();
//            if(command == GameCommands.FORWARD){
//                this.move();
//                tower.sendMoveDone(GameCommands.MOVE_DONE);
//            }else if(command == GameCommands.TURN_LEFT || command == (GameCommands.TURN_LEFT | GameCommands.TURN_NUMBER)){
//                if(command == (GameCommands.TURN_LEFT | GameCommands.TURN_NUMBER)){
//                    ride.TurnLeft90();
//                    ride.TurnLeft90();
//                }else{
//                    ride.TurnLeft90();
//                }
//            }else if(command == GameCommands.TURN_RIGHT || command == (GameCommands.TURN_RIGHT | GameCommands.TURN_NUMBER)){
//                if(command == (GameCommands.TURN_RIGHT | GameCommands.TURN_NUMBER)){
//                    ride.TurnRight90();
//                    ride.TurnRight90();
//                }else{
//                    ride.TurnRight90();
//                }
//            }else if(command == (GameCommands.MOVE_UP_DISCOVER) || command == (GameCommands.MOVE_RIGHT_DISCOVER) || command == (GameCommands.MOVE_DOWN_DISCOVER) || command == (GameCommands.MOVE_LEFT_DISCOVER)){
//                //directions = ride.goToNext();
//                tower.sendMoveDone(GameCommands.MOVE_DONE | directions);
//            }else if(command == GameCommands.LIGHT_ON){
//                this.lightOn();
//            }else if(command == GameCommands.LIGHT_OFF){
//                this.lightOff();
//            }else if(command == GameCommands.BEEP){
//                Sound.twoBeeps();//only two beeps
//            }else if(command == GameCommands.CALIBRATE){
//                //ride.callibrate(tower.getSensor1, tower.getSensor2, tower.getSensor3, tower.getMinGreen, tower.getMaxGreen, tower.getMinBlack, tower.getMaxBlack);
//            }else if(command == GameCommands.SEARCH_NODE){
//                //directions = ride.searchNode();
//                tower.sendMoveDone(GameCommands.MOVE_DONE | directions);
//            }else{
//                
//            }
//        }
//    }
//    
//    private void move(){
//        tower.stopThread();
//        directions = tower.getDirections();
//        //ride.Forward(directions);
//        ride.run(directions,command);
//        tower.startThread();
//    }
//    
//    public static Controller getInstance() {
//        return instance;
//    }
//        
//    private void lightOn() {
//        Motor.B.setPower(7);
//        Motor.B.forward();
//    }
//    
//    private void lightOff() {
//        Motor.B.stop();
//    }
//            
//    private void address(){
//        Button.RUN.addButtonListener(this);
//        Button.PRGM.addButtonListener(this);
//        Button.VIEW.addButtonListener(this);
//        while(addressing == true){
//            LCD.showNumber(address+1);
//        }
//    }
//    
//    public void buttonPressed(Button button) {
//        if (Button.VIEW.isPressed() && addressing == true) {
//            
//        }else if (Button.PRGM.isPressed() && addressing == true) {
//            Sound.beep();
//            address++;
//            address = address%3;
//        }else if (Button.RUN.isPressed() && addressing == true) {
//            Sound.twoBeeps();
//            address++;
//            addressing = false;
//        }
//    }
//    public void buttonReleased(Button button) {
//    }
//    
//    public static void main(String[] args) throws InterruptedException, IOException{
//        Controller noget = Controller.getInstance();
//        noget.run();
//    }
//}