/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.io.File;
import java.io.IOException;

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
  private String pathFiles[] = new String[12];
  private MotionProfiling selectedPaths[] = new MotionProfiling[3];

  private boolean isDriverControlling;  
  public final DriveTrain driveTrain = new DriveTrain(LEFT_DRIVETRAIN_1, LEFT_DRIVETRAIN_2 , RIGHT_DRIVETAIN_1 , RIGHT_DRIVETAIN_2 , GYRO_PORT);
  private final LambdaJoystick joystick1 = new LambdaJoystick(0, driveTrain::updateSpeed);
  
  private final Elevator elevator = new Elevator(ELEVATOR_PORT , ELEVATOR_ZERO_PORT);
  private final Grabber grabber = new Grabber(LEFT_FLYWHEEL_PORT , RIGHT_FLYWHEEL_PORT , CLAW_LEFT , CLAW_RIGHT , CLAW_LEFT_LIMIT_SWITCH , CLAW_RIGHT_LIMIT_SWITCH );
  private final Arm arm = new Arm(ARM_PORT , ARM_LIMIT_SWITCH_PORT);
  private String filePath = "path%s"; 
  private final ImageRecognition imageRec = new ImageRecognition(driveTrain);
  int currentPath;
  
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */ 
  @Override
  public void robotInit() {
    CameraServer.getInstance().startAutomaticCapture();
    updateSmartDB();
    elevator.configurePID();
    grabber.configurePID();
    arm.configurePID();
    elevator.resetToFactorySettings();
    grabber.resetToFactorySettings();
    driveTrain.autoUpdateSpeed(0,0);
    for (int i = 0; i < pathFiles.length; i++) {
      pathFiles[i] = String.format(filePath , i + 1);
    }
    joystick1.addButton(2, elevator::lowCargo);
    joystick1.addButton(3, elevator::lowHatch);
    joystick1.addButton(4, elevator::mediumHigh);
    joystick1.addButton(5, elevator::lowerToZero);
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
    elevator.configurePID();
    grabber.configurePID();
    arm.configurePID();
    driveTrain.autoUpdateSpeed(0,0);
    int firstPath = Integer.valueOf(SmartDashboard.getString("DB/String 7", "1")) - 1;
    int secondPath = Integer.valueOf(SmartDashboard.getString("DB/String 8", "2")) - 1;
    int thirdPath = Integer.valueOf(SmartDashboard.getString("DB/String 9", "3")) - 1;
    //because it's from 0-11 instead of 1-12 with arrays
    int chosenPathNumbers[] = new int[]{firstPath, secondPath, thirdPath};

    for (int i = 0; i < selectedPaths.length; i++) {
      try {
        selectedPaths[i] = new MotionProfiling(driveTrain, pathFiles[chosenPathNumbers[i]] + ".left",
            pathFiles[chosenPathNumbers[i]] + ".right");
      } catch (IOException e) {
		e.printStackTrace();
	}
    }

    currentPath = 0;
  }

  @Override  
  public void teleopInit(){
    elevator.configurePID();
    grabber.configurePID();
    arm.configurePID();
    elevator.resetToFactorySettings();
    grabber.resetToFactorySettings();
    driveTrain.autoUpdateSpeed(0,0);
  }

  

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {

    if(selectedPaths[currentPath].isFinished()){
        isDriverControlling = !isDriverControlling;
        if(currentPath < 2) //prevents indexOutOfBoundsException
        currentPath++;
        selectedPaths[currentPath].reset();
    }
    if (imageRec.isImageRecTriggered()){
      //image rec code here
    } else if (isDriverControlling) {
      joystick1.listen();
    } else {
      selectedPaths[currentPath].update();
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    if(imageRec.isImageRecTriggered()){
      //image recognition code here
    }
    else{
      joystick1.listen();  
    }
  }

public void changeDriverControl(){  
    this.isDriverControlling = !isDriverControlling;
}

private void updateSmartDB(){
  SmartDashboard.putString("DB/String 2", "1st Path --->");
  SmartDashboard.putString("DB/String 3", "2nd Path --->");
  SmartDashboard.putString("DB/String 4", "3rd Path --->");
}


  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
