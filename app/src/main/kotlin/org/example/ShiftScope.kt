package org.example

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume

open class ShiftScope<IR, OR> {

    var continuationResult: Result<IR> = Result.failure(IllegalStateException("continuationResult not set"))
    var resetResult: Result<OR> = Result.failure(IllegalStateException("resetResult not set"))

    suspend fun <C> shift(block: (cont: (C) -> IR) -> OR): C = suspendCoroutineUninterceptedOrReturn { cont ->
        resetResult = Result.success(
            block {
                clone(cont).resume(it)
                continuationResult.getOrThrow()
            }
        )
        COROUTINE_SUSPENDED
    }
}

fun <IR, OR> reset(block: suspend ShiftScope<IR, OR>.() -> IR): OR {
    val scope = ShiftScope<IR, OR>()
    block.startCoroutineUninterceptedOrReturn(receiver = scope, completion = object : Continuation<IR> {
        override val context: CoroutineContext get() = EmptyCoroutineContext
        override fun resumeWith(result: Result<IR>) {
            scope.continuationResult = result
        }
    })

    return scope.resetResult.getOrThrow()
}