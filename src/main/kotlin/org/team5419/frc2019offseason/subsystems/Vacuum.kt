package org.team5419.frc2019offseason.subsystems

import org.team5419.fault.Subsystem
import edu.wpi.first.wpilibj.Solenoid
import org.team5419.fault.hardware.LazyTalonSRX
import com.ctre.phoenix.motorcontrol.ControlMode

class Vacuum(
    masterTalon: LazyTalonSRX,
    releaseSolenoid: Solenoid,
    hatchSolenoid: Solenoid

) : Subsystem() {
    private val mTalon: LazyTalonSRX
    private val mReleaseSolenoid: Solenoid
    private val mHatchSolenoid: Solenoid
    private var isClearingValve: Boolean = false
    private var hasPeice: Boolean = false
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
        realeaseValve = true
        hatchValve = true
        hasPeice = false
    }

    public fun pickBall(percent: Double) {
        realeaseValve = false
        hatchValve = false
        setPercent(percent)
    }

    public fun pickHatch(percent: Double) {
        realeaseValve = false
        hatchValve = true
        setPercent(percent)
    }

    public fun setPercent(percent: Double) {
        // if (percent == 0.0) isPumping = false
        mTalon.set(ControlMode.PercentOutput, Math.max(percent, 0.25))
    }

    public override fun update() {
        // println("${mTalon.getOutputCurrent()}")
        // hasPeice = hasPeice || mTalon.getOutputCurrent() >= Constants.Vacuum.RESTING_THRESHOLD
        // if (hasPeice && !isPumping){
        //     mTalon.set(ControlMode.PercentOutput, 0.2)
        // }
        // else if(!hasPeice && !isPumping){
        //     mTalon.set(ControlMode.PercentOutput, 0.0)
        // }
    }

    public override fun stop() {
        mTalon.set(ControlMode.PercentOutput, 0.0)
        mReleaseSolenoid.set(true)
        mHatchSolenoid.set(true)
    }
    public override fun reset() { stop() }
}
