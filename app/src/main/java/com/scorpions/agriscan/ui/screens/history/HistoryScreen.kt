package com.scorpions.agriscan.ui.screens.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scorpions.agriscan.R
import com.scorpions.agriscan.data.local.PredictionEntity
import com.scorpions.agriscan.ui.components.AgriScanTopBar
import com.scorpions.agriscan.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onPredictionClick: (Long) -> Unit,
    onNavigateToDiagnose: (() -> Unit)? = null,
    isUrdu: Boolean = false
) {
    val state by viewModel.uiState.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // FIX 4: Background subtle Islamic pattern
        Image(
            painter = painterResource(id = R.drawable.bg_islamic_pattern),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Deep dark green overlay (0.92 alpha for subtle texture)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A2A1A).copy(alpha = 0.92f))
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AgriScanTopBar(
                title = if (isUrdu) "تاریخ" else "Prediction History",
                actions = {
                    if (!state.isEmpty) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Clear all", tint = PremiumGold)
                        }
                    }
                }
            )

            if (state.isEmpty) {
                // Phase E: Styled Empty State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = PremiumGold.copy(alpha = 0.3f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = if (isUrdu) "کوئی تاریخ نہیں" else "No predictions yet",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = PlayfairDisplay,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = SoftCream
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = if (isUrdu) "اسکین شروع کریں اور تاریخ بنائیں" else "Start scanning leaves to build history",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = Inter,
                                fontSize = 14.sp
                            ),
                            color = SoftCream.copy(alpha = 0.6f)
                        )
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = { onNavigateToDiagnose?.invoke() },
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PremiumGold,
                                contentColor = DeepForest
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = if (isUrdu) "+ پتا اسکین کریں" else "+ Scan a Leaf",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "${state.totalCount} ${if (isUrdu) "اسکین" else "scans"}",
                    style = MaterialTheme.typography.labelLarge,
                    color = PremiumGold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.predictions, key = { it.id }) { prediction ->
                        HistoryCard(
                            prediction = prediction,
                            onClick = { onPredictionClick(prediction.id) },
                            isUrdu = isUrdu
                        )
                    }
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(if (isUrdu) "تمام تاریخ صاف کریں؟" else "Clear All History?") },
            text = { Text(if (isUrdu) "یہ عمل واپس نہیں ہو سکتا" else "This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAll()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Red600)
                ) {
                    Text(if (isUrdu) "صاف کریں" else "Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(if (isUrdu) "منسوخ" else "Cancel")
                }
            }
        )
    }
}

@Composable
private fun HistoryCard(
    prediction: PredictionEntity,
    onClick: () -> Unit,
    isUrdu: Boolean
) {
    val confidenceColor = when {
        prediction.confidence >= 90f -> AgriGreen500
        prediction.confidence >= 70f -> AgriGreen300
        prediction.confidence >= 50f -> Amber500
        else -> Red600
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = prediction.disease,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SoftCream
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "%.1f%%".format(prediction.confidence),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = confidenceColor
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "• ${prediction.mode}",
                        style = MaterialTheme.typography.labelMedium,
                        color = PremiumGold
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${prediction.date} ${prediction.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftCream.copy(alpha = 0.6f)
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "View details",
                tint = PremiumGold
            )
        }
    }
}

