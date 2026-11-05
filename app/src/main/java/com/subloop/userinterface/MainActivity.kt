package com.subloop.userinterface

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import com.subloop.data.AppDatabase
import com.subloop.data.SessionManager
import com.subloop.data.SubLoopRepository
import com.subloop.data.NotificationScheduler
import com.subloop.network.NetworkModule
import com.subloop.userinterface.navigation.SubLoopApp
import com.subloop.userinterface.theme.SubLoopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        val repository = SubLoopRepository(
            userDao = database.userDao(),
            subscriptionDao = database.subscriptionDao(),
            sessionManager = SessionManager(applicationContext),
            exchangeRateApi = NetworkModule.exchangeRateApi,
            notificationScheduler = NotificationScheduler(applicationContext),
        )

        setContent {
            SubLoopTheme { SubLoopApp(repository) }
        }
    }
}
