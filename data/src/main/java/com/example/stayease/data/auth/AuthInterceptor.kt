package com.example.stayease.data.auth
import com.example.stayease.core.result.AppResult
import com.example.stayease.domain.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val auth: AuthRepository) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val tok = runBlocking { auth.ensureValidTokens() }
    val req = when (tok) {
      is AppResult.Ok -> chain.request().newBuilder().addHeader("Authorization", "Bearer ${tok.value.accessToken}").build()
      else -> chain.request()
    }
    val res = chain.proceed(req)
    if (res.code == 401) {
      res.close()
      val refreshed = runBlocking { auth.forceRefresh() }
      if (refreshed is AppResult.Ok) {
        val retry = chain.request().newBuilder().addHeader("Authorization", "Bearer ${refreshed.value.accessToken}").build()
        return chain.proceed(retry)
      }
    }
    return res
  }
}
