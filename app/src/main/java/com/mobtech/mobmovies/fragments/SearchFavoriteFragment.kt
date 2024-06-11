package com.mobtech.mobmovies.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mobtech.mobmovies.MovieDetailActivity
import com.mobtech.mobmovies.R
import com.mobtech.mobmovies.SerieDetailActivity
import com.mobtech.mobmovies.adapter.FavoriteAdapter
import com.mobtech.mobmovies.data.Favorite
import com.mobtech.mobmovies.databinding.FragmentFavoritosBinding
import com.mobtech.mobmovies.databinding.FragmentSearchFavoriteBinding
import com.mobtech.mobmovies.databinding.FragmentSearchMovieBinding
import com.mobtech.mobmovies.databinding.FragmentSearchSeriesBinding

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFavoriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFavoriteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private lateinit var binding: FragmentSearchFavoriteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchFavoriteBinding.inflate(layoutInflater);
        val view = binding.root

        val inputText: EditText = view.findViewById(R.id.busca_favorite_title)


        val listener = View.OnClickListener {
            if (inputText.text.isBlank()) {
                val alertDialogBuilder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.setTitle("Atenção")
                alertDialogBuilder.setMessage("O campo de busca está vazio. Por favor, insira um termo de busca.")
                alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            } else {
                searchFavorites(inputText.text.toString())
            }
        }

        inputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Não é necessário implementar este método
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    // Limpar a lista de resultados
                    getFavorites()
                } else {
                    // Realizar a busca em tempo real
                    searchFavorites(query)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Não é necessário implementar este método
            }
        })

        inputText.setOnClickListener(listener)

        getFavorites()


        return view
    }

    private fun getFavorites() {
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
                                favoritesList.add(favorite)
                            }
                        }
                        // Atualiza a GridLayout com os favoritos obtidos
                        val adapter = FavoriteAdapter(
                            favoritesList,
                            requireContext(),
                            object : FavoriteAdapter.OnItemClickListener {
                                override fun onItemClick(id: Int) {

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

    private fun clearResults() {
        val gridLayout = binding.layoutResults
        gridLayout.removeAllViews()
    }
}