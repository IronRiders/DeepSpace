package frc.robot;

import com.analog.adis16448.frc.ADIS16448_IMU;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.hal.sim.mockdata.PDPDataJNI;
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

public class RobotDifferentialDriveDisplay {    
    TalonSRX m_frontLeft = new TalonSRX(1);
    VictorSPX m_rearLeft = new VictorSPX(4);
   
    SpeedControllerGroup m_left = new SpeedControllerGroup(m_frontLeft, m_rearLeft);
 
    TalonSRX m_frontRight = new TalonSRX(2);
    VictorSPX m_rearRight = new VictorSPX(3);

    SpeedControllerGroup m_right = new SpeedControllerGroup(m_frontRight, m_rearRight);
 
    DifferentialDrive m_drive = new DifferentialDrive(m_left, m_right);

    
    NetworkTableEntry example = Shuffleboard.getTab("My Tab2")
        .add("DiffDrive", m_drive)
        .withProperties(Map.of("Number of wheels", 6, "Wheel diameter", 50))
        .withWidget(BuiltInWidgets.kDifferentialDrive)
        .getEntry();
}