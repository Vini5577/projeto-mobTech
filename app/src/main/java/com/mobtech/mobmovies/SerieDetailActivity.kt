package com.mobtech.mobmovies

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.mobtech.mobmovies.adapter.SerieCastAdapter
import com.mobtech.mobmovies.adapter.SerieProviderAdapter
import com.mobtech.mobmovies.adapter.SimilarSerieAdapter
import com.mobtech.mobmovies.data.SerieCast
import com.mobtech.mobmovies.data.SerieDetails
import com.mobtech.mobmovies.data.SerieProvider
import com.mobtech.mobmovies.data.SerieResponse
import com.mobtech.mobmovies.databinding.ActivitySerieDetailBinding
import com.mobtech.mobmovies.service.SerieApiService
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SerieDetailActivity : AppCompatActivity(), SerieCastAdapter.OnItemClickListener, SimilarSerieAdapter.OnItemClickListener {

    private lateinit var binding: ActivitySerieDetailBinding

    private val BASE_URL = "https://api.themoviedb.org/3/"
    private val API_KEY = "92f5a194730faec7789a4c569d9ca999"

    private lateinit var backButton: ImageView
    private lateinit var api: SerieApiService
    private val TAG: String = "CHECK_RESPONSE"

    private lateinit var isFavoriteImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySerieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }

        val serieId = intent.getIntExtra("serieId", 0);

        api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SerieApiService::class.java)

        api.getSerieDetails(serieId, API_KEY, "pt-BR").enqueue(object : Callback<SerieDetails> {

            override fun onResponse(call: Call<SerieDetails>, response: Response<SerieDetails>) {
                if (response.isSuccessful) {
                    val tvShow = response.body()

                    tvShow?.let { updateUI(it) }
                } else {
                    Log.e(TAG, "Erro ao obter detalhes da série: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SerieDetails>, t: Throwable) {
                Log.e(TAG, "Falha ao obter detalhes da série", t)
            }
        })

        val actorList = binding.actorsList

        api.getSerieCast(serieId, API_KEY, "pt-BR").enqueue(object : Callback<SerieCast> {
            override fun onResponse(call: Call<SerieCast>, response: Response<SerieCast>) {
                if (response.isSuccessful) {
                    response.body()?.cast?.let { casts ->
                        val adapter = SerieCastAdapter(casts, this@SerieDetailActivity, this@SerieDetailActivity)
                        adapter.bindView(actorList)
                    }
                } else {
                    Log.e(TAG, "Erro ao obter detalhes do filme: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SerieCast>, t: Throwable) {
                Log.e(TAG, "Falha ao obter detalhes do filme", t)
            }
        })

        val providerList = binding.providerList

        api.getSerieProvider(serieId, API_KEY, "pt-BR").enqueue(object : Callback<SerieProvider> {

            override fun onResponse(call: Call<SerieProvider>, response: Response<SerieProvider>) {
                if (response.isSuccessful) {
                    val serieProvider = response.body()?.results?.get("BR")

                    serieProvider?.let { providerData ->
                        val flatRateDataList = providerData.flatrate
                        val adapter = SerieProviderAdapter(flatRateDataList, this@SerieDetailActivity)
                        adapter.bindView(providerList)

                        Log.e(TAG, "Teste: $providerData")
                    }
                } else {
                    Log.e(TAG, "Erro ao obter provedores de serviços: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SerieProvider>, t: Throwable) {
                Log.e(TAG, "Falha ao obter provedores de serviços", t)
            }
        })

        val similarSerie = binding.similarSerie

        api.getSimilarSerie(serieId, API_KEY, "pt-BR").enqueue(object: Callback<SerieResponse> {
            override fun onResponse(call: Call<SerieResponse>, response: Response<SerieResponse>) {
                if (response.isSuccessful) {
                    response.body()?.results?.let { series ->
                        val adapter = SimilarSerieAdapter(series, this@SerieDetailActivity, this@SerieDetailActivity)
                        adapter.bindView(similarSerie)
                    }
                }
            }

            override fun onFailure(call: Call<SerieResponse>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun updateUI(serieDetails: SerieDetails?) {
        val isLikeImageView = binding.isLike
        val isDislikeImageView = binding.isDislike

        serieDetails?.let {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val releaseDate = dateFormat.parse(serieDetails?.first_air_date ?: "") ?: Date()

            val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(releaseDate)

            val dateFormatLastDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val releaseDateLastDate = dateFormatLastDate.parse(serieDetails?.last_air_date ?: "") ?: Date()

            val formattedDateLastDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(releaseDateLastDate)

            val genres = serieDetails?.genres?.joinToString(", ") { it.name }

            if(serieDetails.poster_path != null) {
                Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w500${serieDetails.poster_path}")
                    .into(binding.seriePoster)
            } else {
                Glide.with(this)
                    .load("https://www.movienewz.com/img/films/poster-holder.jpg")
                    .into(binding.seriePoster)
            }

            binding.SerieTitle.text = it.name
            binding.serieSinopse.text = it.overview
            binding.serieReleaseDate.text = formattedDate
            val text: String = it.number_of_seasons?.toString() ?: ""
            val seasonText = if (it.number_of_seasons != null && it.number_of_seasons > 1) " temporadas" else " temporada"
            binding.SerieTime.text = text + seasonText
            binding.serieCategories.text = genres
            binding.serieEpisodeRunTime.text = it.number_of_episodes.toString()
            binding.serieLastAirDate.text = formattedDateLastDate

            isFavoriteImageView = binding.isFavorite

            lifecycleScope.launch {
                val isFavorite = checkIfFavorite(serieDetails.id, "serie")
                if (isFavorite) {
                    isFavoriteImageView.setImageResource(R.drawable.start_favorited)
                } else {
                    isFavoriteImageView.setImageResource(R.drawable.star_favorite)
                }

                val isLike = checkIfLike(serieDetails.id)
                if(isLike) {
                    isLikeImageView.setImageResource(R.drawable.liked)
                }

                val isDislike = checkIfDislike(serieDetails.id)
                if(isDislike) {
                    isDislikeImageView.setImageResource(R.drawable.disliked)
                }

                binding.textAvaliation.text = "${(serieDetails.vote_average * 10).toInt()}% gostram do filme"
            }

            isFavoriteImageView.setOnClickListener({
                if(isUserLoggedIn()) {
                    lifecycleScope.launch {
                        val isFavorite: Boolean = checkIfFavorite(serieDetails.id, "serie");

                        if(isFavorite) {
                            removeFromFavorites(serieDetails.id)
                        } else {
                            addToFavorites(serieDetails.id, serieDetails)
                        }
                    }
                } else {
                    Toast.makeText(this@SerieDetailActivity, "Você precisa estar logado para favoritar esta série", Toast.LENGTH_SHORT).show()
                }
            })

            isLikeImageView.setOnClickListener {
                updateLikeStatus(serieDetails.id, "like")
            }

            isDislikeImageView.setOnClickListener {
                updateLikeStatus(serieDetails.id, "dislike")
            }

        }

        backButton = binding.backButton

        backButton.setOnClickListener({
            onBackPressed()
        })
    }

    override fun onItemClickPerson(personId: Int) {
        val intent = Intent(this, ActorActivity::class.java)
        intent.putExtra("personId", personId)
        startActivity(intent)
    }

    override fun onItemClick(serieId: Int) {
        val intent = Intent(this, SerieDetailActivity::class.java)
        intent.putExtra("serieId", serieId)
        startActivity(intent)
    }

    private fun isUserLoggedIn(): Boolean {
        return Firebase.auth.currentUser != null
    }

    private fun addToFavorites(serieId: Int, serieDetails: SerieDetails) {
        val user = Firebase.auth.currentUser
        if (user != null) {
            val favoritesRef = Firebase.firestore.collection("favorites").document(user.uid)
            val favoriteData = mapOf(
                serieId.toString() to mapOf(
                    "poster_path" to serieDetails.poster_path,
                    "nome" to serieDetails.name,
                    "titulo" to "",
                    "categoria" to "serie",
                    "vote_average" to serieDetails.vote_average
                )
            )
            favoritesRef.set(favoriteData, SetOptions.merge())
                .addOnSuccessListener {
                    isFavoriteImageView.setImageResource(R.drawable.start_favorited)
                }
                .addOnFailureListener {
                    Log.e(TAG, "Falha ao adicionar série aos favoritos", it)
                    Toast.makeText(this@SerieDetailActivity, "Erro ao favoritar a série", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun removeFromFavorites(serieId: Int) {
        isFavoriteImageView = binding.isFavorite
        val user = Firebase.auth.currentUser
        if (user != null) {
            val favoritesRef = Firebase.firestore.collection("favorites").document(user.uid)
            favoritesRef.update(serieId.toString(), FieldValue.delete())
                .addOnSuccessListener {

                    isFavoriteImageView.setImageResource(R.drawable.star_favorite)
                }
                .addOnFailureListener {
                    Log.e(TAG, "Falha ao obter detalhes da série")
                    Toast.makeText(this@SerieDetailActivity, "Você precisa estar logado para favoritar esta série", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private suspend fun checkIfFavorite(serieId: Int, category: String): Boolean {
        val user = Firebase.auth.currentUser
        if (user != null) {
            val favoritesRef = Firebase.firestore.collection("favorites").document(user.uid)
            return try {
                val document = favoritesRef.get().await()
                if (document.exists()) {
                    val favorites = document.data ?: return false
                    val verifyId = favorites.containsKey("${serieId}")
                    val serieData = favorites?.get(serieId.toString()) as Map<String, Any>
                    val serieCategory = serieData["categoria"] as String

                    return verifyId && serieCategory.equals("serie")
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao verificar favoritos", e)
                false
            }
        }
        return false
    }

    private fun updateLikeStatus(serieId: Int, action: String) {
        val isLikeImageView = binding.isLike
        val isDislikeImageView = binding.isDislike
        val user = Firebase.auth.currentUser
        if (user != null) {
            val ratingRef = Firebase.firestore.collection("rating").document(user.uid)
            ratingRef.get()
                .addOnSuccessListener { document ->
                    val serieData = document.data?.get(serieId.toString()) as? Map<String, Any>
                    if (serieData != null && serieData["categoria"] == "serie") {
                        val currentLikes = serieData["likes"] as? Long ?: 0
                        val currentDislikes = serieData["dislikes"] as? Long ?: 0

                        when (action) {
                            "like" -> {
                                if (currentLikes > 0) {
                                    // Remove o like
                                    ratingRef.update(serieId.toString(), mapOf(
                                        "likes" to FieldValue.increment(-1)
                                    )).addOnSuccessListener {
                                        isLikeImageView.setImageResource(R.drawable.like)
                                    }
                                } else {
                                    // Adiciona o like e remove o dislike, se existir
                                    ratingRef.update(serieId.toString(), mapOf(
                                        "categoria" to "serie",
                                        "likes" to FieldValue.increment(1),
                                        "dislikes" to if (currentDislikes > 0) FieldValue.increment(-1) else 0
                                    )).addOnSuccessListener {
                                        isLikeImageView.setImageResource(R.drawable.liked)
                                        if (currentDislikes > 0) {
                                            isDislikeImageView.setImageResource(R.drawable.dislike)
                                        }
                                    }
                                }
                            }
                            "dislike" -> {
                                if (currentDislikes > 0) {
                                    // Remove o dislike
                                    ratingRef.update(serieId.toString(), mapOf(
                                        "dislikes" to FieldValue.increment(-1)
                                    )).addOnSuccessListener {
                                        isDislikeImageView.setImageResource(R.drawable.dislike)
                                    }
                                } else {
                                    // Adiciona o dislike e remove o like, se existir
                                    ratingRef.update(serieId.toString(), mapOf(
                                        "categoria" to "serie",
                                        "dislikes" to FieldValue.increment(1),
                                        "likes" to if (currentLikes > 0) FieldValue.increment(-1) else 0
                                    )).addOnSuccessListener {
                                        isDislikeImageView.setImageResource(R.drawable.disliked)
                                        if (currentLikes > 0) {
                                            isLikeImageView.setImageResource(R.drawable.like)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Adiciona o filme à lista de favoritos com o like ou dislike inicial
                        val initialLike = if (action == "like") 1 else 0
                        val initialDislike = if (action == "dislike") 1 else 0
                        ratingRef.set(mapOf(
                            serieId.toString() to mapOf(
                                "categoria" to "serie",
                                "likes" to initialLike,
                                "dislikes" to initialDislike
                            )
                        ), SetOptions.merge()).addOnSuccessListener {
                            if (action == "like") {
                                isLikeImageView.setImageResource(R.drawable.liked)
                            } else {
                                isDislikeImageView.setImageResource(R.drawable.disliked)
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Erro ao atualizar status de like/dislike", e)
                    Toast.makeText(this@SerieDetailActivity, "Erro ao atualizar status de like/dislike", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private suspend fun checkIfLike(serieId: Int): Boolean {
        val user = Firebase.auth.currentUser
        if (user != null) {
            val favoritesRef = Firebase.firestore.collection("rating").document(user.uid)
            return try {
                val document = favoritesRef.get().await()
                if (document.exists()) {
                    val favorites = document.data ?: return false
                    val serieData = favorites?.get(serieId.toString()) as? Map<String, Any>
                    val serieCategory = serieData?.get("categoria") as? String
                    val serieLike = serieData?.get("likes") as? Long ?: 0

                    return serieCategory.equals("serie") && (serieLike > 0)
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao verificar likes", e)
                false
            }
        }
        return false
    }

    private suspend fun checkIfDislike(serieId: Int): Boolean {
        val user = Firebase.auth.currentUser
        if (user != null) {
            val favoritesRef = Firebase.firestore.collection("rating").document(user.uid)
            return try {
                val document = favoritesRef.get().await()
                if (document.exists()) {
                    val favorites = document.data ?: return false
                    val serieData = favorites?.get(serieId.toString()) as? Map<String, Any>
                    val serieCategory = serieData?.get("categoria") as? String
                    val serieDislike = serieData?.get("dislikes") as? Long ?: 0

                    return serieCategory.equals("serie") && (serieDislike > 0)
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao verificar dislikes", e)
                false
            }
        }
        return false
    }

}