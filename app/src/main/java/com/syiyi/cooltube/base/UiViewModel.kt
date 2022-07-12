package com.syiyi.cooltube.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class UiViewModel<I : UiIntent, S : UiState> : ViewModel() {

    private val intendFlow = MutableSharedFlow<I>()
    private val effectChannel = Channel<UiEffect>()
    private val effectFlow = effectChannel.receiveAsFlow()

    lateinit var state: StateFlow<S>

    init {
        initState()
        dispatchDefaultIntent()
    }

    private fun initState() {
        state = intendFlow
            .mapIntent()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), defaultState())
    }

    private fun dispatchDefaultIntent() {
        defaultIntent()?.let {
            viewModelScope.launch {
                delay(16L)
                it.forEach {
                    intendFlow.emit(it)
                }
            }
        }
    }

    fun dispatch(vararg intents: I) {
        viewModelScope.launch {
            intents.forEach {
                intendFlow.emit(it)
            }
        }
    }

    fun dispatch(vararg effects: UiEffect) {
        viewModelScope.launch {
            effects.forEach {
                effectChannel.send(it)
            }
        }
    }

    suspend fun onEffect(callBack: (UiEffect) -> Unit) {
        effectFlow.onEach { callBack(it) }.collect()
    }

    protected abstract fun Flow<I>.mapIntent(): Flow<S>

    protected abstract fun defaultState(): S

    protected open fun defaultIntent(): List<I>? = null

}