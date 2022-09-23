package com.example.notas.dao

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthDao {
    companion object {
        val auth = Firebase.auth

        fun getCurrentUser() = auth.currentUser

        fun cadastrarUser(email: String, senha: String): Task<AuthResult> {
            return auth.createUserWithEmailAndPassword(email, senha)
        }

        fun validarUser(email: String, senha: String): Task<AuthResult> {
            return auth.signInWithEmailAndPassword(email, senha)
        }

        fun deslogar() {
            return auth.signOut()
        }
    }
}