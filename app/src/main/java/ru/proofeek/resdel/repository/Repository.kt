package ru.proofeek.resdel.repository

import retrofit2.Response
import ru.proofeek.resdel.api.RetrofitInstance
import ru.proofeek.resdel.model.Post
import ru.proofeek.resdel.model.Post2

class Repository {

    suspend fun  getPost(): Response<Post> {
        return RetrofitInstance.api.getPost()
    }

    suspend fun  getPost2(number: Int): Response<Post2> {
        return RetrofitInstance.api.getPost2(number)
    }
}