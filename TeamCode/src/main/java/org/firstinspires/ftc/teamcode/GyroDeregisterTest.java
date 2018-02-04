package org.firstinspires.ftc.teamcode;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by N2Class1 on 12/30/2017.
 */
@Autonomous(name = "gyroDeregisterTest", group = "test")
public class GyroDeregisterTest extends LinearOpMode implements SensorEventListener {
    //variables for gyro operation
    private float zero;
    private float rawGyro;
    public int sensorDataCounter = 0;

    //arrays for gyro operation
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];

    //objects for gyro operation
    private SensorManager mSensorManager;
    private Sensor mRotationVectorSensor;
    protected boolean hasBeenZeroed= false;

    // This is relative to the initial position of the robot.
    // Possible values are:  0-360
    // 0 is set as straight ahead of the robot, 90 is the right, 270 is to the left
    private float zRotation;


    @Override
    public void runOpMode() throws InterruptedException {
        //Accessing gyro and accelerometer from Android
        mSensorManager = (SensorManager) hardwareMap.appContext.getSystemService(SENSOR_SERVICE);
        mRotationVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        mSensorManager.registerListener(this, mRotationVectorSensor, 10000);

        sleep(10000);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
        SensorManager.getOrientation(rotationMatrix, orientation);

        sensorDataCounter++;

        rawGyro = (float) Math.toDegrees(orientation[0]);

        //If the zero hasn't been zeroed do the zero
        if (!hasBeenZeroed) {
            hasBeenZeroed = true;
            zero = rawGyro;
        }
        //Normalize zRotation to be used
        zRotation = normalize360(rawGyro - zero);

        if(Thread.interrupted()) {
            mSensorManager.unregisterListener(this);
        }

    }
    private float normalize360(float val) {
        while (val > 360 || val < 0) {

            if (val > 360) {
                val -= 360;
            }

            if (val < 0) {
                val += 360;
            }
        }
        return val;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
