package com.example.notas.ui

import androidx.lifecycle.ViewModel
import com.example.notas.dao.AuthDao

class HomeViewModel : ViewModel() {

    fun deslogar() {
        AuthDao.deslogar()
    }
}