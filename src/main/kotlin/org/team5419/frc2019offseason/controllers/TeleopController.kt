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
    private var speed: Double = Constants.Input.BASE_SPEED
    private var leftDrive: Double = 0.0
    private var rightDrive: Double = 0.0

    private var liftIndex: Int = 0
    private var lastLiftIndex: Int = 0

    init {
        mDriver = driver
        mCoDriver = driver
        mSubsystems = subsystems
    }

    override fun start() {
        isSlow = false
        isReverse = false
        speed = Constants.Input.BASE_SPEED
        rightDrive = 0.0
        leftDrive = 0.0
    }

    override fun reset() {
        mSubsystems.resetAll()
    }

    @Suppress("ComplexMethod")
    override fun update() {
        // Driver
        speed = 1.0
        isSlow = false
        speed = Constants.Input.BASE_SPEED

        if (mDriver.getBumperPressed(Hand.kLeft)) {
            mSubsystems.drivetrain.setPercent(Constants.Input.SPIN_SPEED, Constants.Input.SPIN_SPEED*-1)
            return
        } else if (mDriver.getBumperPressed(Hand.kRight)) {
            mSubsystems.drivetrain.setPercent(Constants.Input.SPIN_SPEED*-1, Constants.Input.SPIN_SPEED)
            return
        }

        if (mDriver.getAButton()) isReverse = !isReverse
        if (isReverse) speed *= -1

        if (mDriver.getTriggerAxis(Hand.kLeft) > Constants.Input.CONTROLLER_MARGIN)
            speed *= Math.max(1.0 - mDriver.getTriggerAxis(Hand.kLeft), Constants.Input.MINIMAL_SLOW)

        rightDrive = mDriver.getX(Hand.kRight)
        leftDrive = mDriver.getX(Hand.kLeft)
        if (rightDrive > Constants.Input.CONTROLLER_MARGIN) rightDrive = 0.0
        if (leftDrive > Constants.Input.CONTROLLER_MARGIN) rightDrive = 0.0
        mSubsystems.drivetrain.setPercent(leftDrive * speed, rightDrive * speed)
        mSubsystems.updateAll()

        // Codriver
        if (mCoDriver.getBumperPressed(Hand.kLeft)) liftIndex--
        if (mCoDriver.getBumperPressed(Hand.kRight)) liftIndex++
        if (liftIndex != lastLiftIndex) mSubsystems.lift.goToHeight(LiftHeight.values()[liftIndex])
    }
}
