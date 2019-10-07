package org.team5419.frc2019offseason.controllers

import org.team5419.frc2019offseason.subsystems.SubsystemsManager
import org.team5419.frc2019offseason.Constants.Input
import org.team5419.frc2019offseason.Constants
import org.team5419.frc2019offseason.subsystems.Wrist.WristPosistions
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

    private var isManuelOverride = true
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
        mWrist.startZero()
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
        // Valve control

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
            mLift.setPosistion(LiftHeight.HATCH_LOW)
            mWrist.setPosition(WristPosistions.HATCH)
        } else if (mCoDriver.getXButtonPressed()) {
            mLift.setPosistion(LiftHeight.HATCH_MID)
            mWrist.setPosition(WristPosistions.HATCH)
        } else if (mCoDriver.getYButtonPressed()) {
            mLift.setPosistion(LiftHeight.HATCH_HIGH)
            mWrist.setPosition(WristPosistions.HATCH)
        } else if ((mCoDriver.getPOV() >= 340 && mCoDriver.getPOV() <= 360) ||
            (mCoDriver.getPOV() >= 0 && mCoDriver.getPOV() <= 20)) {
            mLift.setPosistion(LiftHeight.BALL_HIGH)
            mWrist.setPosition(WristPosistions.BALL)
        } else if (mCoDriver.getPOV() >= 250 && mCoDriver.getPOV() <= 290) {
            mLift.setPosistion(LiftHeight.BALL_MID)
            mWrist.setPosition(WristPosistions.BALL)
        } else if (mCoDriver.getPOV() >= 160 && mCoDriver.getPOV() <= 200) {
            mLift.setPosistion(LiftHeight.BALL_LOW)
            mWrist.setPosition(WristPosistions.BALL)
        } else if (mCoDriver.getRawButtonPressed(12)) {
            mWrist.setPosition(WristPosistions.BALL)
        } else if (mCoDriver.getRawButtonPressed(11)) {
            mLift.setPosistion(LiftHeight.HUMAN_PLAYER)
            mWrist.setPosition(WristPosistions.BALL)
        }

        if (mDriver.getStartButtonPressed()) {
            mWrist.startZero()
        }
        if (mCoDriver.getAButtonPressed()) {
            mWrist.setPosition(WristPosistions.HATCH)
        }
        if (mCoDriver.getPOV() >= 70 && mCoDriver.getPOV() <= 110) {
            mWrist.setPosition(WristPosistions.FORWARD)
        }
        if (mCoDriver.getRawButtonPressed(14)) {
            mWrist.setPosition(WristPosistions.DEFENSE)
        }

        // mSubsystems.lift.setPercent(mCoDriver.getY(Hand.kLeft) * 0.5)
        // Manuel Lift control
        // if (Math.abs(mCoDriver.getY(Hand.kLeft)) > Input.DEADBAND && isManuelOverride) {
        //     mSubsystems.lift.setPercent(mCoDriver.getY(Hand.kLeft) * 0.5)
        // }
        // if (Math.abs(mCoDriver.getY(Hand.kRight)) > Input.DEADBAND && isManuelOverride) {
        //     mSubsystems.wrist.setPercent(mCoDriver.getY(Hand.kLeft) * 0.5)
        // }
    }
}
