package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;


@Autonomous(name="MVPauto", group="test")
public class MVPauto extends LinearOpMode {

    private RobotBaseM1ttens robotBase;

    @Override
    public void runOpMode() throws InterruptedException {
        robotBase = new RobotBaseM1ttens();
        robotBase.init(this, hardwareMap);
        robotBase.initVuforia();

        waitForStart();

        robotBase.vision();

        telemetry.addData("jewel", robotBase.jewelPosition);
        telemetry.addData("picto", robotBase.pictoPosition);
        telemetry.update();

        robotBase.turn(270);
        robotBase.driveStraight(32, 270);
        robotBase.turn(180);
    }
}