
package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class HatchGrabbyThingy {

    public final DoubleSolenoid extendingPiston;
    public final DoubleSolenoid grabbingPiston;

    HatchGrabbyThingy(final int port3, final int port4, final int port5, final int port6) {
        extendingPiston = new DoubleSolenoid(port3, port4);
        grabbingPiston = new DoubleSolenoid(port5, port6);
        //rightPiston = new DoubleSolenoid(port5, port6);
    }

    public void extend() {
        extendingPiston.set(DoubleSolenoid.Value.kForward);
    }

    public void reteract() {
        extendingPiston.set(DoubleSolenoid.Value.kReverse);
    }


     public void grab() {
        grabbingPiston.set(DoubleSolenoid.Value.kForward);
     }

    public void release(){
        grabbingPiston.set(DoubleSolenoid.Value.kReverse);
    }
    
}

