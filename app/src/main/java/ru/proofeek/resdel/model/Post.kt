package ru.proofeek.resdel.model

import org.json.JSONArray
import java.lang.reflect.Array

data class Post(
    val code: Int,
    val info: String,
    val result: Result
)

data class Result(
    val list: ArrayList<NewsItem>
)

data class NewsItem(
    val id: Int,
    val active_from: Long,
    val name: String,
    val logo: String
)