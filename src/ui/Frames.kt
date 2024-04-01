package ui

import BallType
import exitBounds
import screenBounds
import java.awt.*
import java.awt.geom.AffineTransform
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JPanel

class BallFrame(location: Point, type: BallType) : IconFrame(
    "Ball",
    location,
    Dimension((type.radius * 2).toInt(), (type.radius * 2).toInt()),
    type.iconName
)

class ExitFrame : IconFrame("X", exitBounds.location, exitBounds.size, "X")

open class IconFrame(title: String, framePos: Point, frameSize: Dimension, iconName: String) : JFrame(title) {
    class DrawingPanel(size: Dimension, private val iconName: String, private val transform: AffineTransform) :
        JPanel() {
        init {
            preferredSize = size
            background = Color(0, 0, 0, 0)
        }

        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            val g2 = g as Graphics2D
            g2.addRenderingHints(RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON))
            g2.drawImage(ImageIO.read((javaClass.getResourceAsStream("../images/${iconName}.png"))), transform, null)
        }
    }

    val transform: AffineTransform = AffineTransform.getRotateInstance(0.0)
    private val drawingPanel = DrawingPanel(frameSize.size, iconName, transform)

    init {
        location = Point(screenBounds.x + framePos.x, screenBounds.y + framePos.y)
        isUndecorated = true
        background = Color(0, 0, 0, 0)
        isAlwaysOnTop = true

        this.add(drawingPanel)
        this.pack()
    }

    fun redraw() {
        drawingPanel.repaint()
    }
}

