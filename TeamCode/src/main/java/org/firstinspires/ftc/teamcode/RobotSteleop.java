package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * Created by N2Class1 on 12/31/2017.
 */
@TeleOp(name="RobotS_TeleOp")
public class RobotSteleop extends OpMode {
    DcMotor motorFrontLeft;
    DcMotor motorFrontRight;
    DcMotor motorBackRight;
    DcMotor motorBackLeft;

    float left, right, leftT, rightT, frontLeftPower, backLeftPower, frontRightPower, backRightPower;

    @Override
    public void init() {

        motorFrontLeft = hardwareMap.dcMotor.get("frontLeft");
        motorFrontRight = hardwareMap.dcMotor.get("frontRight");
        motorBackRight = hardwareMap.dcMotor.get("backRight");
        motorBackLeft = hardwareMap.dcMotor.get("backLeft");

        motorBackLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        motorFrontLeft.setDirection(DcMotorSimple.Direction.REVERSE);

    }

    @Override
    public void loop() {

        left = -gamepad1.left_stick_y;
        right = -gamepad1.right_stick_y;
        leftT = gamepad1.left_trigger;
        rightT = gamepad1.right_trigger;

        if (Math.abs(left + leftT + rightT) < 0.3) {
            frontLeftPower = 0;
            backLeftPower = 0;
        }
        else {
            frontLeftPower = left + rightT - leftT;
            backLeftPower = left - rightT + leftT;
        }

        if (Math.abs(right + leftT + rightT) < 0.3) {
            frontRightPower = 0;
            backRightPower = 0;
        }
        else {
            frontRightPower = right - rightT + leftT;
            backRightPower = right + rightT - leftT;
        }

        reducePowers(frontLeftPower);
        reducePowers(frontRightPower);
        reducePowers(backLeftPower);
        reducePowers(backRightPower);

        motorFrontLeft.setPower(frontLeftPower);
        motorFrontRight.setPower(frontRightPower);
        motorBackLeft.setPower(backLeftPower);
        motorBackRight.setPower(backRightPower);

        telemetry.addData("front left: ", frontLeftPower);
        telemetry.addData("front right: ", frontRightPower);
        telemetry.addData("back left: ", backLeftPower);
        telemetry.addData("back right: ", backRightPower);

    }

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
