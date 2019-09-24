package org.team5419.frc2019offseason.subsystems

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced
import com.ctre.phoenix.motorcontrol.InvertType

import org.team5419.fault.hardware.LazyTalonSRX
import org.team5419.fault.Subsystem
import org.team5419.fault.math.pid.PIDF

import org.team5419.frc2019offseason.Constants

class Lift(
    master: LazyTalonSRX,
    slave: LazyTalonSRX
) : Subsystem() {
    private var mMaster: LazyTalonSRX
    private var mSlave: LazyTalonSRX

    public var firstStagePosistion: Double = 0.0
    public var secondStagePosistion: Double = 0.0
    // confirm resting hight
    public enum class LiftHeight(val getHeight: () -> Double = { 0.0 }) {
        BOTTOM({ Constants.Lift.STOW_HEIGHT }),
        HATCH_LOW({ Constants.Lift.HATCH_LOW_HEIGHT }),
        HATCH_MID({ Constants.Lift.HATCH_MID_HEIGHT }),
        HATCH_HIGH({ Constants.Lift.HATCH_HIGH_HEIGHT }),
        BALL_LOW({ Constants.Lift.BALL_LOW_HEIGHT }),
        BALL_MID({ Constants.Lift.BALL_MID_HEIGHT }),
        BALL_HIGH({ Constants.Lift.BALL_HIGH_HEIGHT }),
        BALL_HUMAN_PLAYER({ Constants.Lift.BALL_HUMAN_PLAYER_HEIGHT })
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

    private val pid: PIDF

    init {
        mMaster = masterTalon.apply {
            configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0)
            setSensorPhase(true) // check
            setInverted(false) // check this

            configClosedLoopPeakOutput(kElevatorSlot, 1.0)

            config_kP(kElevatorSlot, Constants.Lift.KP, 0)
            config_kI(kElevatorSlot, Constants.Lift.KI, 0)
            config_kD(kElevatorSlot, Constants.Lift.KD, 0)
            config_kF(kElevatorSlot, Constants.Lift.KF, 0)
            configMotionCruiseVelocity(Constants.Lift.MOTION_MAGIC_VELOCITY, 0)
            configMotionAcceleration(Constants.Lift.MOTION_MAGIC_ACCELERATION, 0)
            selectProfileSlot(kElevatorSlot, 0)
            configAllowableClosedloopError(0, 0, 0)

            enableCurrentLimit(false)
            configPeakCurrentDuration(0, 0)
            configPeakCurrentLimit(0, 0)
            configContinuousCurrentLimit(25, 0) // amps
            enableVoltageCompensation(false)
            configForwardSoftLimitThreshold(Constants.Lift.MAX_ENCODER_TICKS, 0)
            configReverseSoftLimitThreshold(Constants.Lift.MIN_ENCODER_TICKS, 0)
            configForwardSoftLimitEnable(true, 0)
            configReverseSoftLimitEnable(true, 0)
        }
        mSlave = slave.apply {
            follow(mMaster)
            setInverted(InvertType.FollowMaster)
        }
        pid = PIDF(
            Constants.Lift.KP,
            Constants.Lift.KI,
            Constants.Lift.KD,
            Constants.Lift.KF
        )
    }

    public fun setPercent(speed: Double) {
        mMaster.set(ControlMode.PercentOutput, speed)
    }

    public fun goToHeight(height: LiftHeight) {
      mMaster.set(ControlMode.MotionMagic, height)
    }

    public override fun update() {
        setPercent(pid.calculate())
    }

    public override fun stop() {
        mBrakeMode = false
    }

    public override fun reset() {
        stop()
    }
}
