package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.RobotBaseScorpius;

/**
 * Created by N2Class1 on 4/5/2018.
 */
@Disabled
@Autonomous(name="DriveLengthTest", group="Test")
public class DriveLengthTestAuto extends LinearOpMode {

    RobotBaseScorpius robotBase;

    @Override
    public void runOpMode() throws InterruptedException {
        robotBase = new RobotBaseScorpius();

        robotBase.init(this, hardwareMap);

        waitForStart();

        robotBase.driveStraight(60, 0, 0.5, true);
        sleep(2000);
        robotBase.driveStraight(60, 0, -0.5, true);
    }
}
