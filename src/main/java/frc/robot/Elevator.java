
package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;

public class Elevator {
    private TalonSRX talon;
    private final double pConstant = 0.2; //we need to change these
    private final double iConstant = pConstant / 10000;
    private final double dConstant = 0.0;
    private final double fConstant = 0.0;
    private final int maxAmps = 20;
    DigitalInput limitSwitch;

    private final int distancePickUp = 0; //inches for all and baly did the math wrong
    private final int distanceLowHatch = 19; 
    private final int distanceLowCargo = 21;
    private final int distanceMediumHigh = 22;

    public Elevator(int elevatorPort , int limitSwitchPort){
        talon = new TalonSRX(elevatorPort);
        talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
        talon.setSensorPhase(true);
        talon.setSelectedSensorPosition(0);
        talon.config_kD(0, dConstant);
        talon.config_kP(0, pConstant);
        talon.config_kI(0, iConstant);
        talon.config_kF(0, fConstant);
        talon.configPeakCurrentLimit(maxAmps);
        //talon.configClosedloopRamp(0.25);
        //limitSwitch = new DigitalInput(limitSwitchPort);
    }

    //@param distance is in inches
    public void move(double distance){
        talon.setIntegralAccumulator(0);
        int talonPosition =  talon.getSelectedSensorPosition();
        double totalPulses = (distance/(2.1875*Math.PI)) * 4096;
        talon.set(ControlMode.Position, totalPulses);
        double pulsesAfter = talon.getSelectedSensorPosition();
    }

    public void zero(){
        // 1 is true(open) , zero is false(closed)
        if (limitSwitch.get()) {
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
        move(distanceLowCargo);
    }
    public void lowHatch(){
        move(distanceLowHatch);
    }

    public void mediumHigh(){
        move(distanceMediumHigh);
    }

    public void lowerToZero(){
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
}