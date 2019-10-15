package org.team5419.frc2019offseason.controllers

import org.team5419.frc2019offseason.subsystems.SubsystemsManager
import org.team5419.frc2019offseason.Constants.Input
import org.team5419.frc2019offseason.Constants
import org.team5419.frc2019offseason.subsystems.Wrist.WristPosition
import org.team5419.frc2019offseason.subsystems.Lift.LiftHeight
import org.team5419.frc2019offseason.subsystems.Drivetrain
import org.team5419.frc2019offseason.subsystems.Wrist
import org.team5419.frc2019offseason.subsystems.Lift
import org.team5419.frc2019offseason.subsystems.Vacuum
import org.team5419.frc2019offseason.subsystems.Climber

import org.team5419.fault.Controller
import org.team5419.fault.input.SpaceDriveHelper
import org.team5419.fault.input.DriveHelper
import org.team5419.fault.input.DriveSignal

import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.GenericHID.Hand
@SuppressWarnings("ComplexCondition")
public class TeleopController(
    subsystems: SubsystemsManager,
    driver: XboxController,
    codriver: XboxController
) : Controller() {

    private val mDriver: XboxController
    private val mCoDriver: XboxController
    private val mSubsystems: SubsystemsManager

    private val mDrivetrain: Drivetrain
    private val mLift: Lift
    private val mWrist: Wrist
    private val mClimber: Climber
    private val mVacuum: Vacuum

    private var isReverse: Double = 1.0
    private var speed = Input.BASE_SPEED
    private var leftDrive: Double = 0.0
    private var rightDrive: Double = 0.0
    private var driveHelper: DriveHelper

    private var isManualOverride = true
    private val isHighGear: Boolean get() = mDriver.getTriggerAxis(Hand.kLeft) > 0.3
    private val isQuickTurn: Boolean get() = mDriver.getTriggerAxis(Hand.kRight) > 0.3

    public enum class ControlModes { TANK, CHEESY }

    init {
        mDriver = driver
        mCoDriver = codriver
        mSubsystems = subsystems
        driveHelper = SpaceDriveHelper(
            { Constants.Input.JOYSTICK_DEADBAND },
            { Constants.Input.TURN_MULT },
            { Constants.Input.SLOW_MULT }
        )
        mDrivetrain = mSubsystems.drivetrain
        mLift = mSubsystems.lift
        mWrist = mSubsystems.wrist
        mVacuum = mSubsystems.vacuum
        mClimber = mSubsystems.climber
    }

    override fun start() {
        isReverse = 1.0
        rightDrive = 0.0
        leftDrive = 0.0
        // mWrist.startZero()
    }

    override fun reset() {
        mSubsystems.resetAll()
    }

    @Suppress("ComplexMethod")
    override fun update() {
        // Driver
        if (mDriver.getAButtonPressed()) { isReverse = -isReverse }

        val ds: DriveSignal = driveHelper.calculateOutput(
            mDriver.getY(Hand.kLeft) * isReverse,
            -mDriver.getX(Hand.kRight),
            isQuickTurn,
            isHighGear
        )
        mSubsystems.drivetrain.setPercent(ds)

        // Climb control
        if (mDriver.getXButtonPressed()) {
            mClimber.unlock()
        }
        if (mDriver.getXButtonReleased()) {
            mClimber.stopUnlocking()
        }

        if (mDriver.getYButtonPressed()) {
            mClimber.climb()
        }
        if (mDriver.getYButtonReleased()) {
            mClimber.stop()
        }

        // Codriver

        // Vacuum control
        if (mCoDriver.getRawButton(7)) {
            mVacuum.pickBall()
        } else if (mCoDriver.getRawButton(8)) {
            mVacuum.pickHatch()
        } else if (mCoDriver.getBumper(Hand.kLeft) || mCoDriver.getBumper(Hand.kRight)) {
            mSubsystems.vacuum.release()
        } else {
            mVacuum.setPercent(0.2)
        }

        // button control
        if (mCoDriver.getBButtonPressed()) {
            mLift.setPosition(LiftHeight.HATCH_LOW)
            mWrist.setPosition(WristPosition.HATCH)
        } else if (mCoDriver.getXButtonPressed()) {
            mLift.setPosition(LiftHeight.HATCH_MID)
            mWrist.setPosition(WristPosition.HATCH)
        } else if (mCoDriver.getYButtonPressed()) {
            mLift.setPosition(LiftHeight.HATCH_HIGH)
            mWrist.setPosition(WristPosition.HATCH)
        } else if (mCoDriver.getPOV() != -1) {
            if (mCoDriver.getPOV() >= 340 || mCoDriver.getPOV() <= 20) {
                mLift.setPosition(LiftHeight.BALL_HIGH)
                mWrist.setPosition(WristPosition.BALL)
            } else if (mCoDriver.getPOV() >= 250 && mCoDriver.getPOV() <= 290) {
                mLift.setPosition(LiftHeight.BALL_MID)
                mWrist.setPosition(WristPosition.BALL)
            } else if (mCoDriver.getPOV() >= 160 && mCoDriver.getPOV() <= 200) {
                mLift.setPosition(LiftHeight.BALL_LOW)
                mWrist.setPosition(WristPosition.BALL)
            } else if (mCoDriver.getRawButtonPressed(12)) {
                mWrist.setPosition(WristPosition.BALL)
            } else if (mCoDriver.getRawButtonPressed(11)) {
                mLift.setPosition(LiftHeight.HUMAN_PLAYER)
                mWrist.setPosition(WristPosition.BALL)
            }
        }

        // if (mDriver.getStartButtonPressed()) {
        //     mWrist.startZero()
        // }
        if (mCoDriver.getAButtonPressed()) {
            mWrist.setPosition(WristPosition.HATCH)
        }
        if (mCoDriver.getPOV() >= 70 && mCoDriver.getPOV() <= 110) {

            mWrist.setPosition(WristPosition.FORWARD)
        }
        if (mCoDriver.getRawButtonPressed(14)) {
            mWrist.setPosition(WristPosition.DEFENSE)
        }

        // Manual Lift control
        // if (Math.abs(mCoDriver.getY(Hand.kLeft)) > Input.DEADBAND && isManualOverride) {
        //     mSubsystems.lift.setPercent(mCoDriver.getY(Hand.kLeft) * 0.5)
        // }
        // if (Math.abs(mCoDriver.getY(Hand.kRight)) > Input.DEADBAND && isManualOverride) {
        //     mSubsystems.wrist.setPercent(mCoDriver.getY(Hand.kLeft) * 0.5)
        // }
    }
}
