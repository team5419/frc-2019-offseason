package org.team5419.frc2019offseason.subsystems

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.InvertType

import org.team5419.fault.hardware.LazyTalonSRX
import org.team5419.fault.Subsystem

import org.team5419.frc2019offseason.Constants.Lift

class Lift(
    masterTalon: LazyTalonSRX,
    slaveTalon: LazyTalonSRX
) : Subsystem() {

    companion object {
        private const val kElevatorSlot = 0
    }

    private var mMaster: LazyTalonSRX
    private var mSlave: LazyTalonSRX

    private fun ticksToInches(ticks: Int): Double =
        ticks / Lift.ENCODER_TICKS_PER_ROTATION * Lift.INCHES_PER_ROTATION
    private fun inchesToTicks(inches: Double): Int =
        (inches / Lift.INCHES_PER_ROTATION * Lift.ENCODER_TICKS_PER_ROTATION).toInt()

    var firstStagePosistion: Double get() = ticksToInches(mMaster.getSelectedSensorPosition())
    var secondStagePosistion: Double get() = Math.max(ticksToInches(mMaster.getSelectedSensorPosition()), 0.0)
    var setpoint: Int
    var isSecondStage: Boolean
    // confirm resting hight
    public enum class LiftHeight(
        val value: Double = 0.0
    ) {
        BOTTOM(Lift.STOW_HEIGHT),
        HATCH_LOW(Lift.HATCH_LOW_HEIGHT),
        HATCH_MID(Lift.HATCH_MID_HEIGHT),
        HATCH_HIGH(Lift.HATCH_HIGH_HEIGHT),
        BALL_LOW(Lift.BALL_LOW_HEIGHT),
        BALL_MID(Lift.BALL_MID_HEIGHT),
        BALL_HIGH(Lift.BALL_HIGH_HEIGHT)
    }

    private var mBrakeMode: Boolean = false
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
            setSensorPhase(true) // check
            setInverted(false) // check this

            configClosedLoopPeakOutput(kElevatorSlot, 1.0)

            config_kP(kElevatorSlot, Lift.KP, 0)
            config_kI(kElevatorSlot, Lift.KI, 0)
            config_kD(kElevatorSlot, Lift.KD, 0)
            config_kF(kElevatorSlot, Lift.KF, 0)
            configMotionCruiseVelocity(Lift.MOTION_MAGIC_VELOCITY, 0)
            configMotionAcceleration(Lift.MOTION_MAGIC_ACCELERATION, 0)
            selectProfileSlot(kElevatorSlot, 0)
            configAllowableClosedloopError(0, 0, 0)

            enableCurrentLimit(false)
            configPeakCurrentDuration(0, 0)
            configPeakCurrentLimit(0, 0)
            @Suppress("MagicNumber")
            configContinuousCurrentLimit(25, 0) // amps
            enableVoltageCompensation(false)
            configForwardSoftLimitThreshold(Lift.MAX_ENCODER_TICKS, 0)
            configReverseSoftLimitThreshold(Lift.MIN_ENCODER_TICKS, 0)
            configForwardSoftLimitEnable(true, 0)
            configReverseSoftLimitEnable(true, 0)
        }

        mSlave = slaveTalon.apply {
                follow(mMaster)
                setInverted(InvertType.FollowMaster)
        }

        // setpoint = LiftHeight.getHeight()
        setpoint = 0
        firstStagePosistion = 0.0
        secondStagePosistion = Lift.SECOND_STAGE_HIGHT
        isSecondStage = false
    }

    public fun zero() {
        setpoint = 0
        firstStagePosistion = 0.0
        secondStagePosistion = Lift.SECOND_STAGE_HIGHT
        isSecondStage = false
    }

    public fun setPercent(speed: Double) {
        mMaster.set(ControlMode.PercentOutput, speed)
    }

    public fun setPosistion(height: LiftHeight) {
        setTicks(inchesToTicks(height.value))
    }

    public fun setTicks(ticks: Int) {
        setpoint = ticks
        mMaster.set(ControlMode.MotionMagic, ticks.toDouble())
    }

    public fun setInches(inches: Double) {
        setTicks(inchesToTicks(inches))
    }

    public override fun update() {
        if (!isSecondStage && firstStagePosistion + Lift.SECOND_STAGE_EPSILON > Lift.SECOND_STAGE_HIGHT) {
            mMaster.apply {
                config_kP(kElevatorSlot, Lift.KP2, 0)
                config_kI(kElevatorSlot, Lift.KI2, 0)
                config_kD(kElevatorSlot, Lift.KD2, 0)
                config_kF(kElevatorSlot, Lift.KF2, 0)
            }
            isSecondStage = true
        } else if (isSecondStage && Lift.SECOND_STAGE_HIGHT + Lift.SECOND_STAGE_EPSILON > secondStagePosistion) {
            mMaster.apply {
                config_kP(kElevatorSlot, Lift.KP, 0)
                config_kI(kElevatorSlot, Lift.KI, 0)
                config_kD(kElevatorSlot, Lift.KD, 0)
                config_kF(kElevatorSlot, Lift.KF, 0)
            }
            isSecondStage = false
        }
    }

    public override fun stop() {
        setPosistion(LiftHeight.BOTTOM)
        mBrakeMode = false
    }

    public override fun reset() {
        setPercent(0.0)
    }
}
