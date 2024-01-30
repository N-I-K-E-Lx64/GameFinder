package de.hive.gamefinder.utils

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import kotlin.math.roundToInt

/**
 * Google Accompanist Two Pane Layout
 * Availability: https://github.com/google/accompanist/blob/main/adaptive/src/main/java/com/google/accompanist/adaptive/TwoPane.kt
 */
@Composable
fun TwoPane(
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
    strategy: TwoPaneStrategy,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    Layout(
        modifier = modifier.wrapContentSize(),
        content = {
            Box(modifier = Modifier.layoutId("first")) {
                first()
            }
            Box(modifier = Modifier.layoutId("second")) {
                second()
            }
        },
        measurePolicy = { measurables, constraints ->
            lateinit var firstMeasurable: Measurable
            lateinit var secondMeasurable: Measurable
            measurables.forEach {
                when (it.layoutId) {
                    "first" -> firstMeasurable = it
                    "second" -> secondMeasurable = it
                }
            }

            layout(constraints.maxWidth, constraints.maxHeight) {
                val splitResult = strategy.calculateSplitResult(
                    density = density,
                    layoutDirection = layoutDirection,
                    layoutCoordinates = coordinates ?: return@layout
                )

                val gapOrientation = splitResult.gapOrientation
                val gapBounds = splitResult.gapBounds

                val gapLeft = constraints.constrainWidth(gapBounds.left.roundToInt())
                val gapRight = constraints.constrainWidth(gapBounds.right.roundToInt())
                val gapTop = constraints.constrainHeight(gapBounds.top.roundToInt())
                val gapBottom = constraints.constrainHeight(gapBounds.bottom.roundToInt())
                val firstConstraints =
                    if (gapOrientation == Orientation.Vertical) {
                        val width = when (layoutDirection) {
                            LayoutDirection.Ltr -> gapLeft
                            LayoutDirection.Rtl -> constraints.maxWidth - gapRight
                        }

                        constraints.copy(minWidth = width, maxWidth = width)
                    } else {
                        constraints.copy(minHeight = gapTop, maxHeight = gapTop)
                    }
                val secondConstraints =
                    if (gapOrientation == Orientation.Vertical) {
                        val width = when (layoutDirection) {
                            LayoutDirection.Ltr -> constraints.maxWidth - gapRight
                            LayoutDirection.Rtl -> gapLeft
                        }
                        constraints.copy(minWidth = width, maxWidth = width)
                    } else {
                        val height = constraints.maxHeight - gapBottom
                        constraints.copy(minHeight = height, maxHeight = height)
                    }
                val firstPlaceable = firstMeasurable.measure(constraints.constrain(firstConstraints))
                val secondPlaceable = secondMeasurable.measure(constraints.constrain(secondConstraints))

                firstPlaceable.placeRelative(0, 0)
                val detailOffsetX =
                    if (gapOrientation == Orientation.Vertical) {
                        constraints.maxWidth - secondPlaceable.width
                    } else {
                        0
                    }
                val detailOffsetY =
                    if (gapOrientation == Orientation.Vertical) {
                        0
                    } else {
                        constraints.maxHeight - secondPlaceable.height
                    }
                secondPlaceable.placeRelative(detailOffsetX, detailOffsetY)
            }
        }
    )
}

fun HorizontalTwoPaneStrategy(
    splitFraction: Float,
    gapWidth: Dp = 0.dp
): TwoPaneStrategy = FractionHorizontalTwoPaneStrategy(
    splitFraction = splitFraction,
    gapWidth = gapWidth
)

fun FractionHorizontalTwoPaneStrategy(
    splitFraction: Float,
    gapWidth: Dp = 0.dp,
): TwoPaneStrategy = TwoPaneStrategy { density, layoutDirection, layoutCoordinates ->
    val splitX = layoutCoordinates.size.width * when (layoutDirection) {
        LayoutDirection.Ltr -> splitFraction
        LayoutDirection.Rtl -> 1 - splitFraction
    }
    val splitWidthPixel = with(density) { gapWidth.toPx() }

    SplitResult(
        gapOrientation = Orientation.Vertical,
        gapBounds = Rect(
            left = splitX - splitWidthPixel / 2f,
            top = 0f,
            right = splitX + splitWidthPixel / 2f,
            bottom = layoutCoordinates.size.height.toFloat(),
        )
    )
}

fun interface TwoPaneStrategy {
    public fun calculateSplitResult(
        density: Density,
        layoutDirection: LayoutDirection,
        layoutCoordinates: LayoutCoordinates
    ): SplitResult
}

class SplitResult(
    val gapOrientation: Orientation,
    val gapBounds: Rect
)