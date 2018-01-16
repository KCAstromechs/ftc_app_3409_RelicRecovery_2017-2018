package org.firstinspires.ftc.teamcode;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.internal.opmode.RegisteredOpModes;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

@Autonomous(name="AutoBlueFront", group="Scorpius")
public class ScorpiusAutoBlueFront extends LinearOpMode {

    RobotBaseScorpius robotBase;

    private RelativeLayout squaresOverlay = null;
    private AppUtil appUtil = AppUtil.getInstance();

    @Override
    public void runOpMode() throws InterruptedException {
        robotBase = new RobotBaseScorpius();
        robotBase.init(this, hardwareMap);
        robotBase.initVuforia();

        appUtil.synchronousRunOnUiThread(new Runnable() {
            @Override
            public void run() {
                squaresOverlay = (RelativeLayout) View.inflate(appUtil.getActivity(), R.layout.jewel_lineup_square, null);
                squaresOverlay.findViewById(R.id.left_jewel).setVisibility(View.VISIBLE);
                squaresOverlay.findViewById(R.id.Origin).setVisibility(View.VISIBLE);
                appUtil.getActivity().addContentView(squaresOverlay, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        });

        waitForStart();

        robotBase.hasBeenZeroed = false;

        appUtil.synchronousRunOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (squaresOverlay != null){
                    ((ViewGroup)squaresOverlay.getParent()).removeView(squaresOverlay);
                }
                squaresOverlay = null;
            }
        });

        robotBase.vision(450, 720); //TODO tune values
        switch (robotBase.jewelPosition) {
            case RobotBaseScorpius.JEWEL_BLUE_RED:
                robotBase.slapJewel(true);
                telemetry.addLine("Jewel Blue Red");
                break;
            case RobotBaseScorpius.JEWEL_RED_BLUE:
                robotBase.slapJewel(false);
                telemetry.addLine("Jewel Red Blue");
                break;
            case RobotBaseScorpius.JEWEL_UNKNOWN:
                telemetry.addLine("Jewel Unknown");
                break;
        }
        Thread.sleep(2000);


        telemetry.update();
        switch (robotBase.pictoPosition) {
            case LEFT:
                robotBase.driveStraight(28, 0);
                telemetry.addLine("Left");
                break;
            case UNKNOWN:
                telemetry.addLine("Unknown");
            case CENTER:
                robotBase.driveStraight(36, 0);
                telemetry.addLine("Center");
                break;
            case RIGHT:
                robotBase.driveStraight(44, 0);
                telemetry.addLine("Right");
                break;
        }

        telemetry.update();

        robotBase.turn(70);
        robotBase.turn(90, 0.25);
        robotBase.extendGlyphter();
        sleep(1000);
        robotBase.retractGlyphter();
        sleep(750);
        robotBase.driveStraight(6, 90, -0.6);
        robotBase.driveStraight(6, 90);
        robotBase.driveStraight(9, 90, -0.6);
        robotBase.driveStraight(6, 90


        );
        }
}
