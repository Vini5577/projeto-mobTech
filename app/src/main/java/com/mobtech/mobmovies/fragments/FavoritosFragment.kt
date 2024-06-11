package com.mobtech.mobmovies.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mobtech.mobmovies.MovieDetailActivity
import com.mobtech.mobmovies.R
import com.mobtech.mobmovies.SearchActivity
import com.mobtech.mobmovies.SerieDetailActivity
import com.mobtech.mobmovies.adapter.FavoriteAdapter
import com.mobtech.mobmovies.data.Favorite
import com.mobtech.mobmovies.databinding.FragmentFavoritosBinding

class FavoritosFragment : Fragment() {

    private lateinit var binding: FragmentFavoritosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritosBinding.inflate(layoutInflater);
        val view = binding.root

        val inputText: EditText = view.findViewById(R.id.busca_favoritos)

        inputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Não é necessário implementar este método
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    getFavorites()
                } else {
                    searchFavorites(query)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Não é necessário implementar este método
            }
        })

        getFavorites()

        return view;
    }

    private fun getFavorites() {
        val user = FirebaseAuth.getInstance().currentUser

        val gridLayout = binding.layoutResults
        if (user != null) {
            val favoritesRef = FirebaseFirestore.getInstance().collection("favorites").document(user.uid)
            favoritesRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("CHECK RESPONSE", "Erro ao obter favoritos em tempo real", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val favoritesMap = snapshot.data
                    val favoritesList = mutableListOf<Favorite>()
                    favoritesMap?.keys?.forEach { key ->
                        val favoriteData = favoritesMap[key] as? Map<String, Any>
                        favoriteData?.let {
                            val favorite = Favorite(
                                key.toInt(),
                                it["categoria"] as? String ?: "",
                                it["poster_path"] as? String ?: "",
                                it["nome"] as? String ?: ""
                            )
                            favoritesList.add(favorite)
                        }
                    }
                    // Atualiza a GridLayout com os favoritos obtidos
                    val adapter = FavoriteAdapter(favoritesList, requireContext(), object : FavoriteAdapter.OnItemClickListener {
                        override fun onItemClick(id: Int) {

                        }
                    })
                    adapter.bindView(gridLayout, favoritesList)
                } else {
                    Log.d("CHECK RESPONSE", "Nenhum favorito encontrado")
                }
            }
        }
    }

    private fun searchFavorites(query: String) {
        val user = FirebaseAuth.getInstance().currentUser

        val gridLayout = binding.layoutResults
        if (user != null) {
            val favoritesRef =
                FirebaseFirestore.getInstance().collection("favorites").document(user.uid)
            favoritesRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val favoritesMap = document.data
                        val favoritesList = mutableListOf<Favorite>()
                        favoritesMap?.keys?.forEach { key ->
                            val favoriteData = favoritesMap[key] as? Map<String, Any>
                            favoriteData?.let {
                                val favorite = Favorite(
                                    key.toInt(),
                                    it["categoria"] as? String ?: "",
                                    it["poster_path"] as? String ?: "",
                                    it["nome"] as? String ?: ""
                                )
                                if (favorite.nome.contains(query, ignoreCase = true)) {
                                    favoritesList.add(favorite)
                                }
                            }
                        }
                        // Atualiza a GridLayout com os favoritos filtrados
                        val adapter = FavoriteAdapter(
                            favoritesList,
                            requireContext(),
                            object : FavoriteAdapter.OnItemClickListener {
                                override fun onItemClick(id: Int) {
                                    val selectedFavorite = favoritesList.find { it.id == id }
                                    selectedFavorite?.let {
                                        when (it.categoria) {
                                            "filme" -> {
                                                val intent = Intent(
                                                    requireContext(),
                                                    MovieDetailActivity::class.java
                                                )
                                                intent.putExtra("movieId", it.id)
                                                startActivity(intent)
                                            }

                                            "serie" -> {
                                                val intent = Intent(
                                                    requireContext(),
                                                    SerieDetailActivity::class.java
                                                )
                                                intent.putExtra("serieId", it.id)
                                                startActivity(intent)
                                            }

                                            else -> {
                                                // Categoria desconhecida
                                                Log.e(
                                                    "FavoriteAdapter",
                                                    "Categoria desconhecida: ${it.categoria}"
                                                )
                                            }
                                        }
                                    }
                                }
                            })
                        adapter.bindView(gridLayout, favoritesList)
                    } else {
                        Log.d("CHECK RESPONSE", "Nenhum favorito encontrado")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("CHECK RESPONSE", "Erro ao obter favoritos", e)
                }
        }
    }


    override fun onResume() {
        super.onResume()
        getFavorites()
    }
}
