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
    public var pump: Boolean = false
    private var suck: Boolean = false
    public var hasBall: Boolean = false

    init {
        mTalon = masterTalon
        mSolenoid = solenoid
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

    public fun disableValve() = setValve(false)

    public fun setPercent(percent: Double) = mTalon.set(ControlMode.PercentOutput, percent)

    public override fun update() {
        pump = pump || (suck && mTimer.get().toInt() % 2 == 1)
        if (pump) mTalon.set(ControlMode.PercentOutput, 1.0)
        else mTalon.set(ControlMode.PercentOutput, 0.0)
        pump = false
        if (valveCheck() && mSolenoid.get()) {
            setValve(false)
        }
    }

    private fun valveCheck(): Boolean {
        if (mTalon.getMotorOutputVoltage().toDouble() > Constants.Vacuum.MIN_MOTOR_OUTPUT_VOLTAGE) {
            hasBall = true
        }
        return hasBall
    }

    public override fun stop() {
        pump = false
        mTalon.set(ControlMode.PercentOutput, 0.0)
        mSolenoid.set(false)
    }
    public override fun reset() { stop() }
}
