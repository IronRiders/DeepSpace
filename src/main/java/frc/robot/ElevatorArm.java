
package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ElevatorArm {
    private TalonSRX talon;
    private double pConstantElevator = 0.1; //don't change these 
    private double iConstantElevator = (1.6 * pConstantElevator) / 1000;
    private double dConstantElevator = 0;
    private double fConstantElevator = 0.4;
    private final int maxAmpsElevator = 20;
    private final double diameter = 2.25;
    private final int pulsesPerRevolution = 4096;
    private final double multiplier = 1.3;

    private final int distancePickUp = 0; 
 
    //elevator
    private final double distanceLowHatch = 0; 
    private final double distanceHigh = 18;
    private final double distanceCargoRocket = 8.5;
   // private final int distanceBottom = 0;
    private final double[] elevatorDistances = {distanceLowHatch, distanceCargoRocket , distanceHigh};
    private int counter;

    public ElevatorArm(int elevatorPort , int elevatorlimitSwitchPort , int armPort , int armLimitSwitchPort){
        SmartDashboard.putNumber("pid/elevator/p", 0.0);
        SmartDashboard.putNumber("pid/elevator/i", 0.0);
        SmartDashboard.putNumber("pid/elevator/d", 0.0);
        SmartDashboard.putNumber("pid/elevator/f", 0.0);
        
        talon = new TalonSRX(elevatorPort);
        talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
        talon.setSensorPhase(true);
        talon.configMotionCruiseVelocity(1100);
        talon.configMotionAcceleration(1100);
        talon.setSelectedSensorPosition(0);
        talon.configPeakCurrentLimit(maxAmpsElevator);

        counter = 0;

        talon.config_kD(0, dConstantElevator);
        talon.config_kP(0, pConstantElevator);
        talon.config_kI(0, iConstantElevator);
        talon.config_kF(0, fConstantElevator);  
    }
    public void configurePID() {
        this.pConstantElevator = SmartDashboard.getNumber("pid/elevator/p", 0.2);
        this.iConstantElevator = SmartDashboard.getNumber("pid/elevator/i", (0.2 / 10000));
        this.iConstantElevator = (this.iConstantElevator * this.pConstantElevator) / 1000;
        this.dConstantElevator = SmartDashboard.getNumber("pid/elevator/d", 2.0);
        this.fConstantElevator = SmartDashboard.getNumber("pid/elevator/f", 0.0);
    }

    public void updateSmartDB(){
        boolean elevatorUp = talon.getSelectedSensorPosition() > 100;
        SmartDashboard.putBoolean("/diagnostics/elevator/position" , elevatorUp);
    }

    //@param distance is in inches
    public void moveElevator(double distance){
        talon.setIntegralAccumulator(0);
        int talonPosition =  talon.getSelectedSensorPosition();
        double totalPulses = (distance/(diameter*Math.PI)) * pulsesPerRevolution * multiplier;
        talon.set(ControlMode.MotionMagic, totalPulses);
        double pulsesAfter = talon.getSelectedSensorPosition();
    }

   // public void moveArm(int numRevolutions){
     //   pid.setReference(numRevolutions, ControlType.kPosition);

    //}


    public void distanceHigh(){
        moveElevator(distanceHigh);
    }
    public void lowHatch(){
        moveElevator(distanceLowHatch);
    }

    public void cargoRocket(){
        moveElevator(distanceCargoRocket);
    }

    public void test(){
        talon.setSelectedSensorPosition(0);
        talon.setIntegralAccumulator(0);
        int initialPosition = talon.getSelectedSensorPosition();
        talon.set(ControlMode.Position, 1000);
        Faults fault = new Faults();
        talon.getFaults(fault);
        double motorVoltage = talon.getMotorOutputVoltage();
        
        double pulsesAfter = talon.getSelectedSensorPosition();
        int closedLoopError = talon.getClosedLoopError();
        double motorOutput = talon.getMotorOutputPercent();
    }

    public void testPower(){
        talon.set(ControlMode.PercentOutput , 0.5);
    }

    public void getPosition(){
     int position = talon.getSelectedSensorPosition();
    }

    public void elevatorUp(){
        if(counter < 3){
        counter++;
            moveElevator(elevatorDistances[counter]);
        }
    }

    public void elevatorDown(){
        if(counter > 0){
        counter--;
            moveElevator(elevatorDistances[counter]);
        }
    }
    public void zeroPosition(){
        talon.setSelectedSensorPosition(0);
    }
}