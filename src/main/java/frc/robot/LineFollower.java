/*package frc.robot;

import edu.wpi.first.wpilibj.AnalogInput;

public class LineFollower {
    private final AnalogInput leftSensor;
    private final AnalogInput middleSensor;
    private final AnalogInput rightSensor;
    private DriveTrain driveTrain;
    private final int whiteLevel = 600; // reading level is white if <600
    private final int blackLevel = 850; //reading level is black if >850
    public LineFollower(int leftPort, int middlePort, int rightPort , DriveTrain driveTrain) {
        leftSensor = new AnalogInput(leftPort);
        middleSensor = new AnalogInput(middlePort);
        rightSensor = new AnalogInput(rightPort);
        this.driveTrain = driveTrain;
    }

    public void align(){
        if(leftSensor.getValue() > blackLevel && rightSensor.getValue() > blackLevel && middleSensor.getValue() < whiteLevel){
            //go forward
            driveTrain.autoUpdateSpeed(0.3, -0.3);
        }

        else if (leftSensor.getValue() > blackLevel && middleSensor.getValue() > blackLevel && rightSensor.getValue() < whiteLevel){
            //turn right
            driveTrain.autoUpdateSpeed(-0.3, -0.3);

        }

        else if(leftSensor.getValue() > blackLevel && middleSensor.getValue() < whiteLevel && rightSensor.getValue() < whiteLevel){
            //turn right (a little less)
            driveTrain.autoUpdateSpeed(-0.2 , 0.2);
        }
        else if (leftSensor.getValue() < whiteLevel && middleSensor.getValue() > blackLevel && rightSensor.getValue() > blackLevel){
            //turn left
            driveTrain.autoUpdateSpeed(0.3, 0.3);
        }

        else if(leftSensor.getValue() < whiteLevel && middleSensor.getValue() < whiteLevel && rightSensor.getValue() > blackLevel){
            //turn left (a little less)
            driveTrain.autoUpdateSpeed(0.2, 0.2);

        }

        else{
            //driveForward
            driveTrain.autoUpdateSpeed(0.3, -0.3);
        }
    }


}
*/