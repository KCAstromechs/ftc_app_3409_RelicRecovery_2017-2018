package org.firstinspires.ftc.teamcode;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by N2Class1 on 10/19/2017.
 */
@Autonomous(name="Rusty Auto RR", group="test")
public class RustyAutonomousTesting extends LinearOpMode implements SensorEventListener{

    DcMotor motorFrontLeft, motorFrontRight, motorBackLeft, motorBackRight, encoderMotor;

    static final double COUNTS_PER_MOTOR_REV = 1100;    // NeveRest Motor Encoder
    static final double DRIVE_GEAR_REDUCTION = 0.5;     // This is < 1.0 if geared UP
    static final double WHEEL_DIAMETER_INCHES = 4.0;    // For figuring circumference
    static final double P_BEELINE_COEFF = 0.04;           // Larger is more responsive, but also less stable
    static final double TURN_MINIMUM_SPEED = 0.2;

    //variables for gyro operation
    float zero;
    float rawGyro;
    boolean hasBeenZeroed = false;
    public float zRotation;

    //arrays for gyro operation
    float[] rotationMatrix = new float[9];
    float[] orientation = new float[3];

    //more required vars for gyro operation
    private SensorManager mSensorManager;
    private Sensor mRotationVectorSensor;

    //encoder ticks per one inch
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);


    @Override
    public void runOpMode() throws InterruptedException {
        motorFrontLeft = hardwareMap.dcMotor.get("FrontLeft");
        motorFrontRight = hardwareMap.dcMotor.get("FrontRight");
        motorBackLeft = hardwareMap.dcMotor.get("BackLeft");
        motorBackRight = hardwareMap.dcMotor.get("BackRight");
        encoderMotor = hardwareMap.dcMotor.get("FrontLeft");

        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE);
        motorBackLeft.setDirection(DcMotor.Direction.REVERSE);


        //Accessing gyro and accelerometer from Android
        mSensorManager = (SensorManager) hardwareMap.appContext.getSystemService(SENSOR_SERVICE);
        mRotationVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        mSensorManager.registerListener(this, mRotationVectorSensor, 10000);

        waitForStart();

        hasBeenZeroed = false;

        //TODO Figure out how to deregister the gyro on opmode stop
        //TODO Figure out long ADB APK install times
       /** telemetry.addLine("encoderMotor: " + encoderMotor.getCurrentPosition());
        telemetry.addLine("zRotation: " + zRotation);
        telemetry.update(); **/

        turn(270, .75);
        //beeline(24, 0);

       /** while(!Thread.interrupted()) {
            telemetry.addLine("zRotation: " + zRotation);
            telemetry.addLine("encoderMotor: " + encoderMotor.getCurrentPosition());
            telemetry.update();
        } **/
    }

    void beelineCorrecting(double inches, int heading) {
        double power = 1;
        double leftPower = 0, rightPower = 0, error, correction;
        int target = (int) (inches * COUNTS_PER_INCH); //translates the number of inches to be driven into encoder ticks

        motorFrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFrontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //telemetry.addLine("initial encoder value: " + encoderMotor.getCurrentPosition());
        //telemetry.update();

        if(inches < 0) {
            leftPower = -power;
            rightPower = -power;
        }
        else {
            leftPower = power;
            rightPower = power;
        }

        while(Math.abs(encoderMotor.getCurrentPosition()) <= Math.abs(target) && !Thread.interrupted()) {

            //telemetry.addLine("encoder value: " + encoderMotor.getCurrentPosition());
            //telemetry.addLine("target: " + target);
            //telemetry.update();

            error = heading - zRotation;
            correction = Range.clip(error * P_BEELINE_COEFF, -1, 1);
            leftPower = power + correction;
            rightPower = power - correction;
            leftPower = Range.clip(leftPower, -1, 1);
            rightPower = Range.clip(rightPower, -1, 1);

            motorFrontLeft.setPower(leftPower);
            motorBackLeft.setPower(leftPower);
            motorFrontRight.setPower(rightPower);
            motorBackRight.setPower(rightPower);

            Thread.yield();

        }

        motorFrontLeft.setPower(0);
        motorBackLeft.setPower(0);
        motorFrontRight.setPower(0);
        motorBackRight.setPower(0);

        //Reset the motors for future use, just in case
        motorFrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFrontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    }

    void beeline(double inches, int heading) {
        double power = 1;
        int target = (int) (inches * COUNTS_PER_INCH); //translates the number of inches to be driven into encoder ticks

        motorFrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFrontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


        if(inches < 0) {
            power = -power;
        }

        while(Math.abs(encoderMotor.getCurrentPosition()) <= Math.abs(target) && !Thread.interrupted()) {

            motorFrontLeft.setPower(power);
            motorBackLeft.setPower(power);
            motorFrontRight.setPower(power);
            motorBackRight.setPower(power);

            Thread.yield();
        }

        motorFrontLeft.setPower(0);
        motorBackLeft.setPower(0);
        motorFrontRight.setPower(0);
        motorBackRight.setPower(0);

        //Reset the motors for future use, just in case
        motorFrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFrontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    }

    public void turn (float turnHeading, double power) throws InterruptedException {
        turnHeading = normalize360(turnHeading);

        int wrapFix = 0;
        float shiftedTurnHeading = turnHeading;
        double motorSpeed = power;

        float ccwise = zRotation - turnHeading;
        float cwise = turnHeading - zRotation;

        ccwise = normalize360(ccwise);
        cwise = normalize360(cwise);

        int error = 4;
        if (turnHeading - error < 0 || turnHeading + error > 360) {
            wrapFix = 180;
            shiftedTurnHeading = normalize360(turnHeading + wrapFix);
        }

        if (Math.abs(ccwise) >= Math.abs(cwise)) {

            while (Math.abs(normalize360(zRotation + wrapFix) - shiftedTurnHeading) > error &&
                    Math.abs(ccwise) >= Math.abs(cwise) &&
                    !Thread.interrupted()) {

                Thread.sleep(10);

                if (motorSpeed > power)
                    motorSpeed = power;
                if (motorSpeed < TURN_MINIMUM_SPEED)
                    motorSpeed = TURN_MINIMUM_SPEED;

                motorFrontLeft.setPower(motorSpeed);
                motorBackLeft.setPower(motorSpeed);
                motorFrontRight.setPower(-motorSpeed);
                motorBackRight.setPower(-motorSpeed);

                ccwise = zRotation - turnHeading;
                cwise = turnHeading - zRotation;
            }
        }
        else if (Math.abs(cwise) > Math.abs(ccwise)) {

            while (Math.abs(normalize360(zRotation + wrapFix) - shiftedTurnHeading) > error &&
                    Math.abs(cwise) >= Math.abs(ccwise) &&
                    !Thread.interrupted()) {

                Thread.sleep(10);

                if (motorSpeed > power)
                    motorSpeed = power;
                if (motorSpeed < TURN_MINIMUM_SPEED)
                    motorSpeed = TURN_MINIMUM_SPEED;

                motorFrontLeft.setPower(-motorSpeed);
                motorBackLeft.setPower(-motorSpeed);
                motorFrontRight.setPower(motorSpeed);
                motorBackRight.setPower(motorSpeed);

                ccwise = zRotation - turnHeading;
                cwise = turnHeading - zRotation;
            }

        }

        motorFrontLeft.setPower(0);
        motorBackLeft.setPower(0);
        motorFrontRight.setPower(0);
        motorBackRight.setPower(0);

    }

    public float normalize360(float val) {
        while (val > 360 || val < 0) {

            if (val > 360) {
                val -= 360;
            }

            if (val < 0) {
                val += 360;
            }
        }
        return val;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);
        SensorManager.getOrientation(rotationMatrix, orientation);

        rawGyro = (float) Math.toDegrees(orientation[0]);

        //If the zero hasn't been zeroed do the zero
        if (!hasBeenZeroed) {
            hasBeenZeroed = true;
            zero = rawGyro;
        }
        //Normalize zRotation to be used
        zRotation = normalize360(rawGyro - zero);
        System.out.println("SSS zRotation" + zRotation);
//        Dbg("zRotation in callback: " , zRotation, false);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void deconstruct(){
        mSensorManager.unregisterListener(this);
    }

}