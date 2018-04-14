package org.firstinspires.ftc.teamcode;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

@Autonomous (name="AutoRedBack2Block", group = "Scorpius")
public class ScorpiusAutoRedBackTwoBlock extends LinearOpMode {

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

        if (opModeIsActive())robotBase.vision(450, 720); //TODO tune values
        switch (RobotBaseScorpius.jewelPosition) {
            case RobotBaseScorpius.JEWEL_BLUE_RED:
                if (opModeIsActive())robotBase.slapJewel(false);
                break;
            case RobotBaseScorpius.JEWEL_RED_BLUE:
                if (opModeIsActive())robotBase.slapJewel(true);
                break;
        }

        if (opModeIsActive())robotBase.driveStraight(21, 0, -0.6);
        switch (RobotBaseScorpius.pictoPosition) {
            case LEFT:
                if (opModeIsActive())robotBase.strafe(21, 0, 0.6);
                break;
            case UNKNOWN:
            case CENTER:
                if (opModeIsActive())robotBase.strafe(12, 0, 0.6);
                break;
            case RIGHT:
                if (opModeIsActive())robotBase.strafe(4, 0);
                break;
        }

        //
        if (opModeIsActive())robotBase.driveStraight(3, 0, -0.6);
        if (opModeIsActive())robotBase.lowerGrabby();
        if (opModeIsActive())robotBase.extendGlyphter();
        if (opModeIsActive())sleep(1000);

        //
        if (opModeIsActive())robotBase.retractGlyphter(1900);
        try {
            if (opModeIsActive())robotBase.driveStraight(4, 0, -0.5, false, true);
        }
        catch (Exception TimeOutException) {}
        if (opModeIsActive())robotBase.driveStraight(8, 0, 0.5);

        switch (RobotBaseScorpius.pictoPosition) {
            case RIGHT:
                if (opModeIsActive()) robotBase.strafe(18 , 0, 0.6);
                break;
            case UNKNOWN:
            case CENTER:
                if (opModeIsActive()) robotBase.strafe(10 , 0, 0.6);
                break;
        }

        if (opModeIsActive()) robotBase.turn(20, 0.6);
        try {if (opModeIsActive()) robotBase.driveStraight(44, 20, 0.6, false, true);} catch (Exception TimeOutException) {}

        if (opModeIsActive()) robotBase.closeGrabby();
        if (opModeIsActive()) sleep(500);
        try {if (opModeIsActive()) robotBase.driveStraight(10, 20, -0.6, false, true);} catch (Exception TimeOutException) {}
        if (opModeIsActive()) robotBase.raiseGrabby();
        try {if (opModeIsActive()) robotBase.driveStraight(20, 16, -0.6, false, true);} catch (Exception TimeOutException) {}
        if (opModeIsActive()) robotBase.openGrabby();
        if (opModeIsActive()) sleep(500);
        if (opModeIsActive()) robotBase.lowerGrabby();
        if (opModeIsActive()) sleep(500);
        try {if (opModeIsActive()) robotBase.driveStraight(6, 14, -0.6, false, true);} catch (Exception TimeOutException) {}
        if (opModeIsActive()) robotBase.extendGlyphter();
        if (opModeIsActive()) sleep(500);
        if (opModeIsActive()) robotBase.driveStraight(8, 14, 0.6);
    }
}
