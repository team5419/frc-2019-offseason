package org.team5499.frc2019offseason

import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.GenericHID.Hand

import org.team5419.frc2019offseason.subsystems.Drivetrain
import org.team5419.frc2019offseason.subsystems.Lift
// import org.team5419.frc2019offseason.subsystems.Vacuum

import org.team5419.fault.hardware.LazyTalonSRX
import org.team5419.fault.hardware.LazyVictorSPX

import org.team5419.frc2019offseason.Constants

@Suppress("MagicNumber")
class Robot : TimedRobot(0.005) {

    private val driver: XboxController
    private val coDriver: XboxController

    var rightDrive: Double = 0.0
    var leftDrive: Double = 0.0

    private val mLeftMaster: LazyTalonSRX
    private val mLeftSlave: LazyVictorSPX
    private val mRightMaster: LazyTalonSRX
    private val mRightSlave: LazyVictorSPX
    private val mLiftMaster: LazyTalonSRX
    private val mLiftSlave: LazyVictorSPX

    private val mDrivetrain: Drivetrain
    private val mLift: Lift

    // private val mIntake: Intake

    init {
        driver = XboxController(0)
        coDriver = XboxController(1)

        mLeftMaster = LazyTalonSRX(0)
        mLeftSlave = LazyVictorSPX(1)
        mRightMaster = LazyTalonSRX(2)
        mRightSlave = LazyVictorSPX(3)
        mLiftMaster = LazyTalonSRX(4)
        mLiftSlave = LazyVictorSPX(5)

        mDrivetrain = Drivetrain(
            mLeftMaster,
            mLeftSlave,
            mRightMaster,
            mRightSlave
        )

        mLift = Lift(
            mLiftMaster,
            mLiftSlave
        )

        println("init")
    }

    override fun robotInit() {
    }

    override fun robotPeriodic() {
    }

    override fun disabledInit() {
    }

    override fun disabledPeriodic() {
    }

    override fun autonomousInit() {
    }

    override fun autonomousPeriodic() {
    }

    override fun teleopInit() {
        rightDrive = 0.0
        leftDrive = 0.0
    }

    override fun teleopPeriodic() {
        rightDrive = driver.getX(Hand.kRight)
        leftDrive = driver.getX(Hand.kLeft)
        if (rightDrive < Constants.Input.CONTROLLER_MARGIN) rightDrive = 0.0
        if (leftDrive < Constants.Input.CONTROLLER_MARGIN) rightDrive = 0.0
        mDrivetrain.setPercent(leftDrive, rightDrive)
    }
}
