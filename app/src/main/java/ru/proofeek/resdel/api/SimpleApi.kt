package ru.proofeek.resdel.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import ru.proofeek.resdel.model.Post
import ru.proofeek.resdel.model.Post2

interface SimpleApi {
    @Headers(
        "typeclient:ios",
        "tenantId:68"
    )
    @GET("v1/get_news?current_time=423423322&city_id=26068")
    suspend fun getPost(): Response<Post>

    @Headers(
        "typeclient:ios",
        "tenantId:68"
    )
    @GET("v1/get_news/{idNumber}?current_time=423423322&city_id=26068")
    suspend fun getPost2(
        @Path("idNumber") number: Int
    ): Response<Post2>
}