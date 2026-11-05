package com.subloop.userinterface.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.WindowInsets
import com.subloop.userinterface.components.SubLoopBottomBar
import com.subloop.userinterface.components.SubLoopHeader
import com.subloop.userinterface.navigation.AppTab
import com.subloop.viewmodel.SubscriptionViewModel

@Composable
fun MainShell(
    viewModel: SubscriptionViewModel,
    onOpenDetail: (Long) -> Unit,
    onLogout: () -> Unit,
) {
    NotificationPermissionEffect(onGranted = viewModel::rescheduleNotifications)
    var selectedTab by remember { mutableStateOf(AppTab.Home) }
    val subscriptions by viewModel.subscriptions.collectAsState()
    val exchangeRate by viewModel.exchangeRate.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            SubLoopHeader(
                section = when (selectedTab) {
                    AppTab.Home -> "Início"
                    AppTab.Subscriptions -> "Assinaturas"
                    AppTab.Add -> "Adicionar Assinatura"
                    AppTab.Alerts -> "Alertas"
                },
                onProfile = onLogout,
            )
        },
        bottomBar = { SubLoopBottomBar(selectedTab) { selectedTab = it } },
    ) { padding ->
        when (selectedTab) {
            AppTab.Home -> HomeScreen(
                subscriptions = subscriptions,
                exchangeRate = exchangeRate,
                onRefreshRate = viewModel::refreshExchangeRate,
                onAdd = { selectedTab = AppTab.Add },
                onOpenDetail = onOpenDetail,
                contentPadding = padding,
            )
            AppTab.Subscriptions -> SubscriptionsScreen(
                subscriptions = subscriptions,
                onAdd = { selectedTab = AppTab.Add },
                onOpenDetail = onOpenDetail,
                contentPadding = padding,
            )
            AppTab.Add -> AddSubscriptionScreen(
                viewModel = viewModel,
                onSaved = { selectedTab = AppTab.Subscriptions },
                contentPadding = padding,
            )
            AppTab.Alerts -> AlertsScreen(
                subscriptions = subscriptions,
                onOpenDetail = onOpenDetail,
                onToggle = viewModel::updateAlert,
                contentPadding = padding,
            )
        }
    }
}

@Composable
private fun NotificationPermissionEffect(onGranted: () -> Unit) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> if (granted) onGranted() },
    )
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            onGranted()
        }
    }
}
