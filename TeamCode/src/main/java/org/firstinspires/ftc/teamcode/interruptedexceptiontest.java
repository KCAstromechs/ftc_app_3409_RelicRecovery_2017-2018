package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by N2Class1 on 12/30/2017.
 */
@Disabled
@Autonomous(name="Exception test", group="test")
public class interruptedexceptiontest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        try {

            synchronized (this) {
                wait(1);
                while (!opModeIsActive()) {
                    sleep(10);
                }
            }
        }
        catch (InterruptedException e){
            System.out.println("sss exeption is thrown");
            throw e;
        }
    }
}
