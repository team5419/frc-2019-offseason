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
    
    private val kP = 0.01
    private val kI = 0.0
    private val kD = 0.0

    private val mMax = 1.0

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
            var error = mSubsystems.vision.targetXOffset
            mTotalError += error

            var timeelapsed = mTimer.get()
            mTimer.reset()

            var output =
                error * kP
              + (error - mPrevError) / timeelapsed * kI
              + mTotalError * kD
            
            if (output > mMax) {
                output = mMax
            } else if (output < -mMax) {
                output = -mMax
            }

            mSubsystems.drivetrain.setPercent( DriveSignal(+output, -output) )

            mPrevError = error
        }
    }
}
