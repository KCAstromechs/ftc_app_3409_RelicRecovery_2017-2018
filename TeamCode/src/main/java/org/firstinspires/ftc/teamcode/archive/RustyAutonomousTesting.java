package org.firstinspires.ftc.teamcode.archive;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;

import com.google.blocks.ftcrobotcontroller.util.SoundsUtil;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by N2Class1 on 10/19/2017.
 */
@Disabled
@Autonomous(name="Rusty Auto RR", group="test")
public class RustyAutonomousTesting extends LinearOpMode implements SensorEventListener{
    VuforiaLocalizer vuforia;


    DcMotor motorFrontLeft, motorFrontRight, motorBackLeft, motorBackRight, encoderMotor;

    static final double COUNTS_PER_MOTOR_REV = 1100;    // NeveRest Motor Encoder
    static final double DRIVE_GEAR_REDUCTION = 0.5;     // This is < 1.0 if geared UP
    static final double WHEEL_DIAMETER_INCHES = 4.0;    // For figuring circumference
    static final double P_BEELINE_COEFF = 0.01;         // Larger is more responsive, but also less stable
    static final double TURN_MINIMUM_SPEED = 0.2;

    static final int JEWEL_UNKNOWN = 0;
    static final int JEWEL_BLUE_RED = 1;
    static final int JEWEL_RED_BLUE = 2;

