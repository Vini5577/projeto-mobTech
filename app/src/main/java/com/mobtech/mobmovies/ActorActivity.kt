package com.mobtech.mobmovies

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.mobtech.mobmovies.data.MovieDetails
import com.mobtech.mobmovies.data.PersonData
import com.mobtech.mobmovies.data.PersonMovieCredits
import com.mobtech.mobmovies.databinding.ActivityActorBinding
import com.mobtech.mobmovies.databinding.FragmentMovieBinding
import com.mobtech.mobmovies.service.MovieApiService
import com.mobtech.mobmovies.service.PersonApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActorBinding
    private val BASE_URL = "https://api.themoviedb.org/3/"
    private val API_KEY = "92f5a194730faec7789a4c569d9ca999"
    private val TAG: String = "CHECK_RESPONSE"
    private lateinit var api: PersonApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PersonApiService::class.java)

        val personId = intent.getIntExtra("personId", 0);

        api.getPersonDetails(personId, API_KEY, "pt-BR").enqueue(object : Callback<PersonData> {
            override fun onResponse(call: Call<PersonData>, response: Response<PersonData>) {
                if (response.isSuccessful) {
                    val personData = response.body()
                    personData?.let {
                        updateUI(it)
                    }
                } else {
                    Log.e(TAG, "Erro ao obter detalhes da pessoa: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<PersonData>, t: Throwable) {
                Log.e(TAG, "Falha ao obter detalhes da pessoa", t)
            }
        })

        val movieCredits = binding.movieCredits

        api.getPersonMovies(personId, API_KEY, "pt-BR").enqueue(object : Callback<PersonMovieCredits>{
            override fun onResponse(call: Call<PersonMovieCredits>, response: Response<PersonMovieCredits>) {
                if (response.isSuccessful) {
                    val personData = response.cast?.body()

                } else {
                    Log.e(TAG, "Erro ao obter detalhes da pessoa: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<PersonMovieCredits>, t: Throwable) {
                Log.e(TAG, "Falha ao obter detalhes da pessoa", t)
            }
        })
    }

    fun updateUI(personData: PersonData) {
        Log.e(TAG, "$personData")
        Log.e(TAG, "${personData.profile_path}")

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val birthday = dateFormat.parse(personData?.birthday ?: "") ?: Date()

        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(birthday)

        if(personData.profile_path != null) {
            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500${personData.profile_path}")
                .into(binding.personImage)
        } else {
            Glide.with(this)
                .load("https://www.movienewz.com/img/films/poster-holder.jpg")
                .into(binding.personImage)
        }

        binding.personName.text = personData.name
        binding.personPlaceOfBirth.text = personData.place_of_birth
        binding.personGender.text = if(personData.gender == 2) "Masculino" else "Feminino"
        binding.personKnownForDepartment.text = if(personData.known_for_department.equals("Acting")) "Atuação" else personData.known_for_department
        binding.personBirthday.text = formattedDate
        binding.personBiography.text = personData.biography

    }
}