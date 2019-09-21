package org.team5419.frc2019offseason.subsystems

import org.team5419.fault.hardware.LazyTalonSRX
import org.team5419.fault.hardware.LazyVictorSPX
import org.team5419.fault.Subsystem

class Climber(
    trigger: LazyTalonSRX,
    master: LazyTalonSRX,
    slave: LazyVictorSPX
) : Subsystem() {

    private val mTrigger: LazyTalonSRX
    private val mMaster: LazyTalonSRX
    private val mSlave: LazyVictorSPX

    init {
        mTrigger = trigger
        mMaster = master
        mSlave = slave
    }

    public fun climb() {
    }

    public override fun update() {}
    public override fun stop() {}
    public override fun reset() {}
}
