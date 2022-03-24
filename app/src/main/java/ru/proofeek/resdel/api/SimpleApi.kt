package ru.proofeek.resdel.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import ru.proofeek.resdel.model.Post

interface SimpleApi {
    @Headers(
        "typeclient:ios",
        "tenantId:68"
    )
    @GET("v1/get_news?current_time=423423322&city_id=26068")

    suspend fun getPost(): Response<Post>
}