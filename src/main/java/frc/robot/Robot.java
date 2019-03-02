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

import static frc.robot.Ports.*;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.buttons.Button;
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
  private final ElevatorArm elevatorArm = new ElevatorArm(ELEVATOR_PORT , ELEVATOR_ZERO_PORT , ARM_PORT , ARM_LIMIT_SWITCH_PORT);
  private final Grabber grabber = new Grabber(LEFT_FLYWHEEL_PORT , RIGHT_FLYWHEEL_PORT , CLAW_LEFT , CLAW_RIGHT , CLAW_LEFT_LIMIT_SWITCH , CLAW_RIGHT_LIMIT_SWITCH);
  //private final Arm arm = new Arm(ARM_PORT , ARM_LIMIT_SWITCH_PORT);

  private final LambdaJoystick joystick1 = new LambdaJoystick(0, driveTrain::updateSpeed);
  private final LambdaJoystick joystick2 = new LambdaJoystick(1 , grabber::updateSpeed);

  private String filePath = "path%s"; 
  private int currentPath;
  private TalonSRX leftMotor = driveTrain.getLeftMotor(), rightMotor = driveTrain.getRightMotor();
  private final ImageRecognition imageRec = new ImageRecognition(driveTrain, rightMotor, leftMotor, elevatorArm);

  SendableChooser autoChooser1 = new SendableChooser();
  SendableChooser autoChooser2 = new SendableChooser();
  SendableChooser autoChooser3 = new SendableChooser();
  
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */ 
  @Override
  public void robotInit() {
    CameraServer.getInstance().startAutomaticCapture();
    updateSmartDB();
    elevatorArm.configurePID();
    grabber.configurePID();
    joystick2.addButton(4 , grabber::closeClaw);
    joystick2.addButton(5 , grabber::openClaw);
    joystick1.addButton(1, driveTrain::cruiseControl);
    //joystick1.addButton(11 , this::changeDriverControl);
    //joystick1.addButton(12, imageRec::triggerImageRec);

    //for testing
    //joystick1.addButton(4 , grabber::hatch);
    //joystick1.addButton(3 , grabber::cargo);
    //joystick1.addButton(5 , grabber::open);
    //joystick1.addButton(2, grabber::closed);



    joystick2.addButton(3 , elevatorArm::pickup);
    joystick2.addButton(9, elevatorArm::lowCargo);
    joystick2.addButton(8 , elevatorArm::lowHatch);
    joystick2.addButton(10 , elevatorArm::mediumCargo);
    joystick2.addButton(7 , elevatorArm::mediumHatch);
    joystick2.addButton(6 , elevatorArm::highHatch);
   // joystick2.addButton(11 , elevatorArm::highCargo);


    //arm.configurePID();
    driveTrain.autoUpdateSpeed(0,0);
    for (int i = 0; i < pathFiles.length; i++) {
      pathFiles[i] = String.format(filePath , i + 1);
    }
    //joystick1.addButton(1, imageRec::triggerImageRec); // Random joystick button
    //talk to ishan about button placement

    autoChooser1.addDefault("path 1", "1");
    autoChooser1.addOption("path 2", "2");
    autoChooser1.addOption("path 3", "3");
    autoChooser1.addOption("path 4", "4");
    autoChooser1.addOption("path 5", "5");
    autoChooser1.addOption("path 6", "6");
    autoChooser1.addOption("path 7", "7");
    autoChooser1.addOption("path 8", "8");
    autoChooser1.addOption("path 9", "9");
    autoChooser1.addOption("path 10", "10");
    autoChooser1.addOption("path 11", "11");
    autoChooser1.addOption("path 12", "12");

    autoChooser2.addOption("path 1", "1");
    autoChooser2.addDefault("path 2", "2");
    autoChooser2.addOption("path 3", "3");
    autoChooser2.addOption("path 4", "4");
    autoChooser2.addOption("path 5", "5");
    autoChooser2.addOption("path 6", "6");
    autoChooser2.addOption("path 7", "7");
    autoChooser2.addOption("path 8", "8");
    autoChooser2.addOption("path 9", "9");
    autoChooser2.addOption("path 10", "10");
    autoChooser2.addOption("path 11", "11");
    autoChooser2.addOption("path 12", "12");

    autoChooser3.addOption("path 1", "1");
    autoChooser3.addOption("path 2", "2");
    autoChooser3.addDefault("path 3", "3");
    autoChooser3.addOption("path 4", "4");
    autoChooser3.addOption("path 5", "5");
    autoChooser3.addOption("path 6", "6");
    autoChooser3.addOption("path 7", "7");
    autoChooser3.addOption("path 8", "8");
    autoChooser3.addOption("path 9", "9");
    autoChooser3.addOption("path 10", "10");
    autoChooser3.addOption("path 11", "11");
    autoChooser3.addOption("path 12", "12");

    SmartDashboard.putData("autoChooser/path1", autoChooser1);
    SmartDashboard.putData("autoChooser/path2", autoChooser2);
    SmartDashboard.putData("autoChooser/path3", autoChooser3);
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
    elevatorArm.configurePID();
    grabber.configurePID();
    //arm.configurePID();
    driveTrain.autoUpdateSpeed(0,0);
    isDriverControlling = true;

    int firstPath, secondPath, thirdPath;

    // firstPath = Integer.parseInt((String) autoChooser1.getSelected());
    // secondPath = Integer.parseInt((String) autoChooser2.getSelected());
    // thirdPath = Integer.parseInt((String) autoChooser3.getSelected());

    firstPath = Integer.valueOf(SmartDashboard.getString("DB/String 7", "1")) - 1;
    secondPath = Integer.valueOf(SmartDashboard.getString("DB/String 8", "2")) - 1;
    thirdPath = Integer.valueOf(SmartDashboard.getString("DB/String 9", "3")) - 1;
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
    elevatorArm.configurePID();
    grabber.configurePID();
    //arm.configurePID();
    //driveTrain.autoUpdateSpeed(0,0);
  }

  

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    joystick1.listen();
    joystick2.listen();

   /* if(selectedPaths[currentPath].isFinished()){
        isDriverControlling = !isDriverControlling;
        if(currentPath < 2) //prevents indexOutOfBoundsException
        currentPath++;
        selectedPaths[currentPath].reset();
    }
    if (selectedPaths[selectedPaths.length - 1].isFinished() && (imageRec.isImageRecTriggered() || !imageRec.isFinished())){ // Assuming that the last path will only finished after it as occurred
      if(!imageRec.isImageRecTriggered()) {
        imageRec.triggerImageRec();
      }
      imageRec.update();
    } else if (isDriverControlling) { //had to remove driver control now that its path dependant
      joystick1.listen();
    } else {
      selectedPaths[currentPath].update();
    }
    */
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    if(imageRec.isImageRecTriggered()){
      // the imageRec.triggerImageRec() must be called by joystick
      imageRec.update();
    }
    else{
      joystick1.listen();  
      joystick2.listen();
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
    elevatorArm.testArm();
    
  }
}
