
package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Elevator {
    Timer timer = new Timer();
    double previousTime = 0;
    private TalonSRX talon;
    private  double pConstant = 0.2; //we need to change these
    private  double iConstant = pConstant / 10000;
    private  double dConstant = 0.0;
    private  double fConstant = 0.0;
    private final int maxAmps = 20;
    private final double diameter = 2.1875;
    private final int pulsesPerRevolution = 4096;
    DigitalInput limitSwitch;

    private final int distancePickUp = 0; //inches for all and baly did the math wrong
    private final int distanceLowHatch = 19; 
    private final int distanceLowCargo = 21;
    private final int distanceMediumHigh = 22;

    public Elevator(int elevatorPort , int limitSwitchPort){
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
        // talon.config_kD(0, dConstant);
        // talon.config_kP(0, pConstant);
        // talon.config_kI(0, iConstant);
        // talon.config_kF(0, fConstant);
        talon.configPeakCurrentLimit(maxAmps);
        limitSwitch = new DigitalInput(limitSwitchPort);
    }
    public void configurePID() {
        this.pConstant = SmartDashboard.getNumber("pid/elevator/p", 0.2);
        this.iConstant = SmartDashboard.getNumber("pid/elevator/i", pConstant / 10000);
        this.dConstant = SmartDashboard.getNumber("pid/elevator/d", 0.0);
        this.fConstant = SmartDashboard.getNumber("pid/elevator/f", 0.4);
        talon.config_kD(0, dConstant);
        talon.config_kP(0, pConstant);
        talon.config_kI(0, iConstant);
        talon.config_kF(0, fConstant);  
    }

    //@param distance is in inches
    public void move(double distance){
        talon.setIntegralAccumulator(0);
        //int talonPosition =  talon.getSelectedSensorPosition();
        double totalPulses = (distance/(diameter*Math.PI)) * pulsesPerRevolution;
        talon.set(ControlMode.MotionMagic, totalPulses);
        double pulsesAfter = talon.getSelectedSensorPosition();
    }

    public void zeroLimitSwitch(){
        // 1 is true(open) , zero is false(closed)
        if (limitSwitch.get() || timer.get() - previousTime < 3) {
            talon.set(ControlMode.PercentOutput, -0.2);
        }
        else {
            talon.set(ControlMode.PercentOutput, 0);
            talon.setSelectedSensorPosition(0);
        }
        previousTime = timer.get();
    }
    

    public void stop(){
        talon.set(ControlMode.PercentOutput , 0);
    }

    public void lowCargo(){
        move(distanceLowCargo);
    }
    public void lowHatch(){
        move(distanceLowHatch);
    }

    public void mediumHigh(){
        move(distanceMediumHigh);
    }

    public void zeroPid(){
        move(distancePickUp);
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

    public void resetToFactorySettings(){
        talon.configFactoryDefault();
    }
}