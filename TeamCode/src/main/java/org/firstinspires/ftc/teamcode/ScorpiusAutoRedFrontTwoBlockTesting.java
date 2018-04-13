package org.firstinspires.ftc.teamcode;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

@Autonomous(name="AutoRedFront2Current", group="Scorpius")
public class ScorpiusAutoRedFrontTwoBlockTesting extends LinearOpMode {

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
        if (opModeIsActive())robotBase.vision(450, 720);
        switch (RobotBaseScorpius.jewelPosition) {
            case RobotBaseScorpius.JEWEL_BLUE_RED:
                if (opModeIsActive())robotBase.slapJewel(false);
                break;
            case RobotBaseScorpius.JEWEL_RED_BLUE:
                if (opModeIsActive())robotBase.slapJewel(true);
                break;
        }

        //drive based on picto
        switch (RobotBaseScorpius.pictoPosition) {
            case LEFT:
                if (opModeIsActive())robotBase.driveStraight(40, 0, -0.6, true);
                break;
            case UNKNOWN:
            case CENTER:
                if (opModeIsActive())robotBase.driveStraight(32, 0, -0.6, true);
                break;
            case RIGHT:
                if (opModeIsActive())robotBase.driveStraight(24, 0, -0.6, true);
                break;
        }

        //deposit glyph
        if (opModeIsActive())robotBase.turn(70, 0.8);
        if (opModeIsActive())robotBase.turn(90, 0.2);
        if (opModeIsActive())robotBase.driveStraight(6, 90, -0.3);
        if (opModeIsActive())robotBase.lowerGrabby();
        if (opModeIsActive())robotBase.extendGlyphter();
        if (opModeIsActive())sleep(500);

        //return to pickup mode
        if (opModeIsActive())robotBase.retractGlyphter(2300);
        if (opModeIsActive())robotBase.openGrabby();

        //bulldoze glyph pit and G R A B
        if (opModeIsActive())robotBase.driveStraight(22, 90, 0.9);
        if (opModeIsActive())robotBase.driveStraight(4, 90);
        if (opModeIsActive())robotBase.turn(110);
        if (opModeIsActive())robotBase.driveStraight(6, 110);
        if (opModeIsActive())robotBase.closeGrabby();
        if (opModeIsActive())sleep(500);

        //drive to box and lift the Boy
        if (opModeIsActive())robotBase.driveStraight(6, 110, -0.6);
        if (opModeIsActive())robotBase.turn(90);
        if (opModeIsActive())robotBase.driveStraight(4, 90, -0.6);
        if (opModeIsActive())robotBase.raiseGrabby();

        if(RobotBaseScorpius.pictoPosition == RelicRecoveryVuMark.CENTER || RobotBaseScorpius.pictoPosition == RelicRecoveryVuMark.UNKNOWN){
            if (opModeIsActive())robotBase.strafe(6, 90, 0.75);
        }

        try {if (opModeIsActive())robotBase.driveStraight(18, 90, -0.9, true, false);} catch (Exception e) {}
        if (opModeIsActive())robotBase.openGrabby();
        if (opModeIsActive())sleep(500);

        if (opModeIsActive())robotBase.lowerGrabby();
        if (opModeIsActive())robotBase.extendGlyphter();
        if (opModeIsActive())sleep(500);
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
