package ru.proofeek.resdel.repository

import retrofit2.Response
import ru.proofeek.resdel.api.RetrofitInstance
import ru.proofeek.resdel.model.Post

class Repository {

    suspend fun  getPost(): Response<Post> {
        return RetrofitInstance.api.getPost()
    }
}