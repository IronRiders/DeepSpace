package frc.robot;


import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
// import edu.wpi.first.networktables.NetworkTableValue;

public class ImageRecognition {

    // Global Values change change
    private DriveTrain driveTrain;
    private double currentRobotAngle;
    private double distanceToRobotInches;
    private double distanceRightToRobotInches;
    private int stage = 0;
    private int lastSensorPosition;
    private double[] pathData = new double[4];
    private double[] cargoAndRocketAngles = new double[7];
    private double tempTravelDistance = 0;
    private double angleOfRobot;
    private boolean isRocket = false;
    private boolean isImageRecTriggered;

    // Network Tables
    private NetworkTableInstance nwtInstance;
    private NetworkTable table;
    private final NetworkTableEntry DISTANCE_TO_ROBOT_INCHES_ENTRY;
    private final NetworkTableEntry DISTANCE_RIGHT_TO_ROBOT_INCHES_ENTRY;
    private final String GRIP_DISTANCE_TO_ROBOT = "DistanceToRobotInches";
    private final String GRIP_DISTANCE_RIGHT_TO_ROBOT = "DistanceRightToRobot";

    // Final Constants
    private final double STARTING_GYRO_ORIENTATION;
    private final int WIDTH_OF_CAMERA = 1920; //we need to change these!
    private final int HEIGHT_OF_CAMERA = 1080; 
    private static final double ANGLE_TOLERANCE = 10. / 360 * 2 * Math.PI; // 10 Degrees of tolerance
    private static final double DISTANCE_TOLERANCE = 5; // inches
    private static final int CCW_IS_POSITIVE = 1; // 1 = true, -1 = false
    private static final double WHEEL_DIAMETER_INCHES = 6;
    private static final int ENCODER_TICKS_PER_REVOLUTION = 1024;
    private static final int LEFTSIDE_HORIZONTAL = 0;
    private static final int RIGHTSIDE_HORIZONTAL = 1;
    private static final int FORWARDS = 2;
    private static final int LEFT_ROCKET_FRONT = 3;
    private static final int LEFT_ROCKET_BACK = 4;
    private static final int RIGHT_ROCKET_FRONT = 5;
    private static final int RIGHT_ROCKET_BACK = 6;
    private static final double ROCKET_ANGLE = 61.25 / 360 * 2 * Math.PI; // Rocket angle in Radians


    public ImageRecognition(DriveTrain driveTrain) {
        this.driveTrain = driveTrain;
        isImageRecTriggered = false;
        STARTING_GYRO_ORIENTATION = driveTrain.getGyro().getAngle();
        determineCargoAndRocketAngles(STARTING_GYRO_ORIENTATION);

        // Network Tables
        nwtInstance = NetworkTableInstance.getDefault();
        table = nwtInstance.getTable("GRIP");
        DISTANCE_TO_ROBOT_INCHES_ENTRY = table.getEntry(GRIP_DISTANCE_TO_ROBOT);
        DISTANCE_RIGHT_TO_ROBOT_INCHES_ENTRY = table.getEntry(GRIP_DISTANCE_RIGHT_TO_ROBOT);
    }

    public void triggerImageRec() {
        isImageRecTriggered = !isImageRecTriggered;
        if(isImageRecTriggered) {
            getNetworkTablesValues();
            determinePath(distanceToRobotInches, distanceRightToRobotInches);
            lastSensorPosition = driveTrain.getLeftMotor().getSelectedSensorPosition();
        }
    }

    public boolean isImageRecTriggered() {
        return isImageRecTriggered;
    }

    public void getNetworkTablesValues() {
        distanceToRobotInches = DISTANCE_TO_ROBOT_INCHES_ENTRY.getValue().getDouble();
        distanceRightToRobotInches = DISTANCE_RIGHT_TO_ROBOT_INCHES_ENTRY.getValue().getDouble();      
    }

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
                isImageRecTriggered = false;
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
            // Drive backwords at slightly slower speed
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
            // Still needs to take into account original angle of robot
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
            // Still needs to take into account original angle of robot
            // turn left
            pathData[0] = (Math.PI / 2 * CCW_IS_POSITIVE + Math.PI * 2  + currentRobotAngle) % (2 * Math.PI);
            // drive straight for x inches
            pathData[1] = distanceToRightInches;
            // make the robot turn right 
            pathData[2] = (-Math.PI / 2 * CCW_IS_POSITIVE + Math.PI * 2  + currentRobotAngle) % (2 * Math.PI); 
            // drive straight for x inches
            pathData[3] = distanceTapeToRobotInches;
        }

        /*
         * 
         * Here are some of my thoughts so far:
         * 
         * Since we can get the original starting orientation of the robot,
         * we can have the robot perfectly angled for the cargo ship because it is perpendicular
         * to the field. The rocket ship will also be at a certain predeterimined angles.
         * 
         * We can sense this by using line detectors and detecting if we are on
         * a line. (If the middle line sensor is activated but the others are not, then it is the rocket.
         * this is the automatic method of determining if we are at a rocket or the cargo ship.)
         * 
         * Another option could include a button or switch on the dashboard/joystick an be pressed to indicate
         * 90 degrees or "rocket degrees" as the possibilities for the robot to put stuff in.
         * 
         * This way, we do not need very accurate angle calculations for our robot angle.
         * 
         * As of now, there is a rough draft of the driving done by image recognition.
         * Next step is to write some elevator and grabber code.
         *      - Make sure camera is not blocked before image recognition
         * 
         * 
         * - Victor Shan
         * Written on Feb 12, 2019 at 00:12 AM
         * 
         */ 
        
    }

    private void determineCargoAndRocketAngles(double initialGyroAngle) {
        cargoAndRocketAngles[FORWARDS] = initialGyroAngle;
        cargoAndRocketAngles[LEFTSIDE_HORIZONTAL] = (initialGyroAngle + 7/8 * 2 * Math.PI) % (2 * Math.PI);
        cargoAndRocketAngles[RIGHTSIDE_HORIZONTAL] = (initialGyroAngle + 1/8 * 2 * Math.PI) % (2 * Math.PI);
        cargoAndRocketAngles[LEFT_ROCKET_FRONT] = (initialGyroAngle + CCW_IS_POSITIVE * (Math.PI - ROCKET_ANGLE) + 2 * Math.PI) % (2 * Math.PI);
        cargoAndRocketAngles[LEFT_ROCKET_BACK] = (cargoAndRocketAngles[LEFT_ROCKET_BACK] + CCW_IS_POSITIVE * 2 * ROCKET_ANGLE) % (2 * Math.PI);
        cargoAndRocketAngles[RIGHT_ROCKET_FRONT] = (initialGyroAngle - CCW_IS_POSITIVE * (Math.PI - ROCKET_ANGLE) + 2 * Math.PI) % (2 * Math.PI);
        cargoAndRocketAngles[RIGHT_ROCKET_BACK] = (cargoAndRocketAngles[RIGHT_ROCKET_BACK] - CCW_IS_POSITIVE * 2 * ROCKET_ANGLE) % (2 * Math.PI);;
    }

    // Will return one of the cargleAndRocketAngles values
    private int whatIsClosestAngle() {
        return 0;
    }




}
