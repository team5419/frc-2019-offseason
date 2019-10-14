package org.team5419.frc2019offseason.controllers

import org.team5419.fault.Controller
import org.team5419.frc2019offseason.subsystems.SubsystemsManager
import org.team5419.fault.input.DriveSignal
import edu.wpi.first.wpilibj.Timer

public class AutoController(
    subsystems: SubsystemsManager
) : Controller() {
    private val mSubsystems: SubsystemsManager

    private var mPrevError: Double
    private var mTotalError: Double
    private val mTimer: Timer

    init {
        mSubsystems = subsystems

        mPrevError = 0.0
        mTotalError = 0.0

        mTimer = Timer()
    }

    override fun start() {
        mTimer.start()
        mPrevError = 0.0
        mTotalError = 0.0
    }

    override fun reset() {

    }

    override fun update() {
        if ( mSubsystems.vision.hasValidTarget ) {
            var error = mSubsystems.vision.targetSkew
            mTotalError += error

            var timeelapsed = mTimer.get()
            mTimer.reset()

            var output =
                error * 0.01
              + (error - mPrevError) / timeelapsed * 0.01
              + mTotalError * 0.01

            mSubsystems.drivetrain.setPercent( DriveSignal(+output, -output) )

            mPrevError = error
        }
    }
}
