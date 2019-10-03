// package frc.robot;

// import com.analog.adis16448.frc.ADIS16448_IMU;
// import com.ctre.phoenix.motorcontrol.ControlMode;
// import com.ctre.phoenix.motorcontrol.FeedbackDevice;
// import com.ctre.phoenix.motorcontrol.InvertType;
// import com.ctre.phoenix.motorcontrol.NeutralMode;
// import com.ctre.phoenix.motorcontrol.can.TalonSRX;
// import edu.wpi.first.hal.sim.mockdata.PDPDataJNI;
// import edu.wpi.first.networktables.NetworkTableEntry;
// import edu.wpi.first.wpilibj.*;
// import edu.wpi.first.wpilibj.interfaces.Gyro;
// import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// import com.ctre.phoenix.motorcontrol.can.*;
// import frc.robot.LambdaJoystick.ThrottlePosition;
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

// public class PowerDistributionPanel {

//     public void sample() {
//         public PowerDistributionPanel pdPanel = new PowerDistributionPanel();
//         pdPanel.getVoltage();
//     }

//      public final PowerDistributionPanel voltage = new PowerDistributionPanel();
//      public final PowerDistributionPanel power = new PowerDistributionPanel();
    
//     public PowerDistributionPanel getTotalPower() {
//          return power;
//     }
//      public PowerDistributionPanel getVoltage() {
//      return voltage;
    
//      }
//      NetworkTableEntry testVolts = Shuffleboard.getTab("My Tab2")
//         .add("Volts", voltage)
//         .withWidget(BuiltInWidgets.kPowerDistributionPanel)
//         .getEntry();
//     NetworkTableEntry test = Shuffleboard.getTab("My Tab2")
//         .add("Power", power)
//         .withWidget(BuiltInWidgets.kPowerDistributionPanel)
//         .getEntry();

//     }
// //    // .withProperties(Map.of("min", 0, "max", 360))
    


// //     public void	clearStickyFaults()	//Clear all PDP sticky faults.
// //     public double getCurrent​(1 channel);	//Query the current of a single channel of the PDP.
// //     public double getTemperature();	//Query the temperature of the PDP. //Query the current of all monitored PDP channels (0-15).
// //     public double getTotalEnergy()	//Query the total energy drawn from the monitored PDP channels.
// //     public double getTotalPower()	//Query the total power drawn from the monitored PDP channels.
// //     public double getVoltage()	//Query the input voltage of the PDP.
// //     public void	initSendable​(SendableBuilder builder)	//Initializes this Sendable object.
// //     public void	resetTotalEnergy()	//Reset the total energy to 0.
// //     