package com.subloop.userinterface.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.subloop.userinterface.components.EmptyState
import com.subloop.userinterface.components.SubLoopCard
import com.subloop.userinterface.components.SubLoopHeader
import com.subloop.userinterface.theme.Canvas
import com.subloop.userinterface.theme.Ink
import com.subloop.userinterface.theme.Muted
import com.subloop.userinterface.theme.Soft
import com.subloop.viewmodel.SubscriptionViewModel

@Composable
fun DetailScreen(
    subscriptionId: Long,
    viewModel: SubscriptionViewModel,
    onBack: () -> Unit,
) {
    val subscriptions by viewModel.subscriptions.collectAsState()
    val subscription = subscriptions.firstOrNull { it.id == subscriptionId }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(topBar = { SubLoopHeader(section = "Detalhes", onBack = onBack) }) { padding ->
        if (subscription == null) {
            Box(Modifier.fillMaxSize().background(Canvas).padding(padding).padding(16.dp), contentAlignment = Alignment.Center) {
                EmptyState(
                    icon = Icons.Rounded.History,
                    title = "Assinatura não encontrada",
                    message = "Ela pode ter sido removida.",
                    action = "Voltar",
                    onAction = onBack,
                )
            }
        } else {
            Column(
                Modifier.fillMaxSize().background(Canvas).padding(padding).verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                SubLoopCard(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(48.dp).background(Ink, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                            Text(subscription.name.first().uppercase(), color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
                        }
                        Spacer(Modifier.size(9.dp))
                        Column(Modifier.weight(1f)) {
                            Text(subscription.name, style = MaterialTheme.typography.titleLarge)
                            Text(subscription.category, style = MaterialTheme.typography.labelMedium, color = Muted)
                        }
                        Row(Modifier.background(Soft, CircleShape).padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(6.dp).background(Ink, CircleShape))
                            Spacer(Modifier.size(5.dp))
                            Text("ATIVA", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Text("Valor recorrente", style = MaterialTheme.typography.bodySmall, color = Muted)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(formatCurrency(subscription.amountCents), style = MaterialTheme.typography.displaySmall)
                        Text(" / ${subscription.recurrence.lowercase()}", style = MaterialTheme.typography.bodySmall, color = Muted, modifier = Modifier.padding(bottom = 5.dp))
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        DetailMetric(Icons.Rounded.CalendarMonth, "Próximo débito", subscription.nextDueDate, Modifier.weight(1f))
                        DetailMetric(Icons.Rounded.Check, "Categoria", subscription.category, Modifier.weight(1f))
                    }
                }

                SubLoopCard(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(32.dp).background(Soft, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.NotificationsNone, null, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.size(7.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Alerta de Renovação", style = MaterialTheme.typography.titleMedium)
                            Text("Zero cobranças automáticas invisíveis", style = MaterialTheme.typography.bodySmall, color = Muted)
                        }
                        Switch(
                            checked = subscription.alertEnabled,
                            onCheckedChange = { viewModel.updateAlert(subscription, it) },
                            colors = SwitchDefaults.colors(checkedTrackColor = Ink, checkedThumbColor = Color.White),
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                    Text("Quando avisar você?", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        listOf("7 dias antes", "3 dias antes").forEach { option ->
                            Box(
                                Modifier.weight(1f).background(
                                    if (option == "3 dias antes") Ink else Soft,
                                    RoundedCornerShape(8.dp),
                                ).padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(option, color = if (option == "3 dias antes") Color.White else Muted, style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }

                SubLoopCard(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.History, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.size(6.dp))
                        Text("Histórico Recente", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(Modifier.height(16.dp))
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Nenhum pagamento registrado", style = MaterialTheme.typography.titleSmall)
                        Text("O histórico começa vazio.", style = MaterialTheme.typography.bodySmall, color = Muted)
                    }
                }

                Box(Modifier.fillMaxWidth().background(Ink, RoundedCornerShape(12.dp)).padding(17.dp)) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.DeleteOutline, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.size(5.dp))
                            Text("Decidiu não continuar?", color = Color.White, style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Remova esta assinatura do seu acompanhamento local.",
                            color = Color.White.copy(alpha = .75f),
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Ink),
                            shape = RoundedCornerShape(8.dp),
                        ) { Text("Remover Assinatura") }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }

    if (showDeleteDialog && subscription != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Remover assinatura?") },
            text = { Text("Esta ação remove os dados salvos deste usuário no aparelho.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(subscription)
                    showDeleteDialog = false
                    onBack()
                }) { Text("Remover", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun DetailMetric(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier.background(Soft, RoundedCornerShape(8.dp)).padding(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(14.dp), tint = Muted)
            Spacer(Modifier.size(4.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, color = Muted)
        }
        Spacer(Modifier.height(3.dp))
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}
