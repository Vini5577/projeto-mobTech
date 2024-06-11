package com.mobtech.mobmovies.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.mobtech.mobmovies.MovieDetailActivity
import com.mobtech.mobmovies.R
import com.mobtech.mobmovies.SearchActivity
import com.mobtech.mobmovies.adapter.FavoriteAdapter
import com.mobtech.mobmovies.data.Favorite
import com.mobtech.mobmovies.databinding.ActivityMainBinding
import com.mobtech.mobmovies.databinding.FragmentFavoritosBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavoritosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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
                val intent = Intent(requireContext(), SearchActivity::class.java)
                val bundle = Bundle()
                bundle.putInt("fragmentIndex", 2)
                intent.putExtras(bundle)
                intent.putExtra("search_query_favoritos", inputText.text.toString())
                startActivity(intent)
            }
        }

        inputText.setOnClickListener(listener)

        getFavorites()

        return view;
    }

    private fun getFavorites() {
        val user = FirebaseAuth.getInstance().currentUser

        val gridLayout = binding.layoutResults
        if (user != null) {
            val favoritesRef = FirebaseFirestore.getInstance().collection("favorites").document(user.uid)
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
                        val adapter = FavoriteAdapter(favoritesList, requireContext(), object : FavoriteAdapter.OnItemClickListener {
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
}


