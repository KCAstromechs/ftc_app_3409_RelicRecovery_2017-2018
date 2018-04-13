package org.firstinspires.ftc.teamcode;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

@Autonomous (name="AutoBlueBack2Block", group = "Scorpius")
public class ScorpiusAutoBlueBackOHGAWDHELPIMDYING extends LinearOpMode {

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
        if (opModeIsActive())robotBase.driveStraight(22, 0);
        switch (RobotBaseScorpius.pictoPosition) {
            case LEFT:
                if (opModeIsActive())robotBase.strafe(8, 0, 0.6);
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
        if (opModeIsActive())robotBase.driveStraight(4, 180, -0.6);

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

        if (opModeIsActive())robotBase.strafe(13, 180, -0.6);
        /* this last strafe is for the center position only
        it's goal is to line up with a fixed position, and the strafe distance will change based on the cypher key
        from here, we will drive straight into the glyph pit
        then return to the fixed position, and strafe back in front of the correct column
        I was having issues getting this strafe to work right, we will need to tune it.
        it should be easy to mirror this to the red side
        good luck
         */
    }
}
