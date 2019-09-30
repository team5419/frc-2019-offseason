package org.team5499.frc2019offseason

import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController

import org.team5419.frc2019offseason.Constants

import org.team5419.frc2019offseason.subsystems.Drivetrain
import org.team5419.frc2019offseason.subsystems.Climber
import org.team5419.frc2019offseason.subsystems.Wrist
import org.team5419.frc2019offseason.subsystems.Lift
import org.team5419.frc2019offseason.subsystems.Vacuum
import org.team5419.frc2019offseason.subsystems.SubsystemsManager

import org.team5419.frc2019offseason.controllers.AutoController
import org.team5419.frc2019offseason.controllers.TeleopController

import org.team5419.fault.hardware.LazyTalonSRX
import org.team5419.fault.hardware.LazyVictorSPX
import edu.wpi.first.wpilibj.Solenoid

class Robot : TimedRobot(Constants.ROBOT_UPDATE_PERIOD) {
    // input
    private val mDriver: XboxController
    private val mCodriver: XboxController

    // hardware
    private val mLeftMaster: LazyTalonSRX
    private val mLeftSlave: LazyVictorSPX

    private val mRightMaster: LazyTalonSRX
    private val mRightSlave: LazyVictorSPX

    private val mLiftMaster: LazyTalonSRX
    private val mLiftSlave: LazyTalonSRX

    private val mVacuumMaster: LazyTalonSRX
    private val mSolenoid: Solenoid

    private val mWristMaster: LazyTalonSRX

    // subsystems
    private val mDrivetrain: Drivetrain
    private val mLift: Lift
    private val mWrist: Wrist
    private val mClimber: Climber
    private val mVacuum: Vacuum

    private val mSubsystemsManager: SubsystemsManager
    private val mAutoController: AutoController
    private val mTeleopController: TeleopController

    init {
        // initilize Drivetrain
        mLeftMaster = LazyTalonSRX(Constants.Drivetrain.LEFT_MASTER_TALON_PORT)
        mLeftSlave = LazyVictorSPX(Constants.Drivetrain.LEFT_SLAVE_TALON_PORT)

        mRightMaster = LazyTalonSRX(Constants.Drivetrain.RIGHT_MASTER_TALON_PORT)
        mRightSlave = LazyVictorSPX(Constants.Drivetrain.RIGHT_SLAVE_TALON_PORT)

        mDrivetrain = Drivetrain(
            mLeftMaster,
            mLeftSlave,
            mRightMaster,
            mRightSlave
        )

        // initilize Lift
        mLiftMaster = LazyTalonSRX(Constants.Lift.MASTER_TALON_PORT)
        mLiftSlave = LazyTalonSRX(Constants.Lift.SLAVE_TALON_PORT)
        mLift = Lift(
            mLiftMaster,
            mLiftSlave
        )

        // initilize Wrist
        mWristMaster = LazyTalonSRX(Constants.Lift.MASTER_TALON_PORT)
        mWrist = Wrist(
            mWristMaster
        )

        // initilize Climber
        mClimber = Climber()

        // initilize Vacuum
        mVacuumMaster = LazyTalonSRX(Constants.Vacuum.MASTER_TALON_PORT)
        mSolenoid = Solenoid(Constants.Vacuum.SOLENOID_PORT)
        mVacuum = Vacuum(
            mVacuumMaster,
            mSolenoid
        )

        // initilize Subsystems Manager
        mSubsystemsManager = SubsystemsManager(
            mDrivetrain,
            mLift,
            mWrist,
            mClimber,
            mVacuum
        )

        // initilize controllers
        mAutoController = AutoController()

        mDriver = XboxController(Constants.Input.DRIVER_PORT)
        mCodriver = XboxController(Constants.Input.CODRIVER_PORT)
        mTeleopController = TeleopController(
            mSubsystemsManager,
            mDriver,
            mCodriver
        )
    }

    // robot

    override fun robotInit() {
    }

    override fun robotPeriodic() {
    }

    // disabled mode

    override fun disabledInit() {
        mSubsystemsManager.resetAll()
        mTeleopController.reset()
        mAutoController.reset()
    }

    override fun disabledPeriodic() {
    }

    // autonomous mode

    override fun autonomousInit() {
        mAutoController.start()
    }

    override fun autonomousPeriodic() {
        mSubsystemsManager.updateAll()
        mAutoController.update()
    }

    // teleop mode

    override fun teleopInit() {
        mTeleopController.start()
    }

    override fun teleopPeriodic() {
        mSubsystemsManager.updateAll()
        mTeleopController.update()
    }

    override fun testInit() {
    }

    override fun testPeriodic() {
    }
}
