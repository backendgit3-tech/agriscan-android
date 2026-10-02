package com.scorpions.agriscan.ui.screens.history

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.scorpions.agriscan.data.local.PredictionEntity
import com.scorpions.agriscan.ui.components.*
import com.scorpions.agriscan.ui.theme.*
import com.scorpions.agriscan.utils.confidenceLabel

@Composable
fun HistoryDetailScreen(
    prediction: PredictionEntity,
    onBack: () -> Unit,
    isUrdu: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AgriScanTopBar(
            title = if (isUrdu) "تفصیل" else "Prediction Detail",
            onBackClick = onBack
        )

        Column(modifier = Modifier.padding(16.dp)) {

            if (!prediction.localImageUri.isNullOrBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(Uri.parse(prediction.localImageUri)),
                        contentDescription = "Leaf image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(Modifier.height(20.dp))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    DetailRow(
                        icon = Icons.Default.Warning,
                        label = if (isUrdu) "بیماری" else "Disease",
                        value = prediction.disease
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    DetailRow(
                        icon = Icons.Default.Info,
                        label = if (isUrdu) "اعتماد" else "Confidence",
                        value = "%.1f%% (%s)".format(prediction.confidence, confidenceLabel(prediction.confidence))
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    DetailRow(
                        icon = Icons.Default.Star,
                        label = if (isUrdu) "AI موڈ" else "AI Mode",
                        value = prediction.mode.replaceFirstChar { it.uppercase() }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    DetailRow(
                        icon = Icons.Default.Info,
                        label = if (isUrdu) "ماخذ" else "Source",
                        value = prediction.source
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    DetailRow(
                        icon = Icons.Default.DateRange,
                        label = if (isUrdu) "تاریخ" else "Date",
                        value = "${prediction.date} ${prediction.time}"
                    )
                    if (!prediction.imageName.isNullOrBlank()) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        DetailRow(
                            icon = Icons.Default.Image,
                            label = if (isUrdu) "تصویر" else "Image",
                            value = prediction.imageName
                        )
                    }
                }
            }

            if (!prediction.treatment.isNullOrBlank()) {
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AgriGreen50)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = if (isUrdu) "علاج کی سفارش" else "Treatment Recommendation",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreen900
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = prediction.treatment,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Charcoal700
                        )
                    }
                }
            }

            if (!prediction.qwenOpinion.isNullOrBlank()) {
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = if (isUrdu) "Qwen AI کی رائے" else "Qwen AI Opinion",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreen900
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = prediction.qwenOpinion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Charcoal700
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row {
        Icon(
            icon,
            contentDescription = null,
            tint = AgriGreen700,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Charcoal500
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Charcoal900
            )
        }
    }
}
