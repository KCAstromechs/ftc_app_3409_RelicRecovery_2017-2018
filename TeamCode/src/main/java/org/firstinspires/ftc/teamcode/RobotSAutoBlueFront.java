package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created by N2Class1 on 1/10/2018.
 */
@Autonomous(name="RobotS_AutoBlueFront", group="RobotS")
public class RobotSAutoBlueFront extends LinearOpMode {
    RobotBaseS robotBase;
    @Override
    public void runOpMode() throws InterruptedException {
        robotBase = new RobotBaseS();
        robotBase.init(this, hardwareMap);
        waitForStart();
        robotBase.driveStraight(36, 0);
        robotBase.turn(90);
        robotBase.driveStraight(9, 90, -0.6);
        sleep(500);
        robotBase.driveStraight(9, 90);
        robotBase.driveStraight(9, 90, -0.6);
        }
}
