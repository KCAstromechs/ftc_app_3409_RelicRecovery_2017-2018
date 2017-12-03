package org.firstinspires.ftc.teamcode;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import static android.content.Context.SENSOR_SERVICE;

// TODO: kill ralph

@Autonomous(name="Ralph", group="test")
public class Ralph extends LinearOpMode {

    private RobotBaseM1ttens robotBase;

    @Override
    public void runOpMode() throws InterruptedException {
        robotBase = new RobotBaseM1ttens();
        robotBase.init(this, hardwareMap);

        waitForStart();

        robotBase.driveStraight(10, 0);
        sleep(1000);
        robotBase.turn(60);
        sleep(1000);
        robotBase.driveStraight(10, 60);
        sleep(1000);
        robotBase.turn(90);
        sleep(1000);
        robotBase.turn(0);
    }
}