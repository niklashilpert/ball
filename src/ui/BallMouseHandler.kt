package ui

import DimensionProperty
import ballRadius
import screenBounds
import update
import updateWithGravity
import java.awt.Insets
import java.awt.Point
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import javax.swing.SwingUtilities
import javax.swing.Timer
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.system.exitProcess

const val ABSORPTION_FROM_AIR_RESISTANCE = 0.008

const val ABSORPTION_ON_BOUNCE = 0.2

const val GRAVITY = 4000.0

const val FPS = 60
const val TIMER_MS = 1000 / FPS
const val TIMER_S = TIMER_MS.toDouble() / 1000.0

class BallMouseHandler(
    private val ballFrame: BallFrame,
    private val exitFrame: ExitFrame
) : MouseListener, MouseMotionListener {

    private val leftBorder = screenBounds.x
    private val rightBorder = screenBounds.x + screenBounds.width - ballFrame.width
    private val topBorder = screenBounds.y
    private val bottomBorder = screenBounds.y + screenBounds.height - ballFrame.height
    private val bounds = Insets(topBorder, leftBorder, bottomBorder, rightBorder)

    // The absolute coordinates of the ball frame
    private var x = DimensionProperty(ballFrame.x.toDouble(), 0.0)
    private var y = DimensionProperty(ballFrame.y.toDouble(), 0.0)

    // The mouse position relative to the top left corner of the ball frame
    private var relX = 0
    private var relY = 0

    private var isDragging = false

    // Keeps track of the ballFrame positions of the last 500ms
    private var lastFramePositions = Array(FPS / 2) { Point(0, 0) }

    private var physicsTimer = Timer(TIMER_MS) { tick() }
    private var posTimer = Timer(TIMER_MS) {
        for (i in 1..<lastFramePositions.size) {
            lastFramePositions[i] = lastFramePositions[i - 1]
        }
        lastFramePositions[0] = Point(ballFrame.x, ballFrame.y)
    }

    init {
        physicsTimer.start()
    }

    override fun mousePressed(e: MouseEvent) {
        val center = Point(50, 50)
        if (e.point.distance(center) < 50) {
            isDragging = true

            physicsTimer.stop()

            if (e.button == MouseEvent.BUTTON3) {
                SwingUtilities.invokeLater {
                    exitFrame.isVisible = true
                }
            }

            lastFramePositions = Array(30) { Point(ballFrame.x, ballFrame.y) }

            relX = e.x
            relY = e.y
            x.velocity = 0.0
            y.velocity = 0.0

            posTimer.start()
        }
    }

    override fun mouseDragged(e: MouseEvent) {
        if (!isDragging) return

        x.value = max(min(e.xOnScreen - relX, bounds.right), bounds.left).toDouble()
        y.value = max(min(e.yOnScreen - relY, bounds.bottom), bounds.top).toDouble()

        SwingUtilities.invokeLater {
            ballFrame.setLocation(x.value.toInt(), y.value.toInt())
        }
    }

    override fun mouseReleased(e: MouseEvent) {
        if (!isDragging) return

        isDragging = false

        posTimer.stop()

        if (e.button == MouseEvent.BUTTON3) {
            if (e.xOnScreen >= exitFrame.x && e.xOnScreen <= exitFrame.x + exitFrame.width &&
                e.yOnScreen >= exitFrame.y && e.yOnScreen <= exitFrame.y + exitFrame.height
            ) {
                exitFrame.dispose()
                ballFrame.dispose()
                exitProcess(0)
            }

            SwingUtilities.invokeLater {
                exitFrame.isVisible = false
            }
        }

        val latestPos = lastFramePositions[0]
        val middlePos = lastFramePositions[lastFramePositions.size / 2]
        val lastPos = lastFramePositions[lastFramePositions.size - 1]
        val referencePos = Point((.5 * (middlePos.x + lastPos.x)).toInt(), (.5 * (middlePos.y + lastPos.y)).toInt())

        x.velocity = (latestPos.x - referencePos.x).toDouble() * 50
        y.velocity = (latestPos.y - referencePos.y).toDouble() * 50

        x.value = latestPos.x.toDouble()
        y.value = latestPos.y.toDouble()

        physicsTimer.start()
    }

    private fun tick() {
        SwingUtilities.invokeLater {
            x.update(TIMER_S, bounds.left, bounds.right, ABSORPTION_ON_BOUNCE)

            val onGround = y.updateWithGravity(TIMER_S, GRAVITY, bounds.top, bounds.bottom, ABSORPTION_ON_BOUNCE)
            if (onGround) {
                x.velocity *= 1 - ABSORPTION_FROM_AIR_RESISTANCE
            }
            if (x.velocity in -5.0..5.0) {
                x.velocity = 0.0
            }

            ballFrame.location = Point(x.value.roundToInt(), y.value.roundToInt())
            rotateBall(x.velocity)
        }
    }

    private fun rotateBall(rotationDistance: Double) {
        ballFrame.transform.rotate(
            (rotationDistance / (50 * 2 * Math.PI * ballRadius) * 2 * Math.PI),
            ballRadius.toDouble(),
            ballRadius.toDouble()
        )
        ballFrame.redraw()
    }

    override fun mouseClicked(e: MouseEvent) {}
    override fun mouseEntered(e: MouseEvent) {}
    override fun mouseExited(e: MouseEvent) {}
    override fun mouseMoved(e: MouseEvent) {}
}
