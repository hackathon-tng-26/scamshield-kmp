package my.scamshield.core.domain.i18n

import kotlinx.coroutines.flow.StateFlow

interface LocaleRepository {
    val locale: StateFlow<AppLocale>
    fun setLocale(value: AppLocale)
    fun toggle()
}
