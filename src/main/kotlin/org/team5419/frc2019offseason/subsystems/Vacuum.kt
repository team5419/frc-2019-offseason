package org.team5419.frc2019offseason.subsystems
import org.team5419.fault.Subsystem
import org.team5419.frc2019offseason.Constants
import org.team5419.fault.hardware.LazyTalonSRX

import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.GenericHID.RumbleType
import edu.wpi.first.wpilibj.XboxController

import com.ctre.phoenix.motorcontrol.ControlMode
import java.util.Deque
import java.util.LinkedList
import java.util.Collections

class Vacuum(
    masterTalon: LazyTalonSRX,
    releaseSolenoid: Solenoid,
    hatchSolenoid: Solenoid

) : Subsystem() {
    private val mTalon: LazyTalonSRX
    private val mReleaseSolenoid: Solenoid
    private val mHatchSolenoid: Solenoid
    private val mTimer: Timer = Timer()
    lateinit var mVision: Vision
    lateinit var mController: XboxController

    private var isClearingValve: Boolean = false
    private var hasPiece: Boolean = false
    private var isTestingPiece: Boolean = false

    public var hatchValve: Boolean = false
        get() = mHatchSolenoid.get()
        set(value) {
            mHatchSolenoid.set(value)
            field = value
        }
    public var realeaseValve: Boolean = false
        get() = mReleaseSolenoid.get()
        set(value) {
            mReleaseSolenoid.set(value)
            field = value
        }

    private val rollingValues: Deque<Double> = LinkedList(Collections.nCopies(10, 0.0))
    public val rollingAverage: Double
        get() {
            var sum = 0.0
            rollingValues.iterator().forEach { sum += it }
            return sum / rollingValues.size
        }

    init {
        mTalon = masterTalon
        mReleaseSolenoid = releaseSolenoid
        mHatchSolenoid = hatchSolenoid
    }

    public fun release() {
        realeaseValve = true
        hatchValve = true
        hasPiece = false
        mTalon.set(ControlMode.PercentOutput, 0.0)
    }

    public fun pickBall() {
        realeaseValve = false
        hatchValve = false
        setPercent(1.0)
    }

    public fun pickHatch() {
        realeaseValve = false
        hatchValve = true
        setPercent(1.0)
    }

    public fun setPercent(percent: Double) {
        // if (percent == 0.0) isPumping = false
        mTalon.set(ControlMode.PercentOutput, percent)
    }

    @Suppress("ComplexCondition")
    public override fun update() {
        rollingValues.addFirst(mTalon.getOutputCurrent())
        rollingValues.removeLast()
        val tempAve: Double = rollingAverage

        if (
            !isTestingPiece &&
            !hasPiece &&
            tempAve >= Constants.Vacuum.MIN_CURRENT &&
            tempAve <= Constants.Vacuum.MAX_CURRENT
        ) {
            timer.start()
            isTestingPiece = true
        }
        if (isTestingPiece) {
            if (timer.get() > 0.5) {
                hasPiece = true
                isTestingPiece = false
                mVision.flashNumTimes(3)
                mController.setRumble(RumbleType.kLeftRumble, 0.5)
                mController.setRumble(RumbleType.kRightRumble, 0.5)
            } else if (
                tempAve <= Constants.Vacuum.MIN_CURRENT ||
                tempAve >= Constants.Vacuum.MAX_CURRENT
            ) {
                isTestingPiece = false
                timer.stop()
                timer.reset()
            }
        }
    }

    public override fun stop() {
        mTalon.set(ControlMode.PercentOutput, 0.0)
        mReleaseSolenoid.set(true)
        mHatchSolenoid.set(true)
    }
    public override fun reset() { stop() }
}
