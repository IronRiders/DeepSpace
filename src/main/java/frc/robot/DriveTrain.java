package frc.robot;

import com.analog.adis16448.frc.ADIS16448_IMU;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.*;
import frc.robot.LambdaJoystick.ThrottlePosition;

public class DriveTrain {

    private final TalonSRX leftMotor1;
    private final TalonSRX rightMotor1;
    private final VictorSPX leftMotor2;
    private final VictorSPX rightMotor2;
    private final int leftPort1;
    private final int rightPort1;
    private final int leftPort2;
    private final int rightPort2;
    public final ADIS16448_IMU gyro = new ADIS16448_IMU();

    private int counter = 0;


    public DriveTrain(final int leftPort1, final int leftPort2, final int rightPort1, final int rightPort2, final int gyroPortNumber) {
            leftMotor1 = new TalonSRX(leftPort1);
            leftMotor2 = new VictorSPX(leftPort2);
            rightMotor1 = new TalonSRX(rightPort1);
            rightMotor2 = new VictorSPX(rightPort2);
            leftMotor1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
            rightMotor1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
            this.leftPort1 = leftPort1;
            this.leftPort2 = leftPort2;
            this.rightPort1 = rightPort1;
            this.rightPort2 = rightPort2;
            rightMotor1.setNeutralMode(NeutralMode.Brake);
            rightMotor2.setNeutralMode(NeutralMode.Brake);
            leftMotor1.setNeutralMode(NeutralMode.Brake);
            leftMotor2.setNeutralMode(NeutralMode.Brake);
            gyro.reset();
            //gyroPortNumber should be analong 0 or 1

    }

    public void makeVictorsFollowers(){
        leftMotor2.set(ControlMode.Follower , leftMotor1.getDeviceID());
        leftMotor2.setInverted(InvertType.FollowMaster);
        rightMotor2.set(ControlMode.Follower, rightMotor2.getDeviceID());
        rightMotor2.setInverted(InvertType.FollowMaster);
    }
    public void updateSpeed(final ThrottlePosition throttlePosition) {
        double scaledX = throttlePosition.x;
        double scaledY = throttlePosition.y;
        double scaleFactorA = 0.5;
        double scaleFactorB = 0.5;
        //Top is X scale bottem is Y
        double scaleFactorC = .5 ;
        double scaleFactorD = .5;
        scaledY = (scaleFactorC * Math.abs(throttlePosition.y)) + (scaleFactorD * throttlePosition.y * throttlePosition.y);
        scaledX = (scaleFactorA * Math.abs(throttlePosition.x)) + (scaleFactorB * throttlePosition.x * throttlePosition.x);
        if(throttlePosition.x < 0){
            scaledX = -scaledX;
        }

        if(throttlePosition.y < 0){
            scaledY = -scaledY;
        }
        scaledX*=0.5;
        
        final double right = (-scaledX - scaledY)*-1;
        final double left = (scaledY - scaledX)*-1;
        leftMotor1.set(ControlMode.PercentOutput, left);

        leftMotor2.follow(leftMotor1);
        rightMotor1.set(ControlMode.PercentOutput, right);
        rightMotor2.follow(rightMotor1);

        counter++;
        if(counter % 10 == 0){
            double left1Current = leftMotor1.getOutputCurrent();
            double left1Voltage = leftMotor1.getMotorOutputVoltage();
            double right1Current = rightMotor1.getOutputCurrent();
            double right1Voltage = rightMotor1.getMotorOutputVoltage();
            double left2Voltage = leftMotor2.getMotorOutputVoltage();
            double right2Voltage = rightMotor2.getMotorOutputVoltage();
            System.out.print(String.format("Currents: R: %.2f L: %.2f ", right1Current, left1Current));
            System.out.print(String.format("Voltage: L1: %.2f R1: %.2f L2: %.2f R2: %.2f\n", left1Voltage, right1Voltage, left2Voltage, right2Voltage));

        }
        //makeVictorsFollowers();
    }
    public void autoUpdateSpeed(double left, double right) {
        leftMotor1.set(ControlMode.PercentOutput, left);
        rightMotor1.set(ControlMode.PercentOutput, right);
        leftMotor2.follow(leftMotor1);
        rightMotor2.follow(rightMotor1);

        counter++;
        if(counter % 10 == 0){
           double left1Current = leftMotor1.getOutputCurrent();
           double left1Voltage = leftMotor1.getMotorOutputVoltage();
           double right1Current = rightMotor1.getOutputCurrent();
           double right1Voltage = rightMotor1.getMotorOutputVoltage();
           double left2Voltage = leftMotor2.getMotorOutputVoltage();
           double right2Voltage = rightMotor2.getMotorOutputVoltage();
           System.out.print(String.format("Currents: R: %.2f L: %.2f ", right1Current, left1Current));
           System.out.print(String.format("Voltage: L1: %.2f R1: %.2f L2: %.2f R2: %.2f\n", left1Voltage, right1Voltage, left2Voltage, right2Voltage));

        }
    }
    public TalonSRX getLeftMotor() {
        return leftMotor1;
    }
    public TalonSRX getRightMotor() {
        return rightMotor1;
    }
    public ADIS16448_IMU getGyro() {
        return gyro;
    }

    public void getEncoderPosition(){
        int encoderPositionLeft = leftMotor1.getSelectedSensorPosition();
        System.out.println(encoderPositionLeft);
        int encoderPositionRight = rightMotor1.getSelectedSensorPosition();
        System.out.println(encoderPositionRight);
    }
}