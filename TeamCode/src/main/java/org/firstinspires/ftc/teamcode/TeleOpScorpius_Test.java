package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@SuppressWarnings("WeakerAccess")
@TeleOp(name="TeleopScorpius_Test")
public class TeleOpScorpius_Test extends OpMode {

    RobotBaseScorpius robotBase;

    int scoopTarget = 0;
    double scoopPower = 0;
    final double kScoopP = 0.0008;
    int scoopError = 0;
    boolean scoopRecentlyPressed = false;
    boolean down;
    boolean downLast;
    boolean up;
    boolean upLast;
    boolean handClosed = true;
    int elbow = 0;

    //variables to be used in manipulating motor power
    float left, right, leftT, rightT, frontLeftPower, backLeftPower, frontRightPower, backRightPower;

    @Override
    public void init() {

        robotBase = new RobotBaseScorpius();

        robotBase.init(this, hardwareMap);
        robotBase.initGrabby(false);
    }

    @Override
    public void loop() {
        //lifter
        if (gamepad2.right_bumper){
            robotBase.motorLifter.setPower(0.5);
        } else if (gamepad2.right_trigger>0.35){
            robotBase.motorLifter.setPower(-(gamepad2.right_trigger/1.5));
        } else {
            robotBase.motorLifter.setPower(0);
        }

        //scooper
        scoopError = Math.abs(scoopTarget - robotBase.motorScoop.getCurrentPosition());

        if (Math.abs(gamepad2.left_stick_y) > 0.2) {
            //Moves scoop up
            scoopPower = gamepad2.left_stick_y * 0.4;
            scoopRecentlyPressed = true;
        } else if (scoopRecentlyPressed && !(Math.abs(gamepad2.left_stick_y) > 0.2)) {
            scoopRecentlyPressed = false;
            scoopTarget = robotBase.motorScoop.getCurrentPosition();
        } else if(robotBase.motorScoop.getCurrentPosition() < scoopTarget && !scoopRecentlyPressed) {
            scoopPower = scoopError*kScoopP;
        } else if(robotBase.motorScoop.getCurrentPosition() > scoopTarget && !scoopRecentlyPressed) {
            scoopPower = -scoopError*kScoopP;
        }
        robotBase.motorScoop.setPower(scoopPower);

        //grabber
        if(gamepad2.left_trigger > 0.25) {
            robotBase.openGrabby();
        }
        if(gamepad2.left_bumper) {
            robotBase.closeGrabby();
        }
        if(gamepad2.a) {
            robotBase.servoGrabberLeft.setPosition(0.76);
            robotBase.servoGrabberRight.setPosition(0.18);
        }

        if(downLast) {
            down = false;
            if (!gamepad2.dpad_down) {
                downLast = false;
            }
        } else {
            if(gamepad2.dpad_down){
                down = true;
                downLast = true;
            }
        }

        if(upLast) {
            up = false;
            if (!gamepad2.dpad_up) {
                upLast = false;
            }
        } else {
            if(gamepad2.dpad_up){
                up = true;
                upLast = true;
            }
        }

        if(down){
            handClosed = !handClosed;
        }
        if(up){
            if(elbow!=2){
                elbow = 2;
            } else {
                elbow = 1;
            }
        }

        if(handClosed){
            robotBase.servoHand.setPosition(1);
        } else {
            robotBase.servoHand.setPosition(0.35);
        }
        switch(elbow){
            case 1:
                robotBase.servoElbow.setPosition(0.55);
                break;
            case 2:
                robotBase.servoElbow.setPosition(1);
                break;
        }

        robotBase.servoExtender.setPower(gamepad2.right_stick_y);

        //Because this code is in a loop, these get variables are constantly
        //being updated with the current position of the controls.
        //If the value from the gamepad is too small, they will just be set to 0.
        left = (Math.abs(gamepad1.left_stick_y) < 0.05) ? 0 : -gamepad1.left_stick_y;
        right = (Math.abs(gamepad1.right_stick_y) < 0.05) ? 0 : -gamepad1.right_stick_y;
        leftT = (Math.abs(gamepad1.left_trigger) < 0.05) ? 0 : gamepad1.left_trigger;
        rightT = (Math.abs(gamepad1.right_trigger) < 0.05) ? 0 : gamepad1.right_trigger;

        //if the left joystick is pushed, give the motors power to make the left side go.
        //if the right trigger is pushed, give the motors power to make the robot drift to the right.
        //And so on.
        frontLeftPower = left + rightT - leftT;
        backLeftPower = left - rightT + leftT;
        frontRightPower = right + rightT - leftT;
        backRightPower = right - rightT + leftT;

        //if any motor power is over one, this will scale it back and all the other motors' powers correspondingly
        reducePowers(Math.max(frontLeftPower, Math.max(backLeftPower, Math.max(frontRightPower, backRightPower))));

        robotBase.updateDriveMotors(frontLeftPower, frontRightPower, backLeftPower, backRightPower, gamepad1.left_bumper);

        telemetry.addData("front left: ", frontLeftPower);
        telemetry.addData("front right: ", frontRightPower);
        telemetry.addData("back left: ", backLeftPower);
        telemetry.addData("back right: ", backRightPower);
        telemetry.addData("scoopTarget: ", scoopTarget);
        telemetry.addData("scoopPos: ", robotBase.motorScoop.getCurrentPosition());
        telemetry.addData("extenderPower ", robotBase.servoExtender.getPower());
        telemetry.update();
    }

    //This method takes the power, finds what multiplier is needed to scale it back to one, and
    //scales back all motor power accordingly
    private void reducePowers(float power) {

        if (power > 1.0) {

            float multiplier = 1/power;

            frontLeftPower *= multiplier;
            frontRightPower *= multiplier;
            backLeftPower *= multiplier;
            backRightPower *= multiplier;
        }
    }
}
