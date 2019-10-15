package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.LambdaJoystick.ThrottlePosition;

public class DriveTrain {
    private final TalonSRX leftMotor1;
    private final TalonSRX rightMotor1;
    private final VictorSPX leftMotor2;
    private final VictorSPX rightMotor2;

    private boolean throttleMode = true;
    private boolean throttleForward = true;

    public DriveTrain(final int leftPort1, final int leftPort2, final int rightPort1, final int rightPort2) {
        leftMotor1 = new TalonSRX(leftPort1);
        leftMotor2 = new VictorSPX(leftPort2);
        rightMotor1 = new TalonSRX(rightPort1);
        rightMotor2 = new VictorSPX(rightPort2);

        leftMotor1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
        rightMotor1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);

        rightMotor1.setNeutralMode(NeutralMode.Brake);
        rightMotor2.setNeutralMode(NeutralMode.Brake);
        leftMotor1.setNeutralMode(NeutralMode.Brake);
        leftMotor2.setNeutralMode(NeutralMode.Brake);
    }

    public void updateSpeed(final ThrottlePosition throttlePosition) {
        double scaledX = throttlePosition.x;
        double scaledY = throttlePosition.y;
        double scaledZ = throttlePosition.z;

        // Top is X scale bottom is Y
        double scaleFactorA = 0.3;
        double scaleFactorB = 0.7;
        double scaleFactorC = 0.3;
        double scaleFactorD = 0.7;
        scaledY = (scaleFactorC * Math.abs(throttlePosition.y))
                + (scaleFactorD * throttlePosition.y * throttlePosition.y);
        scaledX = (scaleFactorA * Math.abs(throttlePosition.x))
                + (scaleFactorB * throttlePosition.x * throttlePosition.x);
        
        scaledX *= throttlePosition.x < 0 ? -1 : 1;
        scaledY *= throttlePosition.y < 0 ? -1 : 1;
        scaledZ *= -1;

        final double throttle = throttleMode ? ((scaledZ + 1.00) / 2.00) : 0.40;

        scaledX *= 0.5;
        scaledX *= throttleMode ? throttle : 0.70;
        scaledY *= throttleForward ? 1 : -1;
        scaledY *= throttleMode ? throttle : 0.70;

        final double right = scaledX + scaledY;
        final double left = scaledX - scaledY;

        leftMotor1.set(ControlMode.PercentOutput, left);
        leftMotor2.follow(leftMotor1);
        rightMotor1.set(ControlMode.PercentOutput, right);
        rightMotor2.follow(rightMotor1);

        SmartDashboard.putBoolean("forward", throttleForward);
        SmartDashboard.putBoolean("slow", throttleMode);
        SmartDashboard.putNumber("throttle", throttle);
    }

    public void togglethrottleMode() {
        throttleMode = !throttleMode;
    }

    public void setThrottleDirectionConstant() {
        throttleForward = !throttleForward;
    }
}
