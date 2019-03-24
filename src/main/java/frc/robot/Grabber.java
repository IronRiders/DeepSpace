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
    private double pConstant = 0.3; //0.4 is what it was before the last updated testing
    private double iConstant = 0.0;
    private double dConstant = 0.0;
    private double fConstant = 0.2;
    private int pulsesPerRevolution = 4096;
    private int maxAmps = 3; 
    private final double openRevolutions = 9.2;
    private final double cargoRevolutions = 7.5;
    private final double hatchRevolutions = 4;
    private final double closedRevolutions = -1.6;
    private final double grabCargoTightRevolutions = cargoRevolutions - 1.5;
    //private final double closedRevolutionsLeft = -7500;
    private final double[] closedToOpenValues = {closedRevolutions, hatchRevolutions , grabCargoTightRevolutions , cargoRevolutions , openRevolutions};
    private int counter; 



    public Grabber(int leftFlywheelPort , int rightFlywheelPort , int leftClawPort , int rightClawPort , int leftLimitPort , int rightLimitPort){
        leftFlywheel = new VictorSP(leftFlywheelPort);
        rightFlywheel = new VictorSP(rightFlywheelPort);
        rightClaw = new TalonSRX(rightClawPort);
        leftClaw = new TalonSRX(leftClawPort);
        SmartDashboard.putNumber("status/claw/position", 1);
        counter = 0;

       // SmartDashboard.putNumber("pid/claw/p", 0.0);
       // SmartDashboard.putNumber("pid/claw/i", 0.0);
       // SmartDashboard.putNumber("pid/claw/d", 0.0);
       // SmartDashboard.putNumber("pid/claw/f", 0.0);
        
        rightClaw.config_kD(0, dConstant);
        rightClaw.config_kP(0, pConstant);
        rightClaw.config_kI(0, iConstant);
        rightClaw.config_kF(0, fConstant);

        rightClaw.configPeakCurrentLimit(maxAmps);
        rightClaw.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        rightClaw.setInverted(true);
        rightClaw.setSensorPhase(true);
        //rightClaw.setSelectedSensorPosition((int)hatchRevolutions*4096);
        rightClaw.setSelectedSensorPosition(0);
        rightClaw.configMotionCruiseVelocity(3000);
        rightClaw.configMotionAcceleration(6000);

       
        leftClaw.config_kD(0, dConstant);
        leftClaw.config_kP(0, pConstant);
        leftClaw.config_kI(0, iConstant);
        leftClaw.config_kF(0, fConstant);
        
        leftClaw.configPeakCurrentLimit(maxAmps);
        leftClaw.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        leftClaw.setSensorPhase(true); 
        leftClaw.configMotionCruiseVelocity(3000);
        leftClaw.configMotionAcceleration(6000);
        //leftClaw.setSelectedSensorPosition((int)hatchRevolutions*4096);
        leftClaw.setSelectedSensorPosition(0);
        leftLimitSwitch = new DigitalInput(leftLimitPort);
        rightLimitSwitch = new DigitalInput(rightLimitPort);
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
        move(openRevolutions);
    }  

    public void cargo(){
        move(cargoRevolutions);
    }  
    public void hatch(){
        move(hatchRevolutions);
    }
    public void closed(){
        leftClaw.set(ControlMode.MotionMagic, closedRevolutions);
        rightClaw.set(ControlMode.MotionMagic, closedRevolutions);
    }

    public void openClaw(){
        if(counter < 4){
            counter++;
            move(closedToOpenValues[counter]);
        }
    }

    public void closeClaw(){
        if(counter > 0){
            counter--;
             move(closedToOpenValues[counter]);
        }
    }
    public void move(double numRevolutions){
            leftClaw.setIntegralAccumulator(0);
            rightClaw.setIntegralAccumulator(0);
            double totalPulses = numRevolutions * pulsesPerRevolution;
            leftClaw.set(ControlMode.MotionMagic, totalPulses);
            rightClaw.set(ControlMode.MotionMagic , totalPulses);
            SmartDashboard.putNumber("status/claw/position", counter + 1);
            
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

    public void updateSpeed(final LambdaJoystick.ThrottlePosition throttlePosition){
        if(Math.abs(throttlePosition.y) > 0.3) {
            leftFlywheel.set(-1*throttlePosition.y);
            rightFlywheel.set(throttlePosition.y);
        }
        else{
            leftFlywheel.set(0);
            rightFlywheel.set(0);
        }
    }

    public void stop(){
        leftFlywheel.set(0);
        rightFlywheel.set(0);
    }

    public void zeroPosition(){
        leftClaw.setSelectedSensorPosition(0);
        rightClaw.setSelectedSensorPosition(0);
    }

    public void testEncoderPosition(){
        double rightPosition = rightClaw.getSelectedSensorPosition();
        System.out.println(rightPosition);
        double leftPosition = leftClaw.getSelectedSensorPosition();
        System.out.println(leftPosition);
    }

    public void resetEncoders(){
        rightClaw.setSelectedSensorPosition(0);
        leftClaw.setSelectedSensorPosition(0);
    }
}