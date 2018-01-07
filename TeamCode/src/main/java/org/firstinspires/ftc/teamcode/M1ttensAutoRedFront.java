package org.firstinspires.ftc.teamcode;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

@Autonomous(name="AutoRedFront", group="M1ttens")
public class M1ttensAutoRedFront extends LinearOpMode {

    private RobotBaseM1ttens robotBase;

    private RelativeLayout squaresOverlay = null;
    private AppUtil appUtil = AppUtil.getInstance();

    @Override
    public void runOpMode() throws InterruptedException {
        robotBase = new RobotBaseM1ttens();
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

        robotBase.hasBeenZeroed=false;

        appUtil.synchronousRunOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (squaresOverlay != null){
                    ((ViewGroup)squaresOverlay.getParent()).removeView(squaresOverlay);
                }
                squaresOverlay = null;
            }
        });

        robotBase.grabberClose();
        sleep(500);
        robotBase.setLifterHeight(500);

        robotBase.vision(475, 480);

        robotBase.jewelDown();
        sleep(500);
        switch (RobotBaseM1ttens.jewelPosition) {
            case 1:
                robotBase.turn(350);
                break;
            case 2:
                robotBase.turn(10);
                break;
        }
        robotBase.jewelUp();

        robotBase.turn(78);
        switch (RobotBaseM1ttens.pictoPosition) {
            case RIGHT:
                robotBase.driveStraight(28, 90);
                break;
            case CENTER:
                robotBase.driveStraight(36, 90);
                break;
            case LEFT:
                robotBase.driveStraight(44, 90);
                break;
            case UNKNOWN:
                robotBase.driveStraight(36, 90);
                break;
        }
        robotBase.turn(180);

        robotBase.driveStraight(6, 180);

        robotBase.grabberOpen();

        robotBase.driveStraight(6, 180, -0.5);

        robotBase.setLifterHeight(100);

        robotBase.driveStraight(6, 180);
        robotBase.driveStraight(6, 180, -0.5);
        robotBase.driveStraight(7, 180);
        robotBase.driveStraight(6, 180, -0.5);
        robotBase.driveStraight(8, 180);
        robotBase.driveStraight(3.5, 180, -0.5);
    }
}