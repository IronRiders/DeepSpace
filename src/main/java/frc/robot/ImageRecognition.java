package frc.robot;

import java.text.DecimalFormat;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableValue;

public class ImageRecognition {

    private boolean isImageRecTriggered;
    NetworkTableInstance nwtInstance;
    NetworkTable table;
    // private final NetworkTableEntry distanceToRobotInchesEntry;
    // private final NetworkTableEntry distanceRightToRobotInchesEntry;
    // private final NetworkTableEntry angleOfRobotEntry;
    double distanceToRobotInches;
    double distanceRightToRobotInches;
    double angleOfRobot;
    DriveTrain driveTrain;

    // Network tables columns 1, 2, 3 in order.
    private final String GRIP_DISTANCE_TO_ROBOT = "DistanceToRobotInches";
    private final String GRIP_DISTANCE_RIGHT_TO_ROBOT = "DistanceRightToRobot";
    private final String GRIP_ANGLE_OF_ROBOT_TO_TAPE = "AngleOfRobotToTapeRadians";

    int widthOfCamera = 1920; //we need to change these!
    int heightOfCamera = 1080; 

    private static final double WHEEL_DIAMETER_INCHES = 0.1524 * 100 / 2.54; // Converting meters to inches
    private static final int ENCODER_TICKS_PER_REVOLUTION = 1024;
    private double currentRobotAngle;
    private int stage = 0;
    private double[] pathData = new double[4];
    //DecimalFormat df = new DecimalFormat("##.######");
    private static final double ANGLE_TOLERANCE = 10. / 360 * 2 * Math.PI; // 10 Degrees of tolerance
    private static final double DISTANCE_TOLERANCE = 5; // inches
    private static final int CCW_IS_POSITIVE = 1; // 1 = true, -1 = false
    private double tempTravelDistance = 0;
    private int lastSensorPosition;

// https://wpilib.screenstepslive.com/s/currentCS/m/75361 HOW TO USE NETWORKTABLES!

    public ImageRecognition(DriveTrain driveTrain) {
        this.driveTrain = driveTrain;
        isImageRecTriggered = false;
        nwtInstance = NetworkTableInstance.getDefault();
        table = nwtInstance.getTable("GRIP");
    }

    public void triggerImageRec() {
        isImageRecTriggered = !isImageRecTriggered;
        getNetworkTablesValues();
        determinePath(distanceToRobotInches, distanceRightToRobotInches);
        lastSensorPosition = driveTrain.getLeftMotor().getSelectedSensorPosition()
    }

    public boolean isImageRecTriggered() {
        return isImageRecTriggered;
    }

    public void getNetworkTablesValues() {
        distanceToRobotInches = table.getEntry(GRIP_DISTANCE_TO_ROBOT).getValue().getDouble();
        distanceRightToRobotInches = table.getEntry(GRIP_DISTANCE_RIGHT_TO_ROBOT).getValue().getDouble();
        angleOfRobot = table.getEntry(GRIP_ANGLE_OF_ROBOT_TO_TAPE).getValue().getDouble();        
    }

    // Legacy code
    // private void horizontalPID() {
    //     double error = (widthOfCamera / 2) - centerXDouble;
    //     if (error < 0) { 
    //         //turn right
    //         driveTrain.autoUpdateSpeed(-0.3, -0.3);
    //     } else {
    //         //turn left
    //         driveTrain.autoUpdateSpeed(0.3, 0.3);
    //     }
    //     //positive turns right , negative turns left
    // }


    // Does the next action in line 
    public void startNextMove() {
        switch(stage) {
            case 0:
                turnToAngle((pathData[stage] + angleOfRobot) % (2 * Math.PI));
            case 1:
                travelDistanceInches(pathData[stage]);
            case 2:
                turnToAngle(pathData[stage]);
            case 3:
                if(!isPathCorrect()) {
                    stage = 0;
                    startNextMove();
                }
                travelDistanceInches(pathData[stage]);
            case 4:
                stage = 0;
        }
    }

