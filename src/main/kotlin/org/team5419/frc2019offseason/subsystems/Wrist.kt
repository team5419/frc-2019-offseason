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

    private fun ticksToDegrees(ticks: Int): Double =
        (ticks.toDouble() + Constants.Wrist.POSITION_OFFSET) * 180.0 / (630 + Constants.Wrist.POSITION_OFFSET)
    private fun degreesToTicks(heading: Double): Int =
        (heading / 180 * (630 + Constants.Wrist.POSITION_OFFSET) - Constants.Wrist.POSITION_OFFSET).toInt()
    public val mMaster: LazyTalonSRX
    public var position: Double
        get() = ticksToDegrees(mMaster.getSelectedSensorPosition(0))
    private var setPoint: Double
    public var liftPos: Double
    public val canRise: Boolean
        get() = (position > 110.0 && setPoint > 110.0)
    private var isZeroed = false
    public var rawPosition: Int? = null
        get() = mMaster.getSelectedSensorPosition(0)
    lateinit var lift: Lift
    private var targetPosistion: WristPosition
    public val isHatchPosition: Boolean get() = targetPosistion == WristPosition.HATCH
    // public var targetPosistion: WristPosition

    // set posistion
    public enum class WristPosition(val value: Double) {
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
            configSelectedFeedbackSensor(FeedbackDevice.Analog, 0, 0)
            // configSelectedFeedbackCoefficient(-1.0)
            // setSelectedSensorPosition(0)
            setSensorPhase(true)
            setInverted(true)

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
        position = WristPosition.FORWARD.value
        liftPos = 0.0
        targetPosistion = WristPosition.FORWARD
    }

    public fun zero() {
        setPoint = 0.0
        position = WristPosition.FORWARD.value
        mMaster.setSelectedSensorPosition(0)
    }

    public fun startZero() {
        mMaster.overrideSoftLimitsEnable(false)
        setPercent(-0.2)
    }

    public fun setPercent(percent: Double) {
        mMaster.set(ControlMode.PercentOutput, percent)
    }

    public fun setPositionRaw(ticks: Int) {
        println(ticks)
        mMaster.set(ControlMode.MotionMagic, ticks.toDouble())
    }

    @Suppress("ComplexCondition")
    public fun setPosition(point: WristPosition) {
        targetPosistion = point
        setPoint = point.value
        if (
            (position < Constants.Wrist.MAX_RISE_ANGLE && setPoint < Constants.Wrist.MAX_RISE_ANGLE) ||
            (position > 110.0 && setPoint > 110.0) ||
            lift.canFlip) {
                setDegrees(point.value.toDouble())
        } else println("Can't set wrist position")
    }

    private fun setDegrees(heading: Double) {
        println("set degree")
        setPositionRaw(degreesToTicks(heading))
    }

    // private fun setTicks(ticks: Int) {
    //     // println("wrist: $position")
    //     mMaster.set(ControlMode.MotionMagic, ticks.toDouble())
    // }

    public override fun update() {
        // println(mMaster.getClosedLoopError(0))
        if (mMaster.getSensorCollection().isRevLimitSwitchClosed() && !isZeroed) {
            zero()
            mMaster.overrideSoftLimitsEnable(true)
            isZeroed = true
        }
        println(mMaster.getSelectedSensorPosition(0).toString() + " " +
            position + " " +
            setPoint)
    }
    public override fun stop() {}
    public override fun reset() {}
}
