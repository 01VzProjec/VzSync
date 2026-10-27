package com.vztechcore.vzsync.data.git

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Singleton bus between CloneViewModel, CloneService and CloneCancelReceiver.
 *
 * - progress: StateFlow with the most recent CloneProgress (or null).
 * - events: transient events (Stop, CancelRequested).
 * - cancelRequested: shared AtomicBoolean read by the JGit ProgressMonitor.
 *   Any component can set it to abort the running clone.
 */
object CloneProgressBus {

    private val _progress = MutableStateFlow<CloneProgress?>(null)
    val progress: StateFlow<CloneProgress?> = _progress.asStateFlow()

    private val _events = MutableSharedFlow<CloneServiceEvent>(
        replay = 0,
        extraBufferCapacity = 16,
    )
    val events: SharedFlow<CloneServiceEvent> = _events.asSharedFlow()

    val cancelRequested = AtomicBoolean(false)

    fun emitProgress(progress: CloneProgress) {
        _progress.value = progress
    }

    fun emitEvent(event: CloneServiceEvent) {
        _events.tryEmit(event)
    }

    fun requestCancel() {
        cancelRequested.set(true)
        emitEvent(CloneServiceEvent.CancelRequested)
    }

    fun resetCancel() {
        cancelRequested.set(false)
    }

    fun clear() {
        _progress.value = null
        cancelRequested.set(false)
    }

    fun stop() {
        _events.tryEmit(CloneServiceEvent.Stop)
    }
}

sealed interface CloneServiceEvent {
    data object Stop : CloneServiceEvent
    data object CancelRequested : CloneServiceEvent
}