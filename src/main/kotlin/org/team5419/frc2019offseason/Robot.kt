package org.team5499.frc2019offseason

import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.DriverStation

import org.team5419.frc2019offseason.Constants

import org.team5419.frc2019offseason.subsystems.Drivetrain
import org.team5419.frc2019offseason.subsystems.Lift
import org.team5419.frc2019offseason.subsystems.Vacuum
import org.team5419.frc2019offseason.subsystems.SubsystemsManager
import org.team5419.frc2019offseason.subsystems.Wrist
import org.team5419.frc2019offseason.subsystems.Climber
import org.team5419.frc2019offseason.subsystems.Vision
import org.team5419.frc2019offseason.controllers.AutoController
import org.team5419.frc2019offseason.controllers.TeleopController
import org.team5419.frc2019offseason.input.ButtonBoard

import org.team5419.fault.hardware.LazyTalonSRX
import org.team5419.fault.hardware.LazyVictorSPX
import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.Joystick
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.networktables.NetworkTable

class Robot : TimedRobot(Constants.ROBOT_UPDATE_PERIOD) {

    // hardware
    private val mLeftMaster: LazyTalonSRX
    private val mLeftSlave: LazyVictorSPX

    private val mRightMaster: LazyTalonSRX
    private val mRightSlave: LazyVictorSPX

    private val mLiftMaster: LazyTalonSRX
    private val mLiftSlave: LazyTalonSRX

    private val mVacuumMaster: LazyTalonSRX
    private val mReleaseSolenoid: Solenoid
    private val mHatchSolenoid: Solenoid

    private val mClimberMaster: LazyTalonSRX
    private val mClimberSlave: LazyVictorSPX
    private val mLockTalon: LazyTalonSRX

    private val mWristMaster: LazyTalonSRX

    // subsystems
    private val mDrivetrain: Drivetrain
    private val mLift: Lift
    private val mWrist: Wrist
    private val mClimber: Climber
    private val mVacuum: Vacuum
    private val mVision: Vision

    private val mSubsystemsManager: SubsystemsManager
    private val mAutoController: AutoController
    private val mTeleopController: TeleopController

    // input
    private val mDriver: XboxController
    private val mJoystick: Joystick
    private val mButtonBoard: Joystick
    private val mCoDriver: ButtonBoard

    // network tables
    private val nt: NetworkTableInstance
    private val stormxTable: NetworkTable
    private val ds: DriverStation

    init {

        // initilize Drivetrain
        mLeftMaster = LazyTalonSRX(Constants.Drivetrain.LEFT_MASTER_TALON_PORT)
        mLeftSlave = LazyVictorSPX(Constants.Drivetrain.LEFT_SLAVE_TALON_PORT)

        mRightMaster = LazyTalonSRX(Constants.Drivetrain.RIGHT_MASTER_TALON_PORT)
        mRightSlave = LazyVictorSPX(Constants.Drivetrain.RIGHT_SLAVE_TALON_PORT)

        // initilize Lift
        mLiftMaster = LazyTalonSRX(Constants.Lift.MASTER_TALON_PORT)
        mLiftSlave = LazyTalonSRX(Constants.Lift.SLAVE_TALON_PORT)

        // initilize Wrist
        mWristMaster = LazyTalonSRX(Constants.Wrist.MASTER_TALON_PORT)

        // initilize Climber
        mClimberMaster = LazyTalonSRX(Constants.Climber.MASTER_TALON_PORT)
        mClimberSlave = LazyVictorSPX(Constants.Climber.SLAVE_TALON_PORT)
        mLockTalon = LazyTalonSRX(Constants.Climber.LOCK_TALON_PORT)

        // initilize Vacuum
        mVacuumMaster = LazyTalonSRX(Constants.Vacuum.MASTER_TALON_PORT)
        mReleaseSolenoid = Solenoid(Constants.Vacuum.RELEASE_SOLNOID_PORT)
        mHatchSolenoid = Solenoid(Constants.Vacuum.HATCH_SOLENOID_PORT)

        mVision = Vision()

        // initialize inputs
        mJoystick = Joystick(Constants.Input.JOYSTICK_PORT)
        mButtonBoard = Joystick(Constants.Input.BUTTON_BOARD_PORT)
        mCoDriver = ButtonBoard(mJoystick, mButtonBoard)

        // reset hardware
        mLeftMaster.configFactoryDefault()
        mLeftSlave.configFactoryDefault()
        mRightMaster.configFactoryDefault()
        mRightSlave.configFactoryDefault()
        mLiftMaster.configFactoryDefault()
        mLiftSlave.configFactoryDefault()
        mWristMaster.configFactoryDefault()
        mVacuumMaster.configFactoryDefault()

        mClimberMaster.configFactoryDefault()
        mClimberSlave.configFactoryDefault()
        mLockTalon.configFactoryDefault()

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
        mWrist = Wrist(
            mWristMaster
        )
        mWrist.lift = mLift
        mLift.wrist = mWrist
        mClimber = Climber(mClimberMaster, mClimberSlave, mLockTalon)
        mVacuum = Vacuum(
            mVacuumMaster,
            mReleaseSolenoid,
            mHatchSolenoid
        )

        // initilize Subsystems Manager
        mSubsystemsManager = SubsystemsManager(
            mDrivetrain,
            mWrist,
            mVacuum,
            mLift,
            mClimber,
            mVision
        )

        // initilize controllers
        mAutoController = AutoController()

        mDriver = XboxController(Constants.Input.DRIVER_PORT)
        // mCodriver = XboxController(Constants.Input.CODRIVER_PORT)
        mTeleopController = TeleopController(
            mSubsystemsManager,
            mDriver,
            mCoDriver
        )
        mVision.ledState = Vision.LEDState.OFF

        // network tables
        nt = NetworkTableInstance.getDefault()
        stormxTable = nt.getTable("stormx")
        ds = DriverStation()
    }

    // robot

    override fun robotInit() {
        mVision.ledState = Vision.LEDState.OFF
    }

    override fun robotPeriodic() {
        stormxTable.getEntry("matchTime").setDouble(ds.getMatchTime())
        stormxTable.getEntry("matchNumber").setDouble(ds.getMatchNumber().toDouble())
        stormxTable.getEntry("eventName").setString(ds.getEventName())
        stormxTable.getEntry("isEnabled").setBoolean(ds.isEnabled())
    }

    // disabled mode

    override fun disabledInit() {
        mSubsystemsManager.resetAll()
        mTeleopController.reset()
        mAutoController.reset()
    }

    override fun disabledPeriodic() {
        // println(mWrist.mMaster.getSelectedSensorPosition(0))
        // println(mWrist.position)
    }

    // autonomous mode

    override fun autonomousInit() {
        // mAutoController.start()
        teleopInit()
    }

    override fun autonomousPeriodic() {
        // mSubsystemsManager.updateAll()
        // mAutoController.update()
        teleopPeriodic()
    }

    // teleop mode

    override fun teleopInit() {
        mTeleopController.start()
        // mWrist.setPosition(mWrist.WristPosition.DEFENSE)
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
