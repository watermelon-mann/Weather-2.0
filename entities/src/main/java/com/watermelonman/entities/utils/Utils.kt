package com.watermelonman.entities.utils


fun checkIfNullByDisjunction(vararg objects: Any?): Boolean {
    for (obj in objects) {
        if (obj == null) return true
    }
    return false
}