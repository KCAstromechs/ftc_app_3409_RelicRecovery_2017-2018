package org.firstinspires.ftc.teamcode;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.internal.opmode.RegisteredOpModes;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

@Autonomous(name="RobotS_AutoBlueFront", group="RobotS")
public class RobotSAutoBlueFront extends LinearOpMode {

    RobotBaseS robotBase;

    private RelativeLayout squaresOverlay = null;
    private AppUtil appUtil = AppUtil.getInstance();

    @Override
    public void runOpMode() throws InterruptedException {
        robotBase = new RobotBaseS();
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

        appUtil.synchronousRunOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (squaresOverlay != null){
                    ((ViewGroup)squaresOverlay.getParent()).removeView(squaresOverlay);
                }
                squaresOverlay = null;
            }
        });

        robotBase.vision(0, 705); //TODO tune values
        switch (robotBase.jewelPosition) {
            case RobotBaseS.JEWEL_BLUE_RED:
                telemetry.addLine("Jewel Blue Red");
                break;
            case RobotBaseS.JEWEL_RED_BLUE:
                telemetry.addLine("Jewel Red Blue");
                break;
            case RobotBaseS.JEWEL_UNKNOWN:
                telemetry.addLine("Jewel Unknown");
                break;
        }
        robotBase.slapJewel(true);
        Thread.sleep(2000);


        telemetry.update();
        /*
        switch (robotBase.pictoPosition) {
            case LEFT:
                robotBase.driveStraight(28, 0);
                break;
            case UNKNOWN:
            case CENTER:
                robotBase.driveStraight(36, 0);
                break;
            case RIGHT:
                robotBase.driveStraight(44, 0);
                break;
        }
        robotBase.turn(90);
        robotBase.driveStraight(9, 90, -0.6);
        robotBase.extendGlyphter();
        sleep(750);
        robotBase.retractGlyphter();
        sleep(750);
        robotBase.driveStraight(9, 90);
        robotBase.driveStraight(12, 90, -0.6);
        */
        }
}
