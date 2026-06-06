package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

public class RobotHardwareMap {

    // Drive Motors
    public DcMotor FL, FR, BL, BR;
    
    // Mechanism Motors
    public DcMotor intake;
    public DcMotorEx flywheelL, flywheelR; // DcMotorEx is required for setVelocity()
    
    // Servos
    public Servo hoodL, hoodR, gate;
    
    // Sensors
    public IMU imu;

    // Variables / Constants
    public double gate_open_position = 0.5;
    public double gate_close_position = 0.0;
    public double shooter_vel_constant = 2000.0; // In Ticks per Second

    // Initialization Method
    public void init(HardwareMap hwMap) {
        
        // --- Initialize Drive Motors ---
        FL = hwMap.get(DcMotor.class, "FL");
        FR = hwMap.get(DcMotor.class, "FR");
        BL = hwMap.get(DcMotor.class, "BL");
        BR = hwMap.get(DcMotor.class, "BR");

        // Reverse right side motors (adjust based on your physical build)
        FR.setDirection(DcMotor.Direction.REVERSE);
        BR.setDirection(DcMotor.Direction.REVERSE);

        FL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        FR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        BL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        BR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // --- Initialize Mechanisms ---
        intake = hwMap.get(DcMotor.class, "intake");
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        flywheelL = hwMap.get(DcMotorEx.class, "flywheelL");
        flywheelR = hwMap.get(DcMotorEx.class, "flywheelR");
        
        // Reverse one flywheel so they spin together to shoot the ball
        flywheelR.setDirection(DcMotor.Direction.REVERSE); 
        
        flywheelL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheelR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // --- Initialize Servos ---
        hoodL = hwMap.get(Servo.class, "hoodL");
        hoodR = hwMap.get(Servo.class, "hoodR");
        gate = hwMap.get(Servo.class, "gate");
        
        // Reverse one hood servo if they mirror each other
        hoodR.setDirection(Servo.Direction.REVERSE);
        
        // Set initial gate position
        gate.setPosition(gate_close_position);

        // --- Initialize IMU ---
        imu = hwMap.get(IMU.class, "imu");
        // Update these parameters based on how your Control Hub is mounted
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection  usbDirection  = RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu.initialize(new IMU.Parameters(orientationOnRobot));
    }
}