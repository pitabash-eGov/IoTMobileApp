package com.foodchain.iotsolution.data.remote.interceptor

import com.foodchain.iotsolution.core.constants.AppConstants
import com.foodchain.iotsolution.data.local.DataStoreManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = runBlocking { dataStoreManager.getAuthToken() }

        val request = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .header("Authorization", "${AppConstants.TOKEN_PREFIX}$token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(request)
    }
}
