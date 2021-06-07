package com.watermelonman.data.utils

import com.watermelonman.entities.result.CallException
import com.watermelonman.entities.result.ErrorCode
import retrofit2.Response
import java.io.IOException
import com.watermelonman.entities.result.Result
import kotlinx.coroutines.withTimeoutOrNull

suspend fun<T> safeApiCall(
    timeOut: Long = 7_000,
    request: suspend () -> Response<T>
): Result<T> {
    return withTimeoutOrNull(timeOut) {
        return@withTimeoutOrNull try {
            if (!isNetworkAvailable) throw CallException(ErrorCode.NO_CONNECTION, "No Network Connection")
            val response = request()
            if (!response.isSuccessful) throw CallException(ErrorCode.RESPONSE_UNSUCCESSFUL, "Response unsuccessful: CODE:${response.code()} :: MESSAGE:${response.message()}")
            val result = response.body() ?: throw CallException(ErrorCode.NULL_DATA, "Response body is null: CODE:${response.code()} :: MESSAGE:${response.message()}")
            Result.Success(result)
        }catch (t: Throwable) {
            when(t) {
                is CallException -> Result.Error(t)
                is IOException -> Result.Error(
                    CallException(ErrorCode.DATA_CONVERSION, "Data Conversion Error :: ${t.message}")
                )
                else -> Result.Error(
                    CallException(ErrorCode.UNKNOWN, "Unknown Error :: ${t.message}")
                )
            }
        }
    } ?: Result.Error(CallException(ErrorCode.TIMED_OUT, "Request timed out"))
}