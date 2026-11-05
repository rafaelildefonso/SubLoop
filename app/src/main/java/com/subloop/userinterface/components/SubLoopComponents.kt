package com.subloop.userinterface.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.PersonOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.subloop.R
import com.subloop.userinterface.navigation.AppTab
import com.subloop.userinterface.theme.Border
import com.subloop.userinterface.theme.Canvas
import com.subloop.userinterface.theme.Ink
import com.subloop.userinterface.theme.Muted
import com.subloop.userinterface.theme.Soft

@Composable
fun SubLoopHeader(
    section: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onProfile: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Column(modifier.background(Canvas)) {
        Row(
            modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Voltar", tint = Ink)
                    }
                }
                Image(
                    painter = painterResource(R.drawable.subloop_logo),
                    contentDescription = "Logo SubLoop",
                    modifier = Modifier.size(if (onBack == null) 32.dp else 28.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )
                Spacer(Modifier.size(8.dp))
                Column {
                    Text("SubLoop", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (onBack == null) {
                        Text(section.uppercase(), style = MaterialTheme.typography.labelSmall, color = Muted)
                    }
                }
                if (onBack != null) {
                    Text("  |  $section", style = MaterialTheme.typography.bodyMedium, color = Muted)
                }
            }
            when {
                trailing != null -> trailing()
                onProfile != null -> IconButton(onClick = onProfile) {
                    Box(
                        Modifier.size(34.dp).background(Soft, CircleShape).border(1.dp, Border, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) { Icon(Icons.Rounded.PersonOutline, "Perfil", modifier = Modifier.size(19.dp)) }
                }
            }
        }
        HorizontalDivider(color = Border)
    }
}

@Composable
fun SubLoopCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) { Column(Modifier.padding(16.dp), content = content) }
}

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    message: String,
    action: String? = null,
    onAction: (() -> Unit)? = null,
) {
    SubLoopCard(modifier.fillMaxWidth()) {
        Column(
            Modifier.fillMaxWidth().padding(vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(Modifier.size(48.dp).background(Ink, CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = Color.White)
            }
            Spacer(Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(5.dp))
            Text(message, style = MaterialTheme.typography.bodySmall, color = Muted)
            if (action != null && onAction != null) {
                Spacer(Modifier.height(14.dp))
                Box(
                    Modifier.clip(RoundedCornerShape(8.dp)).background(Ink).clickable(onClick = onAction)
                        .padding(horizontal = 18.dp, vertical = 11.dp),
                ) { Text(action, color = Color.White, style = MaterialTheme.typography.labelLarge) }
            }
        }
    }
}

@Composable
fun SubLoopBottomBar(selected: AppTab, onSelect: (AppTab) -> Unit) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        AppTab.entries.forEach { tab ->
            val icon = when (tab) {
                AppTab.Home -> Icons.Rounded.Home
                AppTab.Subscriptions -> Icons.Rounded.CreditCard
                AppTab.Add -> Icons.Rounded.Add
                AppTab.Alerts -> Icons.Rounded.NotificationsNone
            }
            NavigationBarItem(
                selected = tab == selected,
                onClick = { onSelect(tab) },
                icon = {
                    if (tab == AppTab.Add) {
                        Box(Modifier.size(46.dp).background(Ink, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(icon, null, tint = Color.White)
                        }
                    } else Icon(icon, null)
                },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Ink,
                    selectedTextColor = Ink,
                    unselectedIconColor = Muted,
                    unselectedTextColor = Muted,
                    indicatorColor = Color.Transparent,
                ),
            )
        }
    }
}
