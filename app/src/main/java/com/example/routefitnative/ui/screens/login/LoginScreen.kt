package com.example.routefitnative.ui.screens.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.example.routefitnative.ui.components.RouteFitPrimaryButton
import com.example.routefitnative.ui.components.RouteFitSocialButton
import com.example.routefitnative.ui.components.RouteFitTextField
import com.example.routefitnative.ui.theme.RouteFitAccent
import com.example.routefitnative.ui.theme.RouteFitBackground
import com.example.routefitnative.ui.theme.RouteFitOutline
import com.example.routefitnative.ui.theme.RouteFitSurface
import com.example.routefitnative.ui.theme.RouteFitTextPrimary
import com.example.routefitnative.ui.theme.RouteFitTextSecondary

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

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

            Text(
                text = "Täpselt loodud tänapäeva sportlasele.",
                modifier = Modifier.padding(top = 24.dp, start = 20.dp, end = 20.dp),
                color = RouteFitTextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold
            )

            LoginCard(
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                passwordVisible = passwordVisible,
                onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
                modifier = Modifier.padding(top = 54.dp)
            )
        }
    }
}

@Composable
private fun LoginCard(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
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
                text = "Tere tagasi!",
                color = RouteFitTextPrimary,
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = "Jälgi oma aktiivsust ja tulemusi.",
                modifier = Modifier.padding(top = 16.dp),
                color = RouteFitTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            RouteFitTextField(
                label = "E-post",
                value = email,
                onValueChange = onEmailChange,
                placeholder = "name@athlete.com",
                modifier = Modifier.padding(top = 42.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            RouteFitTextField(
                label = "Parool",
                value = password,
                onValueChange = onPasswordChange,
                placeholder = "........",
                modifier = Modifier.padding(top = 34.dp),
                isPassword = true,
                passwordVisible = passwordVisible,
                onPasswordVisibilityChange = onPasswordVisibilityChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            TextButton(
                onClick = {},
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 24.dp)
            ) {
                Text(
                    text = "Unustasid parooli?",
                    color = RouteFitAccent,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            RouteFitPrimaryButton(
                text = "Logi sisse",
                modifier = Modifier.padding(top = 22.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Uus RouteFitis?",
                    color = RouteFitTextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
                TextButton(onClick = {}) {
                    Text(
                        text = "Loo konto",
                        color = RouteFitAccent,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .weight(1f)
                        .background(RouteFitOutline)
                )
                Text(
                    text = "VÕI JÄTKA",
                    modifier = Modifier.padding(horizontal = 18.dp),
                    color = RouteFitTextSecondary,
                    style = MaterialTheme.typography.labelMedium
                )
                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .weight(1f)
                        .background(RouteFitOutline)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                RouteFitSocialButton(
                    provider = "Google",
                    mark = "G",
                    modifier = Modifier.weight(1f)
                )
                RouteFitSocialButton(
                    provider = "Apple",
                    mark = "A",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.size(4.dp))
        }
    }
}
