package frc.robot;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.robot.Constants;
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
    private double[] cargoAndRocketAngles = new double[7];
    private boolean isImageRecTriggered;
    private Trajectory trajectoryLeft;
    private Trajectory trajectoryRight;
    private EncoderFollower left;
    private EncoderFollower right;
    private final TalonSRX leftMotor;
    private final TalonSRX rightMotor;
    private final ElevatorArm elevatorArm;

    // Network Tables
    private NetworkTableInstance nwtInstance;
    private NetworkTable table;
    private final NetworkTableEntry DISTANCE_TO_ROBOT_INCHES_ENTRY;
    private final NetworkTableEntry DISTANCE_RIGHT_TO_ROBOT_INCHES_ENTRY;
    private final String GRIP_DISTANCE_TO_ROBOT = "DistanceToRobotInches";
    private final String GRIP_DISTANCE_RIGHT_TO_ROBOT = "DistanceRightToRobotInches";

    // Final Constants
    private final double STARTING_GYRO_ORIENTATION;
    private static final int CCW_IS_POSITIVE = 1; // 1 = true, -1 = false
    private static final int LEFT_FACING_HORIZONTAL = 0;
    private static final int RIGHT_FACING_HORIZONTAL = 1;
    private static final int FORWARDS = 2;
    private static final int LEFT_ROCKET_FRONT = 3;
    private static final int LEFT_ROCKET_BACK = 4;
    private static final int RIGHT_ROCKET_FRONT = 5;
    private static final int RIGHT_ROCKET_BACK = 6;
    private static final double ROCKET_ANGLE = 61.25; // Rocket angle in Radians


    public ImageRecognition(DriveTrain driveTrain, TalonSRX rightMotor, TalonSRX leftMotor, ElevatorArm elevatorArm) {
        this.driveTrain = driveTrain;
        isImageRecTriggered = false;
        STARTING_GYRO_ORIENTATION = driveTrain.getGyro().getAngleY();
        determineCargoAndRocketAngles(STARTING_GYRO_ORIENTATION);
        left = new EncoderFollower(trajectoryLeft);
        right = new EncoderFollower(trajectoryRight);
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        this.elevatorArm = elevatorArm;

        // Network Tables
        nwtInstance = NetworkTableInstance.getDefault();
        table = nwtInstance.getTable("GRIP");
        DISTANCE_TO_ROBOT_INCHES_ENTRY = table.getEntry(GRIP_DISTANCE_TO_ROBOT);
        DISTANCE_RIGHT_TO_ROBOT_INCHES_ENTRY = table.getEntry(GRIP_DISTANCE_RIGHT_TO_ROBOT);
    }

    public void triggerImageRec() {
        isImageRecTriggered = !isImageRecTriggered;
        System.out.println("Image recognition activation set to " + isImageRecTriggered);
        if(isImageRecTriggered) {
            elevatorArm.getToMediumHatch();
            // update values
            getNetworkTablesValues();
            currentRobotAngle = driveTrain.getGyro().getAngleY();
            determinePath();
        }
    }

    public boolean isImageRecTriggered() {
        return isImageRecTriggered;
    }

    public void getNetworkTablesValues() {
        distanceToRobotInches = DISTANCE_TO_ROBOT_INCHES_ENTRY.getValue().getDouble();
        distanceRightToRobotInches = DISTANCE_RIGHT_TO_ROBOT_INCHES_ENTRY.getValue().getDouble();   
        System.out.printf("Network Table Values:\tDistance to robot=%.2f\tDistance right of robot=%.2f", distanceToRobotInches, distanceRightToRobotInches);   
    }

    public void determinePath() {
        Waypoint[] waypoints = {
            new Waypoint(0, 0, currentRobotAngle),
            new Waypoint(distanceRightToRobotInches, distanceToRobotInches, whatIsClosestAngle(currentRobotAngle))
        };
        Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_LOW, 0.2, Constants.MAX_VELOCITY_METERS, Constants.MAX_ACCELERATION_METERS, Constants.MAX_JERK_METERS);
        Trajectory trajectory = Pathfinder.generate(waypoints, config);
        TankModifier modifier = new TankModifier(trajectory);
        trajectoryLeft = modifier.getLeftTrajectory();
        trajectoryRight = modifier.getRightTrajectory();
    }

    public void update() { 
        double l = left.calculate(leftMotor.getSelectedSensorPosition());
        double r = right.calculate(rightMotor.getSelectedSensorPosition());
        double gyroHeading = driveTrain.getGyro().getAngleY();   // Assuming the gyro is giving a value in degrees
        double desiredHeading = -Pathfinder.r2d(left.getHeading());  // Should also be in degrees

        double angleDifference = Pathfinder.boundHalfDegrees(desiredHeading - gyroHeading);
        double turn = 0.8 * (-1.0/80.0) * angleDifference;

        driveTrain.autoUpdateSpeed(l + turn, r - turn);
    }

    public boolean isFinished() {
        if (left.isFinished() && right.isFinished()) {
            return true;
        } else {
            return false;       
        }      
    }

    public void reset(){
        left.reset();
        right.reset();
        left.configureEncoder(leftMotor.getSelectedSensorPosition(), Constants.ENCODER_TICKS_PER_REVOLUTION, Constants.WHEEL_DIAMETER_INCHES); 
        right.configureEncoder(rightMotor.getSelectedSensorPosition(), Constants.ENCODER_TICKS_PER_REVOLUTION, Constants.WHEEL_DIAMETER_INCHES);
    }


    private void determineCargoAndRocketAngles(double initialGyroAngle) {
        cargoAndRocketAngles[FORWARDS] = initialGyroAngle;
        cargoAndRocketAngles[LEFT_FACING_HORIZONTAL] = (initialGyroAngle + 90) % (360);
        cargoAndRocketAngles[RIGHT_FACING_HORIZONTAL] = (initialGyroAngle - 90) % (360);
        cargoAndRocketAngles[LEFT_ROCKET_FRONT] = (initialGyroAngle + CCW_IS_POSITIVE * (180 - ROCKET_ANGLE) + 360) % (360);
        cargoAndRocketAngles[LEFT_ROCKET_BACK] = (cargoAndRocketAngles[LEFT_ROCKET_BACK] + CCW_IS_POSITIVE * 2 * ROCKET_ANGLE) % (360);
        cargoAndRocketAngles[RIGHT_ROCKET_FRONT] = (initialGyroAngle - CCW_IS_POSITIVE * (180 - ROCKET_ANGLE) + 360) % (360);
        cargoAndRocketAngles[RIGHT_ROCKET_BACK] = (cargoAndRocketAngles[RIGHT_ROCKET_BACK] - CCW_IS_POSITIVE * 2 * ROCKET_ANGLE) % (360);;
    }

    // Will return one of the cargleAndRocketAngles values
    private double whatIsClosestAngle(double currentAngle) {
        double closestAngle = closestAngle(currentAngle, cargoAndRocketAngles[0]);
        for (int i = 1; i < 7; i++) {
            if (Math.abs(closestAngle(currentAngle, cargoAndRocketAngles[i])) < closestAngle) {
                closestAngle = closestAngle(currentAngle, cargoAndRocketAngles[i]);
            }
        }
        return closestAngle;
    }

    private double closestAngle(double currentAngle, double newAngle) {   
        currentAngle %= 360;
        newAngle %= 360;                
        if (newAngle - currentAngle < 180 && newAngle - currentAngle > -180) {         
            return newAngle - currentAngle;                                            
        }                                                                              
        if (newAngle - currentAngle > 180) {                                           
            return newAngle - currentAngle - 360;                                      
        }                                                                              
        return (newAngle +  2 * 180 - currentAngle) % (360);                               
    }                                                                                  

}
