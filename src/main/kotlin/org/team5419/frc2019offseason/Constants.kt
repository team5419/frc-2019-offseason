package org.team5419.frc2019offseason

public object Constants {

    public const val ROBOT_UPDATE_PERIOD = 0.005 // how fast the robot update period runs
    public const val TALON_UPDATE_PERIOD_MS = 10 // how often the talon is updated on the CAN network, in ms
    public const val TALON_PIDF_UPDATE_PERIOD_MS = 1 // how often the PIDF loop on the talons runs in ms
    public const val MAX_OUTPUT = 1.0 // max talon output
    public const val MIN_OUTPUT = -1.0 // min talon output

    object Input {
        public const val DRIVER_PORT = 0 // xbox controller port
        public const val CODRIVER_PORT = 1 // xbox controller port
        public const val DEADBAND = 0.07 // xbox controller deadband(for all variable inputs except POV)
        public const val BASE_SPEED = 1.0 // default max drivetrain speed (percent output)
        public const val SLOW = 0.4 // the speed is multiplied by this when the slow button is pressed
        public const val SLOW_COEFFICIENT = 1.0
        public const val SPIN_SPEED = 0.5
        public const val JOYSTICK_DEADBAND = 0.07
        public const val TURN_MULT = 0.4
        public const val SLOW_MULT = 0.5
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
        public const val ENCODER_TICKS_PER_ROTATION = 4096 // encoder ticks when encoder shaft is rotated 360 degrees
        public const val TURN_UNITS_PER_ROTATION = 3600 // for gyro
        public const val PIGEON_UNITS_PER_ROTATION = 8192 // can't remember what this is
        // dimensions
        public const val WHEEL_BASE = 27.0 // inches
        public const val WHEEL_DIAMETER = 6.0 // inches
        public const val WHEEL_RADIUS = WHEEL_DIAMETER / 2.0 // inches
        public const val WHEEL_CIR = WHEEL_DIAMETER * Math.PI // inches
        // pidf
        public const val VEL_KP = 0.7
        public const val VEL_KI = 0.0
        public const val VEL_KD = 0.0
        public const val VEL_KF = 0.0 // should be tuned
        public const val TALON_PIDF_UPDATE_PERIOD_MS = 1
    }

    object Vacuum {
        public const val MASTER_TALON_PORT = 2
        public const val RELEASE_SOLNOID_PORT = 0
        public const val HATCH_SOLENOID_PORT = 1
        public const val RESTING_THRESHOLD = 0.5
        public const val MIN_THRESHOLD = 0.5

        public const val MOTION_MAGIC_VELOCITY = 11000
        public const val MOTION_MAGIC_ACCELERATION = 11000
        public const val MAX_ENCODER_TICKS = 2048
        public const val MIN_ENCODER_TICKS = 0
    }

    object Climber {
        public const val MASTER_TALON_PORT = 4
        public const val SLAVE_TALON_PORT = 11
        public const val LOCK_TALON_PORT = 7
        public const val MAX_OUTPUT_PERCENTAGE = 0.9
        public const val LOCK_OUTPUT = 0.5
        public const val UNLOCKING_RUN_TIME = 3.0
        public const val UNLOCKING_PULSE_TIME = 0.75
    }

    object Wrist {
        public const val MASTER_TALON_PORT = 6 // to set
        // to check
        public const val FORWARD = 0.0 // need pot value
        public const val MIDDLE = 90.0
        public const val BACKWARD = 180.0
        public const val HATCH_ANGLE = 175.0
        public const val BALL_ANGLE = 130.0
        public const val MIN_RISE_ANGLE = 135.0
        public const val MAX_RISE_ANGLE = 75.0
        public const val HUMAN_PLAYER = 140.0

        public const val ENCODER_TICKS_PER_ROTATION = 4096.0

        public const val KP = 1.0
        public const val KI = 0.0
        public const val KD = 0.1
        public const val KF = 0.0

        public const val MOTION_MAGIC_VELOCITY = 11000
        public const val MOTION_MAGIC_ACCELERATION = 5000
        public const val MAX_ENCODER_TICKS = 2048
        public const val MIN_ENCODER_TICKS = 0
        public const val POSITION_OFFSET = 140
    }

    object Lift {
        // talon port
        public const val MASTER_TALON_PORT = 3
        public const val SLAVE_TALON_PORT = 5
        // inches
        public const val HATCH_LOW_HEIGHT = 0.0
        public const val HATCH_MID_HEIGHT = 26.18
        public const val HATCH_HIGH_HEIGHT = 51.0
        public const val BALL_LOW_HEIGHT = 0.0
        public const val BALL_MID_HEIGHT = 26.3
        public const val BALL_HIGH_HEIGHT = 51.0
        public const val HUMAN_PLAYER = 14.7
        public const val SECOND_STAGE_HIGHT = 22.865
        public const val MAX_HEIGHT = 50.0
        public const val MAX_FLIP_HIGHT = 4.0

        // tf?
        public const val ENCODER_TICKS_PER_ROTATION = 4096.0
        public const val INCHES_PER_ROTATION = 4.4 // inches the elevator moves for 4096 ticks of the encoder
        public const val MOTION_MAGIC_VELOCITY = 300.0 // inches per second
        public const val MOTION_MAGIC_ACCELERATION = 800.0 // inches per second per second
        // public const val MOTION_MAGIC_VELOCITY = 11000 // encoder ticks per 100ms
        // public const val MOTION_MAGIC_ACCELERATION = 11000 // encoder ticks per 100ms per 100ms
        public const val MAX_ENCODER_HEIGHT = 52.0 // inches
        public const val MIN_ENCODER_HEIGHT = 0.0 // inches
        // public const val MAX_ENCODER_TICKS = -41890
        // public const val MIN_ENCODER_TICKS = 0
        // pid
        public const val KP = 1.25
        public const val KI = 0.0
        public const val KD = 0.5
        public const val KF = 0.4
        public const val KF2 = 0.6
        public const val SECOND_STAGE_EPSILON = 10.0
    }

    object Vision {
        public var CAMERA_HEIGHT = 33.0
        public var CAMERA_VERTICAL_ANGLE = 0.0
        public var CAMERA_HORIZONTAL_ANGLE = 7.0
        public var HATCH_TARGET_HEIGHT = 29.0
        public var BALL_TARGET_HEIGHT = 36.0

        public var TARGET_DISTANCE = 24.0 // inches

        public var ACCEPTABLE_ANGLE_ERROR = 3.0 // degrees(?)
        public var ACCEPTABLE_DISTANCE_ERROR = 2.0 // inches (?)

        public var ANGLE_KP = 1.0
        public var ANGLE_KI = 0.0
        public var ANGLE_KD = 0.1
        public var ANGLE_KF = 0.0

        public var DISTANCE_KP = 0.35
        public var DISTANCE_KI = 0.0
        public var DISTANCE_KD = 0.0
        public var DISTANCE_KF = 0.0
    }

    object Auto {
        public const val LOOKAHEAD_DISTANCE: Double = 12.0
    }
}
