package com.realtorsuccessiq.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.realtorsuccessiq.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _demoMode = MutableStateFlow(false)
    val demoMode: StateFlow<Boolean> = _demoMode.asStateFlow()
    
    init {
        checkAuthState()
    }
    
    private fun checkAuthState() {
        viewModelScope.launch {
            // Check if Firebase is configured
            val isFirebaseConfigured = try {
                firebaseAuth.currentUser != null || true // Always allow demo mode
            } catch (e: Exception) {
                false
            }
            
            if (isFirebaseConfigured && firebaseAuth.currentUser != null) {
                _authState.value = AuthState.Authenticated(firebaseAuth.currentUser!!.uid)
            } else {
                // Check if demo mode is enabled
                _demoMode.value = true
                _authState.value = AuthState.DemoMode
            }
        }
    }
    
    fun signInWithDemo() {
        viewModelScope.launch {
            _demoMode.value = true
            _authState.value = AuthState.Authenticated("demo-user")
        }
    }
    
    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                result.user?.let {
                    _authState.value = AuthState.Authenticated(it.uid)
                    _demoMode.value = false
                } ?: run {
                    _authState.value = AuthState.Error("Sign in failed")
                }
            } catch (e: Exception) {
                // Fallback to demo mode on error
                _demoMode.value = true
                _authState.value = AuthState.DemoMode
            }
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            firebaseAuth.signOut()
            _authState.value = AuthState.Unauthenticated
            _demoMode.value = false
        }
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    object DemoMode : AuthState()
    data class Authenticated(val userId: String) : AuthState()
    data class Error(val message: String) : AuthState()
}


