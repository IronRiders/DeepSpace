package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
//we may not need some of these import statements, just not sure which

//import com.analog.adis16448.frc.ADIS16448_IMU;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.LambdaJoystick.ThrottlePosition;

public class DriveTrain {

    private final TalonSRX leftMotor1;
    private final TalonSRX rightMotor1;
    private final VictorSP leftMotor2;
    private final VictorSP rightMotor2;
    //private final Encoder enco;

    public DriveTrain(final int leftPort1, final int leftPort2, final int rightPort1, final int rightPort2) {
            leftMotor1 = new TalonSRX(leftPort1);
            leftMotor2 = new VictorSP(leftPort2);
            rightMotor1 = new TalonSRX(rightPort1);
            rightMotor2 = new VictorSP(rightPort2);

            //enco = new Encoder(8,9);
            //enco.setDistancePerPulse(2.0943/4);
    }

    public void updateSpeed(final ThrottlePosition throttlePosition) {
        final double right = (-throttlePosition.x - throttlePosition.y)*-1;
        final double left = (throttlePosition.y - throttlePosition.x)*-1;
        leftMotor1.set(ControlMode.PercentOutput, left);
        leftMotor2.set(left); //set follower
        rightMotor1.set(ControlMode.PercentOutput, right);
        rightMotor2.set(right); //set follower

    }
}