package com.subloop.userinterface.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.subloop.data.SubscriptionEntity
import com.subloop.userinterface.components.EmptyState
import com.subloop.userinterface.components.SubLoopCard
import com.subloop.userinterface.theme.Canvas
import com.subloop.userinterface.theme.Ink
import com.subloop.userinterface.theme.Muted
import com.subloop.userinterface.theme.Soft
import com.subloop.viewmodel.ExchangeRateUiState
import java.text.NumberFormat
import java.util.Locale

private val brlFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))

@Composable
fun HomeScreen(
    subscriptions: List<SubscriptionEntity>,
    exchangeRate: ExchangeRateUiState,
    onRefreshRate: () -> Unit,
    onAdd: () -> Unit,
    onOpenDetail: (Long) -> Unit,
    contentPadding: PaddingValues,
) {
    val totalCents = subscriptions.sumOf { it.amountCents }
    LazyColumn(
        Modifier.fillMaxSize().background(Canvas).padding(contentPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { SummaryCard(totalCents, subscriptions.size) }
        if (subscriptions.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Rounded.CreditCard,
                    title = "Nenhuma assinatura ainda",
                    message = "Adicione seu primeiro compromisso para montar o resumo mensal.",
                    action = "Nova Assinatura",
                    onAction = onAdd,
                )
            }
        } else {
            item { SectionTitle("Próximos Vencimentos", "${subscriptions.size} cadastradas") }
            items(subscriptions.take(3), key = { it.id }) {
                SubscriptionRow(it, onClick = { onOpenDetail(it.id) })
            }
        }
        item { ExchangeRateCard(exchangeRate, onRefreshRate) }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun SummaryCard(totalCents: Long, count: Int) {
    SubLoopCard(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.CalendarMonth, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.size(5.dp))
                Text("COMPROMISSOS DESTE MÊS", style = MaterialTheme.typography.labelMedium, color = Muted)
            }
            Box(Modifier.background(Ink, CircleShape).padding(horizontal = 10.dp, vertical = 4.dp)) {
                Text("$count ativas", color = Color.White, style = MaterialTheme.typography.labelMedium)
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(formatCurrency(totalCents), style = MaterialTheme.typography.displaySmall)
        Text(
            if (count == 0) "Seu resumo aparecerá quando você cadastrar uma assinatura."
            else "Total recorrente informado por você.",
            style = MaterialTheme.typography.bodySmall,
            color = Muted,
        )
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth().background(Soft, RoundedCornerShape(8.dp)).padding(10.dp)) {
            Metric("Restante a Debitar", formatCurrency(totalCents), Modifier.weight(1f))
            Metric("Já Faturado", formatCurrency(0), Modifier.weight(1f))
        }
    }
}

@Composable
private fun Metric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Muted)
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun SubscriptionsScreen(
    subscriptions: List<SubscriptionEntity>,
    onAdd: () -> Unit,
    onOpenDetail: (Long) -> Unit,
    contentPadding: PaddingValues,
) {
    LazyColumn(
        Modifier.fillMaxSize().background(Canvas).padding(contentPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Suas Assinaturas", style = MaterialTheme.typography.headlineSmall)
                    Text("Organize tudo em um só lugar", style = MaterialTheme.typography.bodySmall, color = Muted)
                }
                Button(
                    onClick = onAdd,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Ink),
                    contentPadding = PaddingValues(12.dp),
                ) { Icon(Icons.Rounded.Add, "Adicionar") }
            }
        }
        if (subscriptions.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Rounded.CreditCard,
                    title = "Lista vazia",
                    message = "As assinaturas salvas com sua conta aparecerão aqui.",
                    action = "Adicionar assinatura",
                    onAction = onAdd,
                )
            }
        } else {
            items(subscriptions, key = { it.id }) { subscription ->
                SubscriptionRow(subscription) { onOpenDetail(subscription.id) }
            }
        }
    }
}

@Composable
fun AlertsScreen(
    subscriptions: List<SubscriptionEntity>,
    onOpenDetail: (Long) -> Unit,
    onToggle: (SubscriptionEntity, Boolean) -> Unit,
    contentPadding: PaddingValues,
) {
    val alerted = subscriptions.filter { it.alertEnabled }
    LazyColumn(
        Modifier.fillMaxSize().background(Color.White).padding(contentPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            SubLoopCard(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(8.dp).background(Ink, CircleShape))
                            Spacer(Modifier.size(6.dp))
                            Text("MONITORAMENTO ATIVO", style = MaterialTheme.typography.labelMedium)
                        }
                        Spacer(Modifier.height(7.dp))
                        Text("Alertas de Renovação", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            if (alerted.isEmpty()) "Nenhuma decisão pendente no momento."
                            else "${alerted.size} ${if (alerted.size == 1) "alerta ativo" else "alertas ativos"} antes do débito.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Muted,
                        )
                    }
                    Box(Modifier.size(44.dp).background(Ink, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.NotificationsNone, null, tint = Color.White)
                    }
                }
            }
        }
        if (subscriptions.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Rounded.NotificationsNone,
                    title = "Tudo tranquilo por aqui",
                    message = "Os alertas aparecem depois que você cadastrar uma assinatura.",
                )
            }
        } else {
            item { SectionTitle("Alertas configurados", "${alerted.size} ativos") }
            items(subscriptions, key = { it.id }) { subscription ->
                SubLoopCard(Modifier.fillMaxWidth().clickable { onOpenDetail(subscription.id) }) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(42.dp).background(if (subscription.alertEnabled) Ink else Soft, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.NotificationsNone, null, tint = if (subscription.alertEnabled) Color.White else Ink)
                        }
                        Spacer(Modifier.size(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(subscription.name, style = MaterialTheme.typography.titleMedium)
                            Text("Vencimento: ${subscription.nextDueDate}", style = MaterialTheme.typography.bodySmall, color = Muted)
                        }
                        Switch(
                            checked = subscription.alertEnabled,
                            onCheckedChange = { onToggle(subscription, it) },
                            colors = SwitchDefaults.colors(checkedTrackColor = Ink, checkedThumbColor = Color.White),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubscriptionRow(subscription: SubscriptionEntity, onClick: () -> Unit) {
    SubLoopCard(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).background(Ink, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                Text(subscription.name.firstOrNull()?.uppercase() ?: "S", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.size(10.dp))
            Column(Modifier.weight(1f)) {
                Text(subscription.name, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                Text("${subscription.recurrence} • Renova ${subscription.nextDueDate}", style = MaterialTheme.typography.bodySmall, color = Muted)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatCurrency(subscription.amountCents), style = MaterialTheme.typography.titleMedium)
                Text(if (subscription.alertEnabled) "Alerta ativo" else "Sem alerta", style = MaterialTheme.typography.labelSmall, color = Muted)
            }
        }
    }
}

@Composable
private fun ExchangeRateCard(state: ExchangeRateUiState, onRefresh: () -> Unit) {
    SubLoopCard(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).background(Ink, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Rounded.TrendingUp, null, tint = Color.White)
            }
            Spacer(Modifier.size(10.dp))
            Column(Modifier.weight(1f)) {
                Text("Cotação internacional", style = MaterialTheme.typography.titleSmall)
                when (state) {
                    ExchangeRateUiState.Loading -> Text("Consultando API pública…", style = MaterialTheme.typography.bodySmall, color = Muted)
                    is ExchangeRateUiState.Success -> Text("USD 1 = BRL ${"%.2f".format(Locale.US, state.rate)} • ${state.date}", style = MaterialTheme.typography.bodySmall, color = Muted)
                    is ExchangeRateUiState.Error -> Text(state.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
            }
            if (state is ExchangeRateUiState.Loading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
            else Box(Modifier.size(40.dp).clickable(onClick = onRefresh), contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.Refresh, "Tentar novamente")
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, trailing: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(trailing, style = MaterialTheme.typography.labelMedium, color = Muted, textAlign = TextAlign.End)
    }
}

fun formatCurrency(cents: Long): String = brlFormatter.format(cents / 100.0)
