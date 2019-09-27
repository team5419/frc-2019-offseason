package org.team5419.frc2019offseason.subsystems

public class SubsystemsManager(
    mDrivetrain: Drivetrain,
    mLift: Lift,
    mWrist: Wrist,
    mClimber: Climber,
    mVacuum: Vacuum
) {

    public val drivetrain: Drivetrain
    public val lift: Lift
    public val wrist: Wrist
    public val climber: Climber
    public val vacuum: Vacuum

    init {
        drivetrain = mDrivetrain
        lift = mLift
        wrist = mWrist
        climber = mClimber
        vacuum = mVacuum
    }

    public fun updateAll() {
        drivetrain.update()
        lift.update()
        wrist.update()
        climber.update()
        vacuum.update()
    }

    public fun stopAll() {
        drivetrain.stop()
        lift.stop()
        wrist.stop()
        climber.stop()
        vacuum.stop()
    }

    public fun resetAll() {
        drivetrain.reset()
        lift.reset()
        wrist.reset()
        climber.reset()
        vacuum.reset()
    }
}
