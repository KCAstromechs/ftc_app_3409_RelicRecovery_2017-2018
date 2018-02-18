package org.firstinspires.ftc.teamcode;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.internal.opmode.RegisteredOpModes;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

@Autonomous(name="ghjyukl", group="Scorpiu88s")
public class cgmhjyb extends LinearOpMode {

    RobotBaseScorpius robotBase;

    private RelativeLayout squaresOverlay = null;
    private AppUtil appUtil = AppUtil.getInstance();

    @Override
    public void runOpMode() throws InterruptedException {
        robotBase = new RobotBaseScorpius();
        robotBase.init(this, hardwareMap);

        robotBase.openGrabby();

        waitForStart();

        robotBase.motorScoop.setPower(0.5);
        while(Math.abs(robotBase.motorScoop.getCurrentPosition()) < 1520) {
            Thread.sleep(10);
        }
        robotBase.motorScoop.setPower(0);

        sleep(5000);
        telemetry.addData("g", robotBase.motorScoop.getCurrentPosition());
        telemetry.update();
        sleep(10000);
    }
}
