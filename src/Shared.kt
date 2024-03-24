import java.awt.GraphicsEnvironment
import java.awt.Rectangle

const val ballRadius = 50
val screenBounds: Rectangle = run {
    val device = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
    return@run device.defaultConfiguration.bounds
}

val exitBounds = Rectangle(25, 25, 50, 50)
val ballBounds = Rectangle(
    screenBounds.size.width/2 - ballRadius,
    screenBounds.size.height/2 - ballRadius,
    2*ballRadius,
    2*ballRadius
)
