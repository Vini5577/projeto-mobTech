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
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.mobtech.mobmovies.adapter.CommentAdapter
import com.mobtech.mobmovies.adapter.MovieAdapter
import com.mobtech.mobmovies.adapter.MovieCastAdapter
import com.mobtech.mobmovies.adapter.MovieProviderAdapter
import com.mobtech.mobmovies.adapter.SimilarMovieAdapter
import com.mobtech.mobmovies.data.MovieCast
import com.mobtech.mobmovies.data.MovieDetails
import com.mobtech.mobmovies.data.MovieProvider
import com.mobtech.mobmovies.data.MovieResponse
import com.mobtech.mobmovies.databinding.ActivityMovieDetailBinding
import com.mobtech.mobmovies.service.MovieApiService
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


class MovieDetailActivity : AppCompatActivity(), MovieAdapter.OnItemClickListener, MovieCastAdapter.OnItemClickListener {

    private lateinit var api: MovieApiService
    private val TAG: String = "CHECK_RESPONSE"

    private val BASE_URL = "https://api.themoviedb.org/3/"
    private val API_KEY = "92f5a194730faec7789a4c569d9ca999"

    private lateinit var backButton: ImageView
    private lateinit var binding: ActivityMovieDetailBinding

    private lateinit var isFavoriteImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieId = intent.getIntExtra("movieId", 0);

        val btnCommentary = findViewById<TextView>(R.id.btn_commetary)
        btnCommentary.setOnClickListener {
            val intent = Intent(this, CommentActivity::class.java).apply {
                putExtra("CONTENT_ID", movieId.toString())
                putExtra("CONTENT_TYPE", "movie")
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
            .create(MovieApiService::class.java)

        api.getMovieDetails(movieId, API_KEY, "pt-BR").enqueue(object : Callback<MovieDetails> {
            override fun onResponse(call: Call<MovieDetails>, response: Response<MovieDetails>) {
                if (response.isSuccessful) {
                    val movieDetails = response.body()
                    updateUI(movieDetails);
                } else {
                    Log.e(TAG, "Erro ao obter detalhes do filme: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MovieDetails>, t: Throwable) {
                Log.e(TAG, "Falha ao obter detalhes do filme", t)
            }
        })

        val actorList = binding.actorsList

        api.getMovieCast(movieId, API_KEY, "pt-BR").enqueue(object : Callback<MovieCast> {
            override fun onResponse(call: Call<MovieCast>, response: Response<MovieCast>) {
                if (response.isSuccessful) {
                    response.body()?.cast?.let { casts ->
                        val adapter = MovieCastAdapter(
                            casts,
                            this@MovieDetailActivity,
                            this@MovieDetailActivity
                        )
                        adapter.bindView(actorList)
                    }
                } else {
                    Log.e(TAG, "Erro ao obter detalhes do filme: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MovieCast>, t: Throwable) {
                Log.e(TAG, "Falha ao obter detalhes do filme", t)
            }
        })

        val providerList = binding.providerList

        api.getMovieProvider(movieId, API_KEY, "pt-BR").enqueue(object : Callback<MovieProvider> {

            override fun onResponse(call: Call<MovieProvider>, response: Response<MovieProvider>) {
                if (response.isSuccessful) {
                    val movieProvider = response.body()?.results?.get("BR")

                    movieProvider?.let { providerData ->
                        val flatRateDataList = providerData.flatrate
                        val adapter =
                            MovieProviderAdapter(flatRateDataList, this@MovieDetailActivity)
                        adapter.bindView(providerList)

                        Log.e(TAG, "Teste: $providerData")
                    }
                } else {
                    Log.e(TAG, "Erro ao obter provedores de serviços: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MovieProvider>, t: Throwable) {
                Log.e(TAG, "Falha ao obter provedores de serviços", t)
            }
        })

        val similarMovies = binding.similarMovie

        api.getSimilarMovie(movieId, API_KEY, "pt-BR").enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    response.body()?.results?.let { movies ->
                        val adapter = SimilarMovieAdapter(
                            movies,
                            this@MovieDetailActivity,
                            this@MovieDetailActivity
                        )
                        adapter.bindView(similarMovies)
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
            }
        })

        backButton = binding.backButton

        backButton.setOnClickListener({
            onBackPressed()
        })

        getComment(movieId) { comments ->
            val commentAdapter = CommentAdapter(comments, this)
            commentAdapter.bindView(binding.commentaryBox)
        }
    }

    private fun updateUI(movieDetails: MovieDetails?) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val releaseDate = dateFormat.parse(movieDetails?.release_date ?: "") ?: Date()

        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(releaseDate)

        val genres = movieDetails?.genres?.joinToString(", ") { it.name }

        movieDetails?.let {
            binding.MovieTitle.text = it.title
            binding.movieSinopse.text = it.overview
            binding.movieReleaseDate.text = formattedDate
            val text: String = it.runtime?.toString() ?: ""
            val minutesText = if (it.runtime != null && it.runtime > 1) " minutos" else " minuto"
            binding.movieTime.text = text + minutesText
            binding.movieCategories.text = genres

            if (movieDetails.poster_path != null) {
                Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w500${movieDetails.poster_path}")
                    .into(binding.moviePoster)
            } else {
                Glide.with(this)
                    .load("https://www.movienewz.com/img/films/poster-holder.jpg")
                    .into(binding.moviePoster)
            }

            isFavoriteImageView = binding.isFavorite

            lifecycleScope.launch {
                val isFavorite = checkIfFavorite(movieDetails.id)
                if (isFavorite) {
                    isFavoriteImageView.setImageResource(R.drawable.start_favorited)
                } else {
                    isFavoriteImageView.setImageResource(R.drawable.star_favorite)
                }
            }

            isFavoriteImageView.setOnClickListener({
                if (isUserLoggedIn()) {
                    lifecycleScope.launch {
                        val isFavorite: Boolean = checkIfFavorite(movieDetails.id);

                        if (isFavorite) {
                            removeFromFavorites(movieDetails.id)
                        } else {
                            addToFavorites(movieDetails.id, movieDetails)
                        }
                    }
                } else {
                    Toast.makeText(
                        this@MovieDetailActivity,
                        "Você precisa estar logado para favoritar esta série",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        }
    }

    override fun onItemClick(movieId: Int) {
        Log.d(TAG, "Filme selecionado - ID: $movieId")
        val intent = Intent(this, MovieDetailActivity::class.java)
        intent.putExtra("movieId", movieId)
        startActivity(intent)
    }

    override fun onItemClickPerson(personId: Int) {
        Log.d(TAG, "Filme selecionado - ID: $personId")
        val intent = Intent(this, ActorActivity::class.java)
        intent.putExtra("personId", personId)
        startActivity(intent)
    }

    private fun isUserLoggedIn(): Boolean {
        return Firebase.auth.currentUser != null
    }

    private fun addToFavorites(movieId: Int, movieDetails: MovieDetails) {
        val user = Firebase.auth.currentUser
        if (user != null) {
            val favoritesRef = Firebase.firestore.collection("favorites").document(user.uid)
            val favoriteData = mapOf(
                movieId.toString() to mapOf(
                    "poster_path" to movieDetails.poster_path,
                    "nome" to movieDetails.title,
                    "categoria" to "movie"
                )
            )
            favoritesRef.set(favoriteData, SetOptions.merge())
                .addOnSuccessListener {
                    isFavoriteImageView.setImageResource(R.drawable.start_favorited)
                }
                .addOnFailureListener {
                    Log.e(TAG, "Falha ao adicionar série aos favoritos", it)
                    Toast.makeText(
                        this@MovieDetailActivity,
                        "Erro ao favoritar a série",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun removeFromFavorites(movieId: Int) {
        isFavoriteImageView = binding.isFavorite
        val user = Firebase.auth.currentUser
        if (user != null) {
            val favoritesRef = Firebase.firestore.collection("favorites").document(user.uid)
            favoritesRef.update(movieId.toString(), FieldValue.delete())
                .addOnSuccessListener {

                    isFavoriteImageView.setImageResource(R.drawable.star_favorite)
                }
                .addOnFailureListener {
                    Log.e(TAG, "Falha ao obter detalhes da série")
                    Toast.makeText(
                        this@MovieDetailActivity,
                        "Você precisa estar logado para favoritar esta série",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private suspend fun checkIfFavorite(movieId: Int): Boolean {
        val user = Firebase.auth.currentUser
        if (user != null) {
            val favoritesRef = Firebase.firestore.collection("favorites").document(user.uid)
            return try {
                val document = favoritesRef.get().await()
                if (document.exists()) {
                    val favorites = document.data ?: return false
                    val verifyId = favorites.containsKey("${movieId}")
                    val movieData = favorites?.get(movieId.toString()) as Map<String, Any>
                    val movieCategory = movieData["categoria"] as String

                    Log.e(
                        "CHECK RESPONSE",
                        "verificando categoria: ${movieCategory.equals("movie")}"
                    )

                    return verifyId && movieCategory.equals("movie")
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

    fun getComment(movieId: Int, callback: (List<Comment>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("comentarios")
            .whereEqualTo("contentId", movieId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.w(TAG, "Erro ao obter comentários", exception)
                    return@addSnapshotListener
                }

                val comments = mutableListOf<Comment>()
                snapshot?.forEach { document ->
                    val username = document.getString("username")
                    val comentario = document.getString("comentario")
                    val dataHora = document.get("data_hora")

                    if (username != null && comentario != null && dataHora != null) {
                        comments.add(Comment(username, comentario, dataHora))
                    }
                }
                callback(comments)
            }
    }

}