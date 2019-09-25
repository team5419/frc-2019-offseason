package org.team5419.frc2019offseason.subsystems

import org.team5419.fault.Subsystem

import edu.wpi.first.wpilibj.Solenoid
import org.team5419.fault.hardware.LazyTalonSRX
import com.ctre.phoenix.motorcontrol.ControlMode

class Vacuum(
    masterTalon: LazyTalonSRX,
    solnoid: Solenoid
) : Subsystem() {

    private val mTalon: LazyTalonSRX
    private val mSolenoid: Solenoid
    public var pump: Boolean = false

    init {
        mTalon = masterTalon
        mSolenoid = solnoid
    }

    public fun setValve(value: Boolean) = mSolenoid.set(value)

    public fun toogleValve() = setValve(!mSolenoid.get())

    public fun setPercent(percent: Double) = mTalon.set(ControlMode.PercentOutput, percent)

    public override fun update() {
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
