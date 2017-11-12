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
    Servo servoRedLeft;
    //Servo servoRedRight;
    Servo servoBlueLeft;
    Servo servoBlueRight;
    Servo servoForkLift;
    Servo servoWinch;

    //private static final double[] scaleArray = {0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
    //        0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00};

    private static double[] scaleArray = {0.0, 0.19, 0.3, 0.38, 0.44, 0.49, 0.53, 0.57,
            0.6, 0.63, 0.65, 0.68, 0.70, 0.72, 0.74, 0.76, 1.00};
    //growth can be expressed by y=(3ln(x+1))/11
    //did somebody say
    //kevin is a math nerd!?

    boolean up = false;
    //boolean red = true;
    boolean blue = true;
    int spoonPos = 0;

    boolean lb = false;
    boolean lbLast = false;
    boolean rb = false;
    boolean rbLast = false;
    boolean a = false;
    boolean aLast = false;
    boolean b = false;
    boolean bLast = false;
    boolean x = false;
    boolean xLast = false;
    boolean y = false;
    boolean yLast = false;
    boolean dl = false;
    boolean dlLast = false;
    boolean dr = false;
    boolean drLast = false;

    //To explsin the variable naming system, the button name (a, lb, et c.) is the variable for if it should register an input.
    //The button name Last (aLast, rbLast, et c.) is for the toggle logic;
    //it is for if the button registered an input on the last time through the loop

    @Override
    public void init() {
        motorRight=hardwareMap.dcMotor.get("right");
        motorLeft=hardwareMap.dcMotor.get("left");
        motorUp=hardwareMap.dcMotor.get("up");
        motorDown=hardwareMap.dcMotor.get("down");
        servoWinch = hardwareMap.servo.get("winch");
        servoRedLeft=hardwareMap.servo.get("redLeft");
        //servoRedRight=hardwareMap.servo.get("redRight");
        servoBlueLeft=hardwareMap.servo.get("blueLeft");
        servoBlueRight=hardwareMap.servo.get("blueRight");
        servoForkLift=hardwareMap.servo.get("forkLift");
        servoWinch.setPosition(0.65);
    }

    @Override
    public void loop() {

        double left = (gamepad1.left_stick_y);
        double right = -(gamepad1.right_stick_y);
        double right2 = (gamepad2.right_stick_y);

        left = scaleInput(left);
        right = scaleInput(right);
        right2 = scaleInput(right2);

        //if (Math.abs(left)<0.25) left = 0;
        //if (Math.abs(right)<0.25) right = 0;

        motorRight.setPower(right);
        motorLeft.setPower(left);
        motorUp.setPower(right2);

        if(lbLast) {
            lb = false;
            if (!gamepad1.left_bumper) {
                lbLast = false;
            }
        } else {
            if(gamepad1.left_bumper){
                lb = true;
                lbLast = true;
            }
        }

        if(rbLast) {
            rb = false;
            if (!gamepad2.right_bumper) {
                rbLast = false;
            }
        } else {
            if(gamepad2.right_bumper){
                rb = true;
                rbLast = true;
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

        if(yLast) {
            y = false;
            if (!gamepad2.y) {
                yLast = false;
            }
        } else {
            if(gamepad2.y){
                y = true;
                yLast = true;
            }
        }

        if(dlLast) {
            dl = false;
            if (!gamepad2.dpad_down) {
                dlLast = false;
            }
        } else {
            if(gamepad2.dpad_down){
                dl = true;
                dlLast = true;
            }
        }

        if(drLast) {
            dr = false;
            if (!gamepad2.dpad_up) {
                drLast = false;
            }
        } else {
            if(gamepad2.dpad_up){
                dr = true;
                drLast = true;
            }
        }

        /*if (rb){
            red = !red;
        }*/

        if (lb){
            blue = !blue;
        }

        if (a){
            servoWinch.setPosition(0.63);
        }
        if (b){
            servoWinch.setPosition(0.53);
        }
        if (x){
            servoWinch.setPosition(0.29);
        }
        if (y){
            servoWinch.setPosition(0.40);
        }
        if (rb){
            servoWinch.setPosition(0.7);
        }

        if (dr){
            if (spoonPos != 1){
                spoonPos += 1;
            }
        } else if (dl) {
            if (spoonPos != -1){
                spoonPos -= 1;
            }
        }

        if (blue) {
            servoBlueLeft.setPosition(0.1);
            servoBlueRight.setPosition(0.63);
        } else {
            servoBlueLeft.setPosition(0.45);
            servoBlueRight.setPosition(0.27);
        }
        /*if (red) {
            servoRedLeft.setPosition(0.28);
            servoRedRight.setPosition(0.62);
        } else {
            servoRedLeft.setPosition(0.4);
            servoRedRight.setPosition(0.5);
        }*/
        /*if(up){
            servoFlipper.setPosition(0.0);
        } else {
            servoFlipper.setPosition(1.0);
        }*/

        /*if(gamepad1.dpad_up || gamepad2.dpad_up) {
            motorUp.setPower(-0.5);
        }
        else if (gamepad1.dpad_down || gamepad2.dpad_down) {
            motorUp.setPower(0.5);
        }
        else {
            motorUp.setPower(0);
            motorDown.setPower(0);
        }*/
        
        

        if (spoonPos == 1) {
            servoForkLift.setPosition(0.4);
        } else if (spoonPos == -1){
            servoForkLift.setPosition(0.1);
        } else {
            servoForkLift.setPosition(0.25);
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