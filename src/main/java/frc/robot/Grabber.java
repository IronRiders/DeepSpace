package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;

public class Grabber {
    private VictorSP leftFlywheel;
    private VictorSP rightFlywheel;
    private TalonSRX rightClaw;
    private TalonSRX leftClaw;
    private DigitalInput leftLimitSwitch;
    private DigitalInput rightLimitSwitch;
    private final double flywheelSpeed = 0.5;
    private final double pConstant = 0.6;
    private final double iConstant = 0.001;
    private final double dConstant = 1.0;
    private final double fConstant = 0.0;
    private final int pulsesPerRevolution = 4096;
    private final int maxAmps = 3; 
    private final double openRevolutions = 12.5;
    private final double cargoRevolutions = 8.33;
    private final double hatchRevolutions = 1.389;
    private final double closedRevolutions = 0;



    public Grabber(int leftFlywheelPort , int rightFlywheelPort , int leftClawPort , int rightClawPort , int leftLimitPort , int rightLimitPort){
        leftFlywheel = new VictorSP(leftFlywheelPort);
        rightFlywheel = new VictorSP(rightFlywheelPort);
        rightClaw = new TalonSRX(rightClawPort);
        leftClaw = new TalonSRX(leftClawPort);
        
        rightClaw.config_kD(0, dConstant);
        rightClaw.config_kP(0, pConstant);
        rightClaw.config_kI(0, iConstant);
        rightClaw.config_kF(0, fConstant);
        rightClaw.configPeakCurrentLimit(maxAmps);
        rightClaw.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

        leftClaw.config_kD(0, dConstant);
        leftClaw.config_kP(0, pConstant);
        leftClaw.config_kI(0, iConstant);
        leftClaw.config_kF(0, fConstant);
        leftClaw.configPeakCurrentLimit(maxAmps);
        leftClaw.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

        leftLimitSwitch = new DigitalInput(leftLimitPort);
        rightLimitSwitch = new DigitalInput(rightLimitPort);
    }

    public void open(){
        leftClaw.config_kI(0,iConstant);
        rightClaw.config_kI(0,iConstant);
        move(openRevolutions);

    }  

    public void cargo(){
        leftClaw.config_kI(0,iConstant);
        rightClaw.config_kI(0,iConstant);
        move(cargoRevolutions);
    }  
    public void hatch(){
        leftClaw.config_kI(0,0.0);
        rightClaw.config_kI(0,0.0);
        move(hatchRevolutions);
    }
    public void closed(){
        leftClaw.config_kI(0,iConstant);
        rightClaw.config_kI(0,iConstant);
        move(closedRevolutions);
    }
    public void move(double numRevolutions){
            double totalPulses = numRevolutions * pulsesPerRevolution;
            leftClaw.set(ControlMode.Position, totalPulses);
            rightClaw.set(ControlMode.Position , totalPulses);
        }
    
        public void toLimitSwitches(){
            // 1 is true(open) , zero is false(closed)
            if (rightLimitSwitch.get()) {
                rightClaw.set(ControlMode.PercentOutput, -0.3);
            }
            else {
                rightClaw.set(ControlMode.PercentOutput, 0);
            }

            if (leftLimitSwitch.get()) {
                leftClaw.set(ControlMode.PercentOutput, -0.3);
            }
            else {
                leftClaw.set(ControlMode.PercentOutput, 0);
            }

        }

    public void intake(){
        leftFlywheel.set(flywheelSpeed);
        rightFlywheel.set(-flywheelSpeed);

    }

    public void output(){
        leftFlywheel.set(-flywheelSpeed);
        rightFlywheel.set(flywheelSpeed);
    }

    public void stop(){
        leftFlywheel.set(0);
        rightFlywheel.set(0);
    }


}