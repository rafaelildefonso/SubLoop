package com.subloop.userinterface.navigation

import kotlinx.serialization.Serializable

@Serializable data object LoginRoute
@Serializable data object RegisterRoute
@Serializable data object MainRoute
@Serializable data class DetailRoute(val subscriptionId: Long)

enum class AppTab(val label: String) {
    Home("Início"),
    Subscriptions("Assinaturas"),
    Add("Adicionar"),
    Alerts("Alertas"),
}
