package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by N2Class1 on 10/15/2017.
 */

public class ServoAdjuster extends OpMode{
    Servo servoFlipper, servoRedLeft, servoRedRight, servoBlueLeft, servoBlueRight;
    boolean aLast;

    @Override
    public void init() {
        servoFlipper=hardwareMap.servo.get("flipper");
        servoRedLeft=hardwareMap.servo.get("redLeft");
        servoRedRight=hardwareMap.servo.get("redRight");
        servoBlueLeft=hardwareMap.servo.get("blueLeft");
        servoBlueRight=hardwareMap.servo.get("blueRight");
        aLast = false;
    }

    enum ServoTracker {
        flipper,
        redLeft,
        redRight,
        blueLeft,
        blueRight
    }

    @Override
    public void loop() {
        ServoTracker currentServo = ServoTracker.flipper;
        if (gamepad1.a) {
            currentServo = ServoTracker.blueLeft;
        }
        if (gamepad1.b) {
            currentServo = ServoTracker.redLeft;
        }
        if (gamepad1.y) {
            currentServo = ServoTracker.blueRight;
        }
        if (gamepad1.x) {
            currentServo = ServoTracker.redRight;
        }

        if(currentServo == ServoTracker.blueLeft) {

        }
        if(currentServo == ServoTracker.blueRight) {

        }
        if(currentServo == ServoTracker.redLeft) {

        }
        if(currentServo == ServoTracker.redRight) {

        }

    }
}
