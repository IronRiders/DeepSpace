package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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

    public void updateSpeed(final double x, final double y, final double z) {
        double scaledX = x;
        double scaledY = y;

        // Top is X scale bottom is Y
        double scaleFactorA = 0.3;
        double scaleFactorB = 0.7;
        double scaleFactorC = 0.3;
        double scaleFactorD = 0.7;
        scaledY = (scaleFactorC * Math.abs(y))
                + (scaleFactorD * y * y);
        scaledX = (scaleFactorA * Math.abs(x))
                + (scaleFactorB * x * x);
        
        scaledX *= x < 0 ? -1 : 1;
        scaledY *= y < 0 ? -1 : 1;

        final double throttle = throttleMode ? ((1 - z) / 2) : 0.4;

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
