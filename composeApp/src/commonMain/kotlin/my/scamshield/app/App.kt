package my.scamshield.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import my.scamshield.core.presentation.theme.ScamShieldTheme
import my.scamshield.feature.login.presentation.LoginScreen

@Composable
fun App() {
    ScamShieldTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Navigator(screen = LoginScreen()) { navigator ->
                    SlideTransition(navigator = navigator)
                }
            }
        }
    }
}
