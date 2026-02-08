package com.example.myapplication.presentation.base

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.myapplication.presentation.mvi.UiEffect
import com.example.myapplication.presentation.mvi.UiEvent
import com.example.myapplication.presentation.mvi.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseScreenModel<S : UiState, E : UiEvent, F : UiEffect>(
    initialState: S
) : ScreenModel {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    protected val currentState: S
        get() = _uiState.value

    private val _event = MutableSharedFlow<E>()

    private val _effect = Channel<F>()
    val effect = _effect.receiveAsFlow()

    init {
        subscribeToEvents()
    }

    private fun subscribeToEvents() {
        screenModelScope.launch {
            _event.collect { event ->
                handleEvent(event)
            }
        }
    }

    fun onEvent(event: E) {
        screenModelScope.launch {
            _event.emit(event)
        }
    }

    protected abstract fun handleEvent(event: E)

    protected fun updateState(reduce: S.() -> S) {
        _uiState.value = currentState.reduce()
    }

    protected fun sendEffect(effect: F) {
        screenModelScope.launch {
            _effect.send(effect)
        }
    }

    protected fun launchScope(block: suspend () -> Unit) {
        screenModelScope.launch {
            block()
        }
    }
}
