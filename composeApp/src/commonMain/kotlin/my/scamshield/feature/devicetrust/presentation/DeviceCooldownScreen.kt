package my.scamshield.feature.devicetrust.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import my.scamshield.core.presentation.theme.AlertRed
import my.scamshield.core.presentation.theme.SafeGreen
import my.scamshield.core.presentation.theme.WarnOrange
import my.scamshield.core.presentation.theme.WarnOrangeBg

class DeviceCooldownScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(WarnOrangeBg),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(WarnOrange)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White)
                    Column {
                        Text("Device trust required", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Layer 1 — Identity & Device Trust", style = MaterialTheme.typography.bodySmall, color = Color.White)
                    }
                }

                Column(modifier = Modifier.padding(20.dp)) {
                    Text("New device detected", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "This device is using your account for the first time. For your safety:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    )
                    Spacer(Modifier.height(16.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            InfoRow("Device", "Samsung Galaxy S24")
                            InfoRow("Location", "Johor Bahru  (new)")
                            InfoRow("First seen", "2 minutes ago")
                            InfoRow("Cooldown ends", "Tomorrow at 4:52 PM")
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Text("Paused for 24 hours:", style = MaterialTheme.typography.labelLarge, color = AlertRed, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    PausedRow("Send money")
                    PausedRow("Rebind wallet to this device")
                    PausedRow("Link wallet to 3rd-party apps")

                    Spacer(Modifier.height(12.dp))
                    Text("Still available:", style = MaterialTheme.typography.labelLarge, color = SafeGreen, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    AvailableRow("View balance and history")
                    AvailableRow("Receive money")

                    Spacer(Modifier.weight(1f))

                    Button(
                        onClick = { navigator.pop() },
                        colors = ButtonDefaults.buttonColors(containerColor = WarnOrange),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                    ) { Text("Verify with video call", style = MaterialTheme.typography.labelLarge) }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { navigator.pop() },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                    ) { Text("This isn't me — secure account", color = AlertRed) }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PausedRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("✗", color = AlertRed, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text("  $text", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun AvailableRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("✓", color = SafeGreen, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text("  $text", style = MaterialTheme.typography.bodyMedium)
    }
}
