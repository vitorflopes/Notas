package com.example.notas.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notas.dao.AuthDao

class CadastroViewModel : ViewModel() {

    val status = MutableLiveData<Boolean>()
    val msg = MutableLiveData<String>()

    init {
        status.value = false
    }

    fun cadastrarUser(email: String, senha: String) {
        AuthDao.cadastrarUser(email, senha).addOnSuccessListener {
            status.value = true
        }.addOnFailureListener {
            msg.value = it.message
        }
    }
}