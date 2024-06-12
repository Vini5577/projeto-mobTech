package com.mobtech.mobmovies

data class Comment(
    val commentId: String,
    val username: String,
    val comentario: String,
    val dataHora: Long = 0L,
    val contentId: Int = 0,
    val contentType: String,
    val userId: String,
)