package org.team5419.frc2019offseason.subsystems

import org.team5419.fault.Subsystem

import edu.wpi.first.wpilibj.Solenoid
import org.team5419.fault.hardware.LazyTalonSRX
import com.ctre.phoenix.motorcontrol.ControlMode

class Vacuum(
    masterTalon: LazyTalonSRX,
    solnoid: Solenoid
) : Subsystem() {

    private val mMaster: LazyTalonSRX
    private val mSolenoid: Solenoid
    private var isPumping: Boolean = false

    init {
        mMaster = masterTalon
        mSolenoid = solnoid
    }

    public fun setValve(value: Boolean) {
        mSolenoid.set(value)
    }

    public override fun update() {
        if (isPumping) mMaster.set(ControlMode.PercentOutput, 1.0)
        else mMaster.set(ControlMode.PercentOutput, 0.0)
    }

    public override fun stop() {
        isPumping = false
        mMaster.set(ControlMode.PercentOutput, 0.0)
        mSolenoid.set(false)
    }
    public override fun reset() { stop() }
}
