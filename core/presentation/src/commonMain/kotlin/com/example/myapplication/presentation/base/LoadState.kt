package com.example.myapplication.presentation.base

sealed interface LoadState {
    data object Idle : LoadState
    data object Loading : LoadState
    data object Success : LoadState
    data class Error(val message: String) : LoadState
}
