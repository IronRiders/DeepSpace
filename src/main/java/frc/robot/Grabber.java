package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Grabber {
    private VictorSP leftFlywheel;
    private VictorSP rightFlywheel;
    private TalonSRX rightClaw;
    public TalonSRX leftClaw;
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
    private final double testRevolutions = 1.389*2;
    private final double closedRevolutions = 0;



    public Grabber(int leftFlywheelPort , int rightFlywheelPort , int leftClawPort , int rightClawPort , int leftLimitPort , int rightLimitPort){
        leftFlywheel = new VictorSP(leftFlywheelPort);
        rightFlywheel = new VictorSP(rightFlywheelPort);
        rightClaw = new TalonSRX(rightClawPort);
        leftClaw = new TalonSRX(leftClawPort);

        SmartDashboard.putNumber("pid/claw/p", 0.0);
        SmartDashboard.putNumber("pid/claw/i", 0.0);
        SmartDashboard.putNumber("pid/claw/d", 0.0);
        SmartDashboard.putNumber("pid/claw/f", 0.0);
        
        rightClaw.configPeakCurrentLimit(maxAmps);
        rightClaw.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        rightClaw.setInverted(true);

        leftClaw.configPeakCurrentLimit(maxAmps);
        leftClaw.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        leftClaw.setSensorPhase(true);

        leftClaw.configMotionCruiseVelocity(3500);
        leftClaw.configMotionAcceleration(7000);

        rightClaw.configMotionCruiseVelocity(3500);
        rightClaw.configMotionAcceleration(7000);

        rightClaw.setSelectedSensorPosition(0);
        leftClaw.setSelectedSensorPosition(0);
        leftLimitSwitch = new DigitalInput(leftLimitPort);
        rightLimitSwitch = new DigitalInput(rightLimitPort);
    }

    public void initializeSettings(){
        rightClaw.configPeakCurrentLimit(maxAmps);
        rightClaw.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        leftClaw.setSensorPhase(true);
        leftClaw.configPeakCurrentLimit(maxAmps);
        leftClaw.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

    }
    public void configurePID() {
        this.pConstant = SmartDashboard.getNumber("pid/claw/p", 0.2);
        this.iConstant = SmartDashboard.getNumber("pid/claw/i", pConstant / 10000);
        this.dConstant = SmartDashboard.getNumber("pid/claw/d", 0.0);
        this.fConstant = SmartDashboard.getNumber("pid/claw/f", 0.0);
        this.iConstant = (this.iConstant*this.pConstant)/1000;
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
        try {
            move(openRevolutions);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }  

    public void cargo(){
        try {
            move(cargoRevolutions);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }  
    public void hatch(){
        try {
            move(hatchRevolutions);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void closed(){
        try {
            move(closedRevolutions);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void move(double numRevolutions) throws InterruptedException {
        leftClaw.setIntegralAccumulator(0);
        rightClaw.setIntegralAccumulator(0);
        double totalPulses = numRevolutions * pulsesPerRevolution;
        System.out.println("About to move");
        leftClaw.set(ControlMode.MotionMagic, totalPulses);
        //rightClaw.set(ControlMode.Follower , totalPulses);
        //rightClaw.follow(leftClaw);
        System.out.println("Move complete");

        double motorVoltage = leftClaw.getMotorOutputVoltage();
        
        double pulsesAfter = rightClaw.getSelectedSensorPosition();
        int closedLoopError = rightClaw.getClosedLoopError();
        double motorOutput = rightClaw.getMotorOutputPercent();
        double rightMotorVoltage = rightClaw.getMotorOutputVoltage();
        double leftPulsesAfter = leftClaw.getSelectedSensorPosition();
        double leftClosedLoopError = leftClaw.getClosedLoopError();
        double leftMotorVoltage = leftClaw.getMotorOutputVoltage();
        System.out.println("Right Voltage: " + motorVoltage + "Position after: " + pulsesAfter + "Closed Loop Error" + closedLoopError);
        System.out.println("Left Voltage: " + leftMotorVoltage + "Position after: " + leftPulsesAfter + "Closed Loop Error" + leftClosedLoopError);
    }

    public void testTenDegrees(){
        try {
            move(testRevolutions);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

    public void output(){
        leftFlywheel.set(flywheelSpeed);
        rightFlywheel.set(-flywheelSpeed);

    }

    public void intake(){
        leftFlywheel.set(-flywheelSpeed);
        rightFlywheel.set(flywheelSpeed);
    }

    public void zeroPosition(){
        leftClaw.setSelectedSensorPosition(0);
        rightClaw.setSelectedSensorPosition(0);
    }

    public void stop(){
        leftFlywheel.set(0);
        rightFlywheel.set(0);
    }

    public void testEncoderPosition(){
        //double rightBeginningPosition = rightClaw.getSelectedSensorPosition();
        //double leftBeginningPosition = leftClaw.getSelectedSensorPosition();
       // System.out.println("Left: " + leftBeginningPosition + " Right: " + rightBeginningPosition);
        //leftClaw.set(ControlMode.PercentOutput , 0.2);
        double leftAfter = leftClaw.getSelectedSensorPosition();
        double rightAfter = rightClaw.getSelectedSensorPosition();
        System.out.println("Left: " + leftAfter + " Right: " + rightAfter);

    }

    public void spinClawMotor(){
        leftClaw.set(ControlMode.PercentOutput , 0.4);
        //rightClaw.set(ControlMode.PercentOutput , 0.4);
    }


}