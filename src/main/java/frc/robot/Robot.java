/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.io.File;
import static frc.robot.Ports.*;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String autoOne = "Auto 1";
  private static final String autoTwo = "Auto 2";
  private static final String autoThree = "Auto 3";
  private static final String autoFour = "Auto 4";
  private static final String autoFive = "Auto 5";
  private static final String autoSix = "Auto 6";
  private static final String autoSeven = "Auto 7";
  private String autoSelected;
  private final SendableChooser<String> autoChooser = new SendableChooser<>();
  public final DriveTrain driveTrain = new DriveTrain(LEFT_DRIVETRAIN_1, LEFT_DRIVETRAIN_2 , RIGHT_DRIVETAIN_1 , RIGHT_DRIVETAIN_2 , GYRO_PORT);
  private final LambdaJoystick joystick1 = new LambdaJoystick(0, driveTrain::updateSpeed);

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    CameraServer.getInstance().startAutomaticCapture();
    autoChooser.setDefaultOption("auto 1", autoOne);
    autoChooser.addOption("Auto 2", autoTwo);
    autoChooser.addOption("Auto 3", autoThree);
    autoChooser.addOption("Auto 4", autoFour);
    autoChooser.addOption("Auto 5", autoFive);
    autoChooser.addOption("Auto 6", autoSix);
    autoChooser.addOption("Auto 7", autoSeven);
    SmartDashboard.putData("Auto choices", autoChooser);

  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    autoSelected = autoChooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    File file;
    switch (autoSelected) {
      case autoOne:
        file = new File(FileUtilities.getFilePath/autoOne);
        break;
      case autoTwo:
        file = new File(FileUtilities.getFilePath/autoTwo);
        break;
      case autoThree:
        file = new File(FileUtilities.getFilePath/autoThree);
        break;
      case autoFour:
        file = new File(FileUtilities.getFilePath/autoFour);
        break;
      case autoFive:
        file = new File(FileUtilities.getFilePath/autoFive);
        break;
      case autoSix:
        file = new File(FileUtilities.getFilePath/autoSix);
        break;
      case autoSeven:
        file = new File(FileUtilities.getFilePath/autoSeven);
        break;
      default:
        file = new File(FileUtilities.getFilePath/autoOne);
        break;
    }
    MotionProfiling auto = new MotionProfiling(driveTrain, file);
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    joystick1.listen();
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
