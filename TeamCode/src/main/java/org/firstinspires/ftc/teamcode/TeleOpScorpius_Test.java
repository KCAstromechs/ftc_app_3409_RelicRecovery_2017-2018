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

    //motor declaration
    DcMotor motorScoop;
    DcMotor motorLifter;
    Servo servoGrabberLeft, servoGrabberRight;
    int scoopTarget = 0;
    double scoopPower = 0;
    final double kScoopP = 0.0008;
    int scoopError = 0;
    boolean scoopRecentlyPressed = false;

    //variables to be used in manipulating motor power
    float left, right, leftT, rightT, frontLeftPower, backLeftPower, frontRightPower, backRightPower;

    @Override
    public void init() {

        robotBase = new RobotBaseScorpius();

        robotBase.init(this, hardwareMap);
        robotBase.initGrabby(false);

        //setup scoop
        motorScoop = hardwareMap.dcMotor.get("scoop");
        motorScoop.setDirection(DcMotorSimple.Direction.REVERSE);
        motorScoop.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorScoop.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorLifter = hardwareMap.dcMotor.get("lifter");
        motorLifter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLifter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void loop() {

        //CURRENTLY USED GAMEPAD CONTROLS:
        //Both Bumpers G2
        //All Triggers (G1 & G2)
        //Both Joysticks G1
        //A Button G2
        //Y Button G2

        //lifter
        if (gamepad2.left_bumper){
            motorLifter.setPower(0.5);
        } else if (gamepad2.left_trigger>0.35){
            motorLifter.setPower(-0.5);
        } else {
            motorLifter.setPower(0);
        }

        //scooper
        scoopError = Math.abs(scoopTarget - motorScoop.getCurrentPosition());

        if (Math.abs(gamepad2.right_stick_y) > 0.2) {
            //Moves scoop up
            scoopPower = gamepad2.right_stick_y * 0.4;
            scoopRecentlyPressed = true;
        } else if (scoopRecentlyPressed && !(Math.abs(gamepad2.right_stick_y) > 0.2)) {
            scoopRecentlyPressed = false;
            scoopTarget = motorScoop.getCurrentPosition();
        } else if(motorScoop.getCurrentPosition() < scoopTarget && !scoopRecentlyPressed) {
            scoopPower = -scoopError*kScoopP;
        } else if(motorScoop.getCurrentPosition() > scoopTarget && !scoopRecentlyPressed) {
            scoopPower = scoopError*kScoopP;
        }
        motorScoop.setPower(scoopPower);

        //grabber
        if(gamepad2.right_trigger > 0.25) {
            robotBase.openGrabby();
        }
        if(gamepad2.right_bumper) {
            robotBase.closeGrabby();
        }
        if(gamepad2.y) {
            servoGrabberLeft.setPosition(0.76);
            servoGrabberRight.setPosition(0.18);
        }

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
        telemetry.addData("scoopPos: ", motorScoop.getCurrentPosition());
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
