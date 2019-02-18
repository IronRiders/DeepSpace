package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;

public class Elevator {
    private TalonSRX talon;
    private final double pConstant = 0.6; //we need to change these
    private final double iConstant = 0.001;
    private final double dConstant = 1.0;
    private final double fConstant = 0.0;
    private final int maxAmps = 6;
    DigitalInput limitSwitch;

    private final int distancePickUp = 0; //inches for all
    private final int distanceLowHatch = 19; 
    private final int distanceLowCargo = 21;
    private final int distanceMediumHigh = 22;
    double pulsesPerInch = 4096 / 2*Math.PI; 

    public Elevator(int elevatorPort , int limitSwitchPort){
        talon = new TalonSRX(elevatorPort);
        talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
        talon.config_kD(0, dConstant);
        talon.config_kP(0, pConstant);
        talon.config_kI(0, iConstant);
        talon.config_kF(0, fConstant);
        talon.configPeakCurrentLimit(maxAmps);
        limitSwitch = new DigitalInput(limitSwitchPort);
    }

    //@param distance is in inches
    public void move(double distance){
        double totalPulses = distance * pulsesPerInch;
        talon.set(ControlMode.Position, totalPulses);
    }

    public void zero(){
        // 1 is true(open) , zero is false(closed)
        if (limitSwitch.get()) {
            talon.set(ControlMode.PercentOutput, -0.3);
        }
        else {
            talon.set(ControlMode.PercentOutput, 0);
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
}