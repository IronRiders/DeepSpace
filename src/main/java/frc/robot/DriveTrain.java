package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
//we may not need some of these import statements, just not sure which

//import com.analog.adis16448.frc.ADIS16448_IMU;
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
    //private final Encoder enco;
    private final int leftPort1;
    private final int rightPort1;
    private final int leftPort2;
    private final int rightPort2;

    public DriveTrain(final int leftPort1, final int leftPort2, final int rightPort1, final int rightPort2) {
            leftMotor1 = new TalonSRX(leftPort1);
            leftMotor2 = new VictorSPX(leftPort2);
            rightMotor1 = new TalonSRX(rightPort1);
            rightMotor2 = new VictorSPX(rightPort2);
            this.leftPort1 = leftPort1;
            this.leftPort2 = leftPort2;
            this.rightPort1 = rightPort1;
            this.rightPort2 = rightPort2;
            //enco = new Encoder(8,9);
            //enco.setDistancePerPulse(2.0943/4);
    }
//hello
    public void updateSpeed(final ThrottlePosition throttlePosition) {
        final double right = (-throttlePosition.x - throttlePosition.y)*-1;
        final double left = (throttlePosition.y - throttlePosition.x)*-1;
        leftMotor1.set(ControlMode.PercentOutput, left);
        leftMotor2.set(ControlMode.Follower, leftPort1);
        rightMotor1.set(ControlMode.PercentOutput, right);
        rightMotor2.set(ControlMode.Follower, rightPort1);

    }
}