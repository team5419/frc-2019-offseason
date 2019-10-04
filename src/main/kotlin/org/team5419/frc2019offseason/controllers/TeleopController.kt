package org.team5419.frc2019offseason.controllers

import org.team5419.frc2019offseason.subsystems.SubsystemsManager
import org.team5419.frc2019offseason.subsystems.Wrist.WristPosistions
import org.team5419.frc2019offseason.subsystems.Lift.LiftHeight
import org.team5419.frc2019offseason.Constants.Input

import org.team5419.fault.Controller
import org.team5419.fault.input.CheesyDriveHelper
import org.team5419.fault.input.TankDriveHelper
import org.team5419.fault.input.DriveHelper
import org.team5419.fault.input.DriveSignal

import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.GenericHID.Hand

public class TeleopController(
    subsystems: SubsystemsManager,
    driver: XboxController,
    codriver: XboxController,
    controlMode: ControlModes
) : Controller() {

    private val mDriver: XboxController
    private val mCoDriver: XboxController
    private val mSubsystems: SubsystemsManager

    private var isSlow: Boolean = false
    private var isReverse: Boolean = false
    private var isUnlocked: Boolean = false
    private var speed = Input.BASE_SPEED
    private var leftDrive: Double = 0.0
    private var rightDrive: Double = 0.0
    private var driveHelper: DriveHelper

    private var isManuelOverride = false
    private val isHighGear: Boolean get() = mDriver.getTriggerAxis(Hand.kLeft) > Input.DEADBAND
    private val isQuickTurn: Boolean get() = mDriver.getTriggerAxis(Hand.kRight) > Input.DEADBAND

    public enum class ControlModes { TANK, CHEESY }

    init {
        mDriver = driver
        mCoDriver = codriver
        mSubsystems = subsystems
        if (controlMode == ControlModes.TANK) {
            driveHelper = TankDriveHelper(Input.DEADBAND, 1.0)
        } else {
            val config: CheesyDriveHelper.CheesyDriveConfig = CheesyDriveHelper.CheesyDriveConfig()
            config.apply {
                deadband = Input.DEADBAND
            }
            driveHelper = CheesyDriveHelper(config)
        }
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
        speed = Input.BASE_SPEED
        if (mDriver.getAButton()) { isReverse = !isReverse }
        if (isReverse) { speed *= -1 }
        if (mDriver.getBumper(Hand.kRight) || mDriver.getBumper(Hand.kLeft)) {
            speed *= Input.SLOW
        }

        val ds: DriveSignal = driveHelper.calculateOutput(
            mDriver.getY(Hand.kLeft) * speed,
            mDriver.getX(Hand.kLeft),
            isQuickTurn,
            isHighGear
        )
        // println("$ds.left $ds.right")
        mSubsystems.drivetrain.setPercent(ds)

        // mSubsystems.drivetrain.setPercent(DriveSignal(1.0, 1.0))

        // Climb control
        if (mDriver.getBumperPressed(Hand.kRight) || mCoDriver.getBumperPressed(Hand.kLeft)) {
            if (isUnlocked == false) {
                mSubsystems.climber.unlock()
                isUnlocked = true
            }
            mSubsystems.climber.climb()
        }

        // Codriver
        // Valve control
        if (mCoDriver.getBumperPressed(Hand.kLeft) || mCoDriver.getBumperPressed(Hand.kRight))
            mSubsystems.vacuum.release()
        // Vacuum control
        if (mCoDriver.getTriggerAxis(Hand.kLeft) > Input.DEADBAND) {
            mSubsystems.vacuum.pickBall(mCoDriver.getTriggerAxis(Hand.kLeft))
        } else if (mCoDriver.getTriggerAxis(Hand.kRight) > Input.DEADBAND) {
            mSubsystems.vacuum.pickHatch(mCoDriver.getTriggerAxis(Hand.kRight))
        } else { mSubsystems.vacuum.setPercent(0.0) }
        // button control
        if (mCoDriver.getAButtonPressed()) {
            mSubsystems.lift.setPosistion(LiftHeight.HATCH_LOW)
        } else if (mCoDriver.getBButtonPressed()) {
            mSubsystems.lift.setPosistion(LiftHeight.HATCH_MID)
        } else if (mCoDriver.getYButtonPressed())
            mSubsystems.lift.setPosistion(LiftHeight.HATCH_HIGH)
        else if ((mCoDriver.getPOV() >= 340 && mCoDriver.getPOV() <= 360) ||
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
            mSubsystems.wrist.setPosition(WristPosistions.BACKWARD)
        }
        if (mCoDriver.getPOV() >= 70 && mCoDriver.getPOV() <= 110) {
            mSubsystems.wrist.setPosition(WristPosistions.FORWARD)
        }

        // Manuel Lift control
        if (Math.abs(mCoDriver.getY(Hand.kLeft)) > Input.DEADBAND && isManuelOverride) {
            mSubsystems.lift.setPercent(mCoDriver.getY(Hand.kLeft))
        }
        if (Math.abs(mCoDriver.getY(Hand.kRight)) > Input.DEADBAND && isManuelOverride) {
            mSubsystems.wrist.setPercent(mCoDriver.getY(Hand.kLeft))
        }
    }
}
