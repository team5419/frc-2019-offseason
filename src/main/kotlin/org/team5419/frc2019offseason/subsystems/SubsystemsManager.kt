package org.team5419.frc2019offseason.subsystems

public class SubsystemsManager(
    mDrivetrain: Drivetrain,
    mLift: Lift,
    mIntake: Intake,
    mClimber: Climber,
    mVacuum: Vacuum
) {

    public val drivetrain: Drivetrain
    public val lift: Lift
    public val intake: Intake
    public val climber: Climber
    public val vacuum: Vacuum

    init {
        drivetrain = mDrivetrain
        lift = mLift
        intake = mIntake
        climber = mClimber
        vacuum = mVacuum
    }

    public fun updateAll() {
        drivetrain.update()
        lift.update()
        intake.update()
        climber.update()
        vacuum.update()
    }

    public fun stopAll() {
        drivetrain.stop()
        lift.stop()
        intake.stop()
        climber.stop()
        vacuum.stop()
    }

    public fun resetAll() {
        drivetrain.reset()
        lift.reset()
        intake.reset()
        climber.reset()
        vacuum.reset()
    }
}
