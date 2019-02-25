
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
    private  double pConstantElevator = 0.2; //we need to change these
    private  double iConstantElevator = pConstantElevator / 10000;
    private  double dConstantElevator = 2;
    private  double fConstantElevator = 0.0;
    private final int maxAmpsElevator = 20;
    private final double diameter = 2.1875;
    private final int pulsesPerRevolution = 4096;
    DigitalInput limitSwitchElevator;


    private CANSparkMax spark;
    private CANEncoder encoder;
    private CANPIDController pid;
    private double pConstantArm = 0.2;
    private double iConstantArm = 0.001;
    private double dConstantArm = 0.0;
    private double fConstantArm = 0.0;
    private int maxAmpsArm = 6;
    DigitalInput limitSwitchArm;

    //arm
    private final int mediumHatchRevolutions = 13;
    private final int mediumCargoRevolutions = 17;
    private final int highHatchRevolutions = 29;
    private final int distancePickUp = 0; 
 
    //elevator
    private final int distanceLowHatch = 19; 
    private final int distanceLowCargo = 21;
    private final int distanceMediumHigh = 22;

    public ElevatorArm(int elevatorPort , int elevatorlimitSwitchPort , int armPort , int armLimitSwitchPort){
        SmartDashboard.putNumber("pid/elevator/p", 0.0);
        SmartDashboard.putNumber("pid/elevator/i", 0.0);
        SmartDashboard.putNumber("pid/elevator/d", 0.0);
        SmartDashboard.putNumber("pid/elevator/f", 0.0);
        
        talon = new TalonSRX(elevatorPort);
        talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
        talon.setSensorPhase(true);
        talon.setSelectedSensorPosition(0);
        talon.configPeakCurrentLimit(maxAmpsElevator);
        limitSwitchElevator = new DigitalInput(elevatorlimitSwitchPort);

        spark = new CANSparkMax(armPort, MotorType.kBrushless);
        encoder = spark.getEncoder();
        pid = spark.getPIDController();
        SmartDashboard.putNumber("pid/arm/p", 0.0);
        SmartDashboard.putNumber("pid/arm/i", 0.0);
        SmartDashboard.putNumber("pid/arm/d", 0.0);
        SmartDashboard.putNumber("pid/arm/f", 0.0);
        spark.setSecondaryCurrentLimit(maxAmpsArm);
        limitSwitchArm = new DigitalInput(armLimitSwitchPort);


    }
    public void configurePID() {
        this.pConstantElevator = SmartDashboard.getNumber("pid/elevator/p", 0.2);
        this.iConstantElevator = SmartDashboard.getNumber("pid/elevator/i", (0.2 / 10000));
        this.dConstantElevator = SmartDashboard.getNumber("pid/elevator/d", 2.0);
        this.fConstantElevator = SmartDashboard.getNumber("pid/elevator/f", 0.0);
        talon.config_kD(0, dConstantElevator);
        talon.config_kP(0, pConstantElevator);
        talon.config_kI(0, iConstantElevator);
        talon.config_kF(0, fConstantElevator);  

        this.pConstantArm = SmartDashboard.getNumber("pid/elevator/p", 0.2);
        this.iConstantArm = SmartDashboard.getNumber("pid/elevator/i",(0.2/ 10000));
        this.dConstantArm = SmartDashboard.getNumber("pid/elevator/d", 2.0);
        this.fConstantArm = SmartDashboard.getNumber("pid/elevator/f", 0.0);
        pid.setD(dConstantArm);
        pid.setP(pConstantArm);
        pid.setI(iConstantArm);
        pid.setFF(fConstantArm);
    }


    public void pushySwitch(){
        // 1 is true(open) , zero is false(closed)
        if (limitSwitchArm.get()) {
            spark.set( -0.3);
        }   
        else {
            spark.set(0);
        }
    }

    //@param distance is in inches
    public void moveElevator(double distance){
        talon.setIntegralAccumulator(0);
        int talonPosition =  talon.getSelectedSensorPosition();
        double totalPulses = (distance/(diameter*Math.PI)) * pulsesPerRevolution;
        talon.set(ControlMode.Position, totalPulses);
        double pulsesAfter = talon.getSelectedSensorPosition();
    }

    public void moveArm(int numRevolutions){
        pid.setReference(numRevolutions, ControlType.kPosition);

    }


    public void zero(){
        // 1 is true(open) , zero is false(closed)
        if (limitSwitchElevator.get()) {
            talon.set(ControlMode.PercentOutput, -0.3);
        }
        else {
            talon.set(ControlMode.PercentOutput, 0);
            talon.setSelectedSensorPosition(0);
        }
    }
    

    public void stop(){
        talon.set(ControlMode.PercentOutput , 0);
    }

    public void lowCargo(){
        moveElevator(distanceLowCargo);
    }
    public void lowHatch(){
        moveElevator(distanceLowHatch);
    }

    public void mediumHigh(){
        moveElevator(distanceMediumHigh);
    }

    public void lowerToZero(){
        moveElevator(distancePickUp);
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

    public void mediumCargo(){
        moveElevator(distanceMediumHigh);
        moveArm(mediumCargoRevolutions);
    }

    public void mediumHatch(){
        moveElevator(distanceMediumHigh);
        moveArm(mediumHatchRevolutions);
    }

    public void highHatch(){
        moveElevator(distanceMediumHigh);
        moveArm(highHatchRevolutions);
    }

    public void getToMediumHatch(){
        moveArm(mediumHatchRevolutions);
    }
s
    public void getToMediumCargo(){
        moveArm(mediumCargoRevolutions);
    }

    public void getToHighHatch(){
        moveArm(highHatchRevolutions);
    }
}