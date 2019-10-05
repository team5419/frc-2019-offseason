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
    public var unlockOut: Double = 0.0
    public var unlockTimes: Int = 0

    init {

        mMasterTalon = masterTalon
        mSlaveTalon = slaveTalon
        mLockTalon = lockTalon

        mMasterTalon.apply {
            configVoltageCompSaturation(12.0, 0)
            configOpenloopRamp(0.1, 0)
        }
        mSlaveTalon.follow(mMasterTalon)
        mLockTalon.apply {
            configNominalOutputForward(0.0, 0)
            configNominalOutputReverse(0.0, 0)
            configPeakOutputForward(0.2, 0)
            configPeakOutputReverse(-0.2, 0)
            configVoltageCompSaturation(12.0, 0)
            // configOpenloopRamp(1.0, 0)
        }
    }

    public fun climb() { // for driver
        mMasterTalon.set(ControlMode.PercentOutput, -Constants.Climber.MAX_OUTPUT_PERCENTAGE.toDouble())
    }

    public fun unlock() {
        unlockOut = Constants.Climber.LOCK_OUTPUT.toDouble()
        isUnlocking = true
        mLockTalon.set(ControlMode.PercentOutput, unlockOut)
        mTimer.start()
    }

    public fun stopUnlocking() {
        isUnlocking = false
        unlockOut = 0.0
        unlockTimes = 0
        mTimer.reset()
        mLockTalon.set(ControlMode.PercentOutput, 0.0)
    }

    public override fun update() {
        if (isUnlocking && mTimer.get() > Constants.Climber.UNLOCKING_PULSE_TIME) {
            unlockOut = -unlockOut
            mLockTalon.set(ControlMode.PercentOutput, unlockOut)
            mTimer.stop()
            mTimer.reset()
            mTimer.start()
            unlockTimes += 1
        }
        if (unlockTimes > 1) {
            stopUnlocking()
        }
    }
    public override fun stop() {
        mMasterTalon.set(ControlMode.PercentOutput, 0.0)
    }
    public override fun reset() {}
}
