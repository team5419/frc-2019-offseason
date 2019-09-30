package org.team5419.frc2019offseason.subsystems

import org.team5419.fault.Subsystem
import org.team5419.frc2019offseason.Constants
import edu.wpi.first.wpilibj.Solenoid
import org.team5419.fault.hardware.LazyTalonSRX
import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.Timer

class Vacuum(
    masterTalon: LazyTalonSRX,
    releaseSolenoid: Solenoid,
    hatchSolenoid: Solenoid

) : Subsystem() {
    private val mTimer: Timer = Timer()

    private val mTalon: LazyTalonSRX
    private val mReleaseSolenoid: Solenoid
    private val mHatchSolenoid: Solenoid
    private var isClearingValve: Boolean = false
    private var hasPeice: Boolean = false
    private var isPumping: Boolean = false
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

    init {
        mTalon = masterTalon
        mReleaseSolenoid = releaseSolenoid
        mHatchSolenoid = hatchSolenoid
    }

    public fun release() {
        isPumping = false
        realeaseValve = true
        hatchValve = true
        hasPeice = false
    }

    public fun pickBall(percent: Double) {
        realeaseValve = false
        hatchValve = false
        setPercent(percent)
        isPumping = true
    }

    public fun pickHatch(percent: Double) {
        realeaseValve = false
        hatchValve = true
        setPercent(percent)
        isPumping = true
    }

    public fun setPercent(percent: Double) {
        if (percent == 0.0) isPumping = false
        mTalon.set(ControlMode.PercentOutput, percent)
    }

    public override fun update() {
    println("Current: " + mTalon.getOutputCurrent().toString() +
            "\nhasPeice: " + hasPeice.toString() +
            "\nisPumping " + isPumping.toString()
    )
        hasPeice = !hasPeice && mTalon.getOutputCurrent() >= Constants.Vacuum.CURRENT_THRESHOLD
        if (hasPeice && mTalon.getOutputCurrent() < Constants.Vacuum.CURRENT_THRESHOLD)
            setPercent(1.0)
        else if (!isPumping) mTalon.set(ControlMode.PercentOutput, 0.0)
    }

    public override fun stop() {
        release()
    }
    public override fun reset() { stop() }
}
