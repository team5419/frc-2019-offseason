package org.team5419.frc2019offseason.subsystems



class Drivetrain() : Subsystem() {

    private left: TalonSRX;
    private right: TalonSRX;

    init {

    }

    public fun drive (left: Double, right: Double) {

    }

    public fun turn (degrees: Double) {

    }

    public fun spin (degrees: Double) {
        drive( a, -a)
    }
    
}