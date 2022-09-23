package com.example.notas.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notas.dao.AuthDao
import com.google.firebase.auth.FirebaseUser

class LoginViewModel : ViewModel() {

    val status = MutableLiveData<Boolean>()
    val msg = MutableLiveData<String>()

    init {
        status.value = false
    }

    fun autenticar (email: String, senha: String) {
        AuthDao.validarUser(email, senha).addOnSuccessListener {
            status.value = true
        }.addOnFailureListener {
            msg.value = it.message
        }
    }

    fun retornaUsuarioLogado(): FirebaseUser? {
        return AuthDao.getCurrentUser()
    }
}