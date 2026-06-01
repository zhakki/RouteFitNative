package com.example.routefitnative

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.routefitnative.data.AuthRepository
import com.example.routefitnative.data.RouteRepository
import com.example.routefitnative.data.UserRepository
import com.example.routefitnative.model.UserProfile
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()
    private val routeRepository = RouteRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                AuthTestScreen(
                    authRepository = authRepository,
                    userRepository = userRepository,
                    routeRepository = routeRepository
                )
            }
        }
    }
}

@Composable
fun AuthTestScreen(
    authRepository: AuthRepository,
    userRepository: UserRepository,
    routeRepository: RouteRepository
) {
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Not logged in") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "RouteFit Native Firebase Test",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        val user = authRepository.register(email, password)

                        val profile = UserProfile(
                            uid = user.uid,
                            email = user.email ?: "",
                            fullName = "Test User"
                        )

                        userRepository.createUserProfile(profile)

                        status = "Registered and profile created: ${user.uid}"
                    } catch (e: Exception) {
                        status = "Register error: ${e.message}"
                    }
                }
            }
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        val user = authRepository.login(email, password)
                        status = "Logged in: ${user.uid}"
                    } catch (e: Exception) {
                        status = "Login error: ${e.message}"
                    }
                }
            }
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        val uid = authRepository.currentUser?.uid

                        if (uid == null) {
                            status = "No logged in user"
                        } else {
                            val routeId = routeRepository.saveTestRoute(uid)
                            status = "Test route saved: $routeId"
                        }
                    } catch (e: Exception) {
                        status = "Route error: ${e.message}"
                    }
                }
            }
        ) {
            Text("Save test route")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                authRepository.logout()
                status = "Logged out"
            }
        ) {
            Text("Logout")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = status)
    }
}