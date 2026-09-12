package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

// Traditional Warm Rajasthani Theme Colors
val DesertOrange = Color(0xFFE07A5F)
val SandyGold = Color(0xFFF4F1DE)
val DeepCrimson = Color(0xFF81171B)
val TerracottaClay = Color(0xFFD66843)
val RoyalBlue = Color(0xFF3D5A80)
val SageGreen = Color(0xFF81B29A)
val CharcoalGray = Color(0xFF333333)

@Composable
fun FoodIllustration(tag: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val center = Offset(w / 2, h / 2)

        when (tag) {
            "haveli", "royal", "Dal Baati Churma Thali", "Grand Rajputana Thali" -> {
                // Large Steel Plate Thali
                drawCircle(
                    color = Color.LightGray,
                    radius = w * 0.48f,
                    center = center
                )
                drawCircle(
                    color = Color(0xFFE0E0E0),
                    radius = w * 0.45f,
                    center = center
                )

                // Katori 1: Spiced Dal (Yellow)
                drawCircle(
                    color = Color(0xFFFFD166),
                    radius = w * 0.14f,
                    center = Offset(w * 0.35f, h * 0.35f)
                )
                drawCircle(
                    color = DeepCrimson,
                    radius = w * 0.14f,
                    center = Offset(w * 0.35f, h * 0.35f),
                    style = Stroke(width = 4f)
                )

                // Katori 2: Spicy Garlic Chutney (Red)
                drawCircle(
                    color = Color(0xFFEF476F),
                    radius = w * 0.10f,
                    center = Offset(w * 0.65f, h * 0.35f)
                )
                drawCircle(
                    color = DeepCrimson,
                    radius = w * 0.10f,
                    center = Offset(w * 0.65f, h * 0.35f),
                    style = Stroke(width = 4f)
                )

                // Baati 1 (Golden Wheat Ball with butter)
                drawCircle(
                    color = Color(0xFFE9C46A),
                    radius = w * 0.15f,
                    center = Offset(w * 0.42f, h * 0.68f)
                )
                // Crack line in Baati
                drawPath(
                    path = Path().apply {
                        moveTo(w * 0.35f, h * 0.68f)
                        quadraticTo(w * 0.42f, h * 0.65f, w * 0.49f, h * 0.68f)
                    },
                    color = Color(0xFF8D6E63),
                    style = Stroke(width = 5f)
                )

                // Baati 2
                drawCircle(
                    color = Color(0xFFF4A261),
                    radius = w * 0.13f,
                    center = Offset(w * 0.62f, h * 0.62f)
                )
            }
            "cafe", "Barmeri Mawa Kachori", "Pyaz ki Kachori (2 Pcs)" -> {
                // Golden puff pastry with syrup glow
                drawCircle(
                    color = Color(0xFFE29578),
                    radius = w * 0.42f,
                    center = center
                )
                // Flaky lines
                drawCircle(
                    color = Color(0xFFBC6C25),
                    radius = w * 0.42f,
                    center = center,
                    style = Stroke(width = 6f)
                )

                // Sugar Glaze (White highlight translucent)
                drawCircle(
                    color = Color.White.copy(alpha = 0.5f),
                    radius = w * 0.25f,
                    center = Offset(w * 0.4f, h * 0.4f)
                )

                // Pistachio / almond toppings (Green/Brown dots)
                drawCircle(color = SageGreen, radius = 8f, center = Offset(w * 0.48f, h * 0.45f))
                drawCircle(color = SageGreen, radius = 6f, center = Offset(w * 0.54f, h * 0.48f))
                drawCircle(color = Color(0xFF8D6E63), radius = 7f, center = Offset(w * 0.46f, h * 0.52f))
            }
            "Desi Kulhad Masala Chai", "Spiced Smoked Butter Milk" -> {
                // Earthen clay Kulhad Cup
                val cupPath = Path().apply {
                    moveTo(w * 0.32f, h * 0.25f)
                    lineTo(w * 0.68f, h * 0.25f)
                    lineTo(w * 0.60f, h * 0.85f)
                    lineTo(w * 0.40f, h * 0.85f)
                    close()
                }
                // Terracotta Clay color
                drawPath(cupPath, color = Color(0xFFB07D62))

                // Rim highlighting
                drawRoundRect(
                    color = Color(0xFF8C533C),
                    topLeft = Offset(w * 0.30f, h * 0.22f),
                    size = Size(w * 0.40f, h * 0.08f),
                    cornerRadius = CornerRadius(10f, 10f)
                )

                // Tea liquid
                drawRoundRect(
                    color = Color(0xFFD4A373),
                    topLeft = Offset(w * 0.33f, h * 0.23f),
                    size = Size(w * 0.34f, h * 0.04f)
                )

                // Rising Steam lines
                drawPath(
                    path = Path().apply {
                        moveTo(w * 0.45f, h * 0.18f)
                        cubicTo(w * 0.43f, h * 0.14f, w * 0.47f, h * 0.10f, w * 0.44f, h * 0.05f)
                        moveTo(w * 0.55f, h * 0.17f)
                        cubicTo(w * 0.53f, h * 0.13f, w * 0.57f, h * 0.09f, w * 0.54f, h * 0.04f)
                    },
                    color = Color.White.copy(alpha = 0.6f),
                    style = Stroke(width = 4f)
                )
            }
            "rasoi", "Gatte ki Sabji Masala", "Ker Sangri Masala", "Rajasthani Kadhi Pakoda" -> {
                // Bowl of Rich Creamy Curry
                drawCircle(
                    color = Color(0xFFFFB703),
                    radius = w * 0.44f,
                    center = center
                )
                // Bowl Rim
                drawCircle(
                    color = TerracottaClay,
                    radius = w * 0.44f,
                    center = center,
                    style = Stroke(width = 8f)
                )

                // Gatte cylinders floating in curry
                drawRoundRect(
                    color = Color(0xFFFB8500),
                    topLeft = Offset(w * 0.32f, h * 0.35f),
                    size = Size(w * 0.16f, w * 0.08f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
                drawRoundRect(
                    color = Color(0xFFFB8500),
                    topLeft = Offset(w * 0.50f, h * 0.45f),
                    size = Size(w * 0.18f, w * 0.09f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
                drawRoundRect(
                    color = Color(0xFFFB8500),
                    topLeft = Offset(w * 0.38f, h * 0.58f),
                    size = Size(w * 0.15f, w * 0.08f),
                    cornerRadius = CornerRadius(8f, 8f)
                )

                // Coriander leaves (green drops)
                drawCircle(color = SageGreen, radius = 6f, center = Offset(center.x, center.y - 15f))
                drawCircle(color = SageGreen, radius = 5f, center = Offset(center.x + 10f, center.y + 10f))
            }
            else -> {
                // Simple Generic Burger/Food Canvas Icon
                drawCircle(color = DesertOrange, radius = w * 0.4f, center = center)
                drawCircle(color = SandyGold, radius = w * 0.3f, center = center)
                drawCircle(color = Color.White, radius = w * 0.15f, center = center)
            }
        }
    }
}

@Composable
fun BarmerFortBackdrop(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Desert Sunset Sky Gradient
        val sunsetGradient = Brush.verticalGradient(
            colors = listOf(Color(0xFF3A1C1C), Color(0xFF81171B), Color(0xFFE07A5F), Color(0xFFF4F1DE))
        )
        drawRect(brush = sunsetGradient, size = size)

        // 2. Dunes & Hills
        val hillPath1 = Path().apply {
            moveTo(0f, h)
            quadraticTo(w * 0.35f, h * 0.65f, w * 0.70f, h * 0.85f)
            quadraticTo(w * 0.88f, h * 0.92f, w, h * 0.82f)
            lineTo(w, h)
            close()
        }
        drawPath(hillPath1, color = Color(0xFFD66843).copy(alpha = 0.8f))

        // 3. Barmer Fort Silhouette on the main hill
        val fortPath = Path().apply {
            moveTo(w * 0.18f, h * 0.72f)
            // Left bastion
            lineTo(w * 0.22f, h * 0.58f)
            lineTo(w * 0.25f, h * 0.58f)
            lineTo(w * 0.25f, h * 0.61f)
            // Wall
            lineTo(w * 0.38f, h * 0.63f)
            // Main gate towers
            lineTo(w * 0.38f, h * 0.52f)
            lineTo(w * 0.44f, h * 0.52f)
            lineTo(w * 0.44f, h * 0.65f)
            // Right Wall
            lineTo(w * 0.62f, h * 0.67f)
            // Right tower
            lineTo(w * 0.62f, h * 0.55f)
            lineTo(w * 0.66f, h * 0.55f)
            lineTo(w * 0.68f, h * 0.78f)
            close()
        }
        drawPath(fortPath, color = Color(0xFF531E20))

        // 4. Foremost golden desert dune
        val dunePath = Path().apply {
            moveTo(0f, h)
            quadraticTo(w * 0.45f, h * 0.82f, w, h * 0.90f)
            lineTo(w, h)
            close()
        }
        drawPath(dunePath, color = Color(0xFFE9C46A))
    }
}

@Composable
fun BarmerMapSimulation(
    riderLat: Double,
    riderLng: Double,
    customerLat: Double,
    customerLng: Double,
    modifier: Modifier = Modifier
) {
    // Standard geographic bounds for this map simulation box
    // Left Lon: 71.385, Right Lon: 71.405
    // Top Lat: 25.758, Bottom Lat: 25.744
    val minLat = 25.744
    val maxLat = 25.758
    val minLng = 71.385
    val maxLng = 71.405

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SandyGold)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Map Lat/Lng to X/Y helper
            fun mapToOffset(lat: Double, lng: Double): Offset {
                val x = ((lng - minLng) / (maxLng - minLng)) * w
                // Latitudes increase going up, but canvas Y increases going down:
                val y = h - (((lat - minLat) / (maxLat - minLat)) * h)
                return Offset(x.toFloat(), y.toFloat())
            }

            // Draw soft grid lines representing desert land blocks
            for (i in 1..8) {
                drawLine(
                    color = Color(0xFFE9DCA3),
                    start = Offset(0f, h * i / 8f),
                    end = Offset(w, h * i / 8f),
                    strokeWidth = 2f
                )
                drawLine(
                    color = Color(0xFFE9DCA3),
                    start = Offset(w * i / 8f, 0f),
                    end = Offset(w * i / 8f, h),
                    strokeWidth = 2f
                )
            }

            // Roads simulation (Lines connecting Gandhi Chowk, Shastri Nagar, Roy Colony)
            val streetStroke = Stroke(width = 16f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            val streetBorder = Stroke(width = 24f, cap = androidx.compose.ui.graphics.StrokeCap.Round)

            // 1. Station Road (Horizontal near top)
            val road1Start = mapToOffset(25.755, 71.386)
            val road1End = mapToOffset(25.755, 71.404)
            drawLine(color = Color(0xFF8D8D8D), start = road1Start, end = road1End, strokeWidth = 24f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            drawLine(color = Color.White, start = road1Start, end = road1End, strokeWidth = 16f, cap = androidx.compose.ui.graphics.StrokeCap.Round)

            // 2. Roy Colony Road (Vertical diagonal)
            val road2Start = mapToOffset(25.757, 71.393)
            val road2End = mapToOffset(25.745, 71.393)
            drawLine(color = Color(0xFF8D8D8D), start = road2Start, end = road2End, strokeWidth = 24f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            drawLine(color = Color.White, start = road2Start, end = road2End, strokeWidth = 16f, cap = androidx.compose.ui.graphics.StrokeCap.Round)

            // 3. Shastri Nagar link road
            val road3Start = mapToOffset(25.752, 71.388)
            val road3End = mapToOffset(25.752, 71.402)
            drawLine(color = Color(0xFF8D8D8D), start = road3Start, end = road3End, strokeWidth = 24f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            drawLine(color = Color.White, start = road3Start, end = road3End, strokeWidth = 16f, cap = androidx.compose.ui.graphics.StrokeCap.Round)

            // Draw scenic spots in Barmer
            // Barmer Fort scenic circle (Top-Left on high elevation)
            val fortOffset = mapToOffset(25.757, 71.388)
            drawCircle(
                color = TerracottaClay.copy(alpha = 0.2f),
                radius = 60f,
                center = fortOffset
            )

            // Park area
            val parkOffset = mapToOffset(25.748, 71.397)
            drawCircle(
                color = SageGreen.copy(alpha = 0.4f),
                radius = 50f,
                center = parkOffset
            )

            // Draw route line from Restaurant (Gandhi Chowk: 25.7535, 71.3910) to Customer
            val restOffset = mapToOffset(25.7535, 71.3910)
            val custOffset = mapToOffset(customerLat, customerLng)
            
            // Dotted Route line
            drawPath(
                path = Path().apply {
                    moveTo(restOffset.x, restOffset.y)
                    lineTo(custOffset.x, custOffset.y)
                },
                color = RoyalBlue.copy(alpha = 0.8f),
                style = Stroke(
                    width = 6f,
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                )
            )

            // Draw Restaurant Node Marker (Red)
            drawCircle(color = DeepCrimson, radius = 24f, center = restOffset)
            drawCircle(color = Color.White, radius = 10f, center = restOffset)

            // Draw Customer Node Marker (Blue)
            drawCircle(color = RoyalBlue, radius = 24f, center = custOffset)
            drawCircle(color = Color.White, radius = 10f, center = custOffset)

            // Draw Active Rider Node Marker (Amber Scooter)
            val riderOffset = mapToOffset(riderLat, riderLng)
            drawCircle(color = DesertOrange, radius = 28f, center = riderOffset)
            drawCircle(color = Color.Black, radius = 14f, center = riderOffset)
            drawCircle(color = Color.White, radius = 8f, center = riderOffset)
        }

        // Legends overlying on map
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
                .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.size(10.dp).background(DeepCrimson, CircleShape))
                Text("Haveli Restaurant", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CharcoalGray)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.size(10.dp).background(RoyalBlue, CircleShape))
                Text("Your Address", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CharcoalGray)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.size(10.dp).background(DesertOrange, CircleShape))
                Text("Rider Scooter (Simulated)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CharcoalGray)
            }
        }

        // Overlay of current coordinates
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .background(CharcoalGray.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                "GPS: ${String.format("%.4f", riderLat)}°N, ${String.format("%.4f", riderLng)}°E",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        }
    }
}
