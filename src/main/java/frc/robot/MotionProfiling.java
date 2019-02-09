package frc.robot;

import java.io.File;

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
    private final double wheelDiameter = 0.1524; //meters
    private final TalonSRX leftMotor;
    private final TalonSRX rightMotor;
    private final int encoderTicksPerRevolution = 1024;
    private final double maxVelocity = 13; //ft/s
    private EncoderFollower left;
    private EncoderFollower right;

    
    public MotionProfiling(DriveTrain driveTrain, File setup) {
        this.driveTrain = driveTrain;
        leftMotor = driveTrain.getLeftMotor();
        rightMotor = driveTrain.getRightMotor();

        Trajectory trajectory = Pathfinder.readFromCSV(setup);
        TankModifier modifier = new TankModifier(trajectory).modify(wheelBaseWidth);

        left = new EncoderFollower(modifier.getLeftTrajectory());
        right = new EncoderFollower(modifier.getRightTrajectory());

        left.configureEncoder(leftMotor.getSelectedSensorPosition(), 1024, wheelDiameter); //1024 or 4096 - before or after quad?
        right.configureEncoder(rightMotor.getSelectedSensorPosition(), 1024, wheelDiameter);

        left.configurePIDVA(1.0, 0.0, 0.0, 1 / maxVelocity, 0); //Filler PID vals
        right.configurePIDVA(1.0, 0.0, 0.0, 1 / maxVelocity, 0);
    }
    public void update() { 
        double l = left.calculate(leftMotor.getSelectedSensorPosition());
        double r = right.calculate(rightMotor.getSelectedSensorPosition());
    
        double gyroHeading = driveTrain.getGyro().getAngle();   // Assuming the gyro is giving a value in degrees
        double desiredHeading = Pathfinder.r2d(left.getHeading());  // Should also be in degrees

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
}