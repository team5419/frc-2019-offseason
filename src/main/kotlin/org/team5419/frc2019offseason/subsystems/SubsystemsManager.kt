package org.team5419.frc2019offseason.subsystems

public class SubsystemsManager(
    mDrivetrain: Drivetrain,
    mWrist: Wrist,
    mVacuum: Vacuum,
    mLift: Lift,
    mClimber: Climber,
    mVision: Vision
) {

    public val drivetrain: Drivetrain
    public val lift: Lift
    public val wrist: Wrist
    public val climber: Climber
    public val vacuum: Vacuum
    public val vision: Vision

    init {
        drivetrain = mDrivetrain
        lift = mLift
        wrist = mWrist
        vacuum = mVacuum
        climber = mClimber
        vision = mVision
    }

    public fun updateAll() {
        drivetrain.update()
        lift.update()
        wrist.update()
        climber.update()
        vacuum.update()
        vision.update()
    }

    public fun stopAll() {
        drivetrain.stop()
        lift.stop()
        wrist.stop()
        climber.stop()
        vacuum.stop()
        vision.stop()
    }

    public fun resetAll() {
        drivetrain.reset()
        lift.reset()
        wrist.reset()
        climber.reset()
        vacuum.reset()
        vision.reset()
    }
}
