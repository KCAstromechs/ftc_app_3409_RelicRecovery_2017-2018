package org.firstinspires.ftc.teamcode;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

@Autonomous(name="ywrtstjehdge", group="Scorpius")
public class Scorpiu80iulewytd2g21hnwmrpwyr1w extends LinearOpMode {

    RobotBaseScorpius robotBase;

    @Override
    public void runOpMode() throws InterruptedException {
        robotBase = new RobotBaseScorpius();
        robotBase.init(this, hardwareMap);

        waitForStart();

        robotBase.strafe(10, 0, -0.6);
        }
}
