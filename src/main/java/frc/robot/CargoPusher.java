package frc.robot;

import edu.wpi.first.wpilibj.Solenoid;

public class CargoPusher {
    private final Solenoid piston;

    CargoPusher(final int port) {
        piston = new Solenoid(port);
    }

    public void drop() {
        piston.set(true);
    }

    public void lock() {
        piston.set(false);
    }
}
