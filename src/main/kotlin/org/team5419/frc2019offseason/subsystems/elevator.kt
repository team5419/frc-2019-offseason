package org.team5419.frc2019offseason.subsystems

class Elevator (
    mMaster: LazyTalonSRX,
    mSlave: LazyTalonSRX 
) : Subsystem () {
    private var master: LazyTalonSRX
    private var slave: LazyTalonSRX

    init{
        master = mMaster
        slave = mSlave

    }
}