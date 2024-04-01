import java.awt.GraphicsEnvironment
import java.awt.Rectangle

const val ballRadius = 50
val screenBounds: Rectangle = run {
    val device = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
    return@run device.defaultConfiguration.bounds
}

val exitBounds = Rectangle(25, 25, 50, 50)
