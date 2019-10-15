package frc.robot;

import frc.robot.Ports;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;

public class Robot extends TimedRobot {
    public final CargoPusher cargoPusher = new CargoPusher(Ports.SOLENIOD);
    public final DriveTrain driveTrain = new DriveTrain(Ports.LEFT_DRIVETRAIN_1, Ports.LEFT_DRIVETRAIN_2,
            Ports.RIGHT_DRIVETAIN_1, Ports.RIGHT_DRIVETAIN_2);

    private final LambdaJoystick joystick1 = new LambdaJoystick(0, driveTrain::updateSpeed);
    private final LambdaJoystick joystick2 = new LambdaJoystick(1);

    @Override
    public void robotInit() {
        CameraServer.getInstance().startAutomaticCapture();
        CameraServer.getInstance().startAutomaticCapture();

        joystick1.addButton(1, driveTrain::setThrottleDirectionConstant);
        joystick1.addButton(4, driveTrain::togglethrottleMode);

        joystick2.addButton(1, cargoPusher::drop);
        joystick2.addButton(3, cargoPusher::lock);

        cargoPusher.lock();
    }

    @Override
    public void autonomousPeriodic() {
        joystick1.listen();
        joystick2.listen();
    }

    @Override
    public void teleopPeriodic() {
        joystick1.listen();
        joystick2.listen();
    }
}
