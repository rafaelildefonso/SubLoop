package com.subloop.userinterface.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.subloop.userinterface.components.SubLoopCard
import com.subloop.userinterface.theme.Canvas
import com.subloop.userinterface.theme.Ink
import com.subloop.userinterface.theme.Muted
import com.subloop.userinterface.theme.Soft
import com.subloop.viewmodel.SubscriptionViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddSubscriptionScreen(
    viewModel: SubscriptionViewModel,
    onSaved: () -> Unit,
    contentPadding: PaddingValues,
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var recurrence by remember { mutableStateOf("Mensal") }
    var dueDate by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Streaming") }
    var alertEnabled by remember { mutableStateOf(true) }
    val saveState by viewModel.saveState.collectAsState()

    LaunchedEffect(saveState.savedId) {
        if (saveState.savedId != null) {
            viewModel.resetSaveState()
            onSaved()
        }
    }

    Column(
        Modifier.fillMaxSize().background(Canvas).padding(contentPadding).verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        SubLoopCard(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(40.dp).background(Ink, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(19.dp))
                }
                Spacer(Modifier.size(9.dp))
                Column(Modifier.weight(1f)) {
                    Text("Cadastro Descomplicado", style = MaterialTheme.typography.titleLarge)
                    Text("Sem vincular conta. Preencha em segundos.", style = MaterialTheme.typography.bodySmall, color = Muted)
                }
                Box(Modifier.background(Color(0xFFE8E8EB), CircleShape).padding(horizontal = 10.dp, vertical = 6.dp)) {
                    Text("100%\nPRIVADO", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar ou informar o serviço…") },
            leadingIcon = { Icon(Icons.Rounded.Search, null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = fieldColors(),
        )

        Column {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Serviços Populares", style = MaterialTheme.typography.titleSmall)
                Text("Sem dados pré-carregados", style = MaterialTheme.typography.labelMedium, color = Muted)
            }
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth().background(Soft, RoundedCornerShape(12.dp)).padding(14.dp)) {
                Text("Digite o nome para cadastrar sua própria assinatura.", style = MaterialTheme.typography.bodySmall, color = Muted)
            }
        }

        SubLoopCard(Modifier.fillMaxWidth()) {
            FieldLabel("Nome da Assinatura", "Obrigatório")
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Ex: academia, streaming ou nuvem") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = fieldColors(),
            )
            Spacer(Modifier.height(14.dp))

            FieldLabel("Valor Recorrente")
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it.filter { char -> char.isDigit() || char == ',' || char == '.' } },
                prefix = { Text("R$", style = MaterialTheme.typography.titleLarge, color = Muted) },
                placeholder = { Text("0,00", style = MaterialTheme.typography.titleLarge) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(8.dp),
                colors = fieldColors(),
            )
            Spacer(Modifier.height(14.dp))

            FieldLabel("Periodicidade de Cobrança")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("Semanal", "Mensal", "Trimestral", "Anual").forEach { option ->
                    FilterChip(
                        selected = recurrence == option,
                        onClick = { recurrence = option },
                        label = { Text(option) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = chipColors(),
                        border = null,
                    )
                }
            }
            Spacer(Modifier.height(14.dp))

            FieldLabel("Próximo Vencimento", "DD/MM/AAAA")
            OutlinedTextField(
                value = dueDate,
                onValueChange = { dueDate = formatDateInput(it) },
                placeholder = { Text("DD/MM/AAAA") },
                trailingIcon = { Icon(Icons.Rounded.CalendarMonth, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(8.dp),
                colors = fieldColors(),
            )
            Spacer(Modifier.height(14.dp))

            FieldLabel("Categoria")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                listOf("Streaming", "Trabalho", "Fitness", "Nuvem", "Games", "Outros").forEach { option ->
                    FilterChip(
                        selected = category == option,
                        onClick = { category = option },
                        label = { Text(option) },
                        shape = CircleShape,
                        colors = chipColors(),
                    )
                }
            }
            Spacer(Modifier.height(10.dp))

            Row(
                Modifier.fillMaxWidth().background(Soft, RoundedCornerShape(12.dp)).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(38.dp).background(Ink, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.NotificationsNone, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.size(8.dp))
                Column(Modifier.weight(1f)) {
                    Text("Alerta Inteligente", style = MaterialTheme.typography.titleMedium)
                    Text("Avisar 3 dias antes da cobrança", style = MaterialTheme.typography.bodySmall, color = Muted)
                }
                Switch(
                    checked = alertEnabled,
                    onCheckedChange = { alertEnabled = it },
                    colors = SwitchDefaults.colors(checkedTrackColor = Ink, checkedThumbColor = Color.White),
                )
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Lock, null, modifier = Modifier.size(14.dp), tint = Muted)
            Spacer(Modifier.size(4.dp))
            Text("Dados salvos apenas no seu aparelho", style = MaterialTheme.typography.labelMedium, color = Muted)
        }

        if (saveState.error != null) {
            Text(saveState.error!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = { viewModel.addSubscription(name, amount, recurrence, dueDate, category, alertEnabled) },
            enabled = !saveState.isSaving,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Ink),
        ) {
            if (saveState.isSaving) CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            else {
                Icon(Icons.Rounded.CheckCircle, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(5.dp))
                Text("SALVAR ASSINATURA")
            }
        }
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun FieldLabel(label: String, trailing: String? = null) {
    Row(Modifier.fillMaxWidth().padding(bottom = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.titleSmall)
        trailing?.let { Text(it, style = MaterialTheme.typography.labelMedium, color = Muted) }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Ink,
    unfocusedBorderColor = Color.Transparent,
    focusedContainerColor = Soft,
    unfocusedContainerColor = Soft,
)

@Composable
private fun chipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = Ink,
    selectedLabelColor = Color.White,
    containerColor = Soft,
    labelColor = Muted,
)

private fun formatDateInput(value: String): String {
    val digits = value.filter(Char::isDigit).take(8)
    return buildString {
        digits.forEachIndexed { index, char ->
            if (index == 2 || index == 4) append('/')
            append(char)
        }
    }
}
