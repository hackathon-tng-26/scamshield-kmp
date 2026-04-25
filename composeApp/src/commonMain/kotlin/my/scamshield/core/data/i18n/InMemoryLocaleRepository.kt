package my.scamshield.core.data.i18n

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import my.scamshield.core.domain.i18n.AppLocale
import my.scamshield.core.domain.i18n.LocaleRepository

class InMemoryLocaleRepository(initial: AppLocale = AppLocale.EN) : LocaleRepository {
    private val _locale = MutableStateFlow(initial)
    override val locale: StateFlow<AppLocale> = _locale.asStateFlow()

    override fun setLocale(value: AppLocale) {
        _locale.value = value
    }

    override fun toggle() {
        _locale.value = if (_locale.value == AppLocale.BM) AppLocale.EN else AppLocale.BM
    }
}
