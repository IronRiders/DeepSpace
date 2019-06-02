package frc.robot;
import com.analog.adis16448.frc.ADIS16448_IMU;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.*;
import frc.robot.LambdaJoystick.ThrottlePosition;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.shuffleboard.*;
import edu.wpi.first.wpilibj.GyroBase;
import edu.wpi.first.wpilibj.AnalogGyro;
//import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets.kGyro;


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
    private boolean throttleMode = true;//formally slowSpeed, side not we're calling the default spped baby mode, outreach mode, or rookie mode
    private int counter = 0;
    private boolean drivingOffSpeed;
    private int throttleDirectionConstant = 1;
    private boolean throttleForward = true;
    public boolean masteralarm = false;
    public boolean velocityNeverToExcede = false;
    public boolean revrSpeedWarn = false;
    public double throttleInput;
    public boolean velocityOne;


    // private double scaledZ = throttlePosition.z;
    // private double throttle1 = joystick2.scaledZ

    public DriveTrain(final int leftPort1, final int leftPort2, final int rightPort1, final int rightPort2,
            final int gyroPortNumber) {
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
        drivingOffSpeed = false;
        SmartDashboard.putBoolean("status/throttleModeEnabled", throttleMode);
        SmartDashboard.putBoolean("status/foward", throttleForward);
        SmartDashboard.putNumber("status/throttle", 0);
        // gyroPortNumber should be analong 0 or 1

    }

    public void makeVictorsFollowers() {
        leftMotor2.set(ControlMode.Follower, leftMotor1.getDeviceID());
        leftMotor2.setInverted(InvertType.FollowMaster);
        rightMotor2.set(ControlMode.Follower, rightMotor2.getDeviceID());
        rightMotor2.setInverted(InvertType.FollowMaster);
    }

    public void updateSpeed(final ThrottlePosition throttlePosition) {
        double scaledX = throttlePosition.x;
        double scaledY = throttlePosition.y;
        double scaledZ = throttlePosition.z;
        double scaleFactorA = 0.3;
        double scaleFactorB = 0.7;
        // Top is X scale bottem is Y
        double scaleFactorC = 0.3;
        double scaleFactorD = 0.7;
        scaledY = (scaleFactorC * Math.abs(throttlePosition.y))
                + (scaleFactorD * throttlePosition.y * throttlePosition.y);
        scaledX = (scaleFactorA * Math.abs(throttlePosition.x))
                + (scaleFactorB * throttlePosition.x * throttlePosition.x);
        if (throttlePosition.x < 0) {
            scaledX = -scaledX;
        }

        if (throttlePosition.y < 0) {
            scaledY = -scaledY;
        }

        double throttle1 = throttlePosition.z * -1.00; //isaac helped fix the broken code (ishan messed up the sig figs)
        double throttle2 = (throttleMode == true)?((throttle1+1.00)/2.00):0.40; //Throttle as a value between 1 and 2
        double throttle3 =  throttle2*100.00;
        double thrust1 = (java.lang.Math.abs((throttlePosition.y*1.00)*throttle3)); //Thrust as a value between 1 and 100

        /*in theory creates a value double trust which gives a value between 0 and 1 
        for the y input and should Give proportion thrust out put when throtle is enabled)*/
        
        velocityNeverToExcede = (thrust1 > 70.00)? true:false;
        velocityOne = (thrust1 > 35.00)? true:false;
        masteralarm = ((velocityNeverToExcede == true)||(revrSpeedWarn==true)|| ((throttleForward==false) && (throttleMode==true)));
        
        revrSpeedWarn = ((throttle3>=85.00) && (throttleForward == false) ? (revrSpeedWarn= true) : (revrSpeedWarn = false));
        SmartDashboard.putBoolean("status/RvsOverSpeed", revrSpeedWarn);
        SmartDashboard.putBoolean("status/masteralarm", masteralarm);     
        SmartDashboard.putNumber("status/throttlePrime", (throttle3));
        SmartDashboard.putNumber("status/throttle1",(throttle1));
        SmartDashboard.putNumber("status/throttle2",(throttle2));
        SmartDashboard.putNumber("status/thrust", ((thrust1)));
        SmartDashboard.putBoolean("status/VNE",velocityNeverToExcede);
        SmartDashboard.putBoolean("status/V1",velocityOne);
        SmartDashboard.putNumber("status/throttle1",(throttle1));
      
        
        scaledX = scaledX * 0.5 * (throttleMode ? (throttle2) : 0.40 ); 
        scaledY = scaledY * throttleDirectionConstant * (throttleMode ?(throttle2) : 0.40 );

        // if (throttleMode == false) {
        //     scaledX = scaledX * (drivingOffSpeed ? 0.27 : (throttle1+1.00));//note to self: default is .5 , .75 I assumed the they were proportinal so sclaed it by a factor of 40/7
        //     scaledY = scaledY * (drivingOffSpeed ? 0.40 : (throttle1+1.00));
        // }

        final double right = (-scaledX - scaledY) * -1;
        final double left = (scaledY - scaledX) * -1;
        leftMotor1.set(ControlMode.PercentOutput, left);

        leftMotor2.follow(leftMotor1);
        rightMotor1.set(ControlMode.PercentOutput, right);
        rightMotor2.follow(rightMotor1);
    }

    public void getGyroValues() {
        SmartDashboard.putNumber("/diagnostics/gryo/x", getGyro().getAngleX());
        SmartDashboard.putNumber("/diagnostics/gryo/y", getGyro().getAngleY());
        SmartDashboard.putNumber("/diagnostics/gryo/z", getGyro().getAngleZ()%360);
       // SmartDashboard.putData(Sendable Gyro);//sends gyro data

        // SmartDashboard.putNumber("diagnostics/gryo/x", getAdjustedAngle('x'));
        // SmartDashboard.putNumber("diagnostics/gryo/y", getAdjustedAngle('y'));
        // SmartDashboard.putNumber("diagnostics/gryo/z", getAdjustedAngle('z'));
    }
 // A bunch of Ishan's crazy code to display PDP that will either help everyone or cre everything up is here. best not to touch it   
    NetworkTableEntry gyroExample = Shuffleboard.getTab("My Tab2")
        .add("My Gyro Number", getGyro().getAngleY())
        .withWidget(BuiltInWidgets.kGyro)
        //.withProperties(Map.of("min", 0, "max", 360))
        .getEntry();
    NetworkTableEntry example1 = Shuffleboard.getTab("My Tab2") //(test of number display)
        .add("My Number2", 7)
        .withWidget(BuiltInWidgets.kNumberSlider)
        .getEntry();

    // void	clearStickyFaults();	//Clear all PDP sticky faults.
    // double	getCurrent​(int channel);	//Query the current of a single channel of the PDP.
    // double	getTemperature();	//Query the temperature of the PDP.
    // double	getTotalCurrent();	//Query the current of all monitored PDP channels (0-15).
    // double	getTotalEnergy();	//Query the total energy drawn from the monitored PDP channels.
    // double	getTotalPower();	//Query the total power drawn from the monitored PDP channels.
    // double	getVoltage();	//Query the input voltage of the PDP.
    // void	initSendable​(SendableBuilder builder);	//Initializes this Sendable object.
    // void	resetTotalEnergy();	//Reset the total energy to 0.
    
  // it is now safe to touch stuff  
    

    public void autoUpdateSpeed(double left, double right) {
        leftMotor1.set(ControlMode.PercentOutput, left);
        rightMotor1.set(ControlMode.PercentOutput, right);
        leftMotor2.follow(leftMotor1);
        rightMotor2.follow(rightMotor1);
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

    public void getEncoderPosition() {
        int encoderPositionLeft = leftMotor1.getSelectedSensorPosition();
        System.out.println(encoderPositionLeft);
        int encoderPositionRight = rightMotor1.getSelectedSensorPosition();
        System.out.println(encoderPositionRight);
    }

    public void togglethrottleMode() {
        throttleMode = !throttleMode;
        SmartDashboard.putBoolean("status/throttleModeEnabled", throttleMode);
    }

    public void cruiseControl() {
        autoUpdateSpeed(0.4, -0.4);
    }

    public double getAdjustedAngle(char c) { // Could easily not work at all
        double x = Math.toRadians(gyro.getAngleX());
        double y = Math.toRadians(gyro.getAngleY());
        double z = Math.toRadians(gyro.getAngleZ());
        double angle = 70; // Random filler number . offset from vertical/horizontal?

        // had to do lots of converting Radians to degrees bc Math.cos takes radians
        // z = z*cos(angle) - x*sin(angle)
        // x = z*sin(angle) + x*cos(angle)
        // y = y
        z = Math.toDegrees(z * Math.cos(Math.toRadians(angle)) - (x * Math.sin(Math.toRadians(angle))));
        x = Math.toDegrees(z * Math.sin(Math.toRadians(angle)) + (x * Math.cos(Math.toRadians(angle))));
        y = Math.toDegrees(y);

        if (c == 'x') {
            return x;
        } else if (c == 'y') {
            return y;
        } else {
            return z;
        }
    }

    public void setThrottleDirectionConstant() {
        throttleDirectionConstant *= -1;
        throttleForward = !throttleForward;
        SmartDashboard.putBoolean("status/foward", throttleForward);
    }

    public void stopDriveMotors() {
        leftMotor1.set(ControlMode.PercentOutput, 0);
        leftMotor2.set(ControlMode.PercentOutput, 0);
        rightMotor1.set(ControlMode.PercentOutput, 0);
        rightMotor2.set(ControlMode.PercentOutput, 0);
    }

    public void setDrivingOffSpeed() {
        drivingOffSpeed = !drivingOffSpeed;
        // SmartDashboard.putBoolean("DB/String 7", drivingOffSpeed);
    }


    public void updateRightSpeed() {
        rightMotor1.set(ControlMode.PercentOutput, -0.5);
        rightMotor2.follow(rightMotor1);
    }

    public void stopRightSpeed() {
        rightMotor1.set(ControlMode.PercentOutput, 0);
        rightMotor2.follow(rightMotor1);
    }

    public void updateLeftSpeed()   {
        leftMotor1.set(ControlMode.PercentOutput, 0.5);
        leftMotor2.follow(leftMotor1);
    }

    public void stopLeftSpeed() {
        leftMotor1.set(ControlMode.PercentOutput, 0);
        leftMotor2.follow(leftMotor1);
    }

    /*public void athenaDoesAnAgena() {
        leftMotor1.set(ControlMode.PercentOutput, -100);
        leftMotor2.follow(leftMotor1);
        rightMotor1.set(ControlMode.PercentOutput, 100);
        rightMotor2.follow(rightMotor1);
    }*/

    int leftControlCount = 0;
    public void leftControl() {
        if (leftControlCount%3==0) {
            updateLeftSpeed();
        } else if (leftControlCount%3==1) {
            stopLeftSpeed();
        } else {

        }
        leftControlCount++;
    }

    int rightControlCount = 0;
    public void rightControl() {
        if (rightControlCount%3==0) {
            updateRightSpeed();
        } else if (rightControlCount%3==1) {
            stopRightSpeed();
        } else {

        }
        leftControlCount++;
    }
}
