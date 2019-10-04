package org.team5419.frc2019offseason

@SuppressWarnings("MagicNumber")
public object Constants {

    public const val ROBOT_UPDATE_PERIOD = 0.005
    public const val TALON_UPDATE_PERIOD_MS = 1
    public const val TALON_PIDF_UPDATE_PERIOD_MS = 1
    public const val MAX_OUTPUT = 0.1

    object Input {
        public const val DRIVER_PORT = 0
        public const val CODRIVER_PORT = 1
        public const val DEADBAND = 0.02
        public const val BASE_SPEED = 1.0
        public const val BASE_INVERSE_SPEED = 0.1
        public const val SLOW_COEFFICIENT = 1.0
        public const val SLOW = 0.4
        public const val SPIN_SPEED = 0.5
    }

    object Drivetrain {
        // talon port
        public const val LEFT_MASTER_TALON_PORT = 10
        public const val LEFT_SLAVE_TALON_PORT = 8
        public const val RIGHT_MASTER_TALON_PORT = 9
        public const val RIGHT_SLAVE_TALON_PORT = 12
        // gyro
        public const val GYRO_PORT = 13
        // units
        public const val ENCODER_TICKS_PER_ROTATION = 4096
        public const val TURN_UNITS_PER_ROTATION = 3600 // for gyro
        public const val PIGEON_UNITS_PER_ROTATION = 8192
        // dimensions
        public const val WHEEL_BASE = 27.0 // inches
        public const val WHEEL_DIAMETER = 6.0 // inches
        public const val WHEEL_RADIUS = WHEEL_DIAMETER / 2.0
        public const val WHEEL_CIR = WHEEL_DIAMETER * Math.PI
        // pidf
        public const val VEL_KP = 0.7
        public const val VEL_KI = 0.0
        public const val VEL_KD = 0.0
        public const val VEL_KF = 0.0
        public const val TALON_PIDF_UPDATE_PERIOD_MS = 1
    }

    object Vacuum {
        public const val MASTER_TALON_PORT = 2
        public const val RELEASE_SOLNOID_PORT = 0
        public const val HATCH_SOLENOID_PORT = 1
        public const val CURRENT_THRESHOLD = 2.125

        public const val MOTION_MAGIC_VELOCITY = 11000
        public const val MOTION_MAGIC_ACCELERATION = 11000
        public const val MAX_ENCODER_TICKS = 2048
        public const val MIN_ENCODER_TICKS = 0
    }

    object Climber {
        public const val MASTER_TALON_PORT = 28 // check this later
        public const val SLAVE_TALON_PORT = 29 // check this later
        public const val MAX_OUTPUT_PERCENTAGE = 90.0
    }

    object Wrist {
        public const val MASTER_TALON_PORT = 6 // to set
        // to check
        public const val FORWARD = 0.0
        public const val MIDDLE = 90.0
        public const val BACKWARD = 180.0
        public const val BALL_ANGLE = 20.0
        public const val BALL_HIGH = 20.0
        public const val BALL_MID = 20.0
        public const val BALL_LOW = 20.0
        public const val MAX_RISE_ANGLE = 75.0

        public const val ENCODER_TICKS_PER_ROTATION = 4096
        public const val ENCODER_TICKS_BALL_POSITSTION = 1692

        public const val KP = 0.7
        public const val KI = 0.0
        public const val KD = 0.1
        public const val KF = 0.0

        public const val MOTION_MAGIC_VELOCITY = 11000
        public const val MOTION_MAGIC_ACCELERATION = 11000
        public const val MAX_ENCODER_TICKS = 2048
        public const val MIN_ENCODER_TICKS = 0
    }

    object Lift {
        // talon port
        public const val MASTER_TALON_PORT = 3
        public const val SLAVE_TALON_PORT = 5

        // inches
        public const val STOW_HEIGHT = 0.0
        public const val HATCH_LOW_HEIGHT = 7.0
        public const val HATCH_MID_HEIGHT = 22.0
        public const val HATCH_HIGH_HEIGHT = 45.0
        public const val BALL_LOW_HEIGHT = 0.0
        public const val BALL_MID_HEIGHT = 0.0
        public const val BALL_HIGH_HEIGHT = 0.0
        public const val BALL_HUMAN_PLAYER_HEIGHT = 0.0
        public const val SECOND_STAGE_HIGHT = 22.865
        public const val MAX_HEIGHT = 50.0
        public const val MAX_FLIP_HIGHT = 4.0

        // tf?
        public const val ENCODER_TICKS_PER_ROTATION = 4096.0
        public const val INCHES_PER_ROTATION = 4.4
        public const val MOTION_MAGIC_VELOCITY = 11000
        public const val MOTION_MAGIC_ACCELERATION = 11000
        public const val MAX_ENCODER_TICKS = 41890
        public const val MIN_ENCODER_TICKS = 0
        // pid
        public const val KP = 0.5
        public const val KI = 0.0
        public const val KD = 0.0
        public const val KF = 0.01
        public const val KF2 = 0.02
        public const val SECOND_STAGE_EPSILON = 10.0
    }

    object Auto {
        public const val LOOKAHEAD_DISTANCE: Double = 12.0
    }
}
