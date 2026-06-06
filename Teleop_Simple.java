package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name="Teleop Simple", group="TeleOp")
public class Teleop_Simple extends LinearOpMode {

    // Instantiate your hardware map
    RobotHardwareMap robot = new RobotHardwareMap();

    // Toggle Trackers
    boolean intakeOn = false;
    boolean lastRb = false;

    boolean flywheelsOn = false;
    boolean lastLb = false;

    boolean gateOpen = false;
    boolean lastRt = false;
    
    boolean lastDpadUp = false;
    boolean lastDpadDown = false;
    
    // NEW: Trackers for shooter velocity adjustment
    boolean lastDpadRight = false;
    boolean lastDpadLeft = false;

    // Mechanism Variables
    double hoodPosition = 0.5; // Starting middle point

    @Override
    public void runOpMode() {
        // Initialize hardware
        robot.init(hardwareMap);

        telemetry.addData("Status", "Initialized. Waiting for Start...");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {

            // ==========================================
            // 1. DRIVETRAIN: Field Centric
            // ==========================================
            double y = -gamepad1.left_stick_y; 
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;

            // Reset IMU heading on Options button
            if (gamepad1.options) {
                robot.imu.resetYaw();
            }

            // Get robot heading
            double botHeading = robot.imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

            // Rotate the movement direction counter to the bot's rotation
            double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
            double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

            rotX = rotX * 1.1;  // Counteract imperfect strafing

            double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1.0);
            double frontLeftPower = (rotY + rotX + rx) / denominator;
            double backLeftPower = (rotY - rotX + rx) / denominator;
            double frontRightPower = (rotY - rotX - rx) / denominator;
            double backRightPower = (rotY + rotX - rx) / denominator;

            robot.FL.setPower(frontLeftPower);
            robot.BL.setPower(backLeftPower);
            robot.FR.setPower(frontRightPower);
            robot.BR.setPower(backRightPower);


            // ==========================================
            // 2. INTAKE: Right Bumper Toggle
            // ==========================================
            if (gamepad1.right_bumper && !lastRb) {
                intakeOn = !intakeOn; 
                robot.intake.setPower(intakeOn ? 1.0 : 0.0);
            }
            lastRb = gamepad1.right_bumper;


            // ==========================================
            // 3. SHOOTER: Left Bumper Toggle & Velocity Adjust
            // ==========================================
            // Toggle On/Off
            if (gamepad1.left_bumper && !lastLb) {
                flywheelsOn = !flywheelsOn; 
                double targetVel = flywheelsOn ? robot.shooter_vel_constant : 0.0;
                robot.flywheelL.setVelocity(targetVel);
                robot.flywheelR.setVelocity(targetVel);
            }
            lastLb = gamepad1.left_bumper;

            // Increase Velocity
            if (gamepad1.dpad_right && !lastDpadRight) {
                robot.shooter_vel_constant += 50.0;
                if (flywheelsOn) {
                    robot.flywheelL.setVelocity(robot.shooter_vel_constant);
                    robot.flywheelR.setVelocity(robot.shooter_vel_constant);
                }
            }
            lastDpadRight = gamepad1.dpad_right;

            // Decrease Velocity
            if (gamepad1.dpad_left && !lastDpadLeft) {
                robot.shooter_vel_constant -= 50.0;
                // Prevent negative velocity
                if (robot.shooter_vel_constant < 0) {
                    robot.shooter_vel_constant = 0;
                }
                if (flywheelsOn) {
                    robot.flywheelL.setVelocity(robot.shooter_vel_constant);
                    robot.flywheelR.setVelocity(robot.shooter_vel_constant);
                }
            }
            lastDpadLeft = gamepad1.dpad_left;


            // ==========================================
            // 4. GATE: Right Trigger Toggle
            // ==========================================
            boolean currentRt = gamepad1.right_trigger > 0.5;
            if (currentRt && !lastRt) {
                gateOpen = !gateOpen; 
                robot.gate.setPosition(gateOpen ? robot.gate_open_position : robot.gate_close_position);
            }
            lastRt = currentRt;


            // ==========================================
            // 5. HOOD: Dpad Up/Down Adjustments
            // ==========================================
            if (gamepad1.dpad_up && !lastDpadUp) {
                hoodPosition += 0.05;
            }
            lastDpadUp = gamepad1.dpad_up;

            if (gamepad1.dpad_down && !lastDpadDown) {
                hoodPosition -= 0.05;
            }
            lastDpadDown = gamepad1.dpad_down;

            // Clamp hood position between 0.0 and 1.0 to prevent servo damage
            hoodPosition = Math.max(0.0, Math.min(1.0, hoodPosition));
            
            robot.hoodL.setPosition(hoodPosition);
            robot.hoodR.setPosition(hoodPosition);

            // ==========================================
            // TELEMETRY
            // ==========================================
            telemetry.addData("Intake On", intakeOn);
            telemetry.addData("Flywheels On", flywheelsOn);
            telemetry.addData("Target Velocity", robot.shooter_vel_constant);
            telemetry.addData("Gate Open", gateOpen);
            telemetry.addData("Hood Position", String.format("%.2f", hoodPosition));
            telemetry.addData("Heading (Deg)", Math.toDegrees(botHeading));
            telemetry.update();
        }
    }
}