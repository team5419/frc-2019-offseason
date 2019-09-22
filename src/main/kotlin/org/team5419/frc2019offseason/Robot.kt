package org.team5499.frc2019offseason

import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.GenericHID.Hand

import org.team5419.frc2019offseason.Constants

import org.team5419.frc2019offseason.subsystems.Drivetrain
import org.team5419.frc2019offseason.subsystems.Climber
import org.team5419.frc2019offseason.subsystems.Intake
import org.team5419.frc2019offseason.subsystems.Lift
import org.team5419.frc2019offseason.subsystems.Vacuum
import org.team5419.frc2019offseason.subsystems.SubsystemsManager

import org.team5419.frc2019offseason.controllers.AutoController
import org.team5419.frc2019offseason.controllers.TeleopController

import org.team5419.fault.hardware.LazyTalonSRX
import org.team5419.fault.hardware.LazyVictorSPX

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
    private val mLiftSlave: LazyVictorSPX

    private val mDrivetrain: Drivetrain
    private val mLift: Lift
    private val mIntake: Intake
    private val mClimber: Climber
    private val mVacuum: Vacuum

    private val mSubsystemsManager: SubsystemsManager
    private val mAutoController: AutoController
    private val mTeleopController: TeleopController

    init {
        // initilize DriveTrain
        driver = XboxController(0)
        coDriver = XboxController(1)

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
        mLiftSlave = LazyVictorSPX(Constants.Lift.SLAVE_TALON_PORT)

        mLift = Lift(
            mLiftMaster,
            mLiftSlave
        )

        // initilize Intake

        mIntake = Intake()

        // initilize Climber

        mClimber = Climber()

        // initilize Vacuum

        mVacuum = Vacuum()

        // initilize Subsystems Manager

        mSubsystemsManager = SubsystemsManager(
            mDrivetrain,
            mLift,
            mIntake,
            mClimber,
            mVacuum
        )

        // initilize controllers

        mAutoController = AutoController()

        mTeleopController = TeleopController()
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

        rightDrive = 0.0
        leftDrive = 0.0
    }

    override fun teleopPeriodic() {
        mSubsystemsManager.updateAll()
        mTeleopController.update()

        rightDrive = driver.getX(Hand.kRight)
        leftDrive = driver.getX(Hand.kLeft)
        if (rightDrive < Constants.Input.CONTROLLER_MARGIN) rightDrive = 0.0
        if (leftDrive < Constants.Input.CONTROLLER_MARGIN) rightDrive = 0.0
        mDrivetrain.setPercent(leftDrive, rightDrive)
    }
}
