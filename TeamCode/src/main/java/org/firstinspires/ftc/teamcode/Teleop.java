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
    DcMotor motorLifter;
    Servo servoLeft;
    Servo servoRight;
    Servo servoJewel;

    private static final double[] scaleArray = {0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
            0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00};

    //private static double[] scaleArray = {0.0, 0.19, 0.3, 0.38, 0.44, 0.49, 0.53, 0.57,
    //        0.6, 0.63, 0.65, 0.68, 0.70, 0.72, 0.74, 0.76, 1.00};


    //growth can be expressed by y=(3ln(x+1))/11
    //did somebody say
    //kevin is a math nerd!?

    boolean x = false;
    boolean xLast = false;
    boolean b = false;
    boolean bLast = false;
    boolean a = false;
    boolean aLast = false;

    int lastEncoderLifter = 0;
    int encoderLifter = 0;


    //To explain the variable naming system, the button name (a, x, et c.) is the variable for if it should register an input.
    //The button name Last (aLast, bLast, et c.) is for the toggle logic;
    //it is for if the button registered an input on the last time through the loop

    @Override
    public void init() {
        motorRight=hardwareMap.dcMotor.get("right");
        motorLeft=hardwareMap.dcMotor.get("left");
        motorLifter=hardwareMap.dcMotor.get("up");
        servoLeft=hardwareMap.servo.get("leftServo");
        servoRight=hardwareMap.servo.get("rightServo");
        servoJewel=hardwareMap.servo.get("jewelServo");

        motorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorLifter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        motorLifter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLifter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorLeft.setDirection(DcMotor.Direction.REVERSE);

        servoLeft.setPosition(0.1);
        servoRight.setPosition(0.63);
        servoJewel.setPosition(0.72);
    }

    @Override
    public void loop() {

        double left = -(gamepad1.left_stick_y);
        double right = -(gamepad1.right_stick_y);
        double left2 = -(gamepad2.left_stick_y);

        left = scaleInput(left);
        right = scaleInput(right);
        left2 = scaleInput(left2);

        //if (Math.abs(left)<0.25) left = 0;
        //if (Math.abs(right)<0.25) right = 0;

        motorRight.setPower(right);
        motorLeft.setPower(left);

        encoderLifter = Math.abs(motorLifter.getCurrentPosition());

        if (Math.abs(left2) < 0.25){
            if (encoderLifter < lastEncoderLifter){
                motorLifter.setPower((double)(lastEncoderLifter - encoderLifter)/100);
                telemetry.addData("hhh", (double)(lastEncoderLifter - encoderLifter));
                telemetry.addData("hhh", (lastEncoderLifter));
                telemetry.addData("hhh", (encoderLifter));


                telemetry.update();
            } else {
                motorLifter.setPower(0);
            }
        } else {
            motorLifter.setPower(left2);
            lastEncoderLifter = encoderLifter;
        }


        if(xLast) {
            x = false;
            if (!gamepad2.x) {
                xLast = false;
            }
        } else {
            if(gamepad2.x){
                x = true;
                xLast = true;
            }
        }
        if(bLast) {
            b = false;
            if (!gamepad2.b) {
                bLast = false;
            }
        } else {
            if(gamepad2.b){
                b = true;
                bLast = true;
            }
        }
        if(aLast) {
            a = false;
            if (!gamepad2.a) {
                aLast = false;
            }
        } else {
            if(gamepad2.a){
                a = true;
                aLast = true;
            }
        }

        if (b) {
            servoLeft.setPosition(0.45);
            servoRight.setPosition(0.27);
        } else if (x) {
            servoLeft.setPosition(0.1);
            servoRight.setPosition(0.61);
        } else if (a) {
            servoLeft.setPosition(0.32);
            servoRight.setPosition(0.39);
        }
    }

    private double scaleInput(double dVal)  {

        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 16.0);

        // index should be positive.
        if (index < 0) {
            index = -index;
        }

        // index cannot exceed size of array minus 1.
        if (index > 16) {
            index = 16;
        }

        // get value from the array.
        double dScale;
        if (dVal < 0) {
            dScale = -scaleArray[index];
        } else {
            dScale = scaleArray[index];
        }

        // return scaled value.
        return dScale;
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