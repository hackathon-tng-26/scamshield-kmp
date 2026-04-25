package my.scamshield.core.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import my.scamshield.core.domain.i18n.AppLocale
import my.scamshield.core.domain.i18n.LocaleRepository
import my.scamshield.core.presentation.i18n.LocalAppLocale
import my.scamshield.core.presentation.theme.AlertRed
import org.koin.compose.koinInject

@Composable
fun LocaleToggle(
    modifier: Modifier = Modifier,
    onDark: Boolean = false,
) {
    val repo: LocaleRepository = koinInject()
    val current = LocalAppLocale.current
    val borderColor = if (onDark) {
        Color.White.copy(alpha = 0.7f)
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(20.dp))
            .padding(2.dp),
    ) {
        Segment(
            label = "BM",
            selected = current == AppLocale.BM,
            onClick = { repo.setLocale(AppLocale.BM) },
            onDark = onDark,
        )
        Segment(
            label = "EN",
            selected = current == AppLocale.EN,
            onClick = { repo.setLocale(AppLocale.EN) },
            onDark = onDark,
        )
    }
}

@Composable
private fun Segment(label: String, selected: Boolean, onClick: () -> Unit, onDark: Boolean) {
    val bg = when {
        selected && onDark -> Color.White
        selected -> MaterialTheme.colorScheme.primary
        else -> Color.Transparent
    }
    val fg = when {
        selected && onDark -> AlertRed
        selected -> MaterialTheme.colorScheme.onPrimary
        onDark -> Color.White.copy(alpha = 0.85f)
        else -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
    }
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = fg,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}
