package org.firstinspires.ftc.teamcode;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

@Autonomous(name="AutoRedFrontTESTING", group="Scorpius")
public class ScorpiusAutoRedFrontTESTING extends LinearOpMode {

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
        robotBase.vision(450, 720);
        switch (RobotBaseScorpius.jewelPosition) {
            case RobotBaseScorpius.JEWEL_BLUE_RED:
                robotBase.slapJewel(false);
                break;
            case RobotBaseScorpius.JEWEL_RED_BLUE:
                robotBase.slapJewel(true);
                break;
        }

        switch (RobotBaseScorpius.pictoPosition) {
            case LEFT:
                robotBase.driveStraight(42, 0, -0.6);
                break;
            case UNKNOWN:
            case CENTER:
                robotBase.driveStraight(34, 0, -0.6);
                break;
            case RIGHT:
                robotBase.driveStraight(26, 0, -0.6);
                break;
        }
        robotBase.turn(70, 0.6);
        robotBase.turn(90, 0.2);
        robotBase.driveStraight(3, 90, -0.6);
        robotBase.extendGlyphter();
        sleep(1000);
        robotBase.retractGlyphter(2000);
        robotBase.driveStraight(3, 90, -0.6);
        robotBase.driveStraight(4, 90);
        robotBase.lowerGrabby();
        robotBase.openGrabby();
        robotBase.driveStraight(22, 90);
        robotBase.driveStraight(4, 90, 0.3);
        robotBase.closeGrabby();
        sleep(500);
        robotBase.driveStraight(4, 90, -0.6);
        robotBase.raiseGrabby();
        robotBase.driveStraight(22, 90, -0.6);
        robotBase.openGrabby();
        sleep(500);
        robotBase.lowerGrabby();
        robotBase.driveStraight(1, 90, -0.3);
        robotBase.extendGlyphter();
        sleep(1000);
        robotBase.retractGlyphter(1800);
        robotBase.driveStraight(5, 90, -0.6);
        robotBase.driveStraight(4, 90);
    }
}
