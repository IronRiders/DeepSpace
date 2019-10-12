/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import static frc.robot.Ports.*;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
// import com.analog.adis16448.frc.ADIS16448_IMU; // Gyro import, leave in

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  // private StrinclawpathFiles[] = new String[12];
  // private MotionProfiling path;

  private boolean isDriverControlling;
  public final CargoPusher cargoPusher = new CargoPusher(SOLENIOD_1, SOLENIOD_2);
  public final HatchGrabbyThingy hatchGrabbyThingy = new HatchGrabbyThingy(SOLENIOD_3, SOLENIOD_4, SOLENIOD_5,
      SOLENIOD_6);
  public final DriveTrain driveTrain = new DriveTrain(LEFT_DRIVETRAIN_1, LEFT_DRIVETRAIN_2, RIGHT_DRIVETAIN_1,
      RIGHT_DRIVETAIN_2, GYRO_PORT);

  private final LambdaJoystick joystick1 = new LambdaJoystick(0, driveTrain::updateSpeed);
  private final LambdaJoystick joystick2 = new LambdaJoystick(1);

  // private String filePath = "path%s";
  // private TalonSRX leftMotor = driveTrain.getLeftMotor(), rightMotor =
  // driveTrain.getRightMotor();

  // private final ImageRecognition imageRec = new ImageRecognition(driveTrain,
  // rightMotor, leftMotor, elevatorArm);

  // SendableChooser autoChooser1 = new SendableChooser();
  // SendableChooser autoChooser2 = new SendableChooser();
  // SendableChooser autoChooser3 = new SendableChooser();

  // PowerDistributionPanel panel = new PowerDistributionPanel();
  // RobotDifferentialDriveDisplay diffDrive = new
  // RobotDifferentialDriveDisplay();
  // NetworkTableEntry example = Shuffleboard.getTab("My Tab").add("My Number", 0)
  // .withWidget(BuiltInWidgets.kPowerDistributionPanel).withPosition(0,
  // 0).getEntry();

  @Override
  public void robotInit() {
    // diffDrive.init();

    CameraServer.getInstance().startAutomaticCapture();
    CameraServer.getInstance().startAutomaticCapture();

    // updateSmartDB();
    // normally for J1 Button 1 changes toogles what the front of the robot is
    // defiend as, in this version we're trying to see if we can set cruise contorl
    // to buton one and exchae the buttons for cruiuse control and toggle direction

    // joystick1.addButton(5, driveTrain::cruiseControl ,
    // driveTrain::stopDriveMotors);//Hold Line
    // above line doesnt seem to work...
    joystick1.addButton(1, driveTrain::setThrottleDirectionConstant);// flips heading
    joystick1.addButton(4, driveTrain::togglethrottleMode);// Switches baby mode

    // yes Jacob I know, this is just for testing dammit
    joystick2.addButton(1, cargoPusher::drop);
    joystick2.addButton(3, cargoPusher::lock);
    joystick2.addButton(2, hatchGrabbyThingy::extend);
    joystick2.addButton(11, hatchGrabbyThingy::reteract);
    joystick2.addButton(10, hatchGrabbyThingy::reteract);
    joystick2.addButton(6, hatchGrabbyThingy::reteract);
    joystick2.addButton(5, hatchGrabbyThingy::grab);
    joystick2.addButton(4, hatchGrabbyThingy::release);

    // driveTrain.gyro.calibrate();

    // RobotDrive
    // autoChooser1.addDefault("path 1", "1");
    // autoChooser1.addOption("path 2", "2");
    // autoChooser1.addOption("path 3", "3");
    // autoChooser1.addOption("path 4", "4");
    // autoChooser1.addOption("path 5", "5");
    // autoChooser1.addOption("path 6", "6");
    // autoChooser1.addOption("path 7", "7");
    // autoChooser1.addOption("path 8", "8");
    // autoChooser1.addOption("path 9", "9");
    // autoChooser1.addOption("path 10", "10");
    // autoChooser1.addOption("path 11", "11");
    // autoChooser1.addOption("path 12", "12");

    // autoChooser2.addOption("path 1", "1");
    // autoChooser2.addDefault("path 2", "2");
    // autoChooser2.addOption("path 3", "3");
    // autoChooser2.addOption("path 4", "4");
    // autoChooser2.addOption("path 5", "5");
    // autoChooser2.addOption("path 6", "6");
    // autoChooser2.addOption("path 7", "7");
    // autoChooser2.addOption("path 8", "8");
    // autoChooser2.addOption("path 9", "9");
    // autoChooser2.addOption("path 10", "10");
    // autoChooser2.addOption("path 11", "11");
    // autoChooser2.addOption("path 12", "12");

    // autoChooser3.addOption("path 1", "1");
    // autoChooser3.addOption("path 2", "2");
    // autoChooser3.addDefault("path 3", "3");
    // autoChooser3.addOption("path 4", "4");
    // autoChooser3.addOption("path 5", "5");
    // autoChooser3.addOption("path 6", "6");
    // autoChooser3.addOption("path 7", "7");
    // autoChooser3.addOption("path 8", "8");
    // autoChooser3.addOption("path 9", "9");
    // autoChooser3.addOption("path 10", "10");
    // autoChooser3.addOption("path 11", "11");
    // autoChooser3.addOption("path 12", "12");

    // SmartDashboard.putData("autoChooser/path1", autoChooser1);
    // SmartDashboard.putData("autoChooser/path2", autoChooser2);
    // SmartDashboard.putData("autoChooser/path3", autoChooser3);

    // driveTrain.getGyroValues();
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

    // elevatorArm.updateSmartDB();

    // elevatorArm.configurePID();

    /*
     * isDriverControlling = false;
     * 
     * int firstPath, secondPath, thirdPath;
     * 
     * firstPath = Integer.parseInt((String) autoChooser1.getSelected()); String
     * firstString = String.format(filePath, firstPath); secondPath =
     * Integer.parseInt((String) autoChooser2.getSelected()); String secondString =
     * String.format(filePath, secondPath); thirdPath = Integer.parseInt((String)
     * autoChooser3.getSelected()); String thirdString = String.format(filePath,
     * thirdPath);
     * 
     * 
     * //firstPath = Integer.valueOf(SmartDashboard.getString("DB/String 7", "1")) -
     * 1; //secondPath = Integer.valueOf(SmartDashboard.getString("DB/String 8",
     * "2")) - 1; //thirdPath =
     * Integer.valueOf(SmartDashboard.getString("DB/String 9", "3")) - 1; // because
     * it's from 0-11 instead of 1-12 with arrays
     * 
     * try { path = new MotionProfiling(driveTrain,firstString ,secondString,
     * thirdString); } catch (IOException e) { e.printStackTrace(); }
     */
  }

  @Override
  public void teleopInit() {
    isDriverControlling = true;
    cargoPusher.lock();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    joystick1.listen();
    joystick2.listen();
  }
 
    /*
     * elevatorArm.updateSmartDB(); if (isDriverControlling) { joystick1.listen();
     * joystick2.listen(); } else if (path.isFinished()) { path.increasePathIndex();
     * elevatorArm.lowHatch();
     * 
     * try { path.initializePath(); } catch (IOException e) { e.printStackTrace(); }
     * } else { path.update(); }
     */
  

  /**
   * This function is called periodically during operator control.
   */
  @Override

  public void teleopPeriodic() {
    joystick1.listen();
    joystick2.listen();

    // driveTrain.getGyroValues();
  }

  public void changeDriverControl() {
    this.isDriverControlling = !isDriverControlling;
  }
}
