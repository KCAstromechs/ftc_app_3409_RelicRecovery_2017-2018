package org.firstinspires.ftc.teamcode;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

@Autonomous(name="AutoBlueFront2Block", group="Scorpius")
public class ScorpiusAutoBlueFrontTwoBlock extends LinearOpMode {

    RobotBaseScorpius robotBase;

    private RelativeLayout squaresOverlay = null;
    private AppUtil appUtil = AppUtil.getInstance();

    @Override
    public void runOpMode() throws InterruptedException {
        robotBase = new RobotBaseScorpius();
        robotBase.init(this, hardwareMap);
        robotBase.initVuforia();
        robotBase.initGrabby();

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

        if (opModeIsActive())robotBase.vision(450, 720); //TODO tune values
        switch (RobotBaseScorpius.jewelPosition) {
            case RobotBaseScorpius.JEWEL_BLUE_RED:
                if (opModeIsActive())robotBase.slapJewel(true);
                break;
            case RobotBaseScorpius.JEWEL_RED_BLUE:
                if (opModeIsActive())robotBase.slapJewel(false);
                break;
        }

        switch (RobotBaseScorpius.pictoPosition) {
            case LEFT:
                if (opModeIsActive())robotBase.driveStraight(28, 0);
                break;
            case UNKNOWN:
            case CENTER:
                if (opModeIsActive())robotBase.driveStraight(36, 0);
                break;
            case RIGHT:
                if (opModeIsActive())robotBase.driveStraight(44, 0);
                break;
        }

        if (opModeIsActive())robotBase.turn(70, 0.6);
        if (opModeIsActive())robotBase.turn(90, 0.2);
        if (opModeIsActive())robotBase.driveStraight(3, 90, -0.6);
        if (opModeIsActive())robotBase.extendGlyphter();
        if (opModeIsActive())sleep(1000);
        if (opModeIsActive())robotBase.retractGlyphter(2000);
        if (opModeIsActive())robotBase.driveStraight(3, 90, -0.6);
        if (opModeIsActive())robotBase.driveStraight(4, 90);
        if (opModeIsActive())robotBase.lowerGrabby();
        if (opModeIsActive())robotBase.openGrabby();
        if (opModeIsActive())robotBase.driveStraight(22, 90);
        if (opModeIsActive())robotBase.driveStraight(4, 90, 0.3);
        if (opModeIsActive())robotBase.closeGrabby();
        if (opModeIsActive())sleep(500);
        if (opModeIsActive())robotBase.driveStraight(4, 90, -0.6);
        if (opModeIsActive())robotBase.raiseGrabby();
        if (opModeIsActive())robotBase.driveStraight(22, 90, -0.6);
        if (opModeIsActive())robotBase.openGrabby();
        if (opModeIsActive())sleep(500);
        if (opModeIsActive())robotBase.lowerGrabby();
        if (opModeIsActive())robotBase.driveStraight(1, 90, -0.3);
        if (opModeIsActive())robotBase.extendGlyphter();
        if (opModeIsActive())sleep(1000);
        if (opModeIsActive())robotBase.retractGlyphter(1800);
        if (opModeIsActive())robotBase.driveStraight(5, 90, -0.6);
        if (opModeIsActive())robotBase.driveStraight(4, 90);
    }
}
