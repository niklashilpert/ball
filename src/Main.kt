import ui.BallMouseHandler
import ui.ExitFrame
import java.awt.Point
import javax.swing.SwingUtilities


val ballFrames = arrayListOf(
    //BallType.Basketball.newFrameAt(Point(25, 25)),
    BallType.Basketball.newFrameAt(Point(0, 0)),
)

val exitFrame = ExitFrame()

fun main() {
    for (bf in ballFrames) {
        val handler = BallMouseHandler(bf, exitFrame)
        bf.addMouseListener(handler)
        bf.addMouseMotionListener(handler)
        SwingUtilities.invokeLater {
            bf.isVisible = true
        }
    }
}
