import ui.BallFrame
import ui.BallMouseHandler
import ui.ExitFrame
import javax.swing.SwingUtilities

val exitFrame = ExitFrame()
val ballFrame = BallFrame().apply {
    val handler = BallMouseHandler(this, exitFrame)
    addMouseListener(handler)
    addMouseMotionListener(handler)
}

fun main() {
    SwingUtilities.invokeLater {
        ballFrame.isVisible = true
    }
}

