package org.firstinspires.ftc.teamcode.archive;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@Disabled
@SuppressWarnings("WeakerAccess")
@TeleOp(name="TeleopScorpius_OLD")
public class TeleopScorpius_OLD extends OpMode {

    //motor declaration
    DcMotor motorFrontLeft;
    DcMotor motorFrontRight;
    DcMotor motorBackRight;
    DcMotor motorBackLeft;
    DcMotor motorScoop;
    DcMotor motorLifter;
    Servo servoGrabberLeft, servoGrabberRight;
    Servo servoSlapperHorizontal, servoSlapperVertical;


    //variables to be used in manipulating motor power
    float left, right, leftT, rightT, frontLeftPower, backLeftPower, frontRightPower, backRightPower;

    @Override
    public void init() {
        //Connect motor variables to real life motors
        motorFrontLeft = hardwareMap.dcMotor.get("frontLeft");
        motorFrontRight = hardwareMap.dcMotor.get("frontRight");
        motorBackRight = hardwareMap.dcMotor.get("backRight");
        motorBackLeft = hardwareMap.dcMotor.get("backLeft");
        motorScoop = hardwareMap.dcMotor.get("scoop");
        motorLifter = hardwareMap.dcMotor.get("lifter");
        servoGrabberLeft = hardwareMap.servo.get("grabberLeft");
        servoGrabberRight = hardwareMap.servo.get("grabberRight");
        servoSlapperHorizontal = hardwareMap.servo.get("slapperHorizontal");
        servoSlapperVertical = hardwareMap.servo.get("slapperVertical");

        //Reverse the left-side motors
        motorBackLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        motorFrontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        motorScoop.setDirection(DcMotorSimple.Direction.REVERSE);
        motorScoop.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        servoSlapperVertical.setPosition(0.875);
        servoSlapperHorizontal.setPosition(0.37);

        servoGrabberLeft.setPosition(0.81);
        servoGrabberRight.setPosition(0.23);
    }

    @Override
    public void loop() {

        //CURRENTLY USED GAMEPAD CONTROLS:
        //Both Bumpers G2
        //All Triggers (G1 & G2)
        //Both Joysticks G1
        //A Button G2
        //Y Button G2

        //Because this code is in a loop, these get variables are constantly
        //being updated with the current position of the controls.
        //If the value from the gamepad is too small, they will just be set to 0.
        left = (Math.abs(gamepad1.left_stick_y) < 0.05) ? 0 : -gamepad1.left_stick_y;
        right = (Math.abs(gamepad1.right_stick_y) < 0.05) ? 0 : -gamepad1.right_stick_y;
        leftT = (Math.abs(gamepad1.left_trigger) < 0.05) ? 0 : gamepad1.left_trigger;
        rightT = (Math.abs(gamepad1.right_trigger) < 0.05) ? 0 : gamepad1.right_trigger;

        //lifter
        if (gamepad2.left_bumper){
            motorLifter.setPower(0.5);
        } else if (gamepad2.left_trigger>0.35){
            motorLifter.setPower(-0.5);
        } else {
            motorLifter.setPower(0);
        }

        //scooper
        if (gamepad2.right_bumper) {
            motorScoop.setPower(-0.1);
        } else if (gamepad2.right_trigger>0.35){
            motorScoop.setPower(0.1);
        } else {
            motorScoop.setPower(0);
        }

        //grabber
        if(gamepad2.a) {
            servoGrabberLeft.setPosition(0.81);
            servoGrabberRight.setPosition(0.23);
        }
        if(gamepad2.y) {
            servoGrabberLeft.setPosition(0.6);
            servoGrabberRight.setPosition(0.4);
        }

        //if the left joystick is pushed, give the motors power to make the left side go.
        //if the right trigger is pushed, give the motors power to make the robot drift to the right.
        //And so on.
        frontLeftPower = left + rightT - leftT;
        backLeftPower = left - rightT + leftT;
        frontRightPower = right + rightT - leftT;
        backRightPower = right - rightT + leftT;

        //if any motor power is over one, this will scale it back and all the other motors' powers correspondingly
        reducePowers(Math.max(frontLeftPower, Math.max(backLeftPower, Math.max(frontRightPower, backRightPower))));

        if (gamepad1.left_bumper) {
            frontLeftPower /= 2;
            frontRightPower /= 2;
            backLeftPower /= 2;
            backRightPower /= 2;
        }

        //Give all the motors their powers
        motorFrontLeft.setPower(frontLeftPower);
        motorFrontRight.setPower(frontRightPower);
        motorBackLeft.setPower(backLeftPower);
        motorBackRight.setPower(backRightPower);

        telemetry.addData("front left: ", frontLeftPower);
        telemetry.addData("front right: ", frontRightPower);
        telemetry.addData("back left: ", backLeftPower);
        telemetry.addData("back right: ", backRightPower);

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
