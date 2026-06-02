package com.example.routefitnative.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.routefitnative.ui.theme.RouteFitAccent
import com.example.routefitnative.ui.theme.RouteFitInputBackground
import com.example.routefitnative.ui.theme.RouteFitOutline
import com.example.routefitnative.ui.theme.RouteFitTextPrimary
import com.example.routefitnative.ui.theme.RouteFitTextSecondary

@Composable
fun RouteFitTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityChange: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
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
                .padding(start = 28.dp, end = if (isPassword) 70.dp else 28.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = RouteFitTextPrimary),
                cursorBrush = SolidColor(RouteFitAccent),
                keyboardOptions = keyboardOptions,
                visualTransformation = if (isPassword && !passwordVisible) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = RouteFitTextSecondary.copy(alpha = 0.35f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    innerTextField()
                }
            )
            if (isPassword && onPasswordVisibilityChange != null) {
                IconButton(
                    onClick = onPasswordVisibilityChange,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    PasswordEyeIcon(isVisible = passwordVisible)
                }
            }
        }
    }
}

@Composable
private fun PasswordEyeIcon(isVisible: Boolean) {
    Canvas(modifier = Modifier.size(34.dp)) {
        val stroke = Stroke(width = 3.dp.toPx())
        val eyeWidth = size.width * 0.82f
        val eyeHeight = size.height * 0.52f
        val left = (size.width - eyeWidth) / 2f
        val top = (size.height - eyeHeight) / 2f

        drawOval(
            color = RouteFitAccent,
            topLeft = Offset(left, top),
            size = Size(eyeWidth, eyeHeight),
            style = stroke
        )
        drawCircle(
            color = RouteFitAccent,
            radius = if (isVisible) 5.dp.toPx() else 3.dp.toPx(),
            center = center,
            style = stroke
        )
        if (!isVisible) {
            drawLine(
                color = RouteFitInputBackground,
                start = Offset(left, top + eyeHeight),
                end = Offset(left + eyeWidth, top),
                strokeWidth = 5.dp.toPx()
            )
            drawLine(
                color = RouteFitAccent,
                start = Offset(left, top + eyeHeight),
                end = Offset(left + eyeWidth, top),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}
