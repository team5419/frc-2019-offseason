package org.team5419.frc2019offseason.subsystems

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.InvertType

import org.team5419.fault.hardware.LazyTalonSRX
import org.team5419.fault.Subsystem

import org.team5419.frc2019offseason.Constants

@Suppress("MaxLineLength", "TooManyFunctions")
class Lift(
    masterTalon: LazyTalonSRX,
    slaveTalon: LazyTalonSRX
) : Subsystem() {

    companion object {
        private const val kElevatorSlot = 0
    }

    private var mMaster: LazyTalonSRX
    private var mSlave: LazyTalonSRX
    lateinit var wrist: Wrist
    public var firstStagePosition: Double
        get() = ticksToInches(-mMaster.getSelectedSensorPosition(kElevatorSlot))
    var secondStagePosition: Double
        get() = Math.max(ticksToInches(-mMaster.getSelectedSensorPosition(kElevatorSlot)) - Constants.Lift.SECOND_STAGE_HIGHT, 0.0)
    var setPoint: Double
    var isSecondStage: Boolean
    val canFlip: Boolean get() = Constants.Lift.MAX_FLIP_HIGHT > firstStagePosition && Constants.Lift.MAX_FLIP_HIGHT > setPoint
    // confirm resting hight
    public enum class LiftHeight(
        val value: Double = 0.0
    ) {
        HUMAN_PLAYER(Constants.Lift.HUMAN_PLAYER),
        HATCH_LOW(Constants.Lift.HATCH_LOW_HEIGHT),
        HATCH_MID(Constants.Lift.HATCH_MID_HEIGHT),
        HATCH_HIGH(Constants.Lift.HATCH_HIGH_HEIGHT),
        BALL_LOW(Constants.Lift.BALL_LOW_HEIGHT),
        BALL_MID(Constants.Lift.BALL_MID_HEIGHT),
        BALL_HIGH(Constants.Lift.BALL_HIGH_HEIGHT)
    }

    private fun ticksToInches(ticks: Int): Double =
        ticks.toDouble() / Constants.Lift.ENCODER_TICKS_PER_ROTATION.toDouble() * Constants.Lift.INCHES_PER_ROTATION
    private fun inchesToTicks(inches: Double): Int =
        (inches / Constants.Lift.INCHES_PER_ROTATION * Constants.Lift.ENCODER_TICKS_PER_ROTATION).toInt()
    private fun canRise(height: Double): Boolean {
        // println("""${wrist.canRise}
        //     ${firstStagePosition < Constants.Lift.MAX_FLIP_HIGHT}
        //     ${height < Constants.Lift.MAX_FLIP_HIGHT} $firstStagePosition $height""")
        return wrist.canRise ||
                (firstStagePosition < Constants.Lift.MAX_FLIP_HIGHT &&
                    height < Constants.Lift.MAX_FLIP_HIGHT)
    }
    private var mBrakeMode: Boolean = true
    set(value) {
        if (value == field) return
        if (value) {
            mMaster.setNeutralMode(NeutralMode.Brake)
            mSlave.setNeutralMode(NeutralMode.Brake)
        } else {
            mMaster.setNeutralMode(NeutralMode.Coast)
            mSlave.setNeutralMode(NeutralMode.Coast)
        }
        field = value
    }

    init {
        mMaster = masterTalon.apply {
            configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0) //
            setSensorPhase(true)
            setInverted(false)
            setSelectedSensorPosition(0)
            configClosedLoopPeakOutput(kElevatorSlot, 1.0)

            config_kP(kElevatorSlot, Constants.Lift.KP, 0)
            config_kI(kElevatorSlot, Constants.Lift.KI, 0)
            config_kD(kElevatorSlot, Constants.Lift.KD, 0)
            config_kF(kElevatorSlot, Constants.Lift.KF, 0)
            configMotionCruiseVelocity(inchesToTicks(Constants.Lift.MOTION_MAGIC_VELOCITY / 10.0), 0)
            configMotionAcceleration(inchesToTicks(Constants.Lift.MOTION_MAGIC_ACCELERATION / 100.0), 0)
            // configMotionCruiseVelocity(Constants.Lift.MOTION_MAGIC_VELOCITY, 0)
            // configMotionAcceleration(Constants.Lift.MOTION_MAGIC_ACCELERATION, 0)
            selectProfileSlot(kElevatorSlot, 0)
            // configAllowableClosedloopError(0, 0, 0)
            configPeakOutputForward(Constants.MAX_OUTPUT)
            configPeakOutputReverse(Constants.MIN_OUTPUT)
            enableCurrentLimit(false)
            // configPeakCurrentDuration(0, 0)
            // configPeakCurrentLimit(0, 0)
            configContinuousCurrentLimit(25, 0) // amps
            enableVoltageCompensation(false)
            configForwardSoftLimitThreshold(inchesToTicks(Constants.Lift.MAX_ENCODER_HEIGHT), 0)
            configReverseSoftLimitThreshold(inchesToTicks(Constants.Lift.MIN_ENCODER_HEIGHT), 0)
            // configForwardSoftLimitThreshold(Constants.Lift.MIN_ENCODER_TICKS, 0)
            // configReverseSoftLimitThreshold(Constants.Lift.MAX_ENCODER_TICKS, 0)
            configForwardSoftLimitEnable(true, 0)
            configReverseSoftLimitEnable(true, 0)

            setNeutralMode(NeutralMode.Brake)
        }

        mSlave = slaveTalon.apply {
            follow(mMaster)
            setInverted(InvertType.FollowMaster)

            setNeutralMode(NeutralMode.Brake)
        }

        // setpoint = LiftHeight.getHeight()
        setPoint = 0.0
        firstStagePosition = 0.0
        secondStagePosition = Constants.Lift.SECOND_STAGE_HIGHT
        isSecondStage = false
    }

    public fun zero() {
        setPoint = 0.0
        firstStagePosition = 0.0
        secondStagePosition = Constants.Lift.SECOND_STAGE_HIGHT
        isSecondStage = false
    }

    public fun setPercent(speed: Double) {
        mMaster.set(ControlMode.PercentOutput, speed)
    }

    public fun setPosistion(height: LiftHeight) {
        // println("set posistion $height.value")
        if (canRise(height.value)) {
            setTicks(inchesToTicks(height.value))
            setPoint = height.value
        } else println("Can't set lift posistion")
    }

    public fun setTicks(ticks: Int) {
        mMaster.set(ControlMode.MotionMagic, ticks.toDouble())
    }

    public fun setInches(inches: Double) {
        setTicks(inchesToTicks(inches))
    }

    @Suppress("MaxLineLength")
    public override fun update() {
        if (!isSecondStage &&
            firstStagePosition + Constants.Lift.SECOND_STAGE_EPSILON > Constants.Lift.SECOND_STAGE_HIGHT) {
            mMaster.config_kF(kElevatorSlot, Constants.Lift.KF2, 0)
            isSecondStage = true
            println("second stage")
        } else if (isSecondStage &&
            Constants.Lift.SECOND_STAGE_HIGHT + Constants.Lift.SECOND_STAGE_EPSILON > secondStagePosition) {
            mMaster.config_kF(kElevatorSlot, Constants.Lift.KF, 0)
            isSecondStage = false
            println("first stage")
        }
    }

    public override fun stop() {
        mMaster.set(ControlMode.PercentOutput, 0.0)
        mBrakeMode = true
    }

    public override fun reset() {
        setPercent(0.0)
    }
}
