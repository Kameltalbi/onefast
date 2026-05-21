package com.fastflow.app.presentation.pricing

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastflow.app.data.billing.BillingException
import com.fastflow.app.domain.model.SubscriptionTier
import com.fastflow.app.domain.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PricingViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PricingUiState())
    val uiState: StateFlow<PricingUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PricingEvent>()
    val events: SharedFlow<PricingEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            subscriptionRepository.observeTier().collect { tier ->
                _uiState.update { it.copy(currentTier = tier, isLoading = false) }
            }
        }
    }

    fun onSubscribePro(activity: Activity) {
        purchase(activity) { subscriptionRepository.purchaseProYearly(activity) }
    }

    fun onSubscribePremium(activity: Activity) {
        purchase(activity) { subscriptionRepository.purchasePremiumYearly(activity) }
    }

    fun onRestorePurchases() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            subscriptionRepository.restorePurchases()
                .onSuccess { tier ->
                    _uiState.update { it.copy(isLoading = false) }
                    if (tier == SubscriptionTier.FREE) {
                        _events.emit(PricingEvent.ShowMessage(PricingMessage.RestoreEmpty))
                    } else {
                        _events.emit(PricingEvent.ShowMessage(PricingMessage.RestoreSuccess))
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(PricingEvent.ShowMessage(mapError(error)))
                }
        }
    }

    private fun purchase(activity: Activity, block: suspend () -> Result<Unit>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            block()
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(PricingEvent.ShowMessage(PricingMessage.PurchaseSuccess))
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    if (error !is BillingException.Cancelled) {
                        _events.emit(PricingEvent.ShowMessage(mapError(error)))
                    }
                }
        }
    }

    private fun mapError(error: Throwable): PricingMessage = when (error) {
        is BillingException.NotReady -> PricingMessage.BillingNotReady
        is BillingException.ProductNotFound -> PricingMessage.ProductUnavailable
        is BillingException.Error -> PricingMessage.BillingError
        else -> PricingMessage.BillingError
    }
}

data class PricingUiState(
    val currentTier: SubscriptionTier = SubscriptionTier.FREE,
    val isLoading: Boolean = false
)

sealed class PricingEvent {
    data class ShowMessage(val message: PricingMessage) : PricingEvent()
}

enum class PricingMessage {
    PurchaseSuccess,
    RestoreSuccess,
    RestoreEmpty,
    BillingNotReady,
    ProductUnavailable,
    BillingError
}
