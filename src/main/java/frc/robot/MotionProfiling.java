import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class MotionProfiling {
    private DriveTrain driveTrain;
    private final double wheelBaseWidth = 2.25; // Width in feet  
    private final double wheelDiameter = .1524; //Meters
    private final TalonSRX leftMotor;
    private final TalonSRX rightMotor;
    private final int encoerTicksPerRevolution = 1024;
    private final double maxVelocity = 1.0;

    
    public MotionProfiling(DriveTrain driveTrain, File setup) {
        this.driveTrain = driveTrain;
        leftMotor = driveTrain.getLeftMotor();
        rightMotor = driveTrain.getRightMotor();

        Trajectory trajectory = Pathfinder.readfromCSV(setup);
        TankModifier modifier = new TankModifier(trajectory).modify(wheelBaseWidth);

        EncoderFollower left = new EncoderFollower(modifier.getLeftTrajectory());
        EncoderFollower right = new EncoderFollower(modifier.getRightTrajectory());

        left.configureEncoder(leftMotor.getEncPosition(), 1024, wheelDiameter); //1024 or 4096 - before or after quad?
        right.configureEncoder(rightMotor.getEncPosition(), 1024, wheelDiameter);

        left.configurePIDVA(1.0, 0.0, 0.0, 1 / maxVelocity, 0); //Filler PID vals
        right.configurePIDVA(1.0, 0.0, 0.0, 1 / maxVelocity, 0);
    }
    public double output() { //probably needs a new name
        double l = left.calculate(leftMotor.getEncPosition());
        double r = right.calculate(rightMotor.getEncPosition());

        double gyroHeading = driveTrain.getGyro().getAngle();   // Assuming the gyro is giving a value in degrees
        double desiredHeading = Pathfinder.r2d(left.getHeading());  // Should also be in degrees

        double angleDifference = Pathfinder.boundHalfDegrees(desiredHeading - gyroHeading);
        double turn = 0.8 * (-1.0/80.0) * angleDifference;

        setLeftMotors(l + turn);
        setRightMotors(r - turn);
    }
}