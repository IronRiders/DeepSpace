/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import static frc.robot.Ports.*;
import frc.robot.DriveTrain;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
// import com.analog.adis16448.frc.ADIS16448_IMU; // Gyro import, leave in
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DriverStation;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  private boolean isDriverControlling;
  public final CargoPusher cargoPusher = new CargoPusher(SOLENIOD_1, SOLENIOD_2);
  public final HatchGrabbyThingy hatchGrabbyThingy = new HatchGrabbyThingy(SOLENIOD_3, SOLENIOD_4, SOLENIOD_5,
      SOLENIOD_6);
  public final DriveTrain driveTrain = new DriveTrain(LEFT_DRIVETRAIN_1, LEFT_DRIVETRAIN_2, RIGHT_DRIVETAIN_1,
      RIGHT_DRIVETAIN_2, GYRO_PORT);

  public boolean driverOneControlling=true;
  private double matchTime;
  private Boolean endgameInit;
  private DriverStation driverStation;
  
  public Robot() {
    driverStation = DriverStation.getInstance();
    driverStation.getMatchTime();
     

  }


  



  public void toggleDriverOneControlling(){
    driverOneControlling=!driverOneControlling;
  }
 
  private final LambdaJoystick joystick1 = driverOneControlling ? (new LambdaJoystick(0, driveTrain::updateSpeed)):new LambdaJoystick(1);
  private final LambdaJoystick joystick2 =  driverOneControlling ? (new LambdaJoystick(1)):new LambdaJoystick(0, driveTrain::updateSpeed);

  // PowerDistributionPanel panel = new PowerDistributionPanel();
  // RobotDifferentialDriveDisplay diffDrive = new
  // RobotDifferentialDriveDisplay();
  // NetworkTableEntry example = Shuffleboard.getTab("My Tab").add("My Number", 0)
  // .withWidget(BuiltInWidgets.kPowerDistributionPanel).withPosition(0,
  // 0).getEntry();

  @Override
  public void robotInit() {
    CameraServer.getInstance().startAutomaticCapture();
    CameraServer.getInstance().startAutomaticCapture();

    // updateSmartDB();

    joystick1.addButton(1, driveTrain::setThrottleDirectionConstant);// flips heading
    joystick1.addButton(3, driveTrain::togglethrottleMode);// Switches throttlemode
    joystick1.addButton(4, driveTrain::stopDriveMotors, driveTrain::restartDriveMotors);;
    joystick1.addButton(8, this::toggleDriverOneControlling);

    joystick2.addButton(1, cargoPusher::drop);
    joystick2.addButton(3, cargoPusher::lock);
    //joystick2.addButton(2, hatchGrabbyThingy::extend);
    //joystick2.addButton(11, hatchGrabbyThingy::reteract);
    //joystick2.addButton(10, hatchGrabbyThingy::reteract);
    //joystick2.addButton(6, hatchGrabbyThingy::reteract);
    //joystick2.addButton(5, hatchGrabbyThingy::grab);
    //joystick2.addButton(4, hatchGrabbyThingy::release);

  }
 @Override
public void robotPeriodic() {
  
  super.robotPeriodic();
  SmartDashboard.putNumber("MatchTime/matchtime", driverStation.getMatchTime());
}
  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString line to get the
   * auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure below with additional strings. If using the SendableChooser
   * make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {

  }
  @Override
  public void teleopInit() {
    isDriverControlling = true;
    driveTrain.stopRightSpeed();
    driveTrain.stopLeftSpeed();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {

   
    if (driverStation.getMatchTime() == 7){
    driveTrain.updateRightSpeed();
    driveTrain.updateLeftSpeed();
    }
 
  }
 
  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    joystick1.listen();
    joystick2.listen();
  }

  public void changeDriverControl() {
    this.isDriverControlling = !isDriverControlling;
  }
}
