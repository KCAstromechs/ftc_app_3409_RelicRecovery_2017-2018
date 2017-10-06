package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@SuppressWarnings("WeakerAccess")
@TeleOp(name="Teleop", group = "test")
public class Teleop extends OpMode {

    DcMotor motorRight;
    DcMotor motorLeft;
    DcMotor motorUp;
    DcMotor motorDown;
    Servo servoFlipper;
    Servo servoRedLeft;
    Servo servoRedRight;
    Servo servoBlueLeft;
    Servo servoBlueRight;

    boolean up = false;
    boolean red = true;
    boolean blue = true;

    @Override
    public void init() {
        motorRight=hardwareMap.dcMotor.get("right");
        motorLeft=hardwareMap.dcMotor.get("left");
        motorUp=hardwareMap.dcMotor.get("up");
        motorDown=hardwareMap.dcMotor.get("down");
        servoFlipper=hardwareMap.servo.get("flipper");
        servoRedLeft=hardwareMap.servo.get("redLeft");
        servoRedRight=hardwareMap.servo.get("redRight");
        servoBlueLeft=hardwareMap.servo.get("blueLeft");
        servoBlueRight=hardwareMap.servo.get("blueRight");
    }

    @Override
    public void loop() {

        float left = -gamepad1.left_stick_y;
        float right = gamepad1.right_stick_y;
        if (Math.abs(left)<0.25) left = 0;
        if (Math.abs(right)<0.25) right = 0;

        motorRight.setPower(right);
        motorLeft.setPower(left);

        if(gamepad1.a){
            if(up){
                servoFlipper.setPosition(0.03);
                up = false;
            } else {
                servoFlipper.setPosition(0.96);
                up = true;
            }
        }

        if (gamepad1.right_bumper){
            if (red){
                red = false;
            } else {
                red = true;
            }
        }

        if (gamepad1.left_bumper){
            if (blue){
                blue = false;
            } else {
                blue = true;
            }
        }

        if (blue) {
            servoBlueLeft.setPosition(0.2);
            servoBlueRight.setPosition(0.53);
        } else {
            servoBlueLeft.setPosition(0.3);
            servoBlueRight.setPosition(0.43);
        }
        if (red) {
            servoRedLeft.setPosition(0.3);
            servoRedRight.setPosition(0.6);
        } else {
            servoRedLeft.setPosition(0.4);
            servoRedRight.setPosition(0.5   );
        }
    }


    @Override
    public void stop() {

    }

}

/*
0.03
0.96

0.3 close
0.4 open

0.6
0.5

0.2
0.3

0.53
0.43
 */