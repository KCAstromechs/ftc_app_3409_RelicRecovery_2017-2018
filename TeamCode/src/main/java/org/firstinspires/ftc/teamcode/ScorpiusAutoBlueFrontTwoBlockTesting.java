package org.firstinspires.ftc.teamcode;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.internal.android.dx.rop.code.Exceptions;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

@Autonomous(name="AutoBlueFront2Current", group="Scorpius")
public class ScorpiusAutoBlueFrontTwoBlockTesting extends LinearOpMode {

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

        appUtil.synchronousRunOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (squaresOverlay != null){
                    ((ViewGroup)squaresOverlay.getParent()).removeView(squaresOverlay);
                }
                squaresOverlay = null;
            }
        });

        //slap jewel
        if (opModeIsActive())robotBase.vision(450, 720); //TODO tune values
        switch (RobotBaseScorpius.jewelPosition) {
            case RobotBaseScorpius.JEWEL_BLUE_RED:
                if (opModeIsActive())robotBase.slapJewel(true);
                break;
            case RobotBaseScorpius.JEWEL_RED_BLUE:
                if (opModeIsActive())robotBase.slapJewel(false);
                break;
        }

        //drive to deposit first glyph
        switch (RobotBaseScorpius.pictoPosition) {
            case LEFT:
                if (opModeIsActive()) robotBase.driveStraight(28, 0, 0.6, true);
                break;
            case UNKNOWN:
            case CENTER:
                if (opModeIsActive())robotBase.driveStraight(37, 0, 0.6, true);
                break;
            case RIGHT:
                if (opModeIsActive())robotBase.driveStraight(46, 0, 0.6, true);
                break;
        }

        //deposit glyph
        if (opModeIsActive())robotBase.turn(70, 0.8);
        if (opModeIsActive())robotBase.turn(90, 0.2);
        if (opModeIsActive())robotBase.driveStraight(4, 90, -0.3); //drive in
        if (opModeIsActive())robotBase.lowerGrabby();
        if (opModeIsActive())robotBase.extendGlyphter(); //dump
        if (opModeIsActive())sleep(500);

        //return to pickup mode
        if (opModeIsActive())robotBase.retractGlyphter(2300);
        if (opModeIsActive())robotBase.openGrabby();

        //bulldoze glyph pile & grab
        if (opModeIsActive())robotBase.driveStraight(22, 90, 0.9);
        if (opModeIsActive())robotBase.driveStraight(4, 90);
        if (opModeIsActive())robotBase.turn(70);
        if (opModeIsActive())robotBase.driveStraight(6, 70);
        if (opModeIsActive())robotBase.closeGrabby();
        if (opModeIsActive())sleep(500);

        //drive back to cryptbox and lift the Boy
        if (opModeIsActive())robotBase.driveStraight(6, 70, -0.6);
        if (opModeIsActive())robotBase.turn(90);
        if (opModeIsActive())robotBase.driveStraight(4, 90, -0.6);
        if (opModeIsActive())robotBase.raiseGrabby();

        //strafe to correct deposition position
        if(RobotBaseScorpius.pictoPosition == RelicRecoveryVuMark.CENTER || RobotBaseScorpius.pictoPosition == RelicRecoveryVuMark.UNKNOWN){
            if (opModeIsActive())robotBase.strafe(6, 90, 0.75);
        }

        //drive into box to dump
        try {if (opModeIsActive())robotBase.driveStraight(15, 90, -0.9, true, false);} catch (Exception e) {}
        if (opModeIsActive())robotBase.openGrabby();
        if (opModeIsActive())sleep(500);

        //Dump The Boy
        if (opModeIsActive())robotBase.lowerGrabby();
        if (opModeIsActive())robotBase.extendGlyphter();
        if (opModeIsActive())sleep(500);

        //bring back glyphter and exit
        if (opModeIsActive())robotBase.retractGlyphter(1600);
        if (opModeIsActive())robotBase.driveStraight(2, 90);

        //one more in-n-out
        try {
            if (opModeIsActive())robotBase.driveStraight(5, 90, -0.7, false, true);
        }
        catch (Exception TimeoutException) {}
        if (opModeIsActive())robotBase.driveStraight(6, 90);
    }
}
