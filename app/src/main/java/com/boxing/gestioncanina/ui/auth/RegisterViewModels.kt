package com.boxing.gestioncanina.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boxing.gestioncanina.data.model.RegisterRequest
import com.boxing.gestioncanina.data.model.RegisterResult
import com.boxing.gestioncanina.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState

    fun registerUser(
        name: String,
        email: String,
        location: String,
        password: String,
        confirmPassword: String
    ) {
        // Validaciones
        when {
            name.isEmpty() || email.isEmpty() || location.isEmpty() ||
                    password.isEmpty() || confirmPassword.isEmpty() -> {
                _registerState.value = RegisterState.Error("Por favor completa todos los campos")
                return
            }
            password != confirmPassword -> {
                _registerState.value = RegisterState.Error("Las contraseñas no coinciden")
                return
            }
            password.length < 6 -> {
                _registerState.value = RegisterState.Error("La contraseña debe tener al menos 6 caracteres")
                return
            }
        }

        _registerState.value = RegisterState.Loading

        viewModelScope.launch {
            val request = RegisterRequest(
                name = name,
                email = email,
                location = location,
                password = password
            )

            val result = authRepository.registerUser(request)

            _registerState.value = if (result.success) {
                RegisterState.Success(result.message)
            } else {
                RegisterState.Error(result.message)
            }
        }
    }

    sealed class RegisterState {
        object Loading : RegisterState()
        data class Success(val message: String) : RegisterState()
        data class Error(val message: String) : RegisterState()
    }
}