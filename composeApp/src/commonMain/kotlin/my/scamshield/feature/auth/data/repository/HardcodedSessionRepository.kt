package my.scamshield.feature.auth.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import my.scamshield.feature.auth.domain.model.DemoUser
import my.scamshield.feature.auth.domain.repository.SessionRepository

class HardcodedSessionRepository : SessionRepository {
    private val _currentUser = MutableStateFlow<DemoUser?>(null)
    override val currentUser: StateFlow<DemoUser?> = _currentUser.asStateFlow()

    override fun isLoggedIn(): Boolean = _currentUser.value != null

    override fun signIn(user: DemoUser) {
        _currentUser.value = user
    }

    override fun signOut() {
        _currentUser.value = null
    }
}
