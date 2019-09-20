package org.team5419.frc2019offseason.subsystems

import org.team5499.monkeyLib.Subsystem
import org.team5499.monkeyLib.hardware.LazyTalonSRX
import org.team5499.monkeyLib.hardware.LazyVictorSPX
import org.team5499.monkeyLib.math.Position
import org.team5499.monkeyLib.math.geometry.Vector2
import org.team5499.monkeyLib.math.geometry.Rotation2d
import org.team5499.monkeyLib.math.geometry.Pose2d
import org.team5499.monkeyLib.util.Utils
import org.team5499.monkeyLib.input.DriveSignal

class Drivetrain(
    leftMaster: LazyTalonSRX,
    leftSlave1: LazyVictorSPX,
    leftSlave2: LazyVictorSPX,
    rightMaster: LazyTalonSRX,
    rightSlave1: LazyVictorSPX,
    rightSlave2: LazyVictorSPX
) : Subsystem() {

    private val mLeftMaster: LazyTalonSRX
    private val mLeftSlave1: LazyVictorSPX

    private val mRightMaster: LazyTalonSRX
    private val mRightSlave1: LazyVictorSPX

    public var brakeMode: Boolean = false
        set(value) {
            if (value == field) return
            val mode = if (value) NeutralMode.Brake else NeutralMode.Coast
            mLeftMaster.setNeutralMode(mode)
            mLeftSlave1.setNeutralMode(mode)

            mRightMaster.setNeutralMode(mode)
            mRightSlave1.setNeutralMode(mode)
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
                0
            )
        }

        mLeftSlave1 = leftSlave1.apply {
            follow(mLeftMaster)
            setInverted(InvertType.FollowMaster)
        }

        mRightMaster = rightMaster.apply {
            setInverted(true)
            setSensorPhase(true)
            setStatusFramePeriod(
                StatusFrameEnhanced.Status_3_Quadrature,
                Constants.TALON_UPDATE_PERIOD_MS,
                0
            )
        }
        mRightSlave1 = rightSlave1.apply {
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
    //encoders 
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

    public fun setPercent(left: Double, right: Double){
        mLeftMaster.set(left)
        mRightMaster.set(right)
    }

    public override fun update(){
        mPosition.update(leftDistance, rightDistance, heading.degrees)

    }

    public override fun stop(){
        setPercent(0.0,0.0)
        brakeMode = false

    }
    public override fun reset(){
        stop()
    }
}