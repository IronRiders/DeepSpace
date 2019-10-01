//package frc.team4180;// why is this diffrent?
package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class CargoPusher {

    private final DoubleSolenoid upperPiston;

    CargoPusher(final int port1, final int port2) {
        upperPiston = new DoubleSolenoid(port1, port2);
    }

    public void extend() {
        upperPiston.set(DoubleSolenoid.Value.kForward);
    }

    public void reset() {
        upperPiston.set(DoubleSolenoid.Value.kReverse);
    }
}