package com.mobtech.mobmovies

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
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
import com.mobtech.mobmovies.adapter.SimilarMovieAdapter
import com.mobtech.mobmovies.adapter.SimilarSerieAdapter
import com.mobtech.mobmovies.data.MovieResponse
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

        val serieId = intent.getIntExtra("serieId", 0);

        val btnCommentary = findViewById<TextView>(R.id.btn_commetary)
        btnCommentary.setOnClickListener {
            val intent = Intent(this, CommentActivity::class.java).apply {
                putExtra("CONTENT_ID", serieId.toString())
                putExtra("CONTENT_TYPE", "serie")
            }
            startActivity(intent)
        }

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }

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
                    val movieProvider = response.body()?.results?.get("BR")

                    movieProvider?.let { providerData ->
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

        val similarMovies = binding.similarSerie

        api.getSimilarSerie(serieId, API_KEY, "pt-BR").enqueue(object: Callback<SerieResponse> {
            override fun onResponse(call: Call<SerieResponse>, response: Response<SerieResponse>) {
                if (response.isSuccessful) {
                    response.body()?.results?.let { series ->
                        val adapter = SimilarSerieAdapter(series, this@SerieDetailActivity, this@SerieDetailActivity)
                        adapter.bindView(similarMovies)
                    }
                }
            }

            override fun onFailure(call: Call<SerieResponse>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun updateUI(serieDetails: SerieDetails) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val releaseDate = dateFormat.parse(serieDetails?.first_air_date ?: "") ?: Date()

        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(releaseDate)

        val dateFormatLastDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val releaseDateLastDate = dateFormatLastDate.parse(serieDetails?.last_air_date ?: "") ?: Date()

        val formattedDateLastDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(releaseDateLastDate)

        val genres = serieDetails?.genres?.joinToString(", ") { it.name }

       serieDetails?.let {
            binding.SerieTitle.text = it.name
            binding.serieSinopse.text = it.overview
            binding.serieReleaseDate.text = formattedDate
            val text: String = it.number_of_seasons?.toString() ?: ""
            val seasonText = if (it.number_of_seasons != null && it.number_of_seasons > 1) " temporadas" else " temporada"
            binding.SerieTime.text = text + seasonText
            binding.serieCategories.text = genres
            binding.serieEpisodeRunTime.text = it.number_of_episodes.toString()
            binding.serieLastAirDate.text = formattedDateLastDate

            if(serieDetails.poster_path != null) {
                Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w500${serieDetails.poster_path}")
                    .into(binding.seriePoster)
            } else {
                Glide.with(this)
                    .load("https://www.movienewz.com/img/films/poster-holder.jpg")
                    .into(binding.seriePoster)
            }

           lifecycleScope.launch {
               val isFavorite = checkIfFavorite(serieDetails.id, "serie")
               if (isFavorite) {
                   isFavoriteImageView.setImageResource(R.drawable.start_favorited)
               } else {
                   isFavoriteImageView.setImageResource(R.drawable.star_favorite)
               }
           }

        }

        backButton = binding.backButton

        backButton.setOnClickListener({
            onBackPressed()
        })

        isFavoriteImageView = binding.isFavorite

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
                    "categoria" to "serie"
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

}