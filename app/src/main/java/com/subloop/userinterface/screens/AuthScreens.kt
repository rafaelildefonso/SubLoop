package com.subloop.userinterface.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.subloop.R
import com.subloop.userinterface.components.SubLoopCard
import com.subloop.userinterface.theme.Canvas
import com.subloop.userinterface.theme.Ink
import com.subloop.userinterface.theme.Muted
import com.subloop.viewmodel.AuthUiState

@Composable
fun LoginScreen(
    state: AuthUiState,
    onLogin: (String, String) -> Unit,
    onRegister: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    AuthLayout(
        eyebrow = "BEM-VINDO DE VOLTA",
        title = "Suas assinaturas,\nsem surpresas.",
        subtitle = "Entre para continuar no controle dos seus compromissos.",
    ) {
        AuthField(email, { email = it }, "E-mail", Icons.Rounded.Email, KeyboardType.Email)
        Spacer(Modifier.height(12.dp))
        PasswordField(password, { password = it }, "Senha")
        AuthError(state.error)
        PrimaryAuthButton("ENTRAR", state.isLoading) { onLogin(email, password) }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text("Ainda não tem uma conta?", style = MaterialTheme.typography.bodySmall, color = Muted)
            TextButton(onClick = onRegister) { Text("Criar conta", color = Ink, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun RegisterScreen(
    state: AuthUiState,
    onRegister: (String, String, String) -> Unit,
    onLogin: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    AuthLayout(
        eyebrow = "COMECE AGORA",
        title = "Controle simples,\nfeito para você.",
        subtitle = "Seus dados ficam neste aparelho e pertencem somente à sua conta.",
    ) {
        AuthField(name, { name = it }, "Nome", Icons.Rounded.Person, KeyboardType.Text)
        Spacer(Modifier.height(12.dp))
        AuthField(email, { email = it }, "E-mail", Icons.Rounded.Email, KeyboardType.Email)
        Spacer(Modifier.height(12.dp))
        PasswordField(password, { password = it }, "Senha (mínimo de 6 caracteres)")
        AuthError(state.error)
        PrimaryAuthButton("CRIAR CONTA", state.isLoading) { onRegister(name, email, password) }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text("Já tem uma conta?", style = MaterialTheme.typography.bodySmall, color = Muted)
            TextButton(onClick = onLogin) { Text("Entrar", color = Ink, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun AuthLayout(
    eyebrow: String,
    title: String,
    subtitle: String,
    content: @Composable () -> Unit,
) {
    Box(Modifier.fillMaxSize().background(Canvas).safeDrawingPadding().imePadding()) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 22.dp, vertical = 36.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painterResource(R.drawable.subloop_logo),
                contentDescription = null,
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(14.dp)),
            )
            Spacer(Modifier.height(20.dp))
            Text(eyebrow, style = MaterialTheme.typography.labelMedium, letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified)
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Muted)
            Spacer(Modifier.height(24.dp))
            SubLoopCard(Modifier.fillMaxWidth()) { content() }
            Spacer(Modifier.height(12.dp))
            Text(
                "Privado por padrão. Senhas protegidas com hash SHA-256.",
                style = MaterialTheme.typography.labelSmall,
                color = Muted,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

@Composable
private fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Ink,
            unfocusedBorderColor = Color(0xFFE2E2E5),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
        ),
    )
}

@Composable
private fun PasswordField(value: String, onValueChange: (String) -> Unit, label: String) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(Icons.Rounded.Lock, null) },
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(if (visible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility, null)
            }
        },
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Ink, unfocusedBorderColor = Color(0xFFE2E2E5)),
    )
}

@Composable
private fun AuthError(error: String?) {
    if (error != null) {
        Spacer(Modifier.height(10.dp))
        Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    } else Spacer(Modifier.height(16.dp))
}

@Composable
private fun PrimaryAuthButton(label: String, loading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = !loading,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Ink),
    ) {
        if (loading) CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
        else Text(label, style = MaterialTheme.typography.labelLarge)
    }
}
