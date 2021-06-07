package com.watermelonman.entities.result

class CallException(
    val errorCode: ErrorCode,
    override val message: String
): Exception(message) {

    override fun toString(): String {
        return "errorCode = $errorCode :|: $message"
    }
}