package com.gity.breadmardira.ui.auth.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gity.breadmardira.database.AppDatabase
import com.gity.breadmardira.model.User
import com.gity.breadmardira.repository.UserRepository
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = UserRepository(AppDatabase.getInstance(application).userDao())
    private val _registerSuccess = MutableLiveData<Boolean>()
    val registerSuccess: LiveData<Boolean> = _registerSuccess

    fun register(username: String, password: String) {
        viewModelScope.launch {
            try {
                repo.register(User(username = username, password = password))
                _registerSuccess.value = true
            } catch (e: Exception) {
                _registerSuccess.value = false // misal username sudah ada
            }
        }
    }
}