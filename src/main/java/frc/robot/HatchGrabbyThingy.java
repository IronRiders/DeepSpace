
package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class HatchGrabbyThingy {

    public final DoubleSolenoid leftPiston;
    //public final DoubleSolenoid rightPiston;

    HatchGrabbyThingy(final int port3, final int port4, final int port5, final int port6) {
        leftPiston = new DoubleSolenoid(port3, port4);
        //rightPiston = new DoubleSolenoid(port5, port6);
    }

    public void grabLeft() {
        leftPiston.set(DoubleSolenoid.Value.kForward);
    }

    public void releaseLeft() {
        leftPiston.set(DoubleSolenoid.Value.kReverse);
    }


     public void grabRight() {
        leftPiston.set(DoubleSolenoid.Value.kForward);
     }

    public void releaseRight(){
        leftPiston.set(DoubleSolenoid.Value.kReverse);
    }
    


public void grabHatch(){
    grabLeft();
    grabRight();
}

public void releaseHatch(){
    releaseLeft();
    releaseRight();
}

}