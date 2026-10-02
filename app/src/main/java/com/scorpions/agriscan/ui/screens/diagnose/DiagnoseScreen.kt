package com.scorpions.agriscan.ui.screens.diagnose

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.scorpions.agriscan.R
import com.scorpions.agriscan.ui.components.*
import com.scorpions.agriscan.ui.theme.*
import java.io.File

@Composable
fun DiagnoseScreen(
    viewModel: DiagnoseViewModel,
    onNavigateToCamera: () -> Unit,
    onResultReady: (String) -> Unit,
    isUrdu: Boolean = false
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { stream ->
                val tempFile = File(context.cacheDir, "picked_${System.currentTimeMillis()}.jpg")
                tempFile.writeBytes(stream.readBytes())
                viewModel.setImage(it, tempFile)
            }
        }
    }

    LaunchedEffect(state.result) {
        state.result?.let { result ->
            val json = """{"disease":"${result.disease}","confidence":${result.confidence},"treatment":"${result.treatment.replace("\"", "\\\"")}","mode":"${result.mode}","source":"${result.source}","qwenOpinion":"${result.qwenOpinion?.replace("\"", "\\\"") ?: ""}","imageUri":"${result.imageUri ?: ""}"}"""
            val encoded = java.net.URLEncoder.encode(json, "UTF-8")
            onResultReady(encoded)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // D1: Full screen background with dark gradient overlay
        Image(
            painter = painterResource(id = R.drawable.bg_scan_leaf),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DeepForest.copy(alpha = 0.40f),
                            DeepForest.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            Text(
                text = if (isUrdu) "پتا تشخیص" else "Leaf Diagnosis",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = SoftCream
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (isUrdu) "تصویر لیں یا گیلری سے منتخب کریں" else "Take a photo or select from gallery",
                style = MaterialTheme.typography.bodyMedium,
                color = SoftCream.copy(alpha = 0.7f)
            )

            Spacer(Modifier.height(24.dp))

            // D2: Large tiles for Camera/Gallery
            ImageSelectionSection(
                imageUri = state.selectedImageUri,
                onCameraClick = onNavigateToCamera,
                onGalleryClick = { galleryLauncher.launch("image/*") },
                isUrdu = isUrdu
            )

            Spacer(Modifier.height(24.dp))

            // D3: Segmented Pill AI Mode Selector
            AiModeSelector(
                selectedMode = state.selectedMode,
                onModeSelected = { viewModel.setMode(it) },
                isUrdu = isUrdu
            )

            Spacer(Modifier.height(24.dp))

            if (state.errorMessage != null) {
                ErrorBanner(
                    message = state.errorMessage!!,
                    onRetry = { viewModel.analyze() }
                )
                Spacer(Modifier.height(16.dp))
            }

            // D4: Styled Analyze Button (FIX 1)
            Button(
                onClick = { viewModel.analyze() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                enabled = state.selectedImageFile != null && !state.isAnalyzing,
                shape = RoundedCornerShape(50.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 2.dp,
                    disabledElevation = 0.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PremiumGold,
                    contentColor = DeepForest,
                    disabledContainerColor = Color.White.copy(alpha = 0.15f),
                    disabledContentColor = SoftCream.copy(alpha = 0.5f)
                )
            ) {
                val buttonContentColor = if (state.selectedImageFile != null && !state.isAnalyzing) DeepForest else SoftCream.copy(alpha = 0.5f)
                Icon(Icons.Default.Search, contentDescription = null, tint = buttonContentColor)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (isUrdu) "تجزیہ کریں" else "Analyze",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = buttonContentColor
                )
            }

            if (state.selectedImageFile == null) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = if (isUrdu) "براہ کرم پہلے تصویر منتخب کریں" else "Please select an image first",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftCream.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(Modifier.height(24.dp))
        }

        LoadingOverlay(
            message = if (isUrdu) "تجزیہ ہو رہا ہے..." else "Analyzing leaf...",
            isVisible = state.isAnalyzing
        )
    }
}

@Composable
private fun ImageSelectionSection(
    imageUri: Uri?,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    isUrdu: Boolean
) {
    if (imageUri != null) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
        ) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Selected leaf",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onCameraClick,
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.12f),
                    contentColor = SoftCream
                ),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = PremiumGold, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(if (isUrdu) "دوبارہ لیں" else "Retake", fontWeight = FontWeight.SemiBold)
            }
            Button(
                onClick = onGalleryClick,
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.12f),
                    contentColor = SoftCream
                ),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Image, contentDescription = null, tint = PremiumGold, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(if (isUrdu) "تبدیل کریں" else "Change", fontWeight = FontWeight.SemiBold)
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ImagePickerCard(
                icon = Icons.Default.CameraAlt,
                label = if (isUrdu) "کیمرہ" else "Camera",
                modifier = Modifier.weight(1f),
                onClick = onCameraClick
            )
            ImagePickerCard(
                icon = Icons.Default.Image,
                label = if (isUrdu) "گیلری" else "Gallery",
                modifier = Modifier.weight(1f),
                onClick = onGalleryClick
            )
        }
    }
}

@Composable
private fun ImagePickerCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                modifier = Modifier.size(48.dp),
                tint = PremiumGold
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = label,
                fontSize = 16.sp,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = SoftCream
            )
        }
    }
}

@Composable
private fun AiModeSelector(
    selectedMode: AiMode,
    onModeSelected: (AiMode) -> Unit,
    isUrdu: Boolean
) {
    Text(
        text = if (isUrdu) "AI موڈ منتخب کریں" else "Select AI Mode",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = SoftCream
    )
    Spacer(Modifier.height(10.dp))
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AiMode.entries.forEach { mode ->
                val isSelected = mode == selectedMode
                Surface(
                    onClick = { onModeSelected(mode) },
                    shape = RoundedCornerShape(50.dp),
                    color = if (isSelected) PremiumGold else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = if (isUrdu) mode.labelUrdu else mode.label,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) DeepForest else SoftCream
                        )
                    }
                }
            }
        }
    }
}

