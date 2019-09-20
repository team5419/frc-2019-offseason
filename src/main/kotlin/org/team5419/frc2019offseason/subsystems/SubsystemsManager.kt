package org.team5419.frc2019offseason.subsystems

import org.team5419.fault.Subsystem

public class SubsystemsManager(
    drivetrain: Drivetrain,
    lift: Lift,
    intake: Intake
) {

    public val drivetrain: Drivetrain
    public val lift: Lift
    public val intake: Intake

    private val mList: List<Subsystem>

    init {
        this.drivetrain = drivetrain
        this.lift = lift
        this.intake = intake
        mList = listOf(this.drivetrain, this.lift, this.intake)
    }

    public fun updateAll() {
        for (s in mList) {
            s.update()
        }
    }

    public fun stopAll() {
        for (s in mList) {
            s.stop()
        }
    }

    public fun resetAll() {
        for (s in mList) {
            s.reset()
        }
    }
}
