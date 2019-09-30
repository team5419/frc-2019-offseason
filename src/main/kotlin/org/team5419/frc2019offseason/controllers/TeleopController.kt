package org.team5419.frc2019offseason.controllers

import org.team5419.frc2019offseason.subsystems.SubsystemsManager
import org.team5419.frc2019offseason.subsystems.Wrist.WristPosistions

import org.team5419.frc2019offseason.Constants
import org.team5419.fault.Controller

import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.GenericHID.Hand

public class TeleopController(
    subsystems: SubsystemsManager,
    driver: XboxController,
    codriver: XboxController
) : Controller() {

    private val mDriver: XboxController
    private val mCoDriver: XboxController
    private val mSubsystems: SubsystemsManager

    private var isSlow: Boolean = false
    private var isReverse: Boolean = false
    private var speed = Constants.Input.BASE_SPEED
    private var leftDrive: Double = 0.0
    private var rightDrive: Double = 0.0

    init {
        mDriver = driver
        mCoDriver = codriver
        mSubsystems = subsystems
    }

    override fun start() {
        isSlow = false
        isReverse = false
        rightDrive = 0.0
        leftDrive = 0.0
    }

    override fun reset() {
        mSubsystems.resetAll()
    }

    @Suppress("ComplexMethod")
    override fun update() {
        // Driver
        isSlow = false
        speed = Constants.Input.BASE_SPEED

        if (mDriver.getTriggerAxis(Hand.kLeft) > Constants.Input.CONTROLLER_MARGIN) {
            mSubsystems.drivetrain.setPercent(mDriver.getTriggerAxis(Hand.kLeft), mDriver.getTriggerAxis(Hand.kLeft)*-1)
        } else if (mDriver.getTriggerAxis(Hand.kRight) > Constants.Input.CONTROLLER_MARGIN) {
            mSubsystems.drivetrain.setPercent(mDriver.getTriggerAxis(Hand.kLeft), mDriver.getTriggerAxis(Hand.kLeft)*-1)
        } else {
            // make better
            if (mDriver.getAButton()) isReverse = !isReverse
            if (isReverse) speed *= -1

            if (mDriver.getTriggerAxis(Hand.kLeft) > Constants.Input.CONTROLLER_MARGIN)
                speed *= Constants.Input.SLOW

            rightDrive = 0.0
            leftDrive = 0.0
            if (rightDrive > Constants.Input.CONTROLLER_MARGIN) rightDrive = mDriver.getY(Hand.kRight)
            if (leftDrive > Constants.Input.CONTROLLER_MARGIN) leftDrive = mDriver.getY(Hand.kLeft)
            mSubsystems.drivetrain.setPercent(leftDrive * speed, rightDrive * speed)
        }

        // Codriver
        // Valve control
        if (mCoDriver.getBumperPressed(Hand.kLeft) || mCoDriver.getBumperPressed(Hand.kRight))
            mSubsystems.vacuum.release()
        // // Vacuum control
        // @Suppress("MaxLineLength")
        if (mCoDriver.getTriggerAxis(Hand.kLeft) > Constants.Input.CONTROLLER_MARGIN)
            mSubsystems.vacuum.pickBall(mCoDriver.getTriggerAxis(Hand.kLeft))
        if (mCoDriver.getTriggerAxis(Hand.kRight) > Constants.Input.CONTROLLER_MARGIN)
            mSubsystems.vacuum.pickHatch(mCoDriver.getTriggerAxis(Hand.kRight))
        // // button control
        // if (mCoDriver.getAButtonPressed())
        //     mSubsystems.lift.setPosistion(LiftHeight.HATCH_LOW)
        // else if (mCoDriver.getBButtonPressed())
        //     mSubsystems.lift.setPosistion(LiftHeight.HATCH_MID)
        // else if (mCoDriver.getYButtonPressed())
        //     mSubsystems.lift.setPosistion(LiftHeight.HATCH_HIGH)
        // else if (mCoDriver.getPOV() == 0)
        //     mSubsystems.lift.setPosistion(LiftHeight.BALL_HIGH)
        // else if (mCoDriver.getPOV() == 90)
        //     mSubsystems.lift.setPosistion(LiftHeight.BALL_HIGH)
        // else if (mCoDriver.getPOV() == 180)
        //     mSubsystems.lift.setPosistion(LiftHeight.BALL_HIGH)

        if (mCoDriver.getXButtonPressed()) {
            mSubsystems.wrist.setPosition(WristPosistions.BACKWARD)
        }
        if (mCoDriver.getPOV() == 270)
            mSubsystems.wrist.setPosition(WristPosistions.FORWARD)

        // // Manuel Lift control
        // if (mCoDriver.getX(Hand.kLeft) > 0.9)
        //     mSubsystems.lift.setPercent(0.5)
        // if (mCoDriver.getX(Hand.kLeft) < -0.9)
        //     mSubsystems.lift.setPercent(-0.5)
        if (mCoDriver.getY(Hand.kRight) > 0.9)
            mSubsystems.wrist.setPercent(-0.5)
        if (mCoDriver.getY(Hand.kRight) < -0.9)
            mSubsystems.wrist.setPercent(-0.5)
    }
}
