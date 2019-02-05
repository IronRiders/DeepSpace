package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class ImageRecognition {
private boolean isImageRecTriggered ;
NetworkTableInstance nwtInstance;
NetworkTable table;
NetworkTableEntry centerX;
NetworkTableEntry startingX;
NetworkTableEntry endingX;


    public ImageRecognition(){
        isImageRecTriggered = false;
        nwtInstance = NetworkTableInstance.getDefault();
        table = nwtInstance.getTable("GRIP");
    }

    public void triggerImageRec(){
        isImageRecTriggered = !isImageRecTriggered;
    }

    public boolean isImageRecTriggered(){
        return isImageRecTriggered;
    }










}