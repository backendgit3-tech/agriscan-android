package com.scorpions.agriscan.ui.screens.about

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scorpions.agriscan.R
import com.scorpions.agriscan.ui.theme.*

@Composable
fun KissanHeroBanner(isUrdu: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
    ) {
        Image(
            painter = painterResource(R.drawable.img_kissan_portrait),
            contentDescription = "Pakistani Farmer",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Dark gradient overlay
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
        // Overlay text
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            Text(
                text = if (isUrdu) "پاکستانی کسانوں کو بااختیار بنانا" else "Empowering Pakistani Farmers",
                color = Color(0xFFD4AF37),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (isUrdu) "ہر کسان، ہر فصل، ہر موسم" else "Har Kisan, Har Fasal, Har Mausam",
                color = Color(0xFFF4F1EA).copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun AboutScreen(
    onLanguageToggle: () -> Unit,
    isUrdu: Boolean = false,
    currentLanguage: String = "en"
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepForest)
            .verticalScroll(rememberScrollState())
    ) {
        KissanHeroBanner(isUrdu = isUrdu)

        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AgriScan AI",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = SoftCream
            )
            Text(
                text = "v2.2.0",
                style = MaterialTheme.typography.labelLarge,
                color = PremiumGold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (isUrdu) "آپ کا AI فصل معاون" else "Your AI Crop Assistant",
                style = MaterialTheme.typography.bodyLarge,
                color = SoftCream.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
            Text(
                text = if (isUrdu) "اسکین کریں۔ سمجھیں۔ عمل کریں۔ اگائیں۔"
                else "Scan. Understand. Act. Grow.",
                style = MaterialTheme.typography.bodyMedium,
                color = PremiumGold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            // AI Capabilities Card (Glassmorphic)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (isUrdu) "AI صلاحیتیں" else "AI Capabilities",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PremiumGold
                    )
                    Spacer(Modifier.height(14.dp))

                    FeatureRow(
                        icon = Icons.Default.Search,
                        title = if (isUrdu) "مقامی AI" else "Local AI",
                        desc = if (isUrdu) "آف لائن فصل کی بیماری کی شناخت" else "Offline crop disease detection using deep learning"
                    )
                    Spacer(Modifier.height(12.dp))
                    FeatureRow(
                        icon = Icons.Default.Star,
                        title = if (isUrdu) "ہائبرڈ AI" else "Hybrid AI",
                        desc = if (isUrdu) "مقامی + Qwen AI مشترکہ تجزیہ" else "Local + Qwen AI combined analysis for best accuracy"
                    )
                    Spacer(Modifier.height(12.dp))
                    FeatureRow(
                        icon = Icons.Default.Info,
                        title = if (isUrdu) "Qwen Vision" else "Qwen Vision",
                        desc = if (isUrdu) "جدید ملٹی ماڈل AI تشخیص" else "Advanced multimodal AI diagnosis via Qwen VL"
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Language Card (Glassmorphic)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (isUrdu) "زبان" else "Language",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PremiumGold
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isUrdu) "موجودہ: اردو" else "Current: English",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoftCream,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = onLanguageToggle,
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PremiumGold,
                                contentColor = DeepForest
                            )
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                if (isUrdu) "English" else "اردو",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Stats row (Glassmorphic)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = PremiumGold,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isUrdu) "بیماریاں" else "Diseases",
                                style = MaterialTheme.typography.labelMedium,
                                color = SoftCream.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "7 classes",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = SoftCream
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = PremiumGold,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isUrdu) "فصلیں" else "Crops",
                                style = MaterialTheme.typography.labelMedium,
                                color = SoftCream.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Tomato, Potato",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = SoftCream
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Glassmorphic Developed by Team Scorpions Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.10f)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isUrdu) "تیار کردہ" else "Developed by",
                        style = MaterialTheme.typography.labelLarge,
                        color = SoftCream.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Team Scorpions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = PremiumGold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (isUrdu) "ہیکاتھون 2026" else "Hackathon 2026",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftCream.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = if (isUrdu) "AgriScan AI فصلوں کی بیماریوں کی شناخت کے لیے بنایا گیا ہے۔ یہ ایپ کسانوں کو فوری اور درست تشخیص فراہم کرتی ہے۔"
                else "AgriScan AI is designed to help farmers quickly identify crop diseases. The app provides instant diagnosis and treatment recommendations powered by AI.",
                style = MaterialTheme.typography.bodyMedium,
                color = SoftCream.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            icon,
            contentDescription = null,
            tint = PremiumGold,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = SoftCream
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = SoftCream.copy(alpha = 0.7f)
            )
        }
    }
}


