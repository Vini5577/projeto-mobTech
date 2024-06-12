package com.mobtech.mobmovies

class ColorRating {

    fun getColorForRating(rating: Int): Int {
        val green = 0x00FF00 // Cor verde
        val red = 0xFF0000 // Cor vermelha

        val redPart = ((100 - rating) / 100f) * 255
        val greenPart = (rating / 100f) * 255

        return android.graphics.Color.rgb(redPart.toInt(), greenPart.toInt(), 0)
    }
}