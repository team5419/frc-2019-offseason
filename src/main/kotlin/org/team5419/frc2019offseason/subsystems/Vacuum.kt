package org.team5419.frc2019offseason.subsystems

import org.team5419.fault.Subsystem

import edu.wpi.first.wpilibj.Solenoid
import org.team5419.fault.hardware.LazyTalonSRX
import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.Timer

class Vacuum(
    masterTalon: LazyTalonSRX,
    solnoid: Solenoid
) : Subsystem() {
    private val mTimer: Timer = Timer()

    private val mTalon: LazyTalonSRX
    private val mSolenoid: Solenoid
    public var pump: Boolean = false
    private var suck: Boolean = false

    init {
        mTalon = masterTalon
        mSolenoid = solnoid
    }

    public fun setValve(value: Boolean) {
        mSolenoid.set(value)
        suck = value
        if (value) mTimer.start()
        else {
            mTimer.stop()
            mTimer.reset()
        }
    }

    public fun toogleValve() = setValve(!mSolenoid.get())

    public fun setPercent(percent: Double) = mTalon.set(ControlMode.PercentOutput, percent)

    public override fun update() {
        pump = pump || (suck && mTimer.get().toInt() % 2 == 1)
        if (pump) mTalon.set(ControlMode.PercentOutput, 1.0)
        else mTalon.set(ControlMode.PercentOutput, 0.0)
        pump = false
    }

    public override fun stop() {
        pump = false
        mTalon.set(ControlMode.PercentOutput, 0.0)
        mSolenoid.set(false)
    }
    public override fun reset() { stop() }
}