    // Needs to be fixed if CCW is not positive
    private void turnToAngle(double newAngle) {
        currentRobotAngle = driveTrain.getGyro().getAngle();
        if(Math.abs((currentRobotAngle - newAngle + 2 * Math.PI) % (2 * Math.PI)) < ANGLE_TOLERANCE) {
            stage++;
            startNextMove();
        }
        else if(newAngle - currentRobotAngle > 0 || newAngle - currentRobotAngle > currentRobotAngle - Math.PI) {
            // turn left
            driveTrain.autoUpdateSpeed(0.3 * CCW_IS_POSITIVE, 0.3 * CCW_IS_POSITIVE);
        }
        else if(newAngle - currentRobotAngle < 0 || newAngle - currentRobotAngle > currentRobotAngle + Math.PI) {
            // turn right
            driveTrain.autoUpdateSpeed(-0.3 *CCW_IS_POSITIVE, -0.3 * CCW_IS_POSITIVE);
        }
    }

    // Drives foward or backwards untill target distance is reached
    private void travelDistanceInches(double travelDistance) {
        tempTravelDistance += straightDistanceTraveled();
        if(Math.abs(tempTravelDistance - travelDistance) < DISTANCE_TOLERANCE) {
            tempTravelDistance = 0;
            stage++;
            startNextMove();
        }
        else if (tempTravelDistance < travelDistance) {
            // Drive fowards
            driveTrain.autoUpdateSpeed(0.3, -0.3);
        } else {
            // Drive backwords
            driveTrain.autoUpdateSpeed(-0.2, 0.2);
        }
    }

    // Gets the straight distance traveled using the left motor
    private double straightDistanceTraveled() {
        int tempSensorPosition = lastSensorPosition;
        lastSensorPosition = driveTrain.getLeftMotor().getSelectedSensorPosition();
        return (lastSensorPosition - tempSensorPosition) / ENCODER_TICKS_PER_REVOLUTION * 
                (Math.PI * WHEEL_DIAMETER_INCHES);
    }

    // Checks the current path to see if it still works
    private boolean isPathCorrect() {
        triggerImageRec();
        isImageRecTriggered = true;
        if(pathData[1] == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    
    // Makes the path that the robot will take with the image recognition data
    private void determinePath(double distanceTapeToRobotInches, double distanceToRightInches) {

        currentRobotAngle = driveTrain.getGyro().getAngle();

        if (distanceToRightInches < 5 && distanceToRightInches > -5) { 
            // if the robot is not too far right or left (values need to be tested and updated)
            // drive straight for x inches
            pathData[0] = currentRobotAngle;
            pathData[1] =  0;
            pathData[2] = currentRobotAngle;
            pathData[3] = distanceTapeToRobotInches;
        }
        else if(distanceToRightInches > 0) {
            // make the robot turn right 
            pathData[0] = (-Math.PI / 2 * CCW_IS_POSITIVE + Math.PI * 2  + currentRobotAngle) % (2 * Math.PI); 
            // drive straight for x inches 
            pathData[1] = distanceToRightInches;
            // turn left
            pathData[2] = (Math.PI / 2 * CCW_IS_POSITIVE + Math.PI * 2  + currentRobotAngle) % (2 * Math.PI);
            // drive straight for x inches
            pathData[3] = distanceTapeToRobotInches;
        }
        else {
            // turn left
            pathData[0] = (Math.PI / 2 * CCW_IS_POSITIVE + Math.PI * 2  + currentRobotAngle) % (2 * Math.PI);
            // drive straight for x inches
            pathData[1] = distanceToRightInches;
            // make the robot turn right 
            pathData[2] = (-Math.PI / 2 * CCW_IS_POSITIVE + Math.PI * 2  + currentRobotAngle) % (2 * Math.PI); 
            // drive straight for x inches
            pathData[3] = distanceTapeToRobotInches;
        }
        
    }




}
