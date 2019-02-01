package frc.robot;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.DigitalInput;
 
public class Arm{
    private CANSparkMax spark;
    private CANEncoder encoder;
    private CANPIDController pid;
    private int numRevolutions = 5; //ask bailey
    private final double pConstant = 0.3;
    private final double iConstant = 0.01;
    private final double dConstant = 4.0;
    private final double fConstant = 0.0;
    private final int maxMotorAmps = 5;
    DigitalInput limitSwitch;
   // double pulsesPerInch = 4096/(Math.PI*diameter of wheel)

    public Arm(int portNumber, int limitSwitchPort){
        spark = new CANSparkMax(portNumber, MotorType.kBrushless);
        encoder = spark.getEncoder();
        pid = spark.getPIDController();
        pid.setD(dConstant);
        pid.setP(pConstant);
        pid.setI(dConstant);
        pid.setFF(fConstant);
        spark.setSecondaryCurrentLimit(maxMotorAmps);
        limitSwitch = new DigitalInput(limitSwitchPort);


    }

    public void makeItFastOrSlow(){
        pid.setReference(numRevolutions, ControlType.kPosition);

    }
    public void pushySwitch(){
        // 1 is true(open) , zero is false(closed)
        if (limitSwitch.get()) {
            spark.set( -0.3);
        }   
        else {
            spark.set(0);
        }
    }
}
