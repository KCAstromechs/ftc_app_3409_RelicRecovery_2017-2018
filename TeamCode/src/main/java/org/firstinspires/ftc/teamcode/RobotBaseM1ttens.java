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

public class RobotBaseM1ttens implements SensorEventListener {

    private DcMotor motorLeft, motorRight, motorLifter;
    private Servo servoLeft, servoRight, servoJewel;
    private DcMotor encoderMotor;
    private HardwareMap hardwareMap;
    private OpMode callingOpMode;

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

    private boolean hasBeenZeroed= false;

    // This is relative to the initial position of the robot.
    // Possible values are:  0-360
    // 0 is set as straight ahead of the robot, 90 is the right, 270 is to the left
    private float zRotation;

    private static final double COUNTS_PER_MOTOR_REV = 1100;    // NeveRest Motor Encoder
    private static final double DRIVE_GEAR_REDUCTION = 1.0;     // This is < 1.0 if geared UP
    private static final double WHEEL_DIAMETER_INCHES = 4.0;    // For figuring circumference

    protected static final double driveSpeed = 0.5;
    protected static final double turnSpeed = 0.2;


    private static final double P_DRIVE_COEFF = 0.01;           // Larger is more responsive, but also less stable
    static final double P_TURN_COEFF = 0.018;          // Larger is more responsive, but also less stable


    //encoder ticks per one inch
    protected static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);


    protected static final double[] scaleArray = {0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
            0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00};

    VuforiaTrackables relicTrackables;
    VuforiaTrackable relicTemplate;

    protected void init (OpMode _callingOpMode, HardwareMap _hardwareMap) {
        hardwareMap = _hardwareMap;
        callingOpMode = _callingOpMode;

        motorRight=hardwareMap.dcMotor.get("right");
        motorLeft=hardwareMap.dcMotor.get("left");
        motorLifter=hardwareMap.dcMotor.get("up");
        servoLeft=hardwareMap.servo.get("leftServo");
        servoRight=hardwareMap.servo.get("rightServo");
        servoJewel=hardwareMap.servo.get("jewelServo");

        motorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        motorLifter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        motorLifter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorLeft.setDirection(DcMotor.Direction.REVERSE);

        encoderMotor = motorLeft;

        servoLeft.setPosition(0.1);
        servoRight.setPosition(0.63);
        servoJewel.setPosition(0.72);

        //Accessing gyro and accelerometer from Android
        mSensorManager = (SensorManager) hardwareMap.appContext.getSystemService(SENSOR_SERVICE);
        mRotationVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        mSensorManager.registerListener(this, mRotationVectorSensor, 10000);
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

        //Set up the trackables for the pictographs so we can grab that information later
        relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        relicTemplate = relicTrackables.get(0);
        relicTrackables.activate();
    }

    protected void driveStraight(double inches, int heading) throws InterruptedException { driveStraight(inches, heading, driveSpeed); }

    /**
     * Tells the robot to drive forward at a certain heading for a specified distance
     * @param inches number of inches we ask the robot to drive, negative numbers drive bot backwards
     * @param heading heading of bot as it drives, range 0-360, DO NOT use to turn as it drives but instead to keep it in a straight line
     * @param power amount of power given to motors, does not affect distance driven, absolute value from 0 to 1
     */
    protected void driveStraight(double inches, int heading, double power) throws InterruptedException {
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

            motorLeft.setPower(power);
            motorRight.setPower(-power);


            //While we're not within our error, and we haven't overshot, and the bot is running
            while(Math.abs(normalize360(zRotation + wrapFix)- shiftedTurnHeading) > error &&
                    Math.abs(cclockwise) >= Math.abs(clockwise) && !Thread.interrupted()) {

                //Chill a hot decisecond
                Thread.sleep(10);
            }
        }
        //If it would take longer to take the cwise path, we go *** COUNTERCLOCKWISE ***
        else if(Math.abs(clockwise) > Math.abs(cclockwise)) {

            motorLeft.setPower(-power);
            motorRight.setPower(power);


            //While we're not within our error, and we haven't overshot, and the bot is running
            while (Math.abs(normalize360(zRotation + wrapFix) - shiftedTurnHeading) > error &&
                    Math.abs(clockwise) > Math.abs(cclockwise) && !Thread.interrupted()) {

                //Hold up a hot decisecond
                Thread.sleep(10);
            }
        }

        motorRight.setPower(0);
        motorLeft.setPower(0);

        Thread.sleep(500);
    }

    protected void grabberOpen(){
        servoLeft.setPosition(0.1);
        servoRight.setPosition(0.63);
    }
    protected void grabberClose(){
        servoLeft.setPosition(0.45);
        servoRight.setPosition(0.27);
    }

    protected void jewelDown(){
        servoJewel.setPosition(0.1);
    }
    protected void jewelUp(){
        servoJewel.setPosition(0.72);
    }


    protected void vision(int startXpx, int startYpx) throws InterruptedException {
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

        System.out.println("timestamp before getting image");
        callingOpMode.telemetry.addData("timestamp ", "before getting image");
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
        callingOpMode.telemetry.addData("timestamp ", "before getting image");
        for (int i = startXpx; i < startXpx + 245; i++) {

//            System.out.println("loop #" + i);
            //If the bot stops you should really stop.
            if(Thread.interrupted()) break;

            //Loop through a certain number of rows to cover a certain area of the image
            for (int j = startYpx; j < startYpx + 275; j++) { //925, 935

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


        callingOpMode.telemetry.addData("timestamp ", "after processing loop before save pic/grab picto");
        System.out.println("timestamp after processing loop, before save pic/grab picto");

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
        callingOpMode.telemetry.addData("timestamp ", "after save pic");
        //Find the averages
        xRedAvg = xRedSum / totalRed;
        xBlueAvg = xBlueSum / totalBlue;


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

        if(totalBlue > totalRed) {
            jewelPosition = JEWEL_BLUE_RED;
        }
        else if(totalRed > totalBlue) {
            jewelPosition = JEWEL_RED_BLUE;
        }
        else {
            jewelPosition = JEWEL_UNKNOWN;
        }

        System.out.println("Red xAvg " + xRedAvg);
        System.out.println("Blue xAvg " + xBlueAvg);
        callingOpMode.telemetry.addData("totalBlue: ", totalBlue);
        callingOpMode.telemetry.addData("totalRed: ", totalRed);
        //callingOpMode.telemetry.update();

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);
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

        if(sensorDataCounter % 100 == 0) {
            //callingOpMode.telemetry.addData("zRotation: ", zRotation);
            //callingOpMode.telemetry.update();
        }
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
