package com.gity.breadmardira.ui.auth.login

import androidx.lifecycle.*
import com.gity.breadmardira.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    // <- String, bukan Unit
    private val _loginState = MutableLiveData<Result<String>>()
    val loginState: LiveData<Result<String>> get() = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.login(email, password)
            if (result.isSuccess) {
                val uid = repository.currentUser?.uid.orEmpty()
                val role = repository.getUserRole(uid)   // <- harus String
                _loginState.value = Result.success(role)
            } else {
                _loginState.value = Result.failure(result.exceptionOrNull()!!)
            }
        }
    }
}

