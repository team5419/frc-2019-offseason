package org.team5419.frc2019offseason.subsystems

import org.team5419.frc2019offseason.Constants
import org.team5419.fault.Subsystem
import org.team5419.fault.hardware.LazyTalonSRX
import org.team5419.fault.hardware.LazyVictorSPX
import edu.wpi.first.wpilibj.Timer

import com.ctre.phoenix.motorcontrol.ControlMode

class Climber(masterTalon: LazyTalonSRX, slaveTalon: LazyVictorSPX, lockTalon: LazyTalonSRX) : Subsystem() {

    private val mMasterTalon: LazyTalonSRX
    private val mSlaveTalon: LazyVictorSPX
    private val mLockTalon: LazyTalonSRX
    private val mTimer: Timer = Timer()
    public var isUnlocked: Boolean = false
    public var isUnlocking: Boolean = false

    init {

        mMasterTalon = masterTalon
        mSlaveTalon = slaveTalon
        mLockTalon = lockTalon

        mMasterTalon.apply {
            configVoltageCompSaturation(10.0, 0)
        }
        mSlaveTalon.follow(mMasterTalon)
        mLockTalon.apply {
            configNominalOutputForward(0.0, 0)
            configNominalOutputReverse(0.0, 0)
            configPeakOutputForward(.9, 0)
            configPeakOutputReverse(-.9, 0)
            configVoltageCompSaturation(10.0, 0)
        }
    }

    public fun climb() { // for driver
        if (isUnlocked) {
            mMasterTalon.set(ControlMode.PercentOutput, Constants.Climber.MAX_OUTPUT_PERCENTAGE.toDouble())
        }
    }

    public fun unlock() {
        mLockTalon.set(ControlMode.PercentOutput, Constants.Climber.LOCK_OUTPUT.toDouble())
        isUnlocking = true
        mTimer.start()
    }

    public override fun update() {
        if (isUnlocking && mTimer.get() > Constants.Climber.UNLOCKING_RUN_TIME) {
            mTimer.stop()
            mLockTalon.set(ControlMode.PercentOutput, 0.0)
            isUnlocking = false
            isUnlocked = true
        }
    }
    public override fun stop() {
        mMasterTalon.set(ControlMode.PercentOutput, 0.0)
    }
    public override fun reset() {}
}
