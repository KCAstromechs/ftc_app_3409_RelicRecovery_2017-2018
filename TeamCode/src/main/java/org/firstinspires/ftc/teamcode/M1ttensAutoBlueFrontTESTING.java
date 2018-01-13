package org.firstinspires.ftc.teamcode;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

@Autonomous(name="AutoBlueFrontTESTING", group="M1ttens")
public class M1ttensAutoBlueFrontTESTING extends LinearOpMode {

    private RobotBaseM1ttens robotBase;

    private RelativeLayout squaresOverlay = null;
    private AppUtil appUtil = AppUtil.getInstance();

    @Override
    public void runOpMode() throws InterruptedException {
        robotBase = new RobotBaseM1ttens();
        robotBase.init(this, hardwareMap);
        robotBase.initVuforia();

        //create the alignment box for the robot initially
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
         //erase the alignment box
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
            case RobotBaseM1ttens.JEWEL_BLUE_RED:
                robotBase.turn(10);
                break;
            case RobotBaseM1ttens.JEWEL_RED_BLUE:
                robotBase.turn(350);
                break;
        }
        robotBase.jewelUp();

        robotBase.turn(282);
        switch (RobotBaseM1ttens.pictoPosition) {
            case LEFT:
                robotBase.driveStraight(28, 270);
                break;
            case CENTER:
                robotBase.driveStraight(36, 270);
                break;
            case RIGHT:
                robotBase.driveStraight(44, 270);
                break;
            case UNKNOWN:
                robotBase.driveStraight(36, 270);
                break;
        }
        robotBase.turn(180);

        robotBase.driveStraight(6, 180);

        robotBase.grabberMid();

        robotBase.driveStraight(6, 180, -0.5);

        robotBase.grabberOpen();
        robotBase.setLifterHeight(100);

        robotBase.driveStraight(8, 180);
        robotBase.driveStraight(10, 180, -0.5);
        robotBase.turn(0);
        robotBase.driveStraight(20, 0);
        robotBase.grabberMid();
        robotBase.driveStraight(2, 0);
        robotBase.grabberClose();
        sleep(500);
        robotBase.driveStraight(12, 0, -0.5);
        robotBase.turn(180);
        robotBase.setLifterHeight(1375);
        robotBase.driveStraight(20, 180);
        robotBase.grabberMid();
        robotBase.driveStraight(6, 180, -0.5);
        robotBase.setLifterHeight(100);
        robotBase.grabberOpen();
        robotBase.driveStraight(8, 180);
        robotBase.driveStraight(3.5, 180, -0.5);
    }
}