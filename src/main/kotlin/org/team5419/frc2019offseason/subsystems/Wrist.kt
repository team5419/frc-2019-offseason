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
    private var position: Double
        get() = ticksToDegrees(mMaster.getSelectedSensorPosition())
    private var setPoint: Double
    public var liftPos: Double
    public val canRise: Boolean
        get() = position < Constants.Wrist.MAX_RISE_ANGLE && setPoint < Constants.Wrist.MAX_RISE_ANGLE // 75.0
    lateinit var lift: Lift
    // public var targetPosistion: WristPosistions

    // set posistion
    public enum class WristPosistions(val value: Double) {
        FORWARD(Constants.Wrist.FORWARD),
        MIDDLE(Constants.Wrist.MIDDLE),
        BACKWARD(Constants.Wrist.BACKWARD),
        BALL_HIGH(Constants.Wrist.BALL_HIGH),
        BALL_MID(Constants.Wrist.BALL_MID),
        BALL_LOW(Constants.Wrist.BALL_LOW)
    }

    init {
        // config talon PIDF
        mMaster = masterTalon.apply {
            configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0) //
            // configSelectedFeedbackCoefficient(-1.0)
            setSelectedSensorPosition(0)
            setSensorPhase(true) // check
            setInverted(true) // check this

            configClosedLoopPeakOutput(kWristSlot, 1.0)

            config_kP(kWristSlot, Constants.Wrist.KP, 0)
            config_kI(kWristSlot, Constants.Wrist.KI, 0)
            config_kD(kWristSlot, Constants.Wrist.KD, 0)
            config_kF(kWristSlot, Constants.Wrist.KF, 0)
            configMotionCruiseVelocity(Constants.Wrist.MOTION_MAGIC_VELOCITY, 0)
            configMotionAcceleration(Constants.Wrist.MOTION_MAGIC_ACCELERATION, 0)
            selectProfileSlot(kWristSlot, 0)
            configAllowableClosedloopError(0, 0, 0)

            enableCurrentLimit(true)
            configPeakCurrentDuration(kWristSlot, 0)
            configPeakCurrentLimit(kWristSlot, 0)
            @Suppress("MagicNumber")
            configContinuousCurrentLimit(25, 0) // amps
            enableVoltageCompensation(false)
            configForwardSoftLimitThreshold(Constants.Wrist.MAX_ENCODER_TICKS, kWristSlot)
            configReverseSoftLimitThreshold(Constants.Wrist.MIN_ENCODER_TICKS, kWristSlot)
            configForwardSoftLimitEnable(true, 0)
            configReverseSoftLimitEnable(true, 0)
            configPeakOutputForward(0.1)
            configPeakOutputReverse(0.1)
        }

        setPoint = 0.0
        position = WristPosistions.FORWARD.value
        liftPos = 0.0
    }

    public fun zero() {
        setPoint = 0.0
        position = WristPosistions.FORWARD.value
    }

    public fun setPercent(percent: Double) {
        mMaster.set(ControlMode.PercentOutput, percent)
    }

    public fun setPosition(point: WristPosistions) {
        setPoint = point.value
        if (
            (position < Constants.Wrist.MAX_RISE_ANGLE &&
            point.value < Constants.Wrist.MAX_RISE_ANGLE) ||
            lift.canFlip) {
            setDegrees(point.value)
        } else println("Can't set wrist posistion")
    }

    public fun setDegrees(heading: Double) {
        setTicks(degreesToTicks(heading))
    }

    public fun setTicks(ticks: Int) {
        mMaster.set(ControlMode.MotionMagic, ticks.toDouble())
    }

    public override fun update() {
    }
    public override fun stop() {}
    public override fun reset() {}
}
