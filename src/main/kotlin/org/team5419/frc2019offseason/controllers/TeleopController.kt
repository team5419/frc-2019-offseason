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
    codriver: XboxController,
    controlMode: ControlModes
) : Controller() {

    private val mDriver: XboxController
    private val mCoDriver: XboxController
    private val mSubsystems: SubsystemsManager

    private val mDrivetrain: Drivetrain
    private val mLift: Lift
    private val mWrist: Wrist
    private val mClimber: Climber
    private val mVacuum: Vacuum

    private var isReverse: Boolean = false
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
        if (mDriver.getAButton()) { mDrivetrain.isReverse = !mDrivetrain.isReverse }

        val ds: DriveSignal = driveHelper.calculateOutput(
            mDriver.getY(Hand.kLeft),
            -mDriver.getX(Hand.kRight),
            isQuickTurn,
            isHighGear
        )
        mSubsystems.drivetrain.setPercent(ds)

        // Climb control
        if (mDriver.getBumperPressed(Hand.kRight) && mCoDriver.getBumperPressed(Hand.kLeft)) {
            mClimber.unlock()
            if(!mClimber.isUnlocking){
                mClimber.climb()
            }
        }

        // Codriver
        // Valve control
        if (mCoDriver.getBumperPressed(Hand.kLeft) || mCoDriver.getBumperPressed(Hand.kRight)){
            mSubsystems.vacuum.release()
        }

        // Vacuum control
        if (mCoDriver.getTriggerAxis(Hand.kLeft) > Input.DEADBAND) {
            mVacuum.pickBall(mCoDriver.getTriggerAxis(Hand.kLeft))
        } else if (mCoDriver.getTriggerAxis(Hand.kRight) > Input.DEADBAND) {
            mVacuum.pickHatch(mCoDriver.getTriggerAxis(Hand.kRight))
        } else { mSubsystems.vacuum.setPercent(0.0) }

        // button control
        if (mCoDriver.getAButtonPressed()) {
            mLift.setPosistion(LiftHeight.HATCH_LOW)
        } else if (mCoDriver.getBButtonPressed()) {
            mSubsystems.lift.setPosistion(LiftHeight.HATCH_MID)
        } else if (mCoDriver.getYButtonPressed()) {
            mSubsystems.lift.setPosistion(LiftHeight.HATCH_HIGH)
        } else if ((mCoDriver.getPOV() >= 340 && mCoDriver.getPOV() <= 360) ||
            (mCoDriver.getPOV() >= 0 && mCoDriver.getPOV() <= 20)) {
            mSubsystems.lift.setPosistion(LiftHeight.BALL_HIGH)
            mSubsystems.wrist.setPosition(WristPosistions.BALL_HIGH)
        } else if (mCoDriver.getPOV() >= 250 && mCoDriver.getPOV() <= 290) {
            mSubsystems.lift.setPosistion(LiftHeight.BALL_MID)
            mSubsystems.wrist.setPosition(WristPosistions.BALL_MID)
        } else if (mCoDriver.getPOV() >= 160 && mCoDriver.getPOV() <= 200) {
            mSubsystems.lift.setPosistion(LiftHeight.BALL_LOW)
            mSubsystems.wrist.setPosition(WristPosistions.BALL_LOW)
        }

        if (mCoDriver.getXButtonPressed()) {
            mWrist.setPosition(WristPosistions.BACKWARD)
        }
        if (mCoDriver.getPOV() >= 70 && mCoDriver.getPOV() <= 110) {
            mSubsystems.wrist.setPosition(WristPosistions.FORWARD)
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
