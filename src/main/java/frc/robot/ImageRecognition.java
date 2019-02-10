package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableValue;

public class ImageRecognition {
private boolean isImageRecTriggered ;
NetworkTableInstance nwtInstance;
NetworkTable table;
NetworkTableEntry centerX;
NetworkTableEntry startingX;
NetworkTableEntry endingX;
double centerXDouble;
double startingXDouble;
double endingXDouble;
DriveTrain driveTrain;

int widthOfCamera = 1920; //we need to change these!
int heightOfCamera = 1080; 

// https://wpilib.screenstepslive.com/s/currentCS/m/75361 HOW TO USE NETWORKTABLES!

    public ImageRecognition(DriveTrain driveTrain) {
        this.driveTrain = driveTrain;
        isImageRecTriggered = false;
        nwtInstance = NetworkTableInstance.getDefault();
        table = nwtInstance.getTable("GRIP");
    }

    public void triggerImageRec() {
        isImageRecTriggered = !isImageRecTriggered;
    }

    public boolean isImageRecTriggered() {
        return isImageRecTriggered;
    }

    public void getNetworkTablesValues() {
        centerXDouble = table.getEntry("centerX").getValue().getDouble();
        startingXDouble = table.getEntry("startingX").getValue().getDouble();
        endingXDouble = table.getEntry("endingX").getValue().getDouble();        
    }

    public void horizontalPID() {
        double error = (widthOfCamera / 2) - centerXDouble;
        if (error < 0) { 
            //turn right
            driveTrain.autoUpdateSpeed(-0.3, -0.3);
        } else {
            //turn left
            driveTrain.autoUpdateSpeed(0.3, 0.3);
        }

        //positive turns right , negative turns left


    }
    
    public void determinedPath(double distanceTapeToRobotInches, double distanceToRightInches) {
    if (distanceToRightInches < 5 && distanceToRightInches > -5) { 
        // if the robot is not too far right or left (values need to be tested and updated)
        // drive straight for x inches
    }
    else if(distanceToRightInches > 0) {
        // make the robot turn right 
        // move x inches 
        // turn left
        // drive straight for x inches
    }
    else {
        // make the robot turn left
        // move x inches
        // turn right
        // drive straight for x inches
    }
        
    }




}
