package ui

import ballRadius
import screenBounds
import java.awt.Point
import java.awt.event.*
import javax.swing.SwingUtilities
import javax.swing.Timer
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.system.exitProcess

const val GRAVITY = 1.5

const val BOUNCE_ABSORB_FACTOR_X = 0.2
const val BOUNCE_ABSORB_FACTOR_Y = 0.3

const val VEL_X_RESET_THRESHOLD = 0.5
const val VEL_Y_RESET_THRESHOLD = 1

const val FPS = 60
const val TIMER_DELAY = 1000 / FPS

class BallMouseHandler(private val ballFrame: BallFrame, private val exitFrame: ExitFrame) : MouseListener, MouseMotionListener {
    // The absolute coordinates of the ball frame
    private var absoluteX = ballFrame.x
    private var absoluteY = ballFrame.y

    // The mouse position relative to the top left corner of the ball frame
    private var relX = 0
    private var relY = 0

    // The velocity of the ball frame
    private var velX = 0.0
    private var velY = 0.0

    private var isDragging = false
    private var applyGravity = true

    // Keeps track of the ballFrame positions of the last 500ms
    private var lastFramePositions = Array(FPS/2) { Point(0, 0) }

    private var physicsTimer = Timer(TIMER_DELAY) { tick() }
    private var posTimer = Timer(TIMER_DELAY) {
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
        println("$center, ${e.point}")
        if (e.point.distance(center) < 50) {
            println("Is")
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
            velX = 0.0
            velY = 0.0
            applyGravity = true

            posTimer.start()
        }
    }

    override fun mouseDragged(e: MouseEvent) {
        if (!isDragging) return

        absoluteX = max(min(e.xOnScreen - relX, screenBounds.x + screenBounds.width - ballFrame.width), screenBounds.x)
        absoluteY = max(min(e.yOnScreen - relY, screenBounds.y + screenBounds.height - ballFrame.height), screenBounds.y)

        SwingUtilities.invokeLater {
            ballFrame.setLocation(absoluteX, absoluteY)
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

        velX = (latestPos.x - referencePos.x).toDouble()
        velY = (latestPos.y - referencePos.y).toDouble()

        absoluteX = latestPos.x
        absoluteY = latestPos.y

        physicsTimer.start()
    }

    private fun tick() {
        SwingUtilities.invokeLater {
            absoluteX = updateX(absoluteX)
            absoluteY = updateY(absoluteY)
            ballFrame.location = Point(absoluteX, absoluteY)
            rotateBall(velX)
        }
    }

    private fun updateX(oldX: Int): Int {
        var x = oldX + Math.round(velX).toInt()

        // Updates velocity
        if (velX != 0.0) {
            velX = (velX / abs(velX)) * max(0.0, abs(velX) * .99)
        }

        // Clips the new coordinates into the default screen bounds and inverts the x velocity if the ball bounces
        if (x < screenBounds.x) {
            x = screenBounds.x
            velX *= -(1 - BOUNCE_ABSORB_FACTOR_X)
        } else if (x > screenBounds.x + screenBounds.width - ballFrame.width) {
            x = screenBounds.x + screenBounds.width - ballFrame.width
            velX *= -(1 - BOUNCE_ABSORB_FACTOR_X)
        }

        // Stops the ball if the velocity becomes too small
        if (velX < VEL_X_RESET_THRESHOLD && velX > -VEL_X_RESET_THRESHOLD) velX = 0.0

        return x
    }

    private fun updateY(oldY: Int): Int {
        var y = oldY + Math.round(velY).toInt()

        if (y < screenBounds.y) {
            y = screenBounds.y
            velY *= -(1 - BOUNCE_ABSORB_FACTOR_Y) * min(abs(velY) / 7, 1.0)
        } else if (y > screenBounds.y + screenBounds.height - ballFrame.height) {
            y = screenBounds.y + screenBounds.height - ballFrame.height
            velY *= -(1 - BOUNCE_ABSORB_FACTOR_Y) * min(abs(velY) / 7, 1.0)

            // If the ball bounces off the ground with very little velocity,
            // no gravity will be added to prevent infinite bounces
            if (velY < VEL_Y_RESET_THRESHOLD && velY > -VEL_Y_RESET_THRESHOLD) {
                velY = 0.0
                applyGravity = false
            }
        } else if (applyGravity) {
            velY += GRAVITY
        }

        return y
    }

    private fun rotateBall(rotationDistance: Double) {
        ballFrame.transform.rotate((rotationDistance / (2 * Math.PI * ballRadius) * 2 * Math.PI), ballRadius.toDouble(), ballRadius.toDouble())
        ballFrame.redraw()
    }


    override fun mouseClicked(e: MouseEvent) {}
    override fun mouseEntered(e: MouseEvent) {}
    override fun mouseExited(e: MouseEvent) {}
    override fun mouseMoved(e: MouseEvent) {}
}
