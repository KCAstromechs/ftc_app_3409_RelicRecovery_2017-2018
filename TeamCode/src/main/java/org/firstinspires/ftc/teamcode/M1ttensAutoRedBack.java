package org.firstinspires.ftc.teamcode;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

@Autonomous(name="AutoRedBack", group="M1ttens")
public class M1ttensAutoRedBack extends LinearOpMode {

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

        robotBase.turn(0);
        robotBase.driveStraight(36, 0);
        switch (RobotBaseM1ttens.pictoPosition) {
            case LEFT:
                robotBase.turn(107);
                robotBase.driveStraight(38, 107);
                robotBase.grabberOpen();
                sleep(500);
                robotBase.driveStraight(3.5, 107, -0.5);
                break;
            case CENTER:
                robotBase.turn(115.5f);
                robotBase.driveStraight(40, 115.5f);
                robotBase.grabberOpen();
                sleep(500);
                robotBase.driveStraight(3.5, 115.5f, -0.5);
                break;
            case RIGHT:
                robotBase.turn(124);
                robotBase.driveStraight(45, 124);
                robotBase.grabberOpen();
                sleep(500);
                robotBase.driveStraight(3.5, 124, -0.5);
                break;
            case UNKNOWN:
                robotBase.turn(115.5f);
                robotBase.driveStraight(40, 115.5f);
                robotBase.grabberOpen();
                sleep(500);
                robotBase.driveStraight(3.5, 115.5f, -0.5);
                break;
        }


    }
}
