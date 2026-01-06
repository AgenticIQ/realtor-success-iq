package com.realtorsuccessiq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.realtorsuccessiq.data.repository.DataInitializer
import com.realtorsuccessiq.ui.auth.AuthViewModel
import com.realtorsuccessiq.ui.auth.AuthScreen
import com.realtorsuccessiq.ui.navigation.MainScreen
import com.realtorsuccessiq.ui.theme.RealtorSuccessTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize data on first launch - will be injected via Hilt
        val dataInitializer = (application as RealtorSuccessApplication).getDataInitializer()
        CoroutineScope(Dispatchers.IO).launch {
            dataInitializer?.initializeIfNeeded()
        }
        
        setContent {
            val authViewModel: AuthViewModel = viewModel()
            val authState by authViewModel.authState.collectAsStateWithLifecycle()
            val agentAuthState = authState
            
            RealtorSuccessTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Check if admin mode
                    var isAdminMode by remember { mutableStateOf(false) }
                    val adminAuthViewModel: com.realtorsuccessiq.ui.admin.AdminAuthViewModel = viewModel()
                    val adminAuthState by adminAuthViewModel.authState.collectAsStateWithLifecycle()
                    val brokerAuthState = adminAuthState
                    
                    when {
                        isAdminMode -> {
                            when (brokerAuthState) {
                                is com.realtorsuccessiq.ui.admin.AdminAuthState.Unauthenticated -> {
                                    com.realtorsuccessiq.ui.admin.AdminAuthScreen(
                                        onSignIn = { email, password ->
                                            adminAuthViewModel.signIn(email, password)
                                        },
                                        onRegister = { brokerageName, adminEmail, password, phone, address ->
                                            adminAuthViewModel.registerBrokerage(
                                                brokerageName = brokerageName,
                                                adminEmail = adminEmail,
                                                password = password,
                                                phone = phone,
                                                address = address
                                            )
                                        },
                                        onBackToAgentLogin = { isAdminMode = false }
                                    )
                                }
                                is com.realtorsuccessiq.ui.admin.AdminAuthState.Authenticated -> {
                                    com.realtorsuccessiq.ui.admin.AdminMainScreen(
                                        brokerageId = brokerAuthState.brokerageId,
                                        onSignOut = {
                                            adminAuthViewModel.signOut()
                                            isAdminMode = false
                                        }
                                    )
                                }
                                is com.realtorsuccessiq.ui.admin.AdminAuthState.Error -> {
                                    com.realtorsuccessiq.ui.admin.AdminAuthScreen(
                                        onSignIn = { email, password ->
                                            adminAuthViewModel.signIn(email, password)
                                        },
                                        onRegister = { brokerageName, adminEmail, password, phone, address ->
                                            adminAuthViewModel.registerBrokerage(
                                                brokerageName = brokerageName,
                                                adminEmail = adminEmail,
                                                password = password,
                                                phone = phone,
                                                address = address
                                            )
                                        },
                                        onBackToAgentLogin = { isAdminMode = false },
                                        errorMessage = brokerAuthState.message
                                    )
                                }
                                else -> {}
                            }
                        }
                        else -> {
                            when (agentAuthState) {
                                is com.realtorsuccessiq.ui.auth.AuthState.Loading -> {
                                    CircularProgressIndicator()
                                }
                                is com.realtorsuccessiq.ui.auth.AuthState.Unauthenticated,
                                is com.realtorsuccessiq.ui.auth.AuthState.DemoMode -> {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End
                                        ) {
                                            TextButton(onClick = { isAdminMode = true }) {
                                                Text("Admin Login")
                                            }
                                        }
                                        AuthScreen(
                                            onSignIn = { authViewModel.signInWithDemo() },
                                            onEmailSignIn = { email, password ->
                                                authViewModel.signInWithEmail(email, password)
                                            }
                                        )
                                    }
                                }
                                is com.realtorsuccessiq.ui.auth.AuthState.Authenticated -> {
                                    MainScreen()
                                }
                                is com.realtorsuccessiq.ui.auth.AuthState.Error -> {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End
                                        ) {
                                            TextButton(onClick = { isAdminMode = true }) {
                                                Text("Admin Login")
                                            }
                                        }
                                        AuthScreen(
                                            onSignIn = { authViewModel.signInWithDemo() },
                                            onEmailSignIn = { email, password ->
                                                authViewModel.signInWithEmail(email, password)
                                            },
                                            errorMessage = agentAuthState.message
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
