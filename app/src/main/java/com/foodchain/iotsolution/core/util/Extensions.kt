package com.foodchain.iotsolution.core.util

import android.util.Patterns
import retrofit2.Response

fun String.isValidEmail(): Boolean = Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidPassword(): Boolean = this.length >= 6

fun <T> Response<T>.toResource(): Resource<T> {
    return if (isSuccessful) {
        body()?.let { Resource.Success(it) } ?: Resource.Error("Empty response body")
    } else {
        Resource.Error(errorBody()?.string() ?: "Unknown error occurred")
    }
}
