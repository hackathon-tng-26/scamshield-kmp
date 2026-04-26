package my.scamshield.core.presentation.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import my.scamshield.core.domain.i18n.AppLocale
import my.scamshield.core.domain.i18n.LocaleRepository
import org.koin.compose.koinInject

val LocalAppLocale: ProvidableCompositionLocal<AppLocale> =
    compositionLocalOf { AppLocale.EN }

@Composable
fun ProvideLocale(content: @Composable () -> Unit) {
    val repo: LocaleRepository = koinInject()
    val locale by repo.locale.collectAsStateWithLifecycle()
    CompositionLocalProvider(LocalAppLocale provides locale) {
        content()
    }
}

@Composable
fun localeText(bm: String, en: String): String =
    if (LocalAppLocale.current == AppLocale.BM) bm else en
