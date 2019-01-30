package frc.robot;

import edu.wpi.first.wpilibj.VictorSP;

public class Grabber {
    private VictorSP leftFlywheel;
    private VictorSP rightFlywheel;
    private final double flywheelSpeed = 0.5;

    public Grabber(int leftFlywheelPort , int rightFlywheelPort){
        leftFlywheel = new VictorSP(leftFlywheelPort);
        rightFlywheel = new VictorSP(rightFlywheelPort);

    }

    public void intake(){
        leftFlywheel.set(flywheelSpeed);
        rightFlywheel.set(-flywheelSpeed);

    }

    public void output(){
        leftFlywheel.set(-flywheelSpeed);
        rightFlywheel.set(flywheelSpeed);
    }


}