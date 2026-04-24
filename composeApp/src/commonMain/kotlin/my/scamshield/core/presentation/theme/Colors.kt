package my.scamshield.core.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val TngRed = Color(0xFFEE2D37)
val TngRedDark = Color(0xFFBE1D25)
val SafeGreen = Color(0xFF2E7D32)
val SafeGreenBg = Color(0xFFE8F5E9)
val WarnOrange = Color(0xFFE36C0A)
val WarnOrangeBg = Color(0xFFFFF4E5)
val AlertRed = Color(0xFFC00000)
val AlertRedBg = Color(0xFFFEEBED)
val NeutralText = Color(0xFF1A1A1A)
val NeutralMuted = Color(0xFF707070)
val SurfaceBg = Color(0xFFF5F6F8)
val BorderGray = Color(0xFFBFBFBF)

val ScamShieldLightColorScheme = lightColorScheme(
    primary = TngRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD8),
    onPrimaryContainer = Color(0xFF410001),
    secondary = SafeGreen,
    onSecondary = Color.White,
    secondaryContainer = SafeGreenBg,
    onSecondaryContainer = Color(0xFF0D2112),
    tertiary = WarnOrange,
    onTertiary = Color.White,
    tertiaryContainer = WarnOrangeBg,
    onTertiaryContainer = Color(0xFF331500),
    background = SurfaceBg,
    onBackground = NeutralText,
    surface = Color.White,
    onSurface = NeutralText,
    surfaceVariant = SurfaceBg,
    onSurfaceVariant = NeutralMuted,
    outline = BorderGray,
    error = AlertRed,
    onError = Color.White,
    errorContainer = AlertRedBg,
    onErrorContainer = Color(0xFF410002),
)

val ScamShieldDarkColorScheme = darkColorScheme(
    primary = TngRed,
    onPrimary = Color.White,
    secondary = SafeGreen,
    onSecondary = Color.White,
    tertiary = WarnOrange,
    onTertiary = Color.White,
    error = AlertRed,
    onError = Color.White,
    background = Color(0xFF121212),
    surface = Color(0xFF1C1C1C),
    onBackground = Color.White,
    onSurface = Color.White,
)
