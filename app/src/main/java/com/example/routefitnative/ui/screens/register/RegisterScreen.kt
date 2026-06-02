package com.example.routefitnative.ui.screens.register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.routefitnative.ui.components.RouteFitTextField
import com.example.routefitnative.ui.theme.RouteFitAccent
import com.example.routefitnative.ui.theme.RouteFitBackground
import com.example.routefitnative.ui.theme.RouteFitOnAccent
import com.example.routefitnative.ui.theme.RouteFitOutline
import com.example.routefitnative.ui.theme.RouteFitSurface
import com.example.routefitnative.ui.theme.RouteFitTextPrimary
import com.example.routefitnative.ui.theme.RouteFitTextSecondary

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onRegisterClick: () -> Unit = {},
    onBackToLoginClick: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var repeatPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(RouteFitBackground)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            RouteFitAccent.copy(alpha = 0.16f),
                            RouteFitBackground
                        ),
                        radius = 900f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "RouteFit",
                color = RouteFitAccent,
                style = MaterialTheme.typography.headlineLarge,
                fontStyle = FontStyle.Italic
            )

            RegisterCard(
                name = name,
                onNameChange = { name = it },
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                repeatPassword = repeatPassword,
                onRepeatPasswordChange = { repeatPassword = it },
                passwordVisible = passwordVisible,
                onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
                repeatPasswordVisible = repeatPasswordVisible,
                onRepeatPasswordVisibilityChange = { repeatPasswordVisible = !repeatPasswordVisible },
                onRegisterClick = onRegisterClick,
                onBackToLoginClick = onBackToLoginClick,
                modifier = Modifier.padding(top = 54.dp)
            )
        }
    }
}

@Composable
private fun RegisterCard(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    repeatPassword: String,
    onRepeatPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    repeatPasswordVisible: Boolean,
    onRepeatPasswordVisibilityChange: () -> Unit,
    onRegisterClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 22.dp,
                shape = RoundedCornerShape(26.dp),
                ambientColor = RouteFitAccent.copy(alpha = 0.08f),
                spotColor = RouteFitAccent.copy(alpha = 0.08f)
            )
            .border(
                BorderStroke(1.dp, RouteFitOutline),
                RoundedCornerShape(26.dp)
            ),
        color = RouteFitSurface.copy(alpha = 0.92f),
        shape = RoundedCornerShape(26.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 30.dp, vertical = 34.dp)
        ) {
            Text(
                text = "Loo konto",
                color = RouteFitTextPrimary,
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = "Loo RouteFit konto ja alusta oma teekonda juba täna",
                modifier = Modifier.padding(top = 16.dp),
                color = RouteFitTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            RouteFitTextField(
                label = "Nimi",
                value = name,
                onValueChange = onNameChange,
                placeholder = "Näidis kasutaja",
                modifier = Modifier.padding(top = 42.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            RouteFitTextField(
                label = "E-post",
                value = email,
                onValueChange = onEmailChange,
                placeholder = "kasutaja@email.ee",
                modifier = Modifier.padding(top = 28.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            RouteFitTextField(
                label = "Parool",
                value = password,
                onValueChange = onPasswordChange,
                placeholder = "........",
                modifier = Modifier.padding(top = 28.dp),
                isPassword = true,
                passwordVisible = passwordVisible,
                onPasswordVisibilityChange = onPasswordVisibilityChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            RouteFitTextField(
                label = "Korda parooli",
                value = repeatPassword,
                onValueChange = onRepeatPasswordChange,
                placeholder = "........",
                modifier = Modifier.padding(top = 28.dp),
                isPassword = true,
                passwordVisible = repeatPasswordVisible,
                onPasswordVisibilityChange = onRepeatPasswordVisibilityChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Button(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 34.dp)
                    .height(70.dp),
                shape = RoundedCornerShape(36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RouteFitAccent,
                    contentColor = RouteFitOnAccent
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "Registreeru",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kas sul on juba konto?",
                    color = RouteFitTextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
                TextButton(onClick = onBackToLoginClick) {
                    Text(
                        text = "Logi sisse",
                        color = RouteFitAccent,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
