import java.awt.GraphicsEnvironment
import java.awt.Point
import java.awt.event.*
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.Timer
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class MouseHandler(val frame: JFrame) : MouseListener, MouseMotionListener {
    private val FPS = 60
    private val DELAY = 1000 / FPS

    private var relX = 0
    private var relY = 0

    private var physicsTimer: Timer? = null
    private var posTimer: Timer? = null

    private lateinit var lastFramePositions: Array<Point?>
    private var positionCount = 0

    private val device = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
    private val bounds = device.defaultConfiguration.bounds

    override fun mousePressed(e: MouseEvent) {
        if (physicsTimer != null) physicsTimer!!.stop() // Cancels the last physics sim

        lastFramePositions = Array(30) { null } // Resets the position record
        positionCount = 0

        relX = e.x
        relY = e.y

        lastFramePositions[0] = Point(frame.x, frame.y)
        for (i in lastFramePositions.indices) {
            lastFramePositions[i] = lastFramePositions[0]
        }

        posTimer = Timer(DELAY) {
            for (i in lastFramePositions.indices) {
                if (i == 0) continue
                //print("Replacing ${lastFramePositions[i]} with ${lastFramePositions[i-1]}")
                lastFramePositions[i] = lastFramePositions[i-1]
            }
            lastFramePositions[0] = Point(frame.x, frame.y)
        }
        posTimer!!.start()
    }
    override fun mouseDragged(e: MouseEvent) {
        //veloX = e.xOnScreen - (x + relX)
        //veloY = e.yOnScreen - (y + relY)
        val x = max(min(e.xOnScreen - relX, bounds.x + bounds.width - frame.width), bounds.x)
        val y = max(min(e.yOnScreen - relY, bounds.y + bounds.height - frame.height), bounds.y)

        println("$x, $y")

        SwingUtilities.invokeLater {
            frame.setLocation(x, y)
        }
    }
    override fun mouseReleased(e: MouseEvent) {
        posTimer!!.stop()

        //var croppedPositionCount = min(positionCount, lastFramePositions.size)

        val latestPos = lastFramePositions[0]!!

        val middlePos = lastFramePositions[lastFramePositions.size / 2]!!
        val lastPos = lastFramePositions[lastFramePositions.size-1]!!
        val referencePos = Point((.5 * (middlePos.x + lastPos.x)).toInt(), (.5 * (middlePos.y + lastPos.y)).toInt())

        var veloX = (latestPos.x - referencePos.x).toDouble()
        var veloY = (latestPos.y - referencePos.y).toDouble()
        //println()
        //println("$latestPos $middlePos, $lastPos, $referencePos")

        //println("$veloX, $veloY")



        physicsTimer = Timer(DELAY) {
            SwingUtilities.invokeLater {
                var x = frame.x
                var y = frame.y

                x += Math.round(veloX).toInt()
                if (veloX != 0.0) {
                    veloX = (veloX / abs(veloX)) * max(0.0, abs(veloX) * .9)
                    //println("${vx / abs(vx)} | ${max(0.0, abs(vx) * .9)} -> $vx")
                    if (veloX > -.05 && veloX < .05)
                        veloX = 0.0
                }
                val (newX, reboundedX) = wrapRebounding(bounds.width - frame.width, x - bounds.x)
                if (reboundedX) {
                    veloX *= -1
                    x = newX + bounds.x
                }

                y += Math.round(veloY).toInt()
                if (veloY != 0.0) {
                    veloY = (veloY / abs(veloY)) * max(0.0, abs(veloY) * .9)
                    if (veloY > -.05 && veloY < .05)
                        veloY = 0.0
                }
                val (newY, reboundedY) = wrapRebounding(bounds.height - frame.height, y - bounds.y)
                if (reboundedY) {
                    veloY *= -1
                    y = newY + bounds.y
                }


                //println("Current position: $x, $y")
                frame.setLocation(x, y)
            }

        }
        physicsTimer!!.start()
    }



    override fun mouseClicked(p0: MouseEvent?) {}
    override fun mouseEntered(p0: MouseEvent?) {}
    override fun mouseExited(p0: MouseEvent?) {}
    override fun mouseMoved(p0: MouseEvent?) {}
}

fun wrapRebounding(bound: Int, value: Int): Pair<Int, Boolean> {
    var modulus = value % bound
    if (modulus < 0) modulus += bound
    val rebounded = value > bound || value < 0
    return Pair(if (rebounded) { bound - modulus } else { modulus }, rebounded)
}