    static int jewelPosition;                           //holds the value of one of the above jewel positions for reference
    static RelicRecoveryVuMark pictoPosition;           //holds one of the pictograph position for reference

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
        //setup motors
        motorFrontLeft = hardwareMap.dcMotor.get("FrontLeft");
        motorFrontRight = hardwareMap.dcMotor.get("FrontRight");
        motorBackLeft = hardwareMap.dcMotor.get("BackLeft");
        motorBackRight = hardwareMap.dcMotor.get("BackRight");
        encoderMotor = hardwareMap.dcMotor.get("FrontLeft");

        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE);
        motorBackLeft.setDirection(DcMotor.Direction.REVERSE);

        //If Vuforia has not yet started, we're screwed. Start it up now in the name of hope
        if(vuforia == null) initVuforia();

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

        Vision();   //decide what pictograph and jewels are

        //print pictograph and jewel positions for our own reference
        if(pictoPosition == RelicRecoveryVuMark.LEFT)
            telemetry.addLine("left");
        else if(pictoPosition == RelicRecoveryVuMark.CENTER)
            telemetry.addLine("center");
        else if(pictoPosition == RelicRecoveryVuMark.RIGHT)
            telemetry.addLine("right");
        else
            telemetry.addLine("unknown");

        if(jewelPosition == JEWEL_BLUE_RED) {
            System.out.println("result BLUE_RED");
            telemetry.addLine("BLUE_RED");
        }
        else if(jewelPosition == JEWEL_RED_BLUE) {
            System.out.println("result RED_BLUE");
            telemetry.addLine("RED_BLUE");
        }
        else {
            System.out.println("result nothing");
            telemetry.addLine("UNKNOWN");
        }

        telemetry.update();

        //move to knock off the correct pixel depending on position
        if (jewelPosition == JEWEL_BLUE_RED) {
            turn(352, .25);
        }
        else if (jewelPosition == JEWEL_RED_BLUE) {
            turn(8, .25);
        }

        sleep(1000);

        //drive off of the ramp. The drive will correct for the previous turn.
        driveStraight(30, 0);

       /** while(!Thread.interrupted()) {
            telemetry.addLine("zRotation: " + zRotation);
            telemetry.addLine("encoderMotor: " + encoderMotor.getCurrentPosition());
            telemetry.update();
        } **/
    }

    void driveStraight(double inches, int heading) {
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


    public void Vision() throws InterruptedException {
        int thisR, thisB, thisG;                    //RGB values of current pixel to translate into HSV
        int xRedAvg = 0;                            //Average X position of red pixels to help find red side location
        int xBlueAvg = 0;                           //Average X position of blue pixels to help find blue side location
        int totalBlue = 1;                          //Total number of blue pixels to help find blue side location
        int totalRed = 1;                           //Total number of red pixels to help find red side location
        int xRedSum = 0;                            //Added-up X pos of red pixels to find red side location
        int xBlueSum = 0;                           //Added-up X pos of blue pix to find blue side location
        int idx = 0;                                //Ensures we get correct image type from Vuforia
        float thisS;
        float minRGB, maxRGB;
        int returnVal = 0;

        //Set up the trackables for the pictographs so we can grab that information later
        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTrackables.activate();

        System.out.println("timestamp before getting image");
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

        for (int i = 0; i < h; i++) {

//            System.out.println("loop #" + i);
            //If the bot stops you should really stop.
            if(Thread.interrupted()) break;

            //Loop through a certain number of rows to cover a certain area of the image
            for (int j = 0; j < w; j++) { //925, 935

                //Take the RGB vals of current pix
                thisR = px.get(i * w * 3 + (j * 3)) & 0xFF;
                thisG = px.get(i * w * 3 + (j * 3) + 1) & 0xFF;
                thisB = px.get(i * w * 3 + (j * 3) + 2) & 0xFF;


                //Convert the RGB vals into S
                minRGB = Math.min(thisR, Math.min(thisB, thisG)) + 1;
                maxRGB = Math.max(thisR, Math.max(thisB, thisG)) + 1;
                thisS = (maxRGB - minRGB) / maxRGB;
                boolean isBlue;
                //System.out.println("Saturation: " + thisS);

                //We now have the colors (one byte each) for any pixel, (j, i) so we can add to the totals
                if (thisS >= 0.95) {
  //                  System.out.println("Jewel pixel found");
                    isBlue = thisB - thisR > 0;
                    if (isBlue) {
                        totalBlue++;
                        xBlueSum += i;
                    } else if (!isBlue) {
                        totalRed++;
                        xRedSum += i;
                    }
                }
            }
        }

        telemetry.addLine("timestamp after processing loop, before save pic");
        System.out.println("timestamp after processing loop, before save pic");

        //now grab the pictograph information since it's had time to set up, and shut it down
        pictoPosition = RelicRecoveryVuMark.from(relicTemplate);
        relicTrackables.deactivate();

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
        //Find the averages
        xRedAvg = xRedSum / totalRed;
        xBlueAvg = xBlueSum / totalBlue;

        //set jewel var based on results
        if(xBlueAvg > xRedAvg) {
            jewelPosition = JEWEL_BLUE_RED;
        }
        else if(xBlueAvg < xRedAvg) {
            jewelPosition = JEWEL_RED_BLUE;
        }
        else {
            jewelPosition = JEWEL_UNKNOWN;
        }

        System.out.println("Red xAvg " + xRedAvg);
        System.out.println("Blue xAvg " + xBlueAvg);
        telemetry.update();

    }

    public void initVuforia() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(com.qualcomm.ftcrobotcontroller.R.id.cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "Ac8xsqH/////AAAAGcG2OeE2NECwo7mM5f9KX1RKmDT79NqkIHc/ATgW2+loN9Fr8fkfb6jE42RZmiRYeei1FvM2M3kUPdl53j" +
                "+oeuhahXi7ApkbRv9cef0kbffj+4EkWKWCgQM39sRegfX+os6PjJh1fwGdxxijW0CYXnp2Rd1vkTjIs/cW2/7TFTtuJTkc17l" +
                "+FNJAeqLEfRnwrQ0FtxvBjO8yQGcLrpeKJKX/+sN+1kJ/cvO345RYfPSoG4Pi+wo/va1wmhuZ/WCLelUeww8w8u0douStuqcuz" +
                "ufrsWmQThsHqQDfDh0oGKZGIckh3jwCV2ABkP0lT6ICBDm4wOZ8REoyiY2kjsDnnFG6cT803cfzuVuPJl+uGTEf";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);
        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB888, true);
        vuforia.setFrameQueueCapacity(1);
    }

    public void turn (float turnHeading, double power) throws InterruptedException {
        //first things first make sure that the heading we've put in is a real thing we can use
        turnHeading = normalize360(turnHeading);

        int wrapFix = 0;                                            //When we need to modify values close to 0 or 360 we use wrapFix to modify them
        float shiftedTurnHeading = turnHeading;                     //We add wrapFix to the turnHeading to get this and carefully modify values close to 0 or 360
        double motorSpeed = power;                                  //so we can modify power without changing the original
        float ccwise = zRotation - turnHeading;                     //The amount in degrees that the robot would have to turn if it went counterclockwise
        float cwise = turnHeading - zRotation;                      //The amnt. in degrees that th bot would have to turn if it went clockwise

        //make absolutely sure that the distances are acceptable for our use
        ccwise = normalize360(ccwise);
        cwise = normalize360(cwise);

        //If the heading we want is too close to 0 or 360, modify it and make note that we did
        int error = 4;
        if (turnHeading - error < 0 || turnHeading + error > 360) {
            wrapFix = 180;
            shiftedTurnHeading = normalize360(turnHeading + wrapFix);
        }

        if (Math.abs(ccwise) >= Math.abs(cwise)) {
            //if clockwise is the shortest way to go, then keep turning as long as we are still fairly far from our target heading and clockwise remains the shortest way to go
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

                //continually re evaluate course based on where we are
                ccwise = zRotation - turnHeading;
                cwise = turnHeading - zRotation;
            }
        }
        else if (Math.abs(cwise) > Math.abs(ccwise)) {
            //if counterclockwise is the shorter way to go, keep turning as long as it still is and we're not too close to our target
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

                //continually re-evaluate what the best course is based on current heading
                ccwise = zRotation - turnHeading;
                cwise = turnHeading - zRotation;
            }

        }

        motorFrontLeft.setPower(0);
        motorBackLeft.setPower(0);
        motorFrontRight.setPower(0);
        motorBackRight.setPower(0);

    }

    /**
     * Takes input of degrees and puts it on a 0 to 360 degree circle.
     * We use this because input from the phone gyro comes in a -180 to 180 degree circle
     * @param val should be any input that is meant to represent a certain number of degrees
     * @return the corrected val
     */
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
        //System.out.println("SSS zRotation" + zRotation);
//        Dbg("zRotation in callback: " , zRotation, false);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void deconstruct(){
        mSensorManager.unregisterListener(this);
    }

}