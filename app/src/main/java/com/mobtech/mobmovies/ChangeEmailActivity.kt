package com.mobtech.mobmovies

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mobtech.mobmovies.databinding.ActivityChangeEmailBinding

class ChangeEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangeEmailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnChangeEmail.setOnClickListener({
            val newEmail = binding.newEmail.text.toString()
            val repeatEmail = binding.repeatNewEmail.text.toString()
            val password = binding.password.text.toString()

            if (TextUtils.isEmpty(newEmail) || TextUtils.isEmpty(repeatEmail) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Todos os campos são obrigatórios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!newEmail.equals(repeatEmail)) {
                Toast.makeText(this, "Os emails devem ser iguais!!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = auth.currentUser

            if(user != null && user.email != null) {
                val credential = EmailAuthProvider.getCredential(user.email.toString(), password)

                user!!.reauthenticate(credential).addOnCompleteListener({task ->
                    if(task.isSuccessful) {
                        user?.updateEmail(newEmail)?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userRef = db.collection("users").document(user.uid)

                                userRef.update("email", newEmail).addOnSuccessListener {
                                    Toast.makeText(this, "E-mail atualizado com sucesso", Toast.LENGTH_SHORT).show()
                                    auth.signOut()
                                    startActivity(Intent(this, LoginActivity::class.java))
                                }.addOnFailureListener { e ->
                                    Toast.makeText(this, "Erro ao atualizar o e-mail.", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this, "Erro ao atualizar o e-mail", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "A senha está incorreta!", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        })


    }
}