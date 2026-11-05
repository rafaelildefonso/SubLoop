package com.subloop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.subloop.data.SubLoopRepository
import com.subloop.data.SubscriptionEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

sealed interface ExchangeRateUiState {
    data object Loading : ExchangeRateUiState
    data class Success(val rate: Double, val date: String) : ExchangeRateUiState
    data class Error(val message: String) : ExchangeRateUiState
}

data class SaveSubscriptionState(
    val isSaving: Boolean = false,
    val savedId: Long? = null,
    val error: String? = null,
)

class SubscriptionViewModel(
    private val repository: SubLoopRepository,
) : ViewModel() {
    private val userId = requireNotNull(repository.currentUserId) { "Sessão não encontrada." }

    val subscriptions: StateFlow<List<SubscriptionEntity>> = repository
        .observeSubscriptions(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _exchangeRate = MutableStateFlow<ExchangeRateUiState>(ExchangeRateUiState.Loading)
    val exchangeRate: StateFlow<ExchangeRateUiState> = _exchangeRate.asStateFlow()

    private val _saveState = MutableStateFlow(SaveSubscriptionState())
    val saveState: StateFlow<SaveSubscriptionState> = _saveState.asStateFlow()

    init {
        refreshExchangeRate()
    }

    fun refreshExchangeRate() {
        viewModelScope.launch {
            _exchangeRate.value = ExchangeRateUiState.Loading
            _exchangeRate.value = repository.loadUsdBrlRate().fold(
                onSuccess = { ExchangeRateUiState.Success(it.first, it.second) },
                onFailure = { ExchangeRateUiState.Error(it.message ?: "Falha ao consultar a cotação.") },
            )
        }
    }

    fun addSubscription(
        name: String,
        amountText: String,
        recurrence: String,
        nextDueDate: String,
        category: String,
        alertEnabled: Boolean,
    ) {
        viewModelScope.launch {
            _saveState.value = SaveSubscriptionState(isSaving = true)
            val result = runCatching {
                require(name.isNotBlank()) { "Informe o nome da assinatura." }
                val cents = amountText.toCents()
                require(cents > 0) { "Informe um valor maior que zero." }
                require(nextDueDate.isNotBlank()) { "Informe o próximo vencimento." }
                repository.addSubscription(
                    userId = userId,
                    name = name,
                    amountCents = cents,
                    recurrence = recurrence,
                    nextDueDate = nextDueDate,
                    category = category,
                    alertEnabled = alertEnabled,
                )
            }
            _saveState.value = result.fold(
                onSuccess = { SaveSubscriptionState(savedId = it) },
                onFailure = { SaveSubscriptionState(error = it.message ?: "Não foi possível salvar.") },
            )
        }
    }

    fun updateAlert(subscription: SubscriptionEntity, enabled: Boolean) {
        viewModelScope.launch { repository.update(subscription.copy(alertEnabled = enabled)) }
    }

    fun delete(subscription: SubscriptionEntity) {
        viewModelScope.launch { repository.delete(subscription) }
    }

    fun resetSaveState() {
        _saveState.value = SaveSubscriptionState()
    }

    private fun String.toCents(): Long {
        val normalized = trim()
            .replace("R$", "")
            .replace(" ", "")
            .let { raw ->
                if (raw.contains(',')) raw.replace(".", "").replace(',', '.') else raw
            }
        return BigDecimal(normalized)
            .multiply(BigDecimal(100))
            .setScale(0, RoundingMode.HALF_UP)
            .longValueExact()
    }
}

class SubscriptionViewModelFactory(
    private val repository: SubLoopRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        SubscriptionViewModel(repository) as T
}
