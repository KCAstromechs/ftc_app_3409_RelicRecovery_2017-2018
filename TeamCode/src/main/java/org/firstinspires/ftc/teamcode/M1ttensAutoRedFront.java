package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="AutoRedFront", group="M1ttens")
public class M1ttensAutoRedFront extends LinearOpMode {

    private RobotBaseM1ttens robotBase;

    @Override
    public void runOpMode() throws InterruptedException {
        robotBase = new RobotBaseM1ttens();
        robotBase.init(this, hardwareMap);
        robotBase.initVuforia();

        waitForStart();

        robotBase.hasBeenZeroed=false;

        robotBase.jewelDown();
        switch (0) {
            case 0:
                robotBase.turn(350);
                break;
            case 1:
                robotBase.turn(10);
                break;
        }
        robotBase.jewelUp();

        robotBase.turn(90);
        switch (0) {
            case 0:
                robotBase.driveStraight(24, 90);
                break;
            case 1:
                robotBase.driveStraight(33, 90);
                break;
            case 2:
                robotBase.driveStraight(42, 90);
                break;
        }
        robotBase.turn(180);
    }
}
