package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Disabled
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
        robotBase.extendGlyphter();
        sleep(500);
        robotBase.retractGlyphter();
        sleep(500);
        robotBase.driveStraight(9, 90);
        robotBase.driveStraight(12, 90, -0.6);
        }
}
