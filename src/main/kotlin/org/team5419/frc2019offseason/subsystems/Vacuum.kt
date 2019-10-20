package org.team5419.frc2019offseason.subsystems

import org.team5419.fault.Subsystem
import edu.wpi.first.wpilibj.Solenoid
import org.team5419.fault.hardware.LazyTalonSRX
import com.ctre.phoenix.motorcontrol.ControlMode
import org.team5419.frc2019offseason.Constants
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
    lateinit var mVision: Vision
    private var isClearingValve: Boolean = false
    private var hasPiece: Boolean = false
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

    private val rollingValues: Deque<Double> = LinkedList(Collections.nCopies(30, 0.0))
    private val rollingAverage: Double
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

    public override fun update() {
        rollingValues.addFirst(mTalon.getOutputCurrent())
        rollingValues.removeLast()

        if (!hasPiece && rollingAverage >= Constants.Vacuum.RESTING_THRESHOLD) {
            hasPiece = true
            mVision.flashNumTimes(3)
        }
    }

    public override fun stop() {
        mTalon.set(ControlMode.PercentOutput, 0.0)
        mReleaseSolenoid.set(true)
        mHatchSolenoid.set(true)
    }
    public override fun reset() { stop() }
}
