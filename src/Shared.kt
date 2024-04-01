import java.awt.DisplayMode
import java.awt.GraphicsEnvironment
import java.awt.Rectangle

const val ballRadius = 50
val defaultScreen = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
val screenBounds: Rectangle = defaultScreen.defaultConfiguration.bounds

val exitBounds = Rectangle(25, 25, 50, 50)
