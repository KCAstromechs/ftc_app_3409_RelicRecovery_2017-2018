package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * Created by N2Class1 on 12/31/2017.
 */
@TeleOp(name="RobotS_TeleOp")
public class TeleopS extends OpMode {

    //motor declaration
    DcMotor motorFrontLeft;
    DcMotor motorFrontRight;
    DcMotor motorBackRight;
    DcMotor motorBackLeft;
    DcMotor motorScoop;
    DcMotor motorLifter;
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
        //Reverse the left-side motors
        motorBackLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        motorFrontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        motorScoop.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void loop() {

        //Because this code is in a loop, these get variables are constantly being updated with the current position of the controls
        left = -gamepad1.left_stick_y;
        right = -gamepad1.right_stick_y;
        leftT = gamepad1.left_trigger;
        rightT = gamepad1.right_trigger;

        if (gamepad1.a){
            motorLifter.setPower(0.5);
        }

        else if (gamepad1.b){
            motorLifter.setPower(-0.5);
        }
        else
        {
            motorLifter.setPower(0);
        }
        if (gamepad1.dpad_up) {
            motorScoop.setPower(0.5);
        }
        else if (gamepad1.dpad_down){
            motorScoop.setPower(-0.5);
        }else
        {
            motorScoop.setPower(0);
        }
        //If you're barely pushing down on the joysticks or the triggers, don't go
        if (Math.abs(left + leftT + rightT) < 0.3) {
            frontLeftPower = 0;
            backLeftPower = 0;
        }
        //if the left joystick is pushed, give the motors power to make the left side go.
        //if the right trigger is pushed, give the motors power to make the robot drift to the right.
        //And so on.
        else {
            frontLeftPower = left + rightT - leftT;
            backLeftPower = left - rightT + leftT;
        }

        //Same as the left side
        if (Math.abs(right + leftT + rightT) < 0.3) {
            frontRightPower = 0;
            backRightPower = 0;
        }
        else {
            frontRightPower = right - rightT + leftT;
            backRightPower = right + rightT - leftT;
        }

        //if any motor power is over one, this will scale it back and all the other motors' powers correspondingly
        reducePowers(Math.max(frontLeftPower, Math.max(backLeftPower, Math.max(frontRightPower, backRightPower))));

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
