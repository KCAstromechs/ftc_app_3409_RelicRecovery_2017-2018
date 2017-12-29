package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="AutoBlueFront", group="M1ttens")
public class M1ttensAutoBlueFront extends LinearOpMode {

    private RobotBaseM1ttens robotBase;

    @Override
    public void runOpMode() throws InterruptedException {
        robotBase = new RobotBaseM1ttens();
        robotBase.init(this, hardwareMap);
        robotBase.initVuforia();

        waitForStart();


        robotBase.jewelDown();
        switch (0) {
            case 0:
                robotBase.turn(10);
                break;
            case 1:
                robotBase.turn(350);
                break;
        }
        robotBase.jewelUp();

        robotBase.turn(270);
        switch (0) {
            case 0:
                robotBase.driveStraight(24, 270);
                break;
            case 1:
                robotBase.driveStraight(33, 270);
                break;
            case 2:
                robotBase.driveStraight(42, 270);
                break;
        }
        robotBase.turn(180);
    }
}