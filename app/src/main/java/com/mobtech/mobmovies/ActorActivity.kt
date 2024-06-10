package com.mobtech.mobmovies

import android.content.Intent
import android.net.ParseException
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.mobtech.mobmovies.adapter.MovieCompactAdapter
import com.mobtech.mobmovies.adapter.SerieCompactAdapter
import com.mobtech.mobmovies.data.PersonData
import com.mobtech.mobmovies.data.PersonMovieCredits
import com.mobtech.mobmovies.data.PersonTVCredits
import com.mobtech.mobmovies.databinding.ActivityActorBinding
import com.mobtech.mobmovies.service.PersonApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale

class ActorActivity : AppCompatActivity(), MovieCompactAdapter.OnItemClickListener, SerieCompactAdapter.OnItemClickListener {

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
                    val personData = response.body()?.cast
                    val adapter =
                        personData?.let { MovieCompactAdapter(it, this@ActorActivity, this@ActorActivity) }
                    adapter?.bindView(movieCredits)

                } else {
                    Log.e(TAG, "Erro ao obter detalhes da pessoa: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<PersonMovieCredits>, t: Throwable) {
                Log.e(TAG, "Falha ao obter detalhes da pessoa", t)
            }
        })

        val serieCredits = binding.serieCredits
        api.getPersonTVs(personId, API_KEY, "pt-BR").enqueue(object : Callback<PersonTVCredits> {
            override fun onResponse(call: Call<PersonTVCredits>, response: Response<PersonTVCredits>) {
                if (response.isSuccessful) {
                    val personTVData = response.body()?.cast
                    Log.e(TAG, "Aqui os créditos de TV: $personTVData")
                    val adapter = personTVData?.let { SerieCompactAdapter(it, this@ActorActivity, this@ActorActivity) }
                    adapter?.bindView(serieCredits)
                } else {
                    Log.e(TAG, "Erro ao obter créditos de TV da pessoa: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<PersonTVCredits>, t: Throwable) {
                Log.e(TAG, "Falha ao obter créditos de TV da pessoa", t)
            }
        })
    }

    fun updateUI(personData: PersonData) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = if (!personData.birthday.isNullOrEmpty()) {
            try {
                val birthday = dateFormat.parse(personData.birthday)
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(birthday)
            } catch (e: ParseException) {
                Log.e(TAG, "Data inválida: ${personData.birthday}", e)
                ""
            }
        } else {
            ""
        }

        if (personData.profile_path != null) {
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
        binding.personGender.text = if (personData.gender == 2) "Masculino" else "Feminino"
        binding.personKnownForDepartment.text = if (personData.known_for_department == "Acting") "Atuação" else personData.known_for_department
        binding.personBirthday.text = formattedDate
        binding.personBiography.text = personData.biography
    }

    override fun onItemClick(movieId: Int) {
        val intent = Intent(this, MovieDetailActivity::class.java)
        intent.putExtra("movieId", movieId)
        startActivity(intent)
    }

    override fun onItemClickSerie(serieId: Int) {
        val intent = Intent(this, SerieDetailActivity::class.java)
        intent.putExtra("serieId", serieId)
        startActivity(intent)
    }
}