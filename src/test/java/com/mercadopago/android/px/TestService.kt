package com.mercadopago.android.px

import okhttp3.ResponseBody
import retrofit2.Response
import java.net.SocketTimeoutException

class TestService : ITestService {}

interface ITestService {
    enum class Responses {
        SOCKET_EX,
        GENERIC_EX,
        ERROR,
        SUCCESS
    }
    suspend fun apiCall(response: Responses): Response<Any> {
        return when (response) {
            Responses.SOCKET_EX -> throw(SocketTimeoutException())
            Responses.GENERIC_EX -> throw(Exception())
            Responses.SUCCESS -> Response.success(
                200,
                ResponseBody.create(null, "Testing response body")
            )
            Responses.ERROR -> Response.error<Any>(400, ResponseBody.create(null, ""))
        }
    }
}