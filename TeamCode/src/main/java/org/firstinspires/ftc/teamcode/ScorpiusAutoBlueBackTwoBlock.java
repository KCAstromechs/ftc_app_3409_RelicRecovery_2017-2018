package org.firstinspires.ftc.teamcode;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

@Autonomous (name="AutoBlueBack2Block", group = "Scorpius")
public class ScorpiusAutoBlueBackTwoBlock extends LinearOpMode {

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

        //slap jewels
        if (opModeIsActive())robotBase.vision(450, 720); //TODO tune values
        switch (RobotBaseScorpius.jewelPosition) {
            case RobotBaseScorpius.JEWEL_BLUE_RED:
                if (opModeIsActive())robotBase.slapJewel(true);
                break;
            case RobotBaseScorpius.JEWEL_RED_BLUE:
                if (opModeIsActive())robotBase.slapJewel(false);
                break;
        }

        //drive, then strafe to drop first cube
        if (opModeIsActive())robotBase.driveStraight(26, 0);
        switch (RobotBaseScorpius.pictoPosition) {
            case LEFT:
                if (opModeIsActive())robotBase.strafe(5.2, 0, 0.6);
                break;
            case UNKNOWN:
            case CENTER:
                if (opModeIsActive())robotBase.strafe(15, 0, 0.6);
                break;
            case RIGHT:
                if (opModeIsActive())robotBase.strafe(26, 0, 0.6);
                break;
        }

        //turn and drive in
        if (opModeIsActive())robotBase.turn(160, 0.6);
        if (opModeIsActive())robotBase.turn(180, 0.2);
        if (opModeIsActive())robotBase.driveStraight(6, 180, -0.6);

        //drop in glyph 2
        if (opModeIsActive())robotBase.lowerGrabby();
        if (opModeIsActive())robotBase.extendGlyphter();
        if (opModeIsActive())sleep(1000);

        //put glyphter back
        if (opModeIsActive())robotBase.retractGlyphter(2000);

        //in-n-out
        try {
            if (opModeIsActive())robotBase.driveStraight(4, 180, -0.5, false, true);
        }
        catch (Exception TimeOutException) {}
        if (opModeIsActive())robotBase.driveStraight(5, 180, 0.5);

        switch (RobotBaseScorpius.pictoPosition) {
            case LEFT:
                if (opModeIsActive()) robotBase.strafe(8.5 , 180, -0.6);
                break;
            case UNKNOWN:
            case CENTER:
                if (opModeIsActive()) robotBase.strafe(6 , 180, -0.6);
                break;
        }

        if (opModeIsActive()) robotBase.turn(160, 0.6);
        try {if (opModeIsActive()) robotBase.driveStraight(50, 160, 0.6, false, true);} catch (Exception TimeOutException) {}

        if (opModeIsActive()) robotBase.closeGrabby();
        if (opModeIsActive()) sleep(500);
        try {if (opModeIsActive()) robotBase.driveStraight(10, 160, -0.6, false, true);} catch (Exception TimeOutException) {}
        if (opModeIsActive()) robotBase.raiseGrabby();
        try {if (opModeIsActive()) robotBase.driveStraight(24, 160, -0.6, false, true);} catch (Exception TimeOutException) {}
        if (opModeIsActive()) robotBase.openGrabby();
        if (opModeIsActive()) sleep(500);
        if (opModeIsActive()) robotBase.lowerGrabby();
        if (opModeIsActive()) sleep(500);
        try {if (opModeIsActive()) robotBase.driveStraight(6, 166, -0.6, false, true);} catch (Exception TimeOutException) {}
        if (opModeIsActive()) robotBase.extendGlyphter();
        if (opModeIsActive()) sleep(500);
        if (opModeIsActive()) robotBase.driveStraight(8, 166, 0.6);
    }
}
