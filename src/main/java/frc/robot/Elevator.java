package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Elevator {
    private TalonSRX talon;
    private final double pConstant = 0.3;
    private final double iConstant = 0.3;
    private final double dConstant = 0.3;
    private final double fConstant = 0.0;

    public Elevator(int elevatorPort){
        talon = new TalonSRX(elevatorPort);
        talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
        talon.config_kD(0, dConstant);
        talon.config_kP(0, pConstant);
        talon.config_kI(0, iConstant);
        talon.config_kF(0, fConstant);
    }

    //@param distance is in inches
    public void move(double distance){
        double totalPulses = distance * (4096/(Math.PI*6));
        talon.set(ControlMode.Position, totalPulses);
    }










}