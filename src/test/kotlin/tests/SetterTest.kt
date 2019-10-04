package tests

import org.junit.jupiter.api.Test

public class SetterTest {
    var mBrakeMode: Boolean = true
    set(value) {
        if (value == field) return
        if (value) {
            println("set brake mode")
        } else {
            println("unset brake mode")
        }
        field = value
    }
    @Test
    fun set_test() {
        println("here")
        mBrakeMode = false
    }
}
