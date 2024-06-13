package com.mobtech.mobmovies

import com.google.firebase.Timestamp

data class Comment(
    val username: String = "",
    val comentario: String = "",
    val data_hora: String = "",
    val contentId: Int = 0,
    val contentType: String = ""
)