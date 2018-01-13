package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Disabled
@Autonomous(name="RobotS_AutoBlueFront", group="RobotS")
public class RobotSAutoBlueFront extends LinearOpMode {
    RobotBaseS robotBase;
    @Override
    public void runOpMode() throws InterruptedException {
        robotBase = new RobotBaseS();
        robotBase.init(this, hardwareMap);
        telemetry.addLine("SELECT PICTOGRAPH VALUE:");
        telemetry.update();
        int pictoGraph = 1;
        for(;;){
            if(gamepad1.x){
                pictoGraph = 1;
                telemetry.addLine("LEFT");
                telemetry.update();
                break;
            }
            if(gamepad1.a){
                pictoGraph = 2;
                telemetry.addLine("CENTER");
                telemetry.update();
                break;
            }
            if(gamepad1.b){
                pictoGraph = 3;
                telemetry.addLine("RIGHT");
                telemetry.update();
                break;
            }
            if (isStopRequested()) {
                break;
            }
        }
        waitForStart();
        if (pictoGraph == 1) {
            robotBase.driveStraight(28, 0);
        }
        if (pictoGraph == 2) {
            robotBase.driveStraight(36, 0);
        }
        if (pictoGraph == 3) {
            robotBase.driveStraight(44, 0);
        }
        robotBase.driveStraight(36, 0);
        robotBase.turn(90);
        robotBase.driveStraight(9, 90, -0.6);
        robotBase.extendGlyphter();
        sleep(750);
        robotBase.retractGlyphter();
        sleep(750);
        robotBase.driveStraight(9, 90);
        robotBase.driveStraight(12, 90, -0.6);
        }
}
