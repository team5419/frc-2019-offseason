package org.team5419.frc2019offseason.subsystems

import org.team5419.fault.Subsystem

public class SubsystemsManager(
    drivetrain: Drivetrain,
    lift: Lift,
    intake: Intake,
    climber: Climber,
    vacuum: Vacuum
) {
    private val mList: List<Subsystem>

    init {
        mList = listOf(
            drivetrain,
            lift,
            intake,
            climber,
            vacuum
        )
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
