package org.team5419.frc2019offseason.subsystems

import org.team5419.frc2019offseason.Constants
import org.team5419.fault.Subsystem
import org.team5419.fault.hardware.LazyTalonSRX
import com.ctre.phoenix.motorcontrol.ControlMode


class Climber(masterTalon: LazyTalonSRX, slaveTalon: LazyTalonSRX) : Subsystem() {

    private val mMasterTalon: LazyTalonSRX
    private val mSlaveTalon: LazyTalonSRX
    public var climbButtonPressed: Boolean = false

    init {
        mMasterTalon = masterTalon
        mSlaveTalon = slaveTalon
        mSlaveTalon.follow(mMasterTalon)
    }

    public fun climb() { //for driver
        mMasterTalon.set(ControlMode.PercentOutput, Constants.Climber.MAX_OUTPUT_PERCENTAGE.toDouble())
    }

    public override fun update() {}
    public override fun stop() {
        mMasterTalon.set(ControlMode.PercentOutput, 0.0)
    }
    public override fun reset() {}
}
