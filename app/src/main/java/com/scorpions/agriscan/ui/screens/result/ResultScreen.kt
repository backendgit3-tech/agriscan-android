package com.scorpions.agriscan.ui.screens.result

import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.scorpions.agriscan.data.api.PredictionResult
import com.scorpions.agriscan.ui.components.*
import com.scorpions.agriscan.ui.theme.*
import com.scorpions.agriscan.utils.confidenceLabel

@Composable
fun ResultScreen(
    result: PredictionResult,
    onScanAnother: () -> Unit,
    onBack: () -> Unit,
    isUrdu: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AgriScanTopBar(
            title = if (isUrdu) "نتیجہ" else "Diagnosis Result",
            onBackClick = onBack
        )

        Column(modifier = Modifier.padding(16.dp)) {

            if (result.imageUri != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(Uri.parse(result.imageUri)),
                        contentDescription = "Scanned leaf",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(Modifier.height(20.dp))
            }

            if (result.isLowConfidence) {
                ErrorBanner(
                    message = if (isUrdu) "کم اعتماد — براہ کرم صاف تصویر لیں"
                    else "Low confidence — try a clearer, closer photo"
                )
                Spacer(Modifier.height(16.dp))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (isUrdu) "بیماری کی تشخیص" else "Disease Detection",
                        style = MaterialTheme.typography.labelLarge,
                        color = Charcoal500
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = result.disease,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (result.disease.contains("Healthy", ignoreCase = true))
                            AgriGreen700 else Red600
                    )

                    Spacer(Modifier.height(16.dp))

                    ConfidenceBar(confidence = result.confidence)

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = {},
                            label = { Text(result.mode.replaceFirstChar { it.uppercase() }) },
                            leadingIcon = {
                                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        )
                        AssistChip(
                            onClick = {},
                            label = { Text(confidenceLabel(result.confidence)) },
                            leadingIcon = {
                                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (result.treatment.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AgriGreen50)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = AgriGreen700
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (isUrdu) "علاج کی سفارش" else "Treatment Recommendation",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AgriGreen900
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = result.treatment,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Charcoal700
                        )
                    }
                }
            }

            if (!result.qwenOpinion.isNullOrBlank()) {
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = AgriGreen700
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (isUrdu) "Qwen AI کی رائے" else "Qwen AI Opinion",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AgriGreen900
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = result.qwenOpinion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Charcoal700
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            InfoCard(
                icon = Icons.Default.Info,
                title = if (isUrdu) "AI ماخذ" else "AI Source",
                value = result.source,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onScanAnother,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreen700)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (isUrdu) "ایک اور اسکین کریں" else "Scan Another Leaf",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
