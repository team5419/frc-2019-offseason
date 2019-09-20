package org.team5419.frc2019offseason.subsystems

import com.ctre.phoenix.sensors.PigeonIMU
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced
import com.ctre.phoenix.motorcontrol.DemandType
import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.SensorTerm
import com.ctre.phoenix.motorcontrol.RemoteSensorSource
import com.ctre.phoenix.motorcontrol.FollowerType
import com.ctre.phoenix.motorcontrol.InvertType

import org.team5419.fault.hardware.LazyTalonSRX
import org.team5419.fault.hardware.LazyVictorSPX
import org.team5419.fault.Subsystem

import org.team5419.frc2019offseason.Constants

class Lift (
    master: LazyTalonSRX,
    slave: LazyVictorSPX 
) : Subsystem () {
    private var mMaster: LazyTalonSRX
    private var mSlave: LazyVictorSPX

    public var position: Double

    init{
        mMaster = master.apply {
            setInverted(false)
            setSensorPhase(true)
            setStatusFramePeriod(
                StatusFrameEnhanced.Status_3_Quadrature,
                Constants.TALON_UPDATE_PERIOD_MS,
                0
            )
        }
        mSlave = slave.apply {
            follow(mMaster)
            setInverted(InvertType.FollowMaster)
        }
        position = 0.0
    }

    public fun setPosistion(){
        
    }

    public override fun update(){
    }

    public override fun stop(){
    }

    public override fun reset(){
        stop()
    }

}