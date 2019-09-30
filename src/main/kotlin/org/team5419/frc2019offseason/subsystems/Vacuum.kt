package org.team5419.frc2019offseason.subsystems

import org.team5419.fault.Subsystem
import org.team5419.frc2019offseason.Constants
import edu.wpi.first.wpilibj.Solenoid
import org.team5419.fault.hardware.LazyTalonSRX
import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.Timer

class Vacuum(
    masterTalon: LazyTalonSRX,
    solenoid: Solenoid
) : Subsystem() {
    private val mTimer: Timer = Timer()

    private val mTalon: LazyTalonSRX
    private val mSolenoid: Solenoid
    private var suck: Boolean = false
    public var pump: Boolean = false
    public var hasPeice: Boolean = false
        get() = mTalon.getMotorOutputVoltage().toDouble() > Constants.Vacuum.MIN_MOTOR_OUTPUT_VOLTAGE
    public var valve: Boolean
        get() = mSolenoid.get()
        set(value) { if (hasPeice && !value) mSolenoid.set(value) }

    init {
        mTalon = masterTalon
        mSolenoid = solenoid
        valve = mSolenoid.get()
    }

    // public fun setValve(value: Boolean) {
    //     mSolenoid.set(value)
    //     suck = value
    //     if (value) mTimer.start()
    //     else {
    //         mTimer.stop()
    //         mTimer.reset()
    //     }
    // }

    public fun disableValve() { valve = false }

    public fun setPercent(percent: Double) = mTalon.set(ControlMode.PercentOutput, percent)

    public override fun update() {
        if (pump) setPercent(1.0)
        else if (valve && mTimer.get().toInt() % 2 == 1) setPercent(0.5)
        else mTalon.set(ControlMode.PercentOutput, 0.0)
        pump = false
        if (hasPeice && mSolenoid.get()) {
            valve = false
        }
    }

    public override fun stop() {
        pump = false
        mTalon.set(ControlMode.PercentOutput, 0.0)
        mSolenoid.set(false)
    }
    public override fun reset() { stop() }
}
