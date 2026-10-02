package com.scorpions.agriscan.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scorpions.agriscan.R
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDiagnose: () -> Unit,
    onNavigateToLogin: () -> Unit,
    isUrdu: Boolean
) {
    Box(modifier = Modifier.fillMaxSize()) {

        // 1. Background Image
        Image(
            painter = painterResource(id = R.drawable.bg_home_field),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Dark Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0A2A1A).copy(alpha = 0.75f),
                            Color(0xFF0A2A1A).copy(alpha = 0.55f),
                            Color(0xFF0A2A1A).copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // 3. UI Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Header
            Text(
                text = "Assalam-o-Alaikum,",
                color = Color(0xFFD4AF37),
                fontSize = 18.sp
            )
            Text(
                text = "AgriScan AI",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Your AI Crop Assistant",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Server Status Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Server Online",
                        color = Color(0xFFD4AF37),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ready to analyze your crops",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total Scans", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        Text("0", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("AI Modes", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        Text("3", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ✅ CROP CAROUSEL (auto-sliding images)
            CropCarousel()

            Spacer(modifier = Modifier.weight(1f))

            // Scan Button
            Button(
                onClick = onNavigateToDiagnose,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(
                    text = if (isUrdu) "+ پتا اسکین کریں" else "+ Scan a Leaf",
                    color = Color(0xFF0A2A1A),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Login Button
            Button(
                onClick = onNavigateToLogin,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = if (isUrdu) "لاگ ان / رجسٹر" else "Login / Register",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// ✅ CROP CAROUSEL COMPOSABLE
@Composable
fun CropCarousel() {
    val crops = listOf(
        R.drawable.img_wheat_1,      // Wheat (existing)
        R.drawable.crop_rice,        // Rice
        R.drawable.crop_sugarcane,   // Sugarcane
        R.drawable.crop_cotton,      // Cotton
        R.drawable.crop_tomato,      // Tomato
        R.drawable.crop_onion,       // Onion
        R.drawable.crop_potato,      // Potato
        R.drawable.crop_brinjal      // Brinjal
    )

    val pagerState = rememberPagerState(pageCount = { crops.size })

    // Auto-scroll every 3 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % crops.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        // Sliding images
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = painterResource(id = crops[page]),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Dark gradient overlay at bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF0A2A1A).copy(alpha = 0.9f)
                        )
                    )
                )
        )

        // Static text overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = "Supporting Pakistani Farmers",
                color = Color(0xFFD4AF37),
                fontSize = 12.sp
            )
            Text(
                text = "Har Fasal, Har Mausam",
                color = Color(0xFFF4F1EA),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Page indicator dots (top-right)
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(crops.size) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == pagerState.currentPage) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == pagerState.currentPage) Color(0xFFD4AF37)
                            else Color.White.copy(alpha = 0.4f)
                        )
                )
            }
        }
    }
}