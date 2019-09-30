package org.team5419.frc2019offseason.controllers

import org.team5419.frc2019offseason.subsystems.SubsystemsManager
import org.team5419.frc2019offseason.subsystems.Lift.LiftHeight

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

    @Suppress("ReturnCount")
    private fun getLiftHeightFromPOV(angle: Int): LiftHeight {
        if (angle == 0) return LiftHeight.BALL_HIGH
        if (angle == 270) return LiftHeight.BALL_MID
        return LiftHeight.BALL_LOW
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

            rightDrive = mDriver.getX(Hand.kRight)
            leftDrive = mDriver.getX(Hand.kLeft)
            if (rightDrive > Constants.Input.CONTROLLER_MARGIN) rightDrive = 0.0
            if (leftDrive > Constants.Input.CONTROLLER_MARGIN) rightDrive = 0.0
            mSubsystems.drivetrain.setPercent(leftDrive * speed, rightDrive * speed)
        }

        // Codriver
        // Valve control
        if (mCoDriver.getBumperPressed(Hand.kLeft) || mCoDriver.getBumperPressed(Hand.kRight))
            mSubsystems.vacuum.valve = false
        // Vacuum control
        @Suppress("MaxLineLength")
        if (
            Math.max(mCoDriver.getTriggerAxis(Hand.kLeft), mCoDriver.getTriggerAxis(Hand.kRight)
        ) > Constants.Input.CONTROLLER_MARGIN)
            mSubsystems.vacuum.setPercent(Math.max(
                mCoDriver.getTriggerAxis(Hand.kLeft),
                mCoDriver.getTriggerAxis(Hand.kRight)))
        // Absolute lift control
        if (mCoDriver.getAButtonPressed())
            mSubsystems.lift.setPoint(LiftHeight.HATCH_LOW)
        else if (mCoDriver.getBButtonPressed())
            mSubsystems.lift.setPoint(LiftHeight.HATCH_MID)
        else if (mCoDriver.getYButtonPressed())
            mSubsystems.lift.setPoint(LiftHeight.HATCH_HIGH)
        else if (mCoDriver.getPOV() != -1 && arrayOf(0, 270, 90).any { it == mCoDriver.getPOV() })
            mSubsystems.lift.setPoint(getLiftHeightFromPOV(mCoDriver.getPOV()))
        // Manuel Lift control
        if (Math.max(mCoDriver.getX(Hand.kLeft), mCoDriver.getX(Hand.kRight)) > 0.9)
            mSubsystems.lift.setPercent(0.5)
        else if (Math.max(mCoDriver.getX(Hand.kLeft), mCoDriver.getX(Hand.kRight)) < -0.9)
            mSubsystems.lift.setPercent(-0.5)
    }
}
