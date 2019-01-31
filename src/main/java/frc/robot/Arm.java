package frc.robot;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
 
public class Arm{
    private CANSparkMax spark;
    private CANEncoder encoder;
    private CANPIDController pid;
    private int numRevolutions;
   // double pulsesPerInch = 4096/(Math.PI*diameter of wheel)

    public Arm(int portNumber){
        spark = new CANSparkMax(portNumber, MotorType.kBrushless);
        encoder = spark.getEncoder();
        pid = spark.getPIDController();
    }


}
