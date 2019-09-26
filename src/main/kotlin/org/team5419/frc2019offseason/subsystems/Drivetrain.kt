package org.team5419.frc2019offseason.subsystems

import org.team5419.frc2019offseason.Constants

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced
import com.ctre.phoenix.motorcontrol.InvertType
import com.ctre.phoenix.ParamEnum
import com.ctre.phoenix.motorcontrol.FeedbackDevice

import org.team5419.fault.Subsystem
import org.team5419.fault.hardware.LazyTalonSRX
import org.team5419.fault.hardware.LazyVictorSPX
import org.team5419.fault.math.Position
import org.team5419.fault.util.Utils


class Drivetrain(
    leftMaster: LazyTalonSRX,
    leftSlave: LazyVictorSPX,
    rightMaster: LazyTalonSRX,
    rightSlave: LazyVictorSPX
) : Subsystem() {

    private val mLeftMaster: LazyTalonSRX
    private val mLeftSlave: LazyVictorSPX

    private val mRightMaster: LazyTalonSRX
    private val mRightSlave: LazyVictorSPX

    public var brakeMode: Boolean = false
        set(value) {
            if (value == field) return
            val mode = if (value) NeutralMode.Brake else NeutralMode.Coast
            mLeftMaster.setNeutralMode(mode)
            mLeftSlave.setNeutralMode(mode)

            mRightMaster.setNeutralMode(mode)
            mRightSlave.setNeutralMode(mode)
            field = value
        }

    private val mPosition = Position()

    init {
        mLeftMaster = leftMaster.apply {
            setInverted(false)
            setSensorPhase(true)
            setStatusFramePeriod(
                StatusFrameEnhanced.Status_3_Quadrature,
                Constants.TALON_UPDATE_PERIOD_MS,
                0)
            configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0)
            config_kP(0, Constants.Drivetrain.VEL_KP, 0)
            config_kI(0, Constants.Drivetrain.VEL_KI, 0)
            config_kD(0, Constants.Drivetrain.VEL_KD, 0)
            config_kF(0, Constants.Drivetrain.VEL_KF, 0)
            configPeakOutputForward(+1.0, 0)
            configPeakOutputReverse(-1.0, 0)
            config_IntegralZone(0, 0, 0)
            configClosedLoopPeakOutput(0, 1.0, 0)
            config_IntegralZone(1, 0, 0)
            configClosedLoopPeakOutput(1, 0.0, 0)
            config_kP(1, 0.0, 0)
            config_kI(1, 0.0, 0)
            config_kD(1, 0.0, 0)
            config_kF(1, 0.0, 0)
            configSetParameter(ParamEnum.ePIDLoopPeriod,
                Constants.TALON_PIDF_UPDATE_PERIOD_MS.toDouble(), 0x00, 0, 0)
            configSetParameter(ParamEnum.ePIDLoopPeriod,
                Constants.TALON_PIDF_UPDATE_PERIOD_MS.toDouble(), 0x00, 1, 0)
        }

        mLeftSlave = leftSlave.apply {
            follow(mLeftMaster)
            setInverted(InvertType.FollowMaster)
        }

        mRightMaster = rightMaster.apply {
            setInverted(true)
            setSensorPhase(true)
            setStatusFramePeriod(
                StatusFrameEnhanced.Status_3_Quadrature,
                Constants.TALON_UPDATE_PERIOD_MS,
                0)
            configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0)
            configPeakOutputForward(+1.0, 0)
            configPeakOutputReverse(-1.0, 0)
            config_IntegralZone(0, 0, 0)
            configClosedLoopPeakOutput(0, 1.0, 0)
            config_IntegralZone(1, 0, 0)
            configClosedLoopPeakOutput(1, 0.0, 0)
            config_kP(0, Constants.Drivetrain.VEL_KP, 0)
            config_kI(0, Constants.Drivetrain.VEL_KI, 0)
            config_kD(0, Constants.Drivetrain.VEL_KD, 0)
            config_kF(0, Constants.Drivetrain.VEL_KF, 0)
            config_kP(1, 0.0, 0)
            config_kI(1, 0.0, 0)
            config_kD(1, 0.0, 0)
            config_kF(1, 0.0, 0)
            configSetParameter(ParamEnum.ePIDLoopPeriod,
                Constants.TALON_PIDF_UPDATE_PERIOD_MS.toDouble(), 0x00, 0, 0)
            configSetParameter(ParamEnum.ePIDLoopPeriod,
                Constants.TALON_PIDF_UPDATE_PERIOD_MS.toDouble(), 0x00, 1, 0)
        }
        mRightSlave = rightSlave.apply {
            follow(mRightMaster)
            setInverted(InvertType.FollowMaster)
        }
    }

    public var leftDistance: Double
        get() {
            return -Utils.encoderTicksToInches(
                Constants.Drivetrain.ENCODER_TICKS_PER_ROTATION,
                Constants.Drivetrain.WHEEL_CIR,
                mLeftMaster.sensorCollection.quadraturePosition
            )
        }
        set(inches) {
            mLeftMaster.sensorCollection.setQuadraturePosition(
                Utils.inchesToEncoderTicks(Constants.Drivetrain.ENCODER_TICKS_PER_ROTATION,
                Constants.Drivetrain.WHEEL_CIR,
                inches), 0)
        }
    // encoders
    public var rightDistance: Double
        get() {
            return Utils.encoderTicksToInches(
                Constants.Drivetrain.ENCODER_TICKS_PER_ROTATION,
                Constants.Drivetrain.WHEEL_CIR,
                mRightMaster.sensorCollection.quadraturePosition
            )
        }
        set(inches) {
            mRightMaster.getSensorCollection().setQuadraturePosition(
                Utils.inchesToEncoderTicks(
                    Constants.Drivetrain.ENCODER_TICKS_PER_ROTATION,
                    Constants.Drivetrain.WHEEL_CIR,
                    inches
                ), 0)
        }

    public fun setPercent(left: Double, right: Double) {
        mLeftMaster.set(ControlMode.PercentOutput, left)
        mRightMaster.set(ControlMode.PercentOutput, right)
    }

    public override fun update() {
        // mPosition.update(leftDistance, rightDistance, heading.degrees)
    }

    public override fun stop() {
        setPercent(0.0, 0.0)
        brakeMode = false
    }
    public override fun reset() {
        stop()
    }
}
