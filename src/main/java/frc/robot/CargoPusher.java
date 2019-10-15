package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class CargoPusher {
    private final DoubleSolenoid piston;

    CargoPusher(final int port1, final int port2) {
        piston = new DoubleSolenoid(port1, port2);
    }

    public void drop() {
        piston.set(DoubleSolenoid.Value.kForward);
    }

    public void lock() {
        piston.set(DoubleSolenoid.Value.kReverse);
    }
}
