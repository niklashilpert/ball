import kotlin.math.abs
import kotlin.math.sqrt

data class DimensionProperty(var value: Double, var velocity: Double)

fun DimensionProperty.updateWithGravity(
    tickTime: Double,
    gravity: Double,
    topBound: Int,
    bottomBound: Int,
    absorptionFactor: Double
): Boolean {
    return stepBounceRecursively(tickTime, tickTime, gravity, topBound, bottomBound, absorptionFactor)
}

fun DimensionProperty.update(tickTime: Double, topBound: Int, bottomBound: Int, absorptionFactor: Double) {
    updateWithGravity(tickTime, 0.0, topBound, bottomBound, absorptionFactor)
}

private fun DimensionProperty.stepBounceRecursively(
    tickTime: Double,
    remainingTime: Double,
    gravity: Double,
    topBound: Int,
    bottomBound: Int,
    absorptionFactor: Double
): Boolean {
    val distanceToBottom = bottomBound - value
    val distanceToTop = topBound - value

    if (distanceToBottom < getTravelDistanceByTime(gravity, velocity, tickTime)) {

        val tb = getTimeTravelled(tickTime, gravity, velocity, distanceToBottom)
        velocity = -(gravity * tb + velocity) * (1 - absorptionFactor)
        value = bottomBound.toDouble()

        if (abs(velocity) < 1.0) {
            velocity = 0.0
            return true
        }

        return stepBounceRecursively(tickTime, remainingTime - tb, gravity, topBound, bottomBound, absorptionFactor)


    } else if (distanceToTop > getTravelDistanceByTime(gravity, velocity, tickTime)) {
        val tb = getTimeTravelled(tickTime, gravity, velocity, distanceToTop)
        velocity = -(gravity * tb + velocity) * (1 - absorptionFactor)
        value = topBound.toDouble()

        return stepBounceRecursively(tickTime, remainingTime - tb, gravity, topBound, bottomBound, absorptionFactor)
    } else {
        value += getTravelDistanceByTime(gravity, velocity, remainingTime)
        velocity += gravity * remainingTime
        return false
    }
}

private fun getTimeTravelled(tickTime: Double, a: Double, v0: Double, s: Double): Double {
    if (a != 0.0) {
        val t1 = (-v0 + sqrt(v0 * v0 + 2 * a * s)) / a
        val t2 = (-v0 - sqrt(v0 * v0 + 2 * a * s)) / a
        return if (t1 in 0.0..tickTime) {
            t1
        } else if (t2 in 0.0..tickTime) {
            t2
        } else {
            Double.NaN
        }
    } else if (v0 != 0.0) {
        return s / v0
    } else {
        return s
    }
}

private fun getTravelDistanceByTime(a: Double, v0: Double, t: Double): Double {
    return 0.5 * a * t * t + v0 * t
}
