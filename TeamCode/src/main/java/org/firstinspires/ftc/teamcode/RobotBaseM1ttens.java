package org.firstinspires.ftc.teamcode;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import static android.content.Context.SENSOR_SERVICE;

public class RobotBaseM1ttens implements SensorEventListener {

    private DcMotor motorLeft, motorRight, motorLifter;
    private Servo servoLeft, servoRight;
    private DcMotor encoderMotor;
    private HardwareMap hardwareMap;
    private OpMode callingOpMode;

    //variables for gyro operation
    private float zero;
    private float rawGyro;

    //arrays for gyro operation
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];

    //objects for gyro operation
    private SensorManager mSensorManager;
    private Sensor mRotationVectorSensor;

    private boolean hasBeenZeroed= false;

    // This is relative to the initial position of the robot.
    // Possible values are:  0-360
    // 0 is set as straight ahead of the robot, 90 is the right, 270 is to the left
    private float zRotation;

    private static final double COUNTS_PER_MOTOR_REV = 1100;    // NeveRest Motor Encoder
    private static final double DRIVE_GEAR_REDUCTION = 1.0;     // This is < 1.0 if geared UP
    private static final double WHEEL_DIAMETER_INCHES = 4.0;    // For figuring circumference

    protected static final double driveSpeed = 0.8;
    protected static final double turnSpeed = 0.5;


    private static final double P_DRIVE_COEFF = 0.02;           // Larger is more responsive, but also less stable
    static final double P_TURN_COEFF = 0.018;          // Larger is more responsive, but also less stable


    //encoder ticks per one inch
    protected static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);


    protected static final double[] scaleArray = {0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
            0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00};

    protected void init (OpMode _callingOpMode, HardwareMap _hardwareMap) {
        hardwareMap = _hardwareMap;
        callingOpMode = _callingOpMode;

        motorRight=hardwareMap.dcMotor.get("right");
        motorLeft=hardwareMap.dcMotor.get("left");
        motorLifter=hardwareMap.dcMotor.get("up");
        servoLeft=hardwareMap.servo.get("leftServo");
        servoRight=hardwareMap.servo.get("rightServo");

        motorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        motorLifter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        motorLifter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorRight.setDirection(DcMotor.Direction.REVERSE);

        encoderMotor = motorLeft;

        //Accessing gyro and accelerometer from Android
        mSensorManager = (SensorManager) hardwareMap.appContext.getSystemService(SENSOR_SERVICE);
        mRotationVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        mSensorManager.registerListener(this, mRotationVectorSensor, 10000);
    }

    protected void driveStraight(double inches, int heading) { driveStraight(inches, heading, driveSpeed); }

    /**
     * Tells the robot to drive forward at a certain heading for a specified distance
     * @param inches number of inches we ask the robot to drive, negative numbers drive bot backwards
     * @param heading heading of bot as it drives, range 0-360, DO NOT use to turn as it drives but instead to keep it in a straight line
     * @param power amount of power given to motors, does not affect distance driven, absolute value from 0 to 1
     */
    protected void driveStraight(double inches, int heading, double power) {
        int target = (int) (inches * COUNTS_PER_INCH);          //translates the number of inches to be driven into encoder ticks
        double error;                                           //The number of degrees between the true heading and desired heading
        double correction;                                      //Modifies power to account for error
        double leftPower;                                       //Power being fed to left side of bot
        double rightPower;                                      //Power being fed to right side of bot
        double max;                                             //To be used to keep powers from exceeding 1

        heading = (int) normalize360(heading);

        //Ensure that motors are set up correctly to drive
        motorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //Clip the input power to keep it from exceeding 1 & -1
        power = Range.clip(power, -1.0, 1.0);

        //Set drive motors to initial input power
        motorRight.setPower(power);
        motorLeft.setPower(power);

        //Begin to correct for heading error
        //While: we have not driven correct distance & bot is not stopped
        while (Math.abs(encoderMotor.getCurrentPosition()) < Math.abs(target) && !Thread.interrupted()) {
            error = heading - zRotation;

            //Modify error onto the -180-180 range
            while (error > 180) error = (error - 360);
            while (error <= -180) error = (error + 360);

            //Determine how much correction to be placed on each side of robot based on error
            correction = Range.clip(error * P_DRIVE_COEFF, -1, 1);

            //Incorporate our correction for our heading into the power
            leftPower = power + correction;
            rightPower = power - correction;

            //Take the larger of the two powers
            max = Math.max(Math.abs(leftPower), Math.abs(rightPower));
            //If the largest power is too big, divide them both by it.
            if (max > 1.0) {
                leftPower /= max;
                rightPower /= max;
            }

            //Put the power on and hit pause for a second
            motorLeft.setPower(leftPower);
            motorRight.setPower(rightPower);
            Thread.yield();
        }

        //When the drive is finished, it is time to turn off the drive motors
        motorLeft.setPower(0);
        motorRight.setPower(0);

        //Reset the motors for future use, just in case
        motorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    protected void turn(float turnHeading) throws InterruptedException { turn(turnHeading, turnSpeed);  }

    /**
     * Tells the robot to turn to a specified heading according to the GAME-ROTATION-VECTOR
     * @param turnHeading the desired heading for the bot. 0-360 range
     * @param power the desired power for the turn. -1-1 range
     * @throws InterruptedException can be interrupted
     */
    protected void turn(float turnHeading, double power) throws InterruptedException {
        int wrapFix = 0;                                        //Can be used to modify values and make math around 0 easier
        float shiftedTurnHeading = turnHeading;                 //Can be used in conjunction with wrapFix to make math around 0 easier

        //If heading is not on correct scale, put it between 0-360
        turnHeading = normalize360(turnHeading);

        //Figure out how far the robot would have to turn in counterclockwise & clockwise directions
        float cclockwise = zRotation - turnHeading;
        float clockwise = turnHeading - zRotation;

        //Normalize cwise & ccwise values to between 0=360
        clockwise = normalize360(clockwise);
        cclockwise = normalize360(cclockwise);

        int error = 1;                                          //sets the distance to the target gyro value that we will accept
        if (turnHeading - error < 0|| turnHeading + error > 360) {
            wrapFix = 180;                                      //if within the range where the clockmath breaks, shift to an easier position
            shiftedTurnHeading = normalize360(turnHeading + wrapFix);
        }

        //If it would be longer to take the ccwise path, we go *** CLOCKWISE ***
        if(Math.abs(cclockwise) >= Math.abs(clockwise)){
            //While we're not within our error, and we haven't overshot, and the bot is running
            while(Math.abs(normalize360(zRotation + wrapFix)- shiftedTurnHeading) > error &&
                    Math.abs(cclockwise) >= Math.abs(clockwise) && !Thread.interrupted()) {

                //Wait a hot decisecond
                Thread.sleep(10);

                motorLeft.setPower(power);
                motorRight.setPower(-power);
            }
        }
        //If it would take longer to take the cwise path, we go *** COUNTERCLOCKWISE ***
        else if(Math.abs(clockwise) > Math.abs(cclockwise)) {
            //While we're not within our error, and we haven't overshot, and the bot is running
            while (Math.abs(normalize360(zRotation + wrapFix) - shiftedTurnHeading) > error &&
                    Math.abs(clockwise) > Math.abs(cclockwise) && !Thread.interrupted()) {

                //Stop a hot decisecond
                Thread.sleep(10);

                motorLeft.setPower(-power);
                motorRight.setPower(power);
            }
        }

        motorRight.setPower(0);
        motorLeft.setPower(0);
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
    }


    private float normalize360(float val) {
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
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

}
