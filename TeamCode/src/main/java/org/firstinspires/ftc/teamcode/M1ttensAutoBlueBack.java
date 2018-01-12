package org.firstinspires.ftc.teamcode;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

@Autonomous(name="AutoBlueBack", group="M1ttens")
public class M1ttensAutoBlueBack extends LinearOpMode {

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
                robotBase.turn(10);
                break;
            case RobotBaseM1ttens.JEWEL_RED_BLUE:
                robotBase.turn(350);
                break;
        }
        robotBase.jewelUp();

        //drop the glyph in the correct column
        robotBase.turn(0);
        robotBase.driveStraight(36, 0);
        switch (RobotBaseM1ttens.pictoPosition) {
            case LEFT:
                robotBase.turn(235);
                robotBase.driveStraight(46, 235);
                robotBase.grabberOpen();
                sleep(500);
                robotBase.driveStraight(3.5, 235, -0.5);
                break;
            case CENTER:
                robotBase.turn(242);
                robotBase.driveStraight(40, 242);
                robotBase.grabberOpen();
                sleep(500);
                robotBase.driveStraight(3.5, 242, -0.5);
                break;
            case RIGHT:
                robotBase.turn(254);
                robotBase.driveStraight(36, 254);
                robotBase.grabberOpen();
                sleep(500);
                robotBase.driveStraight(3.5, 254, -0.5);
                break;
            case UNKNOWN:
                robotBase.turn(242);
                robotBase.driveStraight(40, 242);
                robotBase.grabberOpen();
                sleep(500);
                robotBase.driveStraight(3.5, 242, -0.5);
                break;
        }


    }
}
