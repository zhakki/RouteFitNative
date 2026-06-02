package com.example.routefitnative.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.routefitnative.ui.theme.RouteFitAccent
import com.example.routefitnative.ui.theme.RouteFitBackground
import com.example.routefitnative.ui.theme.RouteFitSurface
import com.example.routefitnative.ui.theme.RouteFitTextSecondary

@Composable
fun BottomNavigationBar(
    selectedItem: BottomNavItem = BottomNavItem.Home,
    onItemClick: (BottomNavItem) -> Unit = {}
) {
    NavigationBar(
        containerColor = RouteFitSurface,
        contentColor = RouteFitTextSecondary,
        tonalElevation = 0.dp
    ) {
        BottomNavItem.entries.forEach { item ->
            val selected = item == selectedItem

            NavigationBarItem(
                selected = selected,
                onClick = { onItemClick(item) },
                icon = {
                    BottomNavIcon(
                        item = item,
                        color = if (selected) RouteFitBackground else RouteFitTextSecondary
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = RouteFitBackground,
                    selectedTextColor = RouteFitAccent,
                    indicatorColor = RouteFitAccent,
                    unselectedIconColor = RouteFitTextSecondary,
                    unselectedTextColor = RouteFitTextSecondary
                )
            )
        }
    }
}

enum class BottomNavItem(val label: String) {
    Home("Avaleht"),
    Map("Kaart"),
    History("Ajalugu"),
    Statistics("Statistika"),
    Profile("Profiil")
}

@Composable
private fun BottomNavIcon(item: BottomNavItem, color: Color) {
    Canvas(modifier = Modifier.size(24.dp)) {
        val stroke = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
        val w = size.width
        val h = size.height

        when (item) {
            BottomNavItem.Home -> {
                val roof = Path().apply {
                    moveTo(w * 0.16f, h * 0.48f)
                    lineTo(w * 0.5f, h * 0.2f)
                    lineTo(w * 0.84f, h * 0.48f)
                }
                drawPath(path = roof, color = color, style = stroke)
                drawRoundRect(
                    color = color,
                    topLeft = Offset(w * 0.28f, h * 0.46f),
                    size = Size(w * 0.44f, h * 0.38f),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
                    style = stroke
                )
            }

            BottomNavItem.Map -> {
                drawCircle(color = color, radius = w * 0.18f, center = Offset(w * 0.5f, h * 0.42f), style = stroke)
                drawLine(color = color, start = Offset(w * 0.5f, h * 0.6f), end = Offset(w * 0.5f, h * 0.82f), strokeWidth = 2.4.dp.toPx(), cap = StrokeCap.Round)
            }

            BottomNavItem.History -> {
                drawArc(
                    color = color,
                    startAngle = 35f,
                    sweepAngle = 300f,
                    useCenter = false,
                    topLeft = Offset(w * 0.18f, h * 0.18f),
                    size = Size(w * 0.64f, h * 0.64f),
                    style = stroke
                )
                drawLine(color = color, start = Offset(w * 0.5f, h * 0.5f), end = Offset(w * 0.5f, h * 0.32f), strokeWidth = 2.4.dp.toPx(), cap = StrokeCap.Round)
                drawLine(color = color, start = Offset(w * 0.5f, h * 0.5f), end = Offset(w * 0.66f, h * 0.58f), strokeWidth = 2.4.dp.toPx(), cap = StrokeCap.Round)
            }

            BottomNavItem.Statistics -> {
                drawLine(color = color, start = Offset(w * 0.24f, h * 0.78f), end = Offset(w * 0.24f, h * 0.5f), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
                drawLine(color = color, start = Offset(w * 0.5f, h * 0.78f), end = Offset(w * 0.5f, h * 0.28f), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
                drawLine(color = color, start = Offset(w * 0.76f, h * 0.78f), end = Offset(w * 0.76f, h * 0.4f), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
            }

            BottomNavItem.Profile -> {
                drawCircle(color = color, radius = w * 0.16f, center = Offset(w * 0.5f, h * 0.34f), style = stroke)
                drawArc(
                    color = color,
                    startAngle = 205f,
                    sweepAngle = 130f,
                    useCenter = false,
                    topLeft = Offset(w * 0.26f, h * 0.52f),
                    size = Size(w * 0.48f, h * 0.36f),
                    style = stroke
                )
            }
        }
    }
}
