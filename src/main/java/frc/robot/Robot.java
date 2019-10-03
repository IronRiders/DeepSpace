/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.io.File;
import java.io.IOException;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
//import com.sun.media.sound.SoftLanczosResampler; //suddenly gave an error. Dunno why, or what this does

import static frc.robot.Ports.*;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.buttons.Button;
import com.analog.adis16448.frc.ADIS16448_IMU;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
// import edu.wpi.first.hal.sim.mockdata.PDPDataJNI;
// import edu.wpi.first.networktables.NetworkTableEntry;
// import edu.wpi.first.wpilibj.*;
// import edu.wpi.first.wpilibj.interfaces.Gyro;
// import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.*;
import frc.robot.LambdaJoystick.ThrottlePosition;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
// All Implemented Interfaces:
// import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
// import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
// import edu.wpi.first.wpilibj.SendableBase;
// import edu.wpi.first.wpilibj.shuffleboard.*;
// import edu.wpi.first.wpilibj.GyroBase;
// import edu.wpi.first.wpilibj.AnalogGyro;
// import edu.wpi.first.wpilibj.SpeedControllerGroup;
// import edu.wpi.first.wpilibj.MotorSafety;
// import edu.wpi.first.wpilibj.drive.RobotDriveBase;
// import edu.wpi.first.wpilibj.drive.DifferentialDrive;
// import edu.wpi.first.wpilibj.SpeedControllerGroup.*;
// import edu.wpi.first.wpilibj.DoubleSolenoid;
// import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating 
 * this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  //private StrinclawpathFiles[] = new String[12];
  private MotionProfiling path;

  private boolean isDriverControlling;
  public final CargoPusher cargoPusher = new CargoPusher(SOLENIOD_1, SOLENIOD_2);
  public final HatchGrabbyThingy hatchGrabbyThingy = new HatchGrabbyThingy(SOLENIOD_3, SOLENIOD_4, SOLENIOD_5, SOLENIOD_6);
  public final DriveTrain driveTrain = new DriveTrain(LEFT_DRIVETRAIN_1, LEFT_DRIVETRAIN_2, RIGHT_DRIVETAIN_1,
      RIGHT_DRIVETAIN_2, GYRO_PORT);
  private final ElevatorArm elevatorArm = new ElevatorArm(ELEVATOR_PORT, ELEVATOR_ZERO_PORT, ARM_PORT,
      ARM_LIMIT_SWITCH_PORT);
  
  // private final Arm arm = new Arm(ARM_PORT , ARM_LIMIT_SWITCH_PORT);

  private final LambdaJoystick joystick1 = new LambdaJoystick(0, driveTrain::updateSpeed);
  private final LambdaJoystick joystick2 = new LambdaJoystick(1, driveTrain::updateSpeed);

  private String filePath = "path%s";
  private TalonSRX leftMotor = driveTrain.getLeftMotor(), rightMotor = driveTrain.getRightMotor();
  //private final ImageRecognition imageRec = new ImageRecognition(driveTrain, rightMotor, leftMotor, elevatorArm);

  // SendableChooser autoChooser1 = new SendableChooser();
  // SendableChooser autoChooser2 = new SendableChooser();
  // SendableChooser autoChooser3 = new SendableChooser();

  // PowerDistributionPanel panel = new PowerDistributionPanel();
  // RobotDifferentialDriveDisplay diffDrive = new RobotDifferentialDriveDisplay();
  // NetworkTableEntry example = Shuffleboard.getTab("My Tab").add("My Number", 0)
  //   .withWidget(BuiltInWidgets.kPowerDistributionPanel).withPosition(0, 0).getEntry();

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
   @Override
   public void robotInit() {
     //diffDrive.init();
    CameraServer.getInstance().startAutomaticCapture();
    CameraServer.getInstance().startAutomaticCapture();
   

    //updateSmartDB();
    //normallry for J1 Button 1 changes toogles what the front of the robot is defiend as, in this version we're trying to see if we can set cruise contorl to buton one and exchae the buttons for cruiuse control and toggle direction
      joystick1.addButton(1, driveTrain::startRush, driveTrain::endRush);
     joystick1.addButton(5, driveTrain::cruiseControl , driveTrain::stopDriveMotors);//Hold Line
     //above line doesnt seem to work...
     joystick1.addButton(2, driveTrain::setThrottleDirectionConstant);//flips heading
     joystick1.addButton(3, driveTrain::togglethrottleMode);//Switches Max Speed
     joystick1.addButton(4, driveTrain::toggleBrakesMode);//swutches motrs from brake to coast. default is brake
     joystick1.addButton(11, this::changeDriverControl);//Toggles Auto?

      //joystick1.addButton(12, imageRec::triggerImageRec);
    //joystick2.addButton(1, driveTrain::ToggleBrakesEngager);//Switches Max Speed

    joystick2.addButton(1, cargoPusher::drop, cargoPusher::lock);;
    joystick2.addButton(2, driveTrain::setThrottleDirectionConstant);//flips heading//flips heading
    joystick2.addButton(3, hatchGrabbyThingy::grabHatch,hatchGrabbyThingy::releaseHatch);;
    joystick2.addButton(6, elevatorArm::distanceHigh);
    joystick2.addButton(7, elevatorArm::cargoRocket);
    joystick2.addButton(8, elevatorArm::lowHatch);
    joystick2.addButton(10, driveTrain::leftControl); 
    joystick2.addButton(11, driveTrain::rightControl);
    // driveTrain.gyro.calibrate();
   }
    
    
  //RobotDrive  
  //   autoChooser1.addDefault("path 1", "1");
  //   autoChooser1.addOption("path 2", "2");
  //   autoChooser1.addOption("path 3", "3");
  //   autoChooser1.addOption("path 4", "4");
  //   autoChooser1.addOption("path 5", "5");
  //   autoChooser1.addOption("path 6", "6");
  //   autoChooser1.addOption("path 7", "7");
  //   autoChooser1.addOption("path 8", "8");
  //   autoChooser1.addOption("path 9", "9");
  //   autoChooser1.addOption("path 10", "10");
  //   autoChooser1.addOption("path 11", "11");
  //   autoChooser1.addOption("path 12", "12");

  //   autoChooser2.addOption("path 1", "1");
  //   autoChooser2.addDefault("path 2", "2");
  //   autoChooser2.addOption("path 3", "3");
  //   autoChooser2.addOption("path 4", "4");
  //   autoChooser2.addOption("path 5", "5");
  //   autoChooser2.addOption("path 6", "6");
  //   autoChooser2.addOption("path 7", "7");
  //   autoChooser2.addOption("path 8", "8");
  //   autoChooser2.addOption("path 9", "9");
  //   autoChooser2.addOption("path 10", "10");
  //   autoChooser2.addOption("path 11", "11");
  //   autoChooser2.addOption("path 12", "12");

  //   autoChooser3.addOption("path 1", "1");
  //   autoChooser3.addOption("path 2", "2");
  //   autoChooser3.addDefault("path 3", "3");
  //   autoChooser3.addOption("path 4", "4");
  //   autoChooser3.addOption("path 5", "5");
  //   autoChooser3.addOption("path 6", "6");
  //   autoChooser3.addOption("path 7", "7");
  //   autoChooser3.addOption("path 8", "8");
  //   autoChooser3.addOption("path 9", "9");
  //   autoChooser3.addOption("path 10", "10");
    // autoChooser3.addOption("path 11", "11");
    // autoChooser3.addOption("path 12", "12");

    // SmartDashboard.putData("autoChooser/path1", autoChooser1);
    // SmartDashboard.putData("autoChooser/path2", autoChooser2);
    // SmartDashboard.putData("autoChooser/path3", autoChooser3);

  //   driveTrain.getGyroValues();
   //}

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {

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
    //elevatorArm.updateSmartDB();
   

    //elevatorArm.configurePID();
    
    /*
    isDriverControlling = false;

    int firstPath, secondPath, thirdPath;

    firstPath = Integer.parseInt((String) autoChooser1.getSelected());
    String firstString = String.format(filePath, firstPath);
    secondPath = Integer.parseInt((String) autoChooser2.getSelected());
    String secondString = String.format(filePath, secondPath);
    thirdPath = Integer.parseInt((String) autoChooser3.getSelected());
    String thirdString = String.format(filePath, thirdPath);

   
    //firstPath = Integer.valueOf(SmartDashboard.getString("DB/String 7", "1")) - 1;
    //secondPath = Integer.valueOf(SmartDashboard.getString("DB/String 8", "2")) - 1;
    //thirdPath = Integer.valueOf(SmartDashboard.getString("DB/String 9", "3")) - 1;
    // because it's from 0-11 instead of 1-12 with arrays

    try {
      path = new MotionProfiling(driveTrain,firstString ,secondString, thirdString);
    } catch (IOException e) {
      e.printStackTrace();
    }
    */
  }

  @Override
  public void teleopInit() {
    
    //elevatorArm.updateSmartDB();
    isDriverControlling = true;
  }
  


  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    joystick1.listen();
    joystick2.listen();
 /*   elevatorArm.updateSmartDB();
    if (isDriverControlling) {
      joystick1.listen();
      joystick2.listen();
    } else if (path.isFinished()) {
      path.increasePathIndex();
      elevatorArm.lowHatch();
    
      try {
        path.initializePath();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      path.update();
    }
    */
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
   // elevatorArm.updateSmartDB();
    joystick1.listen();
    joystick2.listen();
   // driveTrain.getGyroValues();
  }

  public void changeDriverControl() {
    this.isDriverControlling = !isDriverControlling;
  }

 /* private void updateSmartDB() {
    SmartDashboard.putString("DB/String 2", "75% speed enabled?");
    SmartDashboard.putString("DB/String 3", "50% speed enabled?");
  }
  */

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
    elevatorArm.getPosition();


  }

  public int getPosition() {
    return elevatorArm.getPosition();
  }
}
