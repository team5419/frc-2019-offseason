package org.team5419.frc2019offseason

import edu.wpi.first.wpilibj.RobotBase
import org.team5499.frc2019offseason.Robot

import java.util.function.Supplier

public class Main {

    /**
     * Main initialization function. Do not perform any initialization here.
     *
     * <p>If you change your main robot class, change the parameter type.
     */
    companion object {
        @JvmStatic
        public fun main(args: Array<String>) {
            RobotBase.startRobot(Supplier<Robot> { Robot() })
        }
    }
}
