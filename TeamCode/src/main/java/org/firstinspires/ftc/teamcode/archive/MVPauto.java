/*
package org.firstinspires.ftc.teamcode.archive;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

// import org.firstinspires.ftc.robotcore.internal.AppUtil;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.R;
import org.firstinspires.ftc.teamcode.RobotBaseM1ttens;

@Autonomous(name="MVPauto", group="test")
public class MVPauto extends LinearOpMode {

    private RobotBaseM1ttens robotBase;

    protected RelativeLayout squaresOverlay = null;
    protected AppUtil appUtil = AppUtil.getInstance();


    @Override
    public void runOpMode() throws InterruptedException {

        //init bot stuff
        robotBase = new RobotBaseM1ttens();
        robotBase.init(this, hardwareMap);
        robotBase.initVuforia();

        //init green square
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
        System.out.println("SSS passed waitforstart()");

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

        //look for jewel and picto
        robotBase.vision(475, 480);

        System.out.println("SSS passed vision()");

        telemetry.addData("jewel", robotBase.jewelPosition);
        telemetry.addData("picto", robotBase.pictoPosition);
        telemetry.update();

        //try moving
        robotBase.turn(270);
        robotBase.driveStraight(32, 270);
        robotBase.turn(180);
    }
}
*/