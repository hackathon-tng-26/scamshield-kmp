package my.scamshield.feature.auth.domain.repository

import kotlinx.coroutines.flow.StateFlow
import my.scamshield.feature.auth.domain.model.DemoUser

interface SessionRepository {
    val currentUser: StateFlow<DemoUser?>
    fun isLoggedIn(): Boolean
    fun signIn(user: DemoUser)
    fun signOut()
}
