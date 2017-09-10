package com.warrior.jetbrains.test.model

sealed class Result<out T, out E>

data class Ok<out T>(val value: T) : Result<T, Nothing>()
data class Err<out E>(val error: E) : Result<Nothing, E>()

inline fun <T, E, R> Result<T, E>.andThen(action: (T) -> Result<R, E>): Result<R, E> = when (this) {
    is Ok -> action(value)
    is Err -> this
}
