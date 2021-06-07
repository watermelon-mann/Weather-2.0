package com.watermelonman.entities.result

import com.watermelonman.entities.current.CurrentForecastData

sealed class Result<T> {
    class Success<T>(val data: T): Result<T>()
    class Error<T>(val error: CallException): Result<T>()

    override fun toString(): String {
        return when(this) {
            is Success -> "SUCCESS::${this.data}"
            is Error -> "ERROR::${this.error}"
        }
    }
}