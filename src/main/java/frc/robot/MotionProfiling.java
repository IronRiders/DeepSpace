package frc.robot;

import java.io.File;
import java.io.IOException;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.*;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;


public class MotionProfiling {
    private DriveTrain driveTrain;
    private final double wheelBaseWidth = 2.25; // Width in feet  
    private final double wheelDiameter = 0.5; //inches
    private final TalonSRX leftMotor;
    private final TalonSRX rightMotor;
    private final int encoderTicksPerRevolution = 4096;
    private final double maxVelocity = 13; //ft/s
    private EncoderFollower left;
    private EncoderFollower right;
    private String[] pathFiles;
    int index;


    public MotionProfiling(DriveTrain driveTrain, String setup1 , String setup2 , String setup3) throws IOException {
        this.driveTrain = driveTrain;
        leftMotor = driveTrain.getLeftMotor();
        rightMotor = driveTrain.getRightMotor();
        pathFiles = new String[] {setup1, setup2 , setup3};
        index = 0;
        initializePath();
        
        //pathweaver has an error with mixing up left and right
    }

    public void initializePath() throws IOException {
        Trajectory trajectoryLeft = PathfinderFRC.getTrajectory(pathFiles[index] + ".right");
        Trajectory trajectoryRight = PathfinderFRC.getTrajectory(pathFiles[index] + ".left");

        left = new EncoderFollower(trajectoryLeft);
        right = new EncoderFollower(trajectoryRight);

        left.configureEncoder(leftMotor.getSelectedSensorPosition(), encoderTicksPerRevolution, wheelDiameter); 
        right.configureEncoder(rightMotor.getSelectedSensorPosition(), encoderTicksPerRevolution, wheelDiameter);

        left.configurePIDVA(0.9, 0.0, 0.0, 1 / maxVelocity, 0); //Filler PID vals
        right.configurePIDVA(0.9, 0.0, 0.0, 1 / maxVelocity, 0);
    }
    public void update() { 
            double l = left.calculate(leftMotor.getSelectedSensorPosition());
            double r = right.calculate(rightMotor.getSelectedSensorPosition());
            //double gyroHeading = driveTrain.getGyro().getAngleX();
            double gyroHeading = driveTrain.getAdjustedAngle('x');   // Assuming the gyro is giving a value in degrees
            double desiredHeading = -Pathfinder.r2d(left.getHeading());  // Should also be in degrees
    
            double angleDifference = Pathfinder.boundHalfDegrees(desiredHeading - gyroHeading);
            double turn = 0.8 * (-1.0/80.0) * angleDifference;
    
            driveTrain.autoUpdateSpeed(l + turn, r - turn);
    }

    public boolean isFinished(){
        return (left.isFinished() && right.isFinished());
    }

    public void increasePathIndex(){
        if(index < pathFiles.length){
            index++;
        }
    }

    public int getPathIndex(){
        return index;
    }

    public void reset(){
        left.reset();
        right.reset();
        left.configureEncoder(leftMotor.getSelectedSensorPosition(), encoderTicksPerRevolution, wheelDiameter); 
        right.configureEncoder(rightMotor.getSelectedSensorPosition(), encoderTicksPerRevolution, wheelDiameter);
    }
}