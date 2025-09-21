package com.example.p15_eventorias.utils

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object SignedInEventBus {
    private val _googleSignInTasks = MutableSharedFlow<Task<GoogleSignInAccount>?>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val googleSignInTasks = _googleSignInTasks.asSharedFlow()

    @OptIn(DelicateCoroutinesApi::class)
    fun postGoogleTask(task: Task<GoogleSignInAccount>?) {
        GlobalScope.launch {
            _googleSignInTasks.emit(task)
        }
    }
}