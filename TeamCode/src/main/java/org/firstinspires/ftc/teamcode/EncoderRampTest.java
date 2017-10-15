package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by N2Class1 on 10/15/2017.
 */

public class EncoderRampTest extends LinearOpMode {
    RobotBase rb = new RobotBase();

    @Override
    public void runOpMode() throws InterruptedException {
        rb.init(this, hardwareMap);
        rb.beeline(24, 0.75);
    }
}
