package ru.proofeek.resdel.model

data class Post2 (
    val code: Int,
    val info: String,
    val result: ResultNews
)

data class ResultNews(
    val id: Int,
    val active_from: Long,
    val name: String,
    val logo: String,
    val description: String
)