package org.firstinspires.ftc.teamcode;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

@Autonomous(name="AutoBlueFront", group="M1ttens")
public class M1ttensAutoBlueFront extends LinearOpMode {

    private RobotBaseM1ttens robotBase;

    private RelativeLayout squaresOverlay = null;
    private AppUtil appUtil = AppUtil.getInstance();

    @Override
    public void runOpMode() throws InterruptedException {
        robotBase = new RobotBaseM1ttens();
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

        robotBase.vision(475, 480);

        robotBase.jewelDown();
        sleep(500);
        switch (RobotBaseM1ttens.jewelPosition) {
            case 1:
                robotBase.turn(10);
                break;
            case 2:
                robotBase.turn(350);
                break;
        }
        robotBase.jewelUp();

        robotBase.turn(270);
        switch (RobotBaseM1ttens.pictoPosition) {
            case LEFT:
                robotBase.driveStraight(25, 270);
                break;
            case CENTER:
                robotBase.driveStraight(33, 270);
                break;
            case RIGHT:
                robotBase.driveStraight(42, 270);
                break;
        }
        robotBase.turn(180);
    }
}