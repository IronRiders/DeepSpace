package frc.robot;

import frc.robot.Ports;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.TimedRobot;

public class Robot extends TimedRobot {
    private final DoubleSolenoid manipulator = new DoubleSolenoid(Ports.SOLENIOD_1, Ports.SOLENIOD_2);
    private final DriveTrain driveTrain = new DriveTrain(Ports.LEFT_DRIVETRAIN_1, Ports.LEFT_DRIVETRAIN_2,
            Ports.RIGHT_DRIVETAIN_1, Ports.RIGHT_DRIVETAIN_2);

    private final Controller driver = new Controller(0);
    private final Controller pusher = new Controller(1);

    private void update() {
        driveTrain.updateSpeed(driver.getAxis(0), driver.getAxis(1), driver.getAxis(3));

        if (driver.getRawButtonPressed(1)) {
            driveTrain.setThrottleDirectionConstant();
        }
        if (driver.getRawButtonPressed(4)) {
            driveTrain.togglethrottleMode();
        }

        if (pusher.getRawButtonPressed(1)) {
            manipulator.set(DoubleSolenoid.Value.kForward);
        }
        if (pusher.getRawButtonPressed(3)) {
            manipulator.set(DoubleSolenoid.Value.kReverse);
        }
    }

    @Override
    public void robotInit() {
        CameraServer.getInstance().startAutomaticCapture();
        CameraServer.getInstance().startAutomaticCapture();
    }

    @Override
    public void autonomousPeriodic() {
        update();
    }

    @Override
    public void teleopPeriodic() {
        update();
    }
}
