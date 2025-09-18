package com.gity.breadmardira.ui.auth.register

import androidx.lifecycle.*
import com.gity.breadmardira.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _registerState = MutableLiveData<Result<Unit>>()
    val registerState: LiveData<Result<Unit>> get() = _registerState

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = repository.register(email, password)
        }
    }
}
