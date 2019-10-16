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
import org.team5419.frc2019offseason.input.ButtonBoard

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
    codriver: ButtonBoard
) : Controller() {

    private val mDriver: XboxController
    private val mCoDriver: ButtonBoard
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

    private var wristToggle = false

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
        if (mDriver.getBButtonPressed()) {
            mWrist.setPosition(WristPosition.DEFENSE)
        }

        // Codriver

        if (mCoDriver.getVaccumPressed()) {
            println("vacuum")
            if (mWrist.isHatchPosition) {
                mVacuum.pickHatch()
            } else if (!mWrist.isHatchPosition) {
                mVacuum.pickBall()
            } else {
            mVacuum.setPercent(0.2)
            }
        }

        if (mCoDriver.getValvePressed()) {
            println("valve")
            mVacuum.release() // how long should the valve be on?
        }

        // button control
        if (mCoDriver.getHatchLow()) {
            mLift.setPosition(LiftHeight.HATCH_LOW)
            mWrist.setPosition(WristPosition.HATCH)
        } else if (mCoDriver.getHatchMid()) {
            mLift.setPosition(LiftHeight.HATCH_MID)
            mWrist.setPosition(WristPosition.HATCH)
        } else if (mCoDriver.getHatchHigh()) {
            mLift.setPosition(LiftHeight.HATCH_HIGH)
            mWrist.setPosition(WristPosition.HATCH)
        } else if (mCoDriver.getBallLow()) {
            mLift.setPosition(LiftHeight.BALL_HIGH)
            mWrist.setPosition(WristPosition.BALL)
        } else if (mCoDriver.getBallMid()) {
            mLift.setPosition(LiftHeight.BALL_MID)
            mWrist.setPosition(WristPosition.BALL)
        } else if (mCoDriver.getBallHigh()) {
            mLift.setPosition(LiftHeight.BALL_LOW)
            mWrist.setPosition(WristPosition.BALL)
        } else if (mCoDriver.getHatchHumanPlayer()) {
            mLift.setPosition(LiftHeight.HUMAN_PLAYER)
            mWrist.setPosition(WristPosition.HATCH)
        } else if (mCoDriver.getBallHumanPlayer()) {
            mLift.setPosition(LiftHeight.HUMAN_PLAYER)
            mWrist.setPosition(WristPosition.BALL)
        }

        // if (mDriver.getStartButtonPressed()) {
        //     mWrist.startZero()
        // }
        if (mCoDriver.getFlipWrist()) {
            wristToggle = !wristToggle
            if (wristToggle) {
                mWrist.setPosition(WristPosition.FORWARD)
            } else {
                mWrist.setPosition(WristPosition.BACKWARD)
            }
        }
        isManualOverride = mCoDriver.getSlider() > 0.9
        // Manual Lift control
        if (isManualOverride && Math.abs(mCoDriver.getManualInput()) > Constants.Input.DEADBAND) {
            mLift.setPercent(mCoDriver.getManualInput())
        }
    }
}
