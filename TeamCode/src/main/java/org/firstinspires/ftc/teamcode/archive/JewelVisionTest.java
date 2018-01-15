package org.firstinspires.ftc.teamcode.archive;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;

import com.google.blocks.ftcrobotcontroller.util.SoundsUtil;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
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

/**
 * Created by N2Class1 on 9/17/2017.
 */
@Disabled
@Autonomous(name = "JewelVisionTest", group = "test")
public class JewelVisionTest extends LinearOpMode {
    VuforiaLocalizer vuforia;


    int thisR, thisB, thisG;                    //RGB values of current pixel to translate into HSV
    int xRedAvg = 0;                            //Average X position of red pixels to help find red side location
    int xBlueAvg = 0;                           //Average X position of blue pixels to help find blue side location
    int totalBlue = 1;                          //Total number of blue pixels to help find blue side location
    int totalRed = 1;                           //Total number of red pixels to help find red side location
    int xRedSum = 0;                            //Added-up X pos of red pixels to find red side location
    int xBlueSum = 0;                           //Added-up X pos of blue pix to find blue side location
    int idx = 0;                                //Ensures we get correct image type from Vuforia
    float[] hsv = new float[3];                 //Array to hold Hue, Saturation, Value values for each pixel
    float[] v = new float[3];                 //Array to hold Hue, Saturation, Value values for each pixel
    float thisH;                                //Hue value of current pixel to find its color
    float thisS;



    @Override
    public void runOpMode() throws InterruptedException {
        //If Vuforia has not yet started, we're screwed. Start it up now in the name of hope
        if(vuforia == null) initVuforia();

        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTrackables.activate();

        waitForStart();

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

        RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);
        relicTrackables.deactivate();
        //Origin: top right of image (current guess)

        //Loop through every pixel column
        int h = image.getHeight();
        int w = image.getWidth();


        System.out.println("timestamp before processing loop");

        for (int i = 0; i < h; i++) {

            //If the bot stops you should really stop.
            if(Thread.interrupted()) break;

            //Loop through a certain number of rows to cover a certain area of the image
            for (int j = 0; j < w; j++) { //925, 935

                //Take the RGB vals of current pix
                thisR = px.get(i * w * 3 + (j * 3)) & 0xFF;
                thisG = px.get(i * w * 3 + (j * 3) + 1) & 0xFF;
                thisB = px.get(i * w * 3 + (j * 3) + 2) & 0xFF;


                //Convert the RGB vals into the HSV array
                Color.RGBToHSV(thisR, thisG, thisB, hsv);
                v = hsv;
                //Get the hue
                thisH = hsv[0];
                thisS = hsv[1];
                boolean isBlue;
                //System.out.println("Saturation: " + thisS);

                //We now have the colors (one byte each) for any pixel, (j, i) so we can add to the totals
                if (thisS >= 0.95) {
                    System.out.println("Jewel pixel found");
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

            System.out.println("timestamp after processing loop, before save pic");

        boolean bSavePicture = true;
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

        if (vuMark != RelicRecoveryVuMark.UNKNOWN) {
            if (vuMark == RelicRecoveryVuMark.LEFT) {
                System.out.println("RESULT Pictograph Left Column");
                telemetry.addLine("RESULT Pictograph Left Column");
            }
            if (vuMark == RelicRecoveryVuMark.CENTER) {
                System.out.println("RESULT Pictograph Center Column");
                telemetry.addLine("RESULT Pictograph Center Column");
            }
            if (vuMark == RelicRecoveryVuMark.RIGHT) {
                System.out.println("RESULT Pictograph Right Column");
                telemetry.addLine("RESULT Pictograph Right Column");
            }
        }
        else {
            System.out.println("RESULT Pictograph not found");
            telemetry.addLine("RESULT Pictograph not found");
        }
        if(xBlueAvg > xRedAvg) {
            System.out.println("BLUE_RED");
            telemetry.addLine("BLUE_RED");
        }
        if(xBlueAvg < xRedAvg) {
            System.out.println("RED_BLUE");
            telemetry.addLine("RED_BLUE");
        }
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

}
