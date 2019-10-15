package org.team5419.frc2019offseason.input

import edu.wpi.first.wpilibj.Joystick

@SuppressWarnings("MagicNumber", "TooManyFunctions")

public class ButtonBoard(joystick: Joystick, buttonBoard: Joystick) {

    private val mJoystick: Joystick
    private val mButtonBoard: Joystick
    private var liftMode: Boolean = true

    init {
        mJoystick = joystick
        mButtonBoard = buttonBoard
    }

    public fun getManualInput() = mJoystick.getY()

    public fun getHatchHumanPlayer() = mButtonBoard.getRawButtonPressed(1 + 1)

    public fun getHatchLow() = mButtonBoard.getRawButtonPressed(3 + 1)

    public fun getHatchMid() = mButtonBoard.getRawButtonPressed(0 + 1)

    public fun getHatchHigh() = mButtonBoard.getRawButtonPressed(2 + 1)

    public fun getBallLow() = mButtonBoard.getRawButtonPressed(11 + 1)

    public fun getBallMid() = mButtonBoard.getRawButtonPressed(9 + 1)

    public fun getBallHigh() = mButtonBoard.getRawButtonPressed(8 + 1)

    public fun getBallHumanPlayer() = mButtonBoard.getRawButtonPressed(10 + 1)

    public fun getVaccumPressed() = mButtonBoard.getRawButtonPressed(6 + 1)

    // public fun getVaccumReleased() = mButtonBoard.getRawButtonReleased(8 + 1)

    public fun getFlipWrist() = mButtonBoard.getRawButtonPressed(4 + 1)

    // public fun getStowWrist() = mButtonBoard.getRawButton(1 + 1)

    public fun getValvePressed() = mButtonBoard.getRawButtonPressed(7 + 1)
}
