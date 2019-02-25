package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Grabber {
    private VictorSP leftFlywheel;
    private VictorSP rightFlywheel;
    private TalonSRX rightClaw;
    private TalonSRX leftClaw;
    private DigitalInput leftLimitSwitch;
    private DigitalInput rightLimitSwitch;
    private final double flywheelSpeed = 0.5;
    private double pConstant = 0.6;
    private double iConstant = 0.001;
    private double dConstant = 0.0;
    private double fConstant = 0.0;
    private int pulsesPerRevolution = 4096;
    private int maxAmps = 3; 
    private final double openRevolutions = 12.5;
    private final double cargoRevolutions = 8.33;
    private final double hatchRevolutions = 1.389;
    private final double closedRevolutions = 0;
    private final double[] closedToOpenValues = {closedRevolutions, hatchRevolutions , cargoRevolutions , openRevolutions};
    private int counter; 



    public Grabber(int leftFlywheelPort , int rightFlywheelPort , int leftClawPort , int rightClawPort , int leftLimitPort , int rightLimitPort){
        leftFlywheel = new VictorSP(leftFlywheelPort);
        rightFlywheel = new VictorSP(rightFlywheelPort);
        rightClaw = new TalonSRX(rightClawPort);
        leftClaw = new TalonSRX(leftClawPort);
        counter = 0;

        SmartDashboard.putNumber("pid/claw/p", 0.0);
        SmartDashboard.putNumber("pid/claw/i", 0.0);
        SmartDashboard.putNumber("pid/claw/d", 0.0);
        SmartDashboard.putNumber("pid/claw/f", 0.0);
        
        //rightClaw.config_kD(0, dConstant);
        //rightClaw.config_kP(0, pConstant);
        //rightClaw.config_kI(0, iConstant);
       // rightClaw.config_kF(0, fConstant);
        rightClaw.configPeakCurrentLimit(maxAmps);
        rightClaw.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

        //leftClaw.config_kD(0, dConstant);
        //leftClaw.config_kP(0, pConstant);
        //leftClaw.config_kI(0, iConstant);
        //leftClaw.config_kF(0, fConstant);
        leftClaw.configPeakCurrentLimit(maxAmps);
        leftClaw.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

        leftLimitSwitch = new DigitalInput(leftLimitPort);
        rightLimitSwitch = new DigitalInput(rightLimitPort);
    }
    public void configurePID() {
        this.pConstant = SmartDashboard.getNumber("pid/claw/p", 0.2);
        this.iConstant = SmartDashboard.getNumber("pid/claw/i", pConstant / 10000);
        this.dConstant = SmartDashboard.getNumber("pid/claw/d", 0.0);
        this.fConstant = SmartDashboard.getNumber("pid/claw/f", 0.0);
        leftClaw.config_kD(0, dConstant);
        leftClaw.config_kP(0, pConstant);
        leftClaw.config_kI(0, iConstant);
        leftClaw.config_kF(0, fConstant);  

        rightClaw.config_kD(0, dConstant);
        rightClaw.config_kP(0, pConstant);
        rightClaw.config_kI(0, iConstant);
        rightClaw.config_kF(0, fConstant);
    }
    public void open(){
        move(openRevolutions);

    }  

    public void cargo(){
        move(cargoRevolutions);
    }  
    public void hatch(){
        move(hatchRevolutions);
    }
    public void closed(){
        move(closedRevolutions);
    }

    public void openClaw(){
        counter++;
        move(closedToOpenValues[counter]);
    }

    public void closeClaw(){
        counter--;
        move(closedToOpenValues[counter]);
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