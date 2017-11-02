package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class RobotBase {


    DcMotor motorRight, motorLeft;
    OpMode callingOpMode;
    HardwareMap hardwareMap;
    DcMotor encoderMotor;

    static final double COUNTS_PER_MOTOR_REV = 1100;    // NeveRest Motor Encoder
    static final double DRIVE_GEAR_REDUCTION = 1.0;     // This is < 1.0 if geared UP
    static final double WHEEL_DIAMETER_INCHES = 4.0;    // For figuring circumference

    //encoder ticks per one inch
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);


    private static final double[] scaleArray = {0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
            0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00};


    void init(OpMode _callingOpMode, HardwareMap _hardwareMap) {
        callingOpMode = _callingOpMode;
        hardwareMap = _hardwareMap;

        motorLeft = hardwareMap.dcMotor.get("left");
        motorRight = hardwareMap.dcMotor.get("right");
        encoderMotor = motorLeft;

        motorRight.setDirection(DcMotor.Direction.REVERSE);
    }

    void updateDriveMotors(double left, double right, boolean slowDrive) {
        double kSlowDrive = 0.3;

        right = Range.clip(right, -1, 1);
        left = Range.clip(left, -1, 1);

        if(slowDrive) {
            left *= 0.3;
            right *= 0.3;
        }
        left = scaleInput(left);
        right = scaleInput(right);

        motorLeft.setPower(left);
        motorRight.setPower(right);
    }

    void beeline(double inches, double power) {
        motorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        callingOpMode.telemetry.addLine("SSS initial encoder value: " + encoderMotor.getCurrentPosition());
        callingOpMode.telemetry.update();

        int target = (int) (inches * COUNTS_PER_INCH); //translates the number of inches to be driven into encoder ticks

        //TODO: finish this method fam

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
}
