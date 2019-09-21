package org.team5419.frc2019offseason.subsystems

import org.team5419.fault.hardware.LazyTalonSRX
import org.team5419.fault.Subsystem

class Intake(
    master: LazyTalonSRX
) : Subsystem() {
    private val mMaster: LazyTalonSRX

    init {
        mMaster = master
    }

    public override fun update() {}
    public override fun stop() {}
    public override fun reset() {}
}
