package org.team5499.frc2019offseason

import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController

import org.team5419.frc2019offseason.Constants

import org.team5419.frc2019offseason.subsystems.Drivetrain
import org.team5419.frc2019offseason.subsystems.Lift
import org.team5419.frc2019offseason.subsystems.Vacuum
import org.team5419.frc2019offseason.subsystems.SubsystemsManager
import org.team5419.frc2019offseason.subsystems.Wrist
import org.team5419.frc2019offseason.controllers.AutoController
import org.team5419.frc2019offseason.controllers.TeleopController

import org.team5419.fault.hardware.LazyTalonSRX
import org.team5419.fault.hardware.LazyVictorSPX
import edu.wpi.first.wpilibj.Solenoid

class Robot : TimedRobot(Constants.ROBOT_UPDATE_PERIOD) {

    private val driver: XboxController
    private val coDriver: XboxController

    var rightDrive: Double = 0.0
    var leftDrive: Double = 0.0

    private val mLeftMaster: LazyTalonSRX
    private val mLeftSlave: LazyVictorSPX

    private val mRightMaster: LazyTalonSRX
    private val mRightSlave: LazyVictorSPX

    private val mLiftMaster: LazyTalonSRX
    private val mLiftSlave: LazyTalonSRX

    private val mVacuumMaster: LazyTalonSRX
    private val mReleaseSolenoid: Solenoid
    private val mHatchSolenoid: Solenoid

    private val mWristMaster: LazyTalonSRX

    private val mDrivetrain: Drivetrain
    private val mLift: Lift
    private val mWrist: Wrist
    // private val mClimber: Climber
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
        // mClimber = Climber()

        // initilize Vacuum
        mVacuumMaster = LazyTalonSRX(Constants.Vacuum.MASTER_TALON_PORT)
        mReleaseSolenoid = Solenoid(Constants.Vacuum.RELEASE_SOLNOID_PORT)
        mHatchSolenoid = Solenoid(Constants.Vacuum.HATCH_SOLENOID_PORT)
        mVacuum = Vacuum(
            mVacuumMaster,
            mReleaseSolenoid,
            mHatchSolenoid
        )

        // initilize Subsystems Manager
        mSubsystemsManager = SubsystemsManager(
            mDrivetrain,
            mWrist,
            // mClimber,
            mVacuum,
            mLift
        )

        // initilize controllers
        mAutoController = AutoController()

        driver = XboxController(0)
        coDriver = XboxController(1)
        mTeleopController = TeleopController(
            mSubsystemsManager,
            driver,
            coDriver,
            TeleopController.ControlModes.CHEESY
        )
    }

    // robot

    override fun robotInit() {
    }

    override fun robotPeriodic() {
        mWrist.canFlip = mLift.canSwich
    }

    // disabled mode

    override fun disabledInit() {
        mSubsystemsManager.resetAll()
        mTeleopController.reset()
        mAutoController.reset()
    }

    override fun disabledPeriodic() {
        println(mLift.firstStagePosistion.toString())
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
}
