package org.team5419.frc2019offseason.controllers

import org.team5419.fault.Controller
import org.team5419.frc2019offseason.subsystems.SubsystemsManager
import org.team5419.fault.input.DriveSignal
import edu.wpi.first.wpilibj.Timer
import org.team5419.fault.math.pid.PIDF
import org.team5419.frc2019offseason.Constants

public class AutoController(
    subsystems: SubsystemsManager
) : Controller() {
    private val mSubsystems: SubsystemsManager

    private val kP = 0.01
    private val kI = 0.0
    private val kD = 0.0

    private val mPID: PIDF

    private val mMax = 1.0

    init {
        mSubsystems = subsystems

        mPID = PIDF(
            Constants.Drivetrain.ALIGN_KP,
            Constants.Drivetrain.ALIGN_KI,
            Constants.Drivetrain.ALIGN_KD,
            Constants.Drivetrain.ALIGN_KF,
        )
    }

    override fun start() {
        
    }

    override fun reset() {
        mPID.reset()
    }

    override fun update() {
        if (mSubsystems.vision.hasValidTarget) {
            mPID.processVariable = mSubsystems.vision.targetXOffset
            var output = mPID.calculate()

            mSubsystems.drivetrain.setPercent(DriveSignal(+output, -output))
        }
    }
}
