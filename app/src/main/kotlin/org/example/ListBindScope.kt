package org.example

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume

object ListBindScope {
    suspend fun <T> List<T>.bind(): T = suspendCoroutineUninterceptedOrReturn { cont ->
        forEach { clone(cont).resume(it) }
        COROUTINE_SUSPENDED
    }
}

fun <R> list(block: suspend ListBindScope.() -> R): List<R> {
    val listResult = mutableListOf<R>()
    block.startCoroutineUninterceptedOrReturn(receiver = ListBindScope, completion = object : Continuation<R> {
        override val context: CoroutineContext get() = EmptyCoroutineContext
        override fun resumeWith(result: Result<R>) {
            listResult.add(result.getOrThrow())
        }
    })
    return listResult
}




