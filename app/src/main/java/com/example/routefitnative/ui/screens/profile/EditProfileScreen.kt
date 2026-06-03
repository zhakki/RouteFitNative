package com.example.routefitnative.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.routefitnative.ui.components.RouteFitTextField
import com.example.routefitnative.ui.theme.RouteFitAccent
import com.example.routefitnative.ui.theme.RouteFitBackground
import com.example.routefitnative.ui.theme.RouteFitInputBackground
import com.example.routefitnative.ui.theme.RouteFitOnAccent
import com.example.routefitnative.ui.theme.RouteFitOutline
import com.example.routefitnative.ui.theme.RouteFitSurface
import com.example.routefitnative.ui.theme.RouteFitSurfaceVariant
import com.example.routefitnative.ui.theme.RouteFitTextPrimary
import com.example.routefitnative.ui.theme.RouteFitTextSecondary

@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    var name by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }
    var weight by rememberSaveable { mutableStateOf("") }
    var height by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("Naine") }

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
                            RouteFitAccent.copy(alpha = 0.12f),
                            RouteFitBackground
                        ),
                        radius = 980f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp)
                .padding(top = 30.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EditProfileTopBar(onBackClick = onBackClick)

            Text(
                text = "Muuda profiili",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                color = RouteFitTextPrimary,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 34.sp,
                    lineHeight = 40.sp
                ),
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            EditProfileCard(
                modifier = Modifier.padding(top = 24.dp)
            ) {
                RouteFitTextField(
                    label = "Nimi",
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Sisesta nimi"
                )
                RouteFitTextField(
                    label = "Vanus",
                    value = age,
                    onValueChange = { age = it },
                    placeholder = "Sisesta vanus",
                    modifier = Modifier.padding(top = 18.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                RouteFitTextField(
                    label = "Kaal (kg)",
                    value = weight,
                    onValueChange = { weight = it },
                    placeholder = "Sisesta kaal",
                    modifier = Modifier.padding(top = 18.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                RouteFitTextField(
                    label = "Pikkus (cm)",
                    value = height,
                    onValueChange = { height = it },
                    placeholder = "Sisesta pikkus",
                    modifier = Modifier.padding(top = 18.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                GenderDropdown(
                    selectedGender = gender,
                    onGenderChange = { gender = it },
                    modifier = Modifier.padding(top = 18.dp)
                )
            }

            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp)
                    .height(64.dp),
                shape = RoundedCornerShape(34.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RouteFitAccent,
                    contentColor = RouteFitOnAccent
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "Salvesta",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            OutlinedButton(
                onClick = onCancelClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
                    .height(62.dp),
                shape = RoundedCornerShape(34.dp),
                border = BorderStroke(1.dp, RouteFitOutline),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = RouteFitSurfaceVariant.copy(alpha = 0.72f),
                    contentColor = RouteFitTextPrimary
                )
            ) {
                Text(
                    text = "Tühista",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun EditProfileTopBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(46.dp)
                .clickable(onClick = onBackClick),
            shape = CircleShape,
            color = RouteFitSurfaceVariant.copy(alpha = 0.82f),
            border = BorderStroke(1.dp, RouteFitOutline)
        ) {
            Box(contentAlignment = Alignment.Center) {
                BackArrowIcon(
                    modifier = Modifier.size(26.dp),
                    color = RouteFitAccent
                )
            }
        }

        Text(
            text = "RouteFit",
            modifier = Modifier.align(Alignment.Center),
            color = RouteFitAccent,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 40.sp,
                lineHeight = 46.sp
            ),
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun GenderDropdown(
    selectedGender: String,
    onGenderChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val options = listOf("Naine", "Mees")

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "SUGU",
            color = RouteFitTextSecondary,
            style = MaterialTheme.typography.labelMedium
        )
        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .height(78.dp)
                .border(
                    width = 1.dp,
                    color = RouteFitOutline,
                    shape = RoundedCornerShape(18.dp)
                )
                .background(
                    color = RouteFitInputBackground,
                    shape = RoundedCornerShape(18.dp)
                )
                .clickable { expanded = true }
                .padding(horizontal = 28.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = selectedGender,
                color = RouteFitTextPrimary,
                style = MaterialTheme.typography.bodyMedium
            )
            DownArrowIcon(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(26.dp),
                color = RouteFitAccent
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(RouteFitSurfaceVariant)
                    .border(BorderStroke(1.dp, RouteFitOutline))
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                color = RouteFitTextPrimary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        onClick = {
                            onGenderChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EditProfileCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = RouteFitAccent.copy(alpha = 0.08f),
                spotColor = RouteFitAccent.copy(alpha = 0.08f)
            )
            .border(
                BorderStroke(1.dp, RouteFitOutline),
                RoundedCornerShape(24.dp)
            ),
        color = RouteFitSurface.copy(alpha = 0.92f),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            content = content
        )
    }
}

@Composable
private fun BackArrowIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.8.dp.toPx()
        drawLine(
            color = color,
            start = Offset(size.width * 0.68f, size.height * 0.2f),
            end = Offset(size.width * 0.32f, size.height * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.32f, size.height * 0.5f),
            end = Offset(size.width * 0.68f, size.height * 0.8f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun DownArrowIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round)
        drawLine(
            color = color,
            start = Offset(size.width * 0.22f, size.height * 0.36f),
            end = Offset(size.width * 0.5f, size.height * 0.64f),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.5f, size.height * 0.64f),
            end = Offset(size.width * 0.78f, size.height * 0.36f),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round
        )
    }
}
