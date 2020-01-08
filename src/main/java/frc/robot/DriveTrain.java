package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import frc.robot.LambdaJoystick.ThrottlePosition;

public class DriveTrain {
    private final CANSparkMax leftMotor1;
    private final CANSparkMax rightMotor1;
    private final CANSparkMax leftMotor2;
    private final CANSparkMax rightMotor2;

    public int throttleDirectionConstant = 1;

    public DriveTrain(final int leftPort1, final int leftPort2, final int rightPort1, final int rightPort2,
            final int gyroPortNumber) {
        leftMotor1 = new CANSparkMax(leftPort1, MotorType.kBrushed);
        leftMotor2 = new CANSparkMax(leftPort2, MotorType.kBrushed);
        rightMotor1 = new CANSparkMax(rightPort1, MotorType.kBrushed);
        rightMotor2 = new CANSparkMax(rightPort2, MotorType.kBrushed);

        leftMotor1.setIdleMode(IdleMode.kBrake);
        leftMotor2.setIdleMode(IdleMode.kBrake);
        rightMotor1.setIdleMode(IdleMode.kBrake);
        rightMotor2.setIdleMode(IdleMode.kBrake);
        leftMotor2.follow(leftMotor1);
        rightMotor2.follow(rightMotor1);
    }

    public void updateSpeed(final ThrottlePosition throttlePosition) {
        double scaledX = throttlePosition.x;
        double scaledY = throttlePosition.y;
        // double scaledZ = throttlePosition.z;
        double scaleFactorA = 0.3;
        double scaleFactorB = 0.7;
        // Top is X scale bottem is Y
        double scaleFactorC = 0.3;
        double scaleFactorD = 0.7;
        scaledY = (scaleFactorC * Math.abs(throttlePosition.y))
                + (scaleFactorD * throttlePosition.y * throttlePosition.y);
        scaledX = (scaleFactorA * Math.abs(throttlePosition.x))
                + (scaleFactorB * throttlePosition.x * throttlePosition.x);
        if (throttlePosition.x < 0)
            scaledX = -scaledX;
        if (throttlePosition.y < 0)
            scaledY = -scaledY;

        leftMotor1.set(scaledY + scaledX);
        rightMotor1.set(scaledY - scaledX);
    }
}