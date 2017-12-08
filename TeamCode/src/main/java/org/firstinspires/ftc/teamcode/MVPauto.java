package org.firstinspires.ftc.teamcode;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

// import org.firstinspires.ftc.robotcore.internal.AppUtil;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

@Autonomous(name="MVPauto", group="test")
public class MVPauto extends LinearOpMode {

    private RobotBaseM1ttens robotBase;

    protected RelativeLayout squaresOverlay = null;
    protected AppUtil appUtil = AppUtil.getInstance();


    @Override
    public void runOpMode() throws InterruptedException {
        robotBase = new RobotBaseM1ttens();
        robotBase.init(this, hardwareMap);
        robotBase.initVuforia();

        appUtil.synchronousRunOnUiThread(new Runnable() {
            @Override
            public void run() {
                squaresOverlay = (RelativeLayout) View.inflate(appUtil.getActivity(), R.layout.beacon_line_up_squares, null);
                squaresOverlay.findViewById(R.id.blueSideBeacon).setVisibility(View.VISIBLE);
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

        robotBase.vision();

        telemetry.addData("jewel", robotBase.jewelPosition);
        telemetry.addData("picto", robotBase.pictoPosition);
        telemetry.update();

        robotBase.turn(270);
        robotBase.driveStraight(32, 270);
        robotBase.turn(180);
    }
}