package com.example.notas.ui

import androidx.lifecycle.ViewModel
import com.example.notas.dao.AuthDao
import com.google.firebase.auth.FirebaseUser

class HomeViewModel : ViewModel() {

    fun deslogar() {
        AuthDao.deslogar()
    }

    fun retornaUsuario(): FirebaseUser? {
        return AuthDao.getCurrentUser()
    }
}