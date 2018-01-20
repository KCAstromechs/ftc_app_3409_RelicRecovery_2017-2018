package org.firstinspires.ftc.teamcode;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

@Autonomous (name="AutoRedBack", group = "Scorpius")
public class ScorpiusAutoRedBack extends LinearOpMode {

    private RelativeLayout squaresOverlay = null;
    private AppUtil appUtil = AppUtil.getInstance();

    @Override
    public void runOpMode() throws InterruptedException {

        //initialize all bot stuff
        RobotBaseScorpius robotBase = new RobotBaseScorpius();
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

        robotBase.vision(450, 720); //TODO tune values
        switch (RobotBaseScorpius.jewelPosition) {
            case RobotBaseScorpius.JEWEL_BLUE_RED:
                robotBase.slapJewel(false);
                break;
            case RobotBaseScorpius.JEWEL_RED_BLUE:
                robotBase.slapJewel(true);
                break;
        }

        robotBase.driveStraight(24, 0, -0.6);
        switch (RobotBaseScorpius.pictoPosition) {
            case LEFT:
                robotBase.strafe(21, 0);
                break;
            case CENTER:
                robotBase.strafe(12, 0);
                break;
            case RIGHT:
                robotBase.strafe(5, 0);
                break;
            case UNKNOWN:
                robotBase.strafe(12, 0);
                break;
        }
        robotBase.extendGlyphter();
        sleep(1000);
        robotBase.retractGlyphter(2000);
        robotBase.driveStraight(4, 0, -0.6);
        robotBase.driveStraight(3, 0);
    }
}
