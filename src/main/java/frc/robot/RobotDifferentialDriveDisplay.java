package frc.robot;

import com.analog.adis16448.frc.ADIS16448_IMU;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj.MotorSafety;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.PWMSpeedController;
import edu.wpi.first.wpilibj.PWMTalonSRX;
import edu.wpi.first.hal.sim.mockdata.PDPDataJNI;
import edu.wpi.first.networktables.NetworkTable;
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
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.MotorSafety;
import edu.wpi.first.wpilibj.drive.RobotDriveBase;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardComponent.*;
import edu.wpi.first.wpilibj.shuffleboard.ComplexWidget;
//import edu.wpi.first.wpilibj.ShuffleboardTab;
import java.util.Map;
import edu.wpi.first.wpilibj.shuffleboard.*;
import edu.wpi.first.networktables.NetworkTable;

public class RobotDifferentialDriveDisplay {    
    PWMTalonSRX m_frontLeft = new PWMTalonSRX(1);
    PWMVictorSPX m_rearLeft = new PWMVictorSPX(4);
   
    SpeedControllerGroup m_left = new SpeedControllerGroup(m_frontLeft, m_rearLeft);
 
    PWMTalonSRX m_frontRight = new PWMTalonSRX(2);
    PWMVictorSPX m_rearRight = new PWMVictorSPX(3);

    SpeedControllerGroup m_right = new SpeedControllerGroup(m_frontRight, m_rearRight);
 
    DifferentialDrive m_drive = new DifferentialDrive(m_left, m_right);

     /*       
     NetworkTableEntry DiffDrive = Shuffleboard.getTab("My Tab2")
        .add(m_drive)
        .withWidget(BuiltInWidgets.kDifferentialDrive)
        .withProperties(Map.of("Number of wheels", 6, "Wheel diameter", 50))
        .getEntry();
        */
    //Check out https://wpilib.screenstepslive.com/s/currentCS/m/shuffleboard/I/1021942-sending-data
    public void init() {
        ShuffleboardTab my_tab3 = Shuffleboard.getTab("My Tab3");
        my_tab3.add("Main Differential Drive",m_drive).withWidget(BuiltInWidgets.kDifferentialDrive);
       
    }
}