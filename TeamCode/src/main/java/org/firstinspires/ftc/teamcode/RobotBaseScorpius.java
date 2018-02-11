package org.firstinspires.ftc.teamcode;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;
import com.vuforia.CameraDevice;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.SENSOR_SERVICE;

@SuppressWarnings("WeakerAccess")
public class RobotBaseScorpius implements SensorEventListener{
    DcMotor motorFrontRight, motorFrontLeft, motorBackLeft, encoderMotor, motorBackRight, motorLifter, motorScoop;
    Servo servoSlapperHorizontal, servoSlapperVertical, servoGrabberLeft, servoGrabberRight;

    private OpMode callingOpMode;
    private HardwareMap hardwareMap;

    private static final double COUNTS_PER_MOTOR_REV = 1100;    // NeveRest Motor Encoder
    private static final double DRIVE_GEAR_REDUCTION = 26.0/32.0;     // Numerator is gear on motor; Denominator is gear on wheel
    private static final double WHEEL_DIAMETER_INCHES = 4.0;    // For figuring circumference

    //encoder ticks per one inch
    private static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV) / (WHEEL_DIAMETER_INCHES * Math.PI * DRIVE_GEAR_REDUCTION);


    //sets variables for vision
    private VuforiaLocalizer vuforia;
    protected static int jewelPosition;                           //holds the value of one of the above jewel positions for reference
    protected static RelicRecoveryVuMark pictoPosition;           //holds one of the pictograph position for reference
    static final int JEWEL_UNKNOWN = 0;
    static final int JEWEL_BLUE_RED = 1;
    static final int JEWEL_RED_BLUE = 2;

    //variables for gyro operation
    private float zero;
    private float rawGyro;
    public int sensorDataCounter = 0;

    //arrays for gyro operation
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];

    //objects for gyro operation
    private SensorManager mSensorManager;
    private Sensor mRotationVectorSensor;

    protected boolean hasBeenZeroed= false;

    VuforiaTrackables relicTrackables;
    VuforiaTrackable relicTemplate;

    // This is relative to the initial position of the robot.
    // Possible values are:  0-360
    // 0 is set as straight ahead of the robot, 90 is the right, 270 is to the left
    private float zRotation;

    private static final double P_DRIVE_COEFF = 0.013;           // Larger is more responsive, but also less stable
    private static final double P_TURN_COEFF = 0.018;           // Larger is more responsive, but also less stable

    protected static final double driveSpeed = 0.6;
    protected static final double turnSpeed = 0.4;

    final double slapperVertical_INITIAL = 0.875;
    final double slapperHorizontal_INITIAL = 0.37;

    final double slapperHorizontal_READY = 0.195;
    final double slapperVertical_READY = 0.4;

    final double slapperHorizontal_FORWARD = 0.1;
    final double slapperHorizontal_BACKWARD = 0.28;


    protected void init(OpMode _callingOpMode, HardwareMap _hardwareMap) {
        callingOpMode = _callingOpMode;
        hardwareMap = _hardwareMap;

        //retrieve all motors and servos from the hardware map
        motorFrontLeft = hardwareMap.dcMotor.get("frontLeft");
        motorFrontRight = hardwareMap.dcMotor.get("frontRight");
        motorBackRight = hardwareMap.dcMotor.get("backRight");
        motorBackLeft = hardwareMap.dcMotor.get("backLeft");

        motorScoop = hardwareMap.dcMotor.get("scoop");
        motorLifter = hardwareMap.dcMotor.get("lifter");

        servoSlapperHorizontal = hardwareMap.servo.get("slapperHorizontal");
        servoSlapperVertical = hardwareMap.servo.get("slapperVertical");
        servoGrabberLeft = hardwareMap.servo.get("grabberLeft");
        servoGrabberRight = hardwareMap.servo.get("grabberRight");

        //reverses left side so that the robot drives forward when positive power is applied to all drive motors
        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE);
        motorBackLeft.setDirection(DcMotor.Direction.REVERSE);

        //Resets all encoders
        motorFrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLifter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorScoop.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Ensures that speed control is turned off
        motorFrontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFrontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorLifter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorScoop.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //Brake the motors when power is 0
        motorFrontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorFrontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBackLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBackRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        encoderMotor = motorFrontLeft;

        //sets all initial servo values
        servoSlapperVertical.setPosition(0.875);
        servoSlapperHorizontal.setPosition(0.37);
    }
    protected void initVuforia() {
        //Accessing gyro and accelerometer from Android
        mSensorManager = (SensorManager) hardwareMap.appContext.getSystemService(SENSOR_SERVICE);
        mRotationVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        mSensorManager.registerListener(this, mRotationVectorSensor, 10000);

        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(com.qualcomm.ftcrobotcontroller.R.id.cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "Ac8xsqH/////AAAAGcG2OeE2NECwo7mM5f9KX1RKmDT79NqkIHc/ATgW2+loN9Fr8fkfb6jE42RZmiRYeei1FvM2M3kUPdl53j" +
                "+oeuhahXi7ApkbRv9cef0kbffj+4EkWKWCgQM39sRegfX+os6PjJh1fwGdxxijW0CYXnp2Rd1vkTjIs/cW2/7TFTtuJTkc17l" +
                "+FNJAeqLEfRnwrQ0FtxvBjO8yQGcLrpeKJKX/+sN+1kJ/cvO345RYfPSoG4Pi+wo/va1wmhuZ/WCLelUeww8w8u0douStuqcuz" +
                "ufrsWmQThsHqQDfDh0oGKZGIckh3jwCV2ABkP0lT6ICBDm4wOZ8REoyiY2kjsDnnFG6cT803cfzuVuPJl+uGTEf";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);
        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB888, true);
        vuforia.setFrameQueueCapacity(1);

        //Set up the trackables for the pictographs so we can grab that information later
        relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        relicTemplate = relicTrackables.get(0);
        relicTrackables.activate();
        CameraDevice.getInstance().setFlashTorchMode(true);
    }

    /**
     * Extends the glypter mechanism, hopefully placing a glyph into the cryptobox.
     * @throws InterruptedException
     */
    protected void extendGlyphter () throws InterruptedException {
        //the distance that the motor is ran before the springs take over
        int encoderDist = 800;
        //change in encoder clicks per loop
        int speed;
        int lastPos = 0;
        //measures the amount of stall
        int stallCount = 0;

        motorLifter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLifter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorLifter.setPower(0.3);
        while(Math.abs(motorLifter.getCurrentPosition()) < Math.abs(encoderDist)) {
            speed = Math.abs(motorLifter.getCurrentPosition()) - lastPos;
            if(speed < 3) {
                stallCount++;
            }
            if(stallCount > 50) {
                motorLifter.setPower(-0.3);
            }
            lastPos = Math.abs(motorLifter.getCurrentPosition());
            Thread.sleep(10);
        }
        motorLifter.setPower(0);
    }
    protected void retractGlyphter (int i) throws InterruptedException{
        motorLifter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLifter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        int timer = 0;
        motorLifter.setPower(-0.5);
        while(Math.abs(motorLifter.getCurrentPosition()) < Math.abs(i) && timer < 200 ) {
            Thread.sleep(10);
            timer++;
        }
        motorLifter.setPower(0);
    }

    protected void lowerGrabby () throws InterruptedException{
        motorScoop.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorScoop.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        motorScoop.setPower(-0.1);
        while(Math.abs(motorScoop.getCurrentPosition()) < 825) {
            Thread.sleep(10);
        }
        motorScoop.setPower(0);
    }

    protected void raiseGrabby () throws InterruptedException{
        motorScoop.setPower(0.1);
        Thread.sleep(500);
        motorScoop.setPower(0);
    }

    protected void closeGrabby () {
        servoGrabberLeft.setPosition(0.6);
        servoGrabberRight.setPosition(0.4);
    }

    protected void openGrabby () {
        servoGrabberLeft.setPosition(0.81);
        servoGrabberRight.setPosition(0.23);
    }
    protected void initGrabby(boolean isAuto) {
        if (isAuto) {
            servoGrabberLeft.setPosition(0.90);
            servoGrabberRight.setPosition(0.14);
        }
        else {
            servoGrabberLeft.setPosition(0.81);
            servoGrabberRight.setPosition(0.23);
        }
    }

    protected void slapJewel (boolean forward) throws InterruptedException {
        servoSlapperHorizontal.setPosition(slapperHorizontal_READY);
        Thread.sleep(800);
        servoSlapperVertical.setPosition(slapperVertical_READY);
        Thread.sleep(1000);
        if(forward) {
            servoSlapperHorizontal.setPosition(slapperHorizontal_FORWARD);
        }
        else {
            servoSlapperHorizontal.setPosition(slapperHorizontal_BACKWARD);
        }
        Thread.sleep(800);
        servoSlapperVertical.setPosition(slapperVertical_INITIAL);
        Thread.sleep(200);
        servoSlapperHorizontal.setPosition(slapperHorizontal_INITIAL);

    }

    protected void updateDriveMotors(double frontLeft, double frontRight, double backLeft, double backRight, boolean slowDrive) {
        //tank drive

        if (slowDrive) {
            frontLeft /= 2;
            frontRight /= 2;
            backLeft /= 2;
            backRight /= 2;
        }

        motorFrontLeft.setPower(frontLeft);
        motorFrontRight.setPower(frontRight);
        motorBackLeft.setPower(backLeft);
        motorBackRight.setPower(backRight);
    }

    protected void vision(int startXpx, int startYpx) throws InterruptedException {
        int thisR, thisB, thisG;                    //RGB values of current pixel to translate into HSV
        int totalBlue = 1;                          //Total number of blue pixels to help find blue side location
        int totalRed = 1;                           //Total number of red pixels to help find red side location
        int idx = 0;                                //Ensures we get correct image type from Vuforia
        float thisS;
        float minRGB, maxRGB;

        System.out.println("timestamp before getting image");
        callingOpMode.telemetry.addData("timestamp ", "before getting image");
        callingOpMode.telemetry.update();
        //Take an image from Vuforia in the correct format
        VuforiaLocalizer.CloseableFrame frame = vuforia.getFrameQueue().take();
        for (int i = 0; i < frame.getNumImages(); i++) {
            if (frame.getImage(i).getFormat() == PIXEL_FORMAT.RGB888) {
                idx = i;
                break;
            }
        }

        //Create an instance of the image and then of the pixels
        Image image = frame.getImage(idx);
        ByteBuffer px = image.getPixels();

        //Origin: top right of image (current guess)

        //Loop through every pixel column
        int h = image.getHeight();
        int w = image.getWidth();


        System.out.println("timestamp before processing loop");
        callingOpMode.telemetry.addData("timestamp ", "before processing image");
        callingOpMode.telemetry.update();
        for (int i = startXpx; i < startXpx + 270; i++) {

//            System.out.println("loop #" + i);
            //If the bot stops you should really stop.
            if(Thread.interrupted()) break;

            //Loop through a certain number of rows to cover a certain area of the image
            for (int j = startYpx; j < startYpx + 300; j++) { //925, 935

                //Take the RGB vals of current pix
                thisR = px.get(i * w * 3 + (j * 3)) & 0xFF;
                thisG = px.get(i * w * 3 + (j * 3) + 1) & 0xFF;
                thisB = px.get(i * w * 3 + (j * 3) + 2) & 0xFF;


                //Convert the RGB vals into S
                minRGB = Math.min(thisR, Math.min(thisB, thisG)) + 1;
                maxRGB = Math.max(thisR, Math.max(thisB, thisG)) + 1;
                thisS = (maxRGB - minRGB) / maxRGB;
                //System.out.println("Saturation: " + thisS);

                //We now have the colors (one byte each) for any pixel, (j, i) so we can add to the totals
                //if (thisS >= 0.85) {
                    //                  System.out.println("Jewel pixel found");
                    int diff = thisB - thisR;
                    if(diff > 10) {
                        totalBlue++;
                    }
                    else if (diff < -10) {
                        totalRed++;
                    }
                //}
            }
        }


        callingOpMode.telemetry.addData("timestamp ", "after processing loop before save pic/grab picto");
        callingOpMode.telemetry.update();
        System.out.println("timestamp after processing loop, before save pic/grab picto");

        //now grab the pictograph information since it's had time to set up, and shut it down
        pictoPosition = RelicRecoveryVuMark.from(relicTemplate);
        relicTrackables.deactivate();

        CameraDevice.getInstance().setFlashTorchMode(false);

        //save picture block
        boolean bSavePicture = false;
        if (bSavePicture) {
            // Reset the pixel pointer to the start of the image
            px = image.getPixels();
            // Create a buffer to hold 32-bit image dataa and fill it
            int bmpData[] = new int[w * h];
            int pixel;
            int index = 0;
            int x,y;
            for (y = 0; y < h; y++) {
                for (x = 0; x < w; x++) {
                    thisR = px.get() & 0xFF;
                    thisG = px.get() & 0xFF;
                    thisB = px.get() & 0xFF;
                    bmpData[index] = Color.rgb(thisR, thisG, thisB);
                    index++;
                }
            }
            // Now create a bitmap object from the buffer
            Bitmap bmp = Bitmap.createBitmap(bmpData, w, h, Bitmap.Config.ARGB_8888);
            // And save the bitmap to the file system
            // NOTE:  AndroidManifest.xml needs <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
            try {
                //to convert Date to String, use format method of SimpleDateFormat class.
                DateFormat dateFormat = new SimpleDateFormat("mm-dd__hh-mm-ss");
                String strDate = dateFormat.format(new Date());
                String path = Environment.getExternalStorageDirectory() + "/Snapshot__" + strDate + ".png";
                System.out.println("Snapshot filename" + path);
                File file = new File(path);
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } catch (Exception e) {
                System.out.println("Snapshot exception" + e.getStackTrace().toString());
            }
        }

        System.out.println("timestamp after save pic");
        callingOpMode.telemetry.addData("timestamp ", "after save pic");
        callingOpMode.telemetry.update();


        /*THIS BLOCK OF CODE IS FOR WHEN TWO JEWELS ARE IN SIGHT*/
        //set jewel var based on results
        /*if(xBlueAvg > xRedAvg) {
            jewelPosition = JEWEL_BLUE_RED;
        }
        else if(xBlueAvg < xRedAvg) {
            jewelPosition = JEWEL_RED_BLUE;
        }
        else {
            jewelPosition = JEWEL_UNKNOWN;
        }*/

        callingOpMode.telemetry.addData("totalBlue: ", totalBlue);
        callingOpMode.telemetry.addData("totalRed: ", totalRed);
        callingOpMode.telemetry.update();
        if(totalBlue > totalRed) {
            jewelPosition = JEWEL_BLUE_RED;
        }
        else if(totalRed > totalBlue) {
            jewelPosition = JEWEL_RED_BLUE;
        }
        else {
            jewelPosition = JEWEL_UNKNOWN;
        }

        //System.out.println("Red xAvg " + xRedAvg);
        //System.out.println("Blue xAvg " + xBlueAvg);
        //callingOpMode.telemetry.addData("totalBlue: ", totalBlue);
        //callingOpMode.telemetry.addData("totalRed: ", totalRed);
        //callingOpMode.telemetry.update();

    }


    protected void driveStraight(double inches, float heading) throws InterruptedException { driveStraight(inches, heading, driveSpeed); }

    /**
     * Tells the robot to drive forward at a certain heading for a specified distance
     * @param inches number of inches we ask the robot to drive, only posative numbers
     * @param heading heading of bot as it drives, range 0-360, DO NOT use to turn as it drives but instead to keep it in a straight line
     * @param power amount of power given to motors, does not affect distance driven, absolute value from 0 to 1
     */
    protected void driveStraight(double inches, float heading, double power) throws InterruptedException {
        int target = (int) (inches * COUNTS_PER_INCH);          //translates the number of inches to be driven into encoder ticks
        double error;                                           //The number of degrees between the true heading and desired heading
        double correction;                                      //Modifies power to account for error
        double leftPower;                                       //Power being fed to left side of bot
        double rightPower;                                      //Power being fed to right side of bot
        double max;                                             //To be used to keep powers from exceeding 1

        heading = (int) normalize360(heading);

        //Ensure that motors are set up correctly to drive
        motorFrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        motorFrontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFrontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //Clip the input power to keep it from exceeding 1 & -1
        power = Range.clip(power, -1.0, 1.0);

        //Set drive motors to initial input power
        motorFrontRight.setPower(power);
        motorFrontLeft.setPower(power);
        motorBackLeft.setPower(power);
        motorBackRight.setPower(power);

        //TODO determine whether an thread.interrupted or the isStopRequested method

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
            motorFrontRight.setPower(rightPower);
            motorFrontLeft.setPower(leftPower);
            motorBackLeft.setPower(leftPower);
            motorBackRight.setPower(rightPower);

            Thread.yield();
        }

        //When the drive is finished, it is time to turn off the drive motors
        motorFrontRight.setPower(0);
        motorFrontLeft.setPower(0);
        motorBackLeft.setPower(0);
        motorBackRight.setPower(0);
        Thread.sleep(500);
    }

    protected void turn(float turnHeading) throws InterruptedException { turn(turnHeading, turnSpeed);  }

    /**
     * Tells the robot to turn to a specified heading according to the GAME-ROTATION-VECTOR
     * @param turnHeading the desired heading for the bot. 0-360 range
     * @param power the desired power for the turn. 0-1 range
     * @throws InterruptedException can be interrupted
     */
    protected void turn(float turnHeading, double power) throws InterruptedException {
        int wrapFix = 0;                                        //Can be used to modify values and make math around 0 easier
        float shiftedTurnHeading = turnHeading;                 //Can be used in conjunction with wrapFix to make math around 0 easier

        power = Math.abs(power);                                //makes sure the power is positive
        if (power>1) power = 1;                                 //makes sure the power isn't >1

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

            motorFrontRight.setPower(-power);
            motorFrontLeft.setPower(power);
            motorBackLeft.setPower(power);
            motorBackRight.setPower(-power);


            //While we're not within our error, and we haven't overshot, and the bot is running
            while(Math.abs(normalize360(zRotation + wrapFix)- shiftedTurnHeading) > error &&
                    Math.abs(cclockwise) >= Math.abs(clockwise) && !Thread.interrupted()) {

                //Figure out how far the robot would have to turn in counterclockwise & clockwise directions
                cclockwise = zRotation - turnHeading;
                clockwise = turnHeading - zRotation;

                //Normalize cwise & ccwise values to between 0=360
                clockwise = normalize360(clockwise);
                cclockwise = normalize360(cclockwise);

                //Chill a hot decisecond
                Thread.sleep(10);
            }
        }
        //If it would take longer to take the cwise path, we go *** COUNTERCLOCKWISE ***
        else if(Math.abs(clockwise) > Math.abs(cclockwise)) {

            motorFrontRight.setPower(power);
            motorFrontLeft.setPower(-power);
            motorBackLeft.setPower(-power);
            motorBackRight.setPower(power);

            //While we're not within our error, and we haven't overshot, and the bot is running
            while (Math.abs(normalize360(zRotation + wrapFix) - shiftedTurnHeading) > error &&
                    Math.abs(clockwise) > Math.abs(cclockwise) && !Thread.interrupted()) {

                //Figure out how far the robot would have to turn in counterclockwise & clockwise directions
                cclockwise = zRotation - turnHeading;
                clockwise = turnHeading - zRotation;

                //Normalize cwise & ccwise values to between 0=360
                clockwise = normalize360(clockwise);
                cclockwise = normalize360(cclockwise);

                //Hold up a hot decisecond
                Thread.sleep(10);
            }
        }

        motorFrontRight.setPower(0);
        motorFrontLeft.setPower(0);
        motorBackLeft.setPower(0);
        motorBackRight.setPower(0);

        Thread.sleep(500);
    }

    protected void strafe(double inches, float heading) throws InterruptedException { strafe(inches, heading, driveSpeed); }

    /**
     * Tells the robot to strafe at a certain heading for a specified distance
     * @param inches number of inches we ask the robot to drive, only posative numbers
     * @param heading heading of bot as it drives, range 0-360, DO NOT use to turn as it drives but instead to keep it in a straight line
     * @param power amount of power given to motors, does not affect distance driven; positive goes left, negative goes right
     */
    protected void strafe(double inches, float heading, double power) throws InterruptedException {
        int target = (int) (inches * COUNTS_PER_INCH);          //translates the number of inches to be driven into encoder ticks
        double error;                                           //The number of degrees between the true heading and desired heading
        double correction;                                      //Modifies power to account for error
        double frontPower;                                      //Power being fed to front side of bot
        double backPower;                                       //Power being fed to back side of bot
        double max;                                             //To be used to keep powers from exceeding 1

        heading = (int) normalize360(heading);

        //Ensure that motors are set up correctly to drive
        motorFrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        motorFrontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFrontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //Clip the input power to keep it from exceeding 1 & -1
        power = Range.clip(power, -1.0, 1.0);

        //Set drive motors to initial input power
        motorFrontRight.setPower(-power);
        motorFrontLeft.setPower(power);
        motorBackLeft.setPower(-power);
        motorBackRight.setPower(power);

        //TODO determine whether an thread.interrupted or the isStopRequested method

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
            frontPower = power + correction;
            backPower = power - correction;

            //Take the larger of the two powers
            max = Math.max(Math.abs(frontPower), Math.abs(backPower));
            //If the largest power is too big, divide them both by it.
            if (max > 1.0) {
                frontPower /= max;
                backPower /= max;
            }

            //Put the power on and hit pause for a second
            motorFrontRight.setPower(frontPower);
            motorFrontLeft.setPower(frontPower);
            motorBackLeft.setPower(-backPower);
            motorBackRight.setPower(-backPower);

            Thread.yield();
        }

        //When the drive is finished, it is time to turn off the drive motors
        motorFrontRight.setPower(0);
        motorFrontLeft.setPower(0);
        motorBackLeft.setPower(0);
        motorBackRight.setPower(0);
        Thread.sleep(500);
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
    public void onSensorChanged(SensorEvent event) {
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
        SensorManager.getOrientation(rotationMatrix, orientation);

        sensorDataCounter++;

        rawGyro = (float) Math.toDegrees(orientation[0]);

        //If the zero hasn't been zeroed do the zero
        if (!hasBeenZeroed) {
            hasBeenZeroed = true;
            zero = rawGyro;
        }
        //Normalize zRotation to be used
        zRotation = normalize360(rawGyro - zero);

        /*
        if(sensorDataCounter % 100 == 0) {
            //callingOpMode.telemetry.addData("zRotation: ", zRotation);
            //callingOpMode.telemetry.update();
        }*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
