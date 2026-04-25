package my.scamshield.core.presentation.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp

fun Modifier.animatedTraceBorder(
    progress: Float,
    color: Color,
    width: Dp,
    shape: Shape,
): Modifier = drawBehind {
    val clamped = progress.coerceIn(0f, 1f)
    if (clamped <= 0f) return@drawBehind
    val outline = shape.createOutline(size, layoutDirection, this)
    val perimeter = Path().apply {
        when (outline) {
            is Outline.Rounded -> addRoundRect(outline.roundRect)
            is Outline.Rectangle -> addRect(outline.rect)
            is Outline.Generic -> addPath(outline.path)
        }
    }
    val measure = PathMeasure().apply { setPath(perimeter, false) }
    val length = measure.length
    if (length <= 0f) return@drawBehind
    val segment = Path()
    measure.getSegment(0f, length * clamped, segment, true)
    drawPath(
        path = segment,
        color = color,
        style = Stroke(width = width.toPx(), cap = StrokeCap.Round),
    )
}
