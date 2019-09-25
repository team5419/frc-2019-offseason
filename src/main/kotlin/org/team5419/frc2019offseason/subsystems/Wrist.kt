package org.team5419.frc2019offseason.subsystems

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.FeedbackDevice

import org.team5419.fault.hardware.LazyTalonSRX
import org.team5419.fault.Subsystem

import org.team5419.frc2019offseason.Constants

class Wrist(
    masterTalon: LazyTalonSRX
) : Subsystem() {

    companion object {
        val kWristSlot = 0
    }

    private val mMaster: LazyTalonSRX
    private val isFlipped: Boolean = false
    private val isFlipping: Boolean = false

    public var setPoint = 0.0
    public var posistion: WristPosistions

    // set posistion
    public enum class WristPosistions(val value: Double) {
        FORWARD(Constants.Wrist.FORWARD_TICKS),
        MIDDLE(Constants.Wrist.MIDDLE_TICKS),
        BACKWARD(Constants.Wrist.BACKWARD_TICKS)
    }

    init {
        // config talon PIDF
        mMaster = masterTalon.apply {
            configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0) //
            setSensorPhase(true) // check
            setInverted(false) // check this

            configClosedLoopPeakOutput(kWristSlot, 1.0)

            config_kP(kWristSlot, Constants.Wrist.KP, 0)
            config_kI(kWristSlot, Constants.Wrist.KI, 0)
            config_kD(kWristSlot, Constants.Wrist.KD, 0)
            config_kF(kWristSlot, Constants.Wrist.KF, 0)
            configMotionCruiseVelocity(Constants.Wrist.MOTION_MAGIC_VELOCITY, 0)
            configMotionAcceleration(Constants.Wrist.MOTION_MAGIC_ACCELERATION, 0)
            selectProfileSlot(kWristSlot, 0)
            configAllowableClosedloopError(0, 0, 0)

            enableCurrentLimit(false)
            configPeakCurrentDuration(kWristSlot, 0)
            configPeakCurrentLimit(kWristSlot, 0)
            @Suppress("MagicNumber")
            configContinuousCurrentLimit(25, 0) // amps
            enableVoltageCompensation(false)
            configForwardSoftLimitThreshold(Constants.Wrist.MAX_ENCODER_TICKS, kWristSlot)
            configReverseSoftLimitThreshold(Constants.Wrist.MIN_ENCODER_TICKS, kWristSlot)
            configForwardSoftLimitEnable(true, 0)
            configReverseSoftLimitEnable(true, 0)
        }

        setPoint = 0.0
        posistion = WristPosistions.FORWARD
    }

    public fun setPoint(point: WristPosistions) {
        mMaster.set(ControlMode.MotionMagic, point.value)
    }

    public override fun update() {
        // mMaster.get()
    }
    public override fun stop() {}
    public override fun reset() {}
}
