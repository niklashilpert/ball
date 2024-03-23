import org.apache.batik.swing.JSVGCanvas
import java.awt.GraphicsEnvironment
import java.awt.Point
import java.awt.event.*
import java.awt.geom.AffineTransform
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities
import javax.swing.Timer
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class MouseHandler(private val frame: JFrame, private val ballPanel: JPanel) : MouseListener, MouseMotionListener {
    private var rotation = 0.0

    private val FPS = 60
    private val DELAY = 1000 / FPS

    private var relX = 0
    private var relY = 0
    private var veloX = 0.0
    private var veloY = 0.0
    private var stayOnGround = false

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
        veloX = 0.0
        veloY = 0.0
        stayOnGround = false

        lastFramePositions[0] = Point(frame.x, frame.y)
        for (i in lastFramePositions.indices) {
            lastFramePositions[i] = lastFramePositions[0]
        }

        posTimer = Timer(DELAY) {
            for (i in lastFramePositions.indices) {
                if (i == 0) continue
                lastFramePositions[i] = lastFramePositions[i-1]
            }
            lastFramePositions[0] = Point(frame.x, frame.y)
        }
        posTimer!!.start()
    }
    override fun mouseDragged(e: MouseEvent) {
        val x = max(min(e.xOnScreen - relX, bounds.x + bounds.width - frame.width), bounds.x)
        val y = max(min(e.yOnScreen - relY, bounds.y + bounds.height - frame.height), bounds.y)

        println("$x, $y")

        SwingUtilities.invokeLater {
            frame.setLocation(x, y)
        }
    }
    override fun mouseReleased(e: MouseEvent) {
        posTimer!!.stop()

        val latestPos = lastFramePositions[0]!!

        val middlePos = lastFramePositions[lastFramePositions.size / 2]!!
        val lastPos = lastFramePositions[lastFramePositions.size-1]!!
        val referencePos = Point((.5 * (middlePos.x + lastPos.x)).toInt(), (.5 * (middlePos.y + lastPos.y)).toInt())

        veloX = (latestPos.x - referencePos.x).toDouble()
        veloY = (latestPos.y - referencePos.y).toDouble()

        physicsTimer = Timer(DELAY) {
            step()
        }
        physicsTimer!!.start()
    }


    fun step() {
        SwingUtilities.invokeLater {
            var x = frame.x
            var y = frame.y

            x += Math.round(veloX).toInt()
            if (veloX != 0.0) {
                veloX = (veloX / abs(veloX)) * max(0.0, abs(veloX) * .99)
                //println("${vx / abs(vx)} | ${max(0.0, abs(vx) * .9)} -> $vx")
                if (veloX > -.05 && veloX < .05)
                    veloX = 0.0
            }
            val (newX, reboundedX) = wrapRebounding(bounds.width - frame.width, x - bounds.x)
            if (reboundedX != Rebound.None) {
                veloX *= -1
                x = newX + bounds.x
            }

            val oldY = y + Math.round(veloY).toInt()
            val (newY, reboundedY) = wrapRebounding(bounds.height - frame.height, oldY - bounds.y)

            if (reboundedY != Rebound.None) {
                println(reboundedY)
                if (reboundedY == Rebound.Bottom) {
                    if (veloY < 2) {
                        veloY = 0.0
                        stayOnGround = true
                    }
                }
                veloY *= -.7


                if (reboundedY == Rebound.Bottom) {
                    y = bounds.height - frame.height + bounds.y
                } else {
                    y += Math.round(veloY).toInt()
                }


            } else {
                if (!stayOnGround) veloY += 1.5
                y += Math.round(veloY).toInt()
                if (veloY > 0) {
                    //print("+")
                } else {
                    //print("-")
                }
            }
            frame.setLocation(x, y)

            if (veloX < .5 && veloX > -0.5) veloX = 0.0

            transform.rotate((veloX / (2 * Math.PI * 50) * 2 * Math.PI), 50.0, 50.0)
            ballPanel.repaint()

            //println("$veloX $veloY")
        }

    }



    override fun mouseClicked(p0: MouseEvent?) {}
    override fun mouseEntered(p0: MouseEvent?) {}
    override fun mouseExited(p0: MouseEvent?) {}
    override fun mouseMoved(p0: MouseEvent?) {}


}

fun wrapRebounding(bound: Int, value: Int): Pair<Int, Rebound> {
    var modulus = value % bound
    if (modulus < 0) modulus += bound

    val reboundState = if (value > bound) Rebound.Bottom else if (value < 0) Rebound.Top else Rebound.None
    return Pair(if (reboundState != Rebound.None) { bound - modulus } else { modulus }, reboundState)
}

enum class Rebound {
    None,
    Top,
    Bottom
}

