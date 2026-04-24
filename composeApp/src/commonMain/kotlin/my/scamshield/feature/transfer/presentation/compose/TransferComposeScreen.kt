package my.scamshield.feature.transfer.presentation.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import my.scamshield.feature.transfer.domain.model.Recipient
import my.scamshield.feature.transfer.domain.model.Transaction
import my.scamshield.feature.transfer.presentation.confirm.TransferConfirmScreen

class TransferComposeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: TransferComposeViewModel = koinScreenModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Send Money") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                )
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
                    .padding(24.dp),
            ) {
                Text(
                    "Rehearsed demo scenarios",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { viewModel.selectRehearsedContact() }) {
                        Text("G1 · Siti · RM 50")
                    }
                    OutlinedButton(onClick = { viewModel.selectRehearsedMule() }) {
                        Text("R1 · mule · RM 2,000")
                    }
                }

                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = state.recipientPhone,
                    onValueChange = viewModel::onPhoneChanged,
                    label = { Text("Recipient phone") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.recipientDisplayName,
                    onValueChange = viewModel::onDisplayNameChanged,
                    label = { Text("Recipient name (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.amountRm,
                    onValueChange = viewModel::onAmountChanged,
                    label = { Text("Amount (RM)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.note,
                    onValueChange = viewModel::onNoteChanged,
                    label = { Text("Note (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = {
                        val recipient = Recipient(
                            id = state.recipientPhone,
                            displayName = state.recipientDisplayName.ifBlank { state.recipientPhone },
                            phone = state.recipientPhone,
                            isInContacts = state.recipientDisplayName == "Siti Aminah",
                            priorTransferCount = if (state.recipientDisplayName == "Siti Aminah") 8 else 0,
                        )
                        val transaction = Transaction(
                            id = "pending",
                            senderId = "demo_user_01",
                            recipient = recipient,
                            amount = state.amountRm.toDoubleOrNull() ?: 0.0,
                            note = state.note,
                        )
                        navigator.push(TransferConfirmScreen(transaction))
                    },
                    enabled = state.isValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                ) {
                    Text("Continue", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
