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

    private fun ticksToDegrees(ticks: Int): Double =
        ticks / Constants.Wrist.ENCODER_TICKS_PER_ROTATION * 360.0
    private fun degreesToTicks(heading: Double): Int =
        (heading / 360.0 * Constants.Wrist.ENCODER_TICKS_PER_ROTATION).toInt()
    private val mMaster: LazyTalonSRX
    private var posistion: Double get() = ticksToDegrees(mMaster.getSelectedSensorPosition())
    private var setPoint: Double
    // public var targetPosistion: WristPosistions

    // set posistion
    public enum class WristPosistions(val value: Double) {
        FORWARD(Constants.Wrist.FORWARD),
        MIDDLE(Constants.Wrist.MIDDLE),
        BACKWARD(Constants.Wrist.BACKWARD)
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
        posistion = WristPosistions.FORWARD.value
    }

    public fun zero() {
        setPoint = 0.0
        posistion = WristPosistions.FORWARD.value
    }

    public fun setPosistion(point: WristPosistions) {
        setTicks(degreesToTicks(point.value))
    }

    public fun setDegrees(heading: Double) {
        setTicks(degreesToTicks(heading))
    }

    public fun setTicks(ticks: Int) {
        mMaster.set(ControlMode.MotionMagic, ticks.toDouble())
    }

    public override fun update() {
        println(mMaster.getSelectedSensorPosition(0))
    }
    public override fun stop() {}
    public override fun reset() {}
}
