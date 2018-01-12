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

        //initialize all bot stuff
        robotBase = new RobotBaseM1ttens();
        robotBase.init(this, hardwareMap);
        robotBase.initVuforia();

        //initialize green square for lineup
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

        //get rid of green square
        appUtil.synchronousRunOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (squaresOverlay != null){
                    ((ViewGroup)squaresOverlay.getParent()).removeView(squaresOverlay);
                }
                squaresOverlay = null;
            }
        });

        //grab glyph and lift it up
        robotBase.grabberClose();
        sleep(500);
        robotBase.setLifterHeight(500);

        //look at jewel and pictograph
        robotBase.vision(475, 480);

        //knock off correct jewel
        robotBase.jewelDown();
        sleep(500);
        switch (RobotBaseM1ttens.jewelPosition) {
            case RobotBaseM1ttens.JEWEL_BLUE_RED:
                robotBase.turn(350);
                break;
            case RobotBaseM1ttens.JEWEL_RED_BLUE:
                robotBase.turn(10);
                break;
        }
        robotBase.jewelUp();

        //drop the glyph in the correct column
        robotBase.turn(0);
        robotBase.driveStraight(36, 0);
        switch (RobotBaseM1ttens.pictoPosition) {
            case LEFT:
                robotBase.turn(107);
                robotBase.driveStraight(36, 107);
                robotBase.grabberMid();
                sleep(500);
                robotBase.driveStraight(3.5, 107, -0.5);
                robotBase.grabberOpen();
                break;
            case CENTER:
                robotBase.turn(115);
                robotBase.driveStraight(40, 115);
                robotBase.grabberMid();
                sleep(500);
                robotBase.driveStraight(3.5, 115, -0.5);
                robotBase.grabberOpen();
                break;
            case RIGHT:
                robotBase.turn(123);
                robotBase.driveStraight(46, 123);
                robotBase.grabberMid();
                sleep(500);
                robotBase.driveStraight(3.5, 123, -0.5);
                robotBase.grabberOpen();
                break;
            case UNKNOWN:
                robotBase.turn(115);
                robotBase.driveStraight(40, 115);
                robotBase.grabberMid();
                sleep(500);
                robotBase.driveStraight(3.5, 115, -0.5);
                robotBase.grabberOpen();
                break;
        }


    }
}
