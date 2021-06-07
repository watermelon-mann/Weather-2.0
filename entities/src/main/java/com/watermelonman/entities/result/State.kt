package com.watermelonman.entities.result

sealed class State<T> {
    class Loading<T>: State<T>()
    class Success<T>(val data: T): State<T>()
    class Error<T>(val errorCode: ErrorCode): State<T>()
}
