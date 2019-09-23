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
    public enum class LiftHeight(val carriageHeightInches: () -> Double = { 0.0 }) {
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
        mMaster = master.apply {
            setInverted(false)
            setSensorPhase(true)
            setStatusFramePeriod(
                StatusFrameEnhanced.Status_3_Quadrature,
                Constants.TALON_UPDATE_PERIOD_MS,
                0
            )
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
        pid.setpoint = height.carriageHeightInches()
    }

    // public fun setPosistion() {

    // }

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
