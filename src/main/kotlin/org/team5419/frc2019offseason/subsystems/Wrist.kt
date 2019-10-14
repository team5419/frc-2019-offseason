package org.team5419.frc2019offseason.subsystems

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.FeedbackDevice

import org.team5419.fault.hardware.LazyTalonSRX
import org.team5419.fault.Subsystem

import org.team5419.frc2019offseason.Constants

@SuppressWarnings("TooManyFunctions")
class Wrist(
    masterTalon: LazyTalonSRX
) : Subsystem() {

    companion object {
        const val kWristSlot = 0
    }
    lateinit var lift: Lift
    public val mMaster: LazyTalonSRX
    public var position: Double
        get() = ticksToDegrees(mMaster.getSelectedSensorPosition())
    private var setPoint: Double
    public var liftPos: Double
    public val canRise: Boolean
        get() = (position > 110.0 && setPoint > 110.0)
    private var isZeroed = false
    val needsToFlipBackward: Boolean get() = (position > Constants.Wrist.MIN_RISE_ANGLE && setPoint < Constants.Wrist.MAX_RISE_ANGLE)
    val needsToFlipForward: Boolean get() = (position < Constants.Wrist.MAX_RISE_ANGLE && setPoint > Constants.Wrist.MIN_RISE_ANGLE)
    val needsToFlip: Boolean get() = needsToFlipForward || needsToFlipBackward
    val isDangerousPosition: Boolean get() = (position > Constants.Wrist.MAX_RISE_ANGLE || position < Constants.Wrist.MIN_RISE_ANGLE)
    val isDangerousSetPoint: Boolean get() = (setPoint > Constants.Wrist.MAX_RISE_ANGLE || setPoint < Constants.Wrist.MIN_RISE_ANGLE)
    val isDangerous: Boolean get() = isDangerousPosition || isDangerousSetPoint
    var isFlipping: Boolean
    val isAtSetPoint: Boolean get() = Math.abs(mMaster.getSelectedSensorVelocity(0)) < 0.01
    // public var targetPosistion: WristPosistions

    // set posistion
    public enum class WristPosistions(val value: Double) {
        FORWARD(Constants.Wrist.FORWARD),
        BACKWARD(Constants.Wrist.BACKWARD),
        HATCH(Constants.Wrist.HATCH_ANGLE),
        BALL(Constants.Wrist.BALL_ANGLE),
        HUMAN_PLAYER_BALL(Constants.Wrist.HUMAN_PLAYER),
        DEFENSE(80.0)
        // HUMAN_PLAYER_HATCH(Constants.Wrist.)
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
            configContinuousCurrentLimit(25, 0) // amps
            enableVoltageCompensation(false)
            configForwardSoftLimitThreshold(Constants.Wrist.MAX_ENCODER_TICKS, kWristSlot)
            configReverseSoftLimitThreshold(Constants.Wrist.MIN_ENCODER_TICKS, kWristSlot)
            configForwardSoftLimitEnable(true, 0)
            configReverseSoftLimitEnable(true, 0)
            configPeakOutputForward(Constants.MAX_OUTPUT)
            configPeakOutputReverse(Constants.MIN_OUTPUT)
        }

        setPoint = 0.0
        position = WristPosistions.FORWARD.value
        liftPos = 0.0
        isFlipping = false
    }

    private fun ticksToDegrees(ticks: Int): Double =
        ticks / Constants.Wrist.ENCODER_TICKS_PER_ROTATION * 360.0

    private fun degreesToTicks(heading: Double): Int =
        (heading / 360.0 * Constants.Wrist.ENCODER_TICKS_PER_ROTATION).toInt()

    public fun zero() {
        setPoint = 0.0
        position = WristPosistions.FORWARD.value
        mMaster.setSelectedSensorPosition(0)
    }

    public fun startZero() {
        mMaster.overrideSoftLimitsEnable(false)
        setPercent(-0.2)
    }

    public fun setPercent(percent: Double) {
        mMaster.set(ControlMode.PercentOutput, percent)
    }

    @Suppress("ComplexCondition")
    public fun setPosition(point: WristPosistions) {
        setPoint = point.value
        if (!this.needsToFlip || lift.canFlip) {
            mMaster.set(ControlMode.MotionMagic, setPoint)
            this.isFlipping = true
        }
    }

    private fun setDegrees(heading: Double) {
        setTicks(degreesToTicks(heading))
    }

    private fun setTicks(ticks: Int) {
        // println("wrist: $position")
        mMaster.set(ControlMode.MotionMagic, ticks.toDouble())
    }

    public override fun update() {
        if (mMaster.getSensorCollection().isRevLimitSwitchClosed() && !isZeroed) {
            zero()
            mMaster.overrideSoftLimitsEnable(true)
            isZeroed = true
        }

        if (this.needsToFlip && lift.canFlip) {
            mMaster.set(ControlMode.MotionMagic, setPoint)
            this.isFlipping = true
        }
        this.isFlipping = this.isFlipping && this.needsToFlip
    }
    public override fun stop() {}
    public override fun reset() {}
}
