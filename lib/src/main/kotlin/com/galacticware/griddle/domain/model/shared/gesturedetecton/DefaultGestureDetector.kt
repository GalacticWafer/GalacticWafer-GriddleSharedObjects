package com.galacticware.griddle.domain.model.shared.gesturedetecton

import com.galacticware.griddle.domain.model.shared.Direction
import com.galacticware.griddle.domain.model.shared.Point
import com.galacticware.griddle.domain.model.shared.RotationDirection
import com.galacticware.griddle.domain.model.shared.GenericGestureType
import kotlin.math.abs
import kotlin.math.hypot

/**
 * Warning: This gesture detector does not detect Boomerang, (Drag-and-return) or Circle gestures.
 * In order to use this gesture detector, specify the library for it in your `build.gradle.kts`
 * file.
 */
object DefaultGestureDetector : IGestureDetector {

    override fun reset() {}

    override fun determineGesture(
        minimumHoldTime: Int,
        minimumDragLength: Int,
        duration: Long,
        touchPoints: MutableList<Point>,
    ): Triple<GenericGestureType, Direction?, RotationDirection?> {
        if(touchPoints.isEmpty()) throw Exception()
        val pair = touchPoints.last() to touchPoints.first()
        val (diffX, diffY, distance) = pair.let { (b, a) ->
            val diffX = b.x - a.x
            val diffY = b.y - a.y
            Triple(diffX, diffY, hypot(diffX, diffY))
        }
        if(touchPoints.size < 2 || distance < minimumDragLength) {
            return Triple(
                if(duration > minimumHoldTime) GenericGestureType.HOLD else GenericGestureType.CLICK,
                null,
                null
            )
        }
        return Triple(
            GenericGestureType.SWIPE,
            if(abs(diffX) > abs(diffY/2) && abs(diffY) > abs(diffX/2)) {
                if(diffX > 0 && diffY > 0) {
                    Direction.SOUTHEAST
                } else if(diffX > 0 && diffY < 0) {
                    Direction.NORTHEAST
                } else if(diffX < 0 && diffY > 0) {
                    Direction.SOUTHWEST
                } else {
                    Direction.NORTHWEST
                }
            } else {
                if (abs(diffX) > abs(diffY)) {
                    if (diffX > 0) {
                        Direction.EAST
                    } else {
                        Direction.WEST
                    }
                } else {
                    if (diffY > 0) {
                        Direction.SOUTH
                    } else {
                        Direction.NORTH
                    }
                }
            }, null)
    }
}