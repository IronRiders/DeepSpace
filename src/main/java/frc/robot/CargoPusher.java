//package frc.team4180;// why is this diffrent?
package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class CargoPusher {
    public boolean BoxTipped;
    private final DoubleSolenoid upperPiston;
    //For now cargoPusher handles both cargo and hatches lille debugging. 
    //To run just hatches remove code for ports 4-6

    CargoPusher(final int port1, final int port2) {
        upperPiston = new DoubleSolenoid(port1, port2);
    }

    public void drop() {
        upperPiston.set(DoubleSolenoid.Value.kForward);
        //BoxTipped=true;
    }

    public void lock() {
        upperPiston.set(DoubleSolenoid.Value.kReverse);
    }
}
