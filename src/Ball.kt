import ui.BallFrame
import java.awt.Point

enum class BallType(val mass: Double, val iconName: String, val radius: Double) {
    Basketball(1.0, "Basketball", 50.0);

    fun newFrameAt(location: Point): BallFrame {
        return BallFrame(location, this)
    }
}
