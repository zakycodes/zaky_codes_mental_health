package com.zakycodes.zcmh.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zakycodes.zcmh.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ConsultationScreen() {
    val context = LocalContext.current
    val phoneNumber = "6289691789422"

    // ✨ Animasi scale untuk cards
    var isPsychologyVisible by remember { mutableStateOf(false) }
    var isHypnoVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isPsychologyVisible = true
        kotlinx.coroutines.delay(150)
        isHypnoVisible = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = MintGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Konsultasi Mental Health",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(LightCream)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header Section
            HeaderSection()

            // Card Psikolog dengan animasi
            AnimatedVisibility(
                visible = isPsychologyVisible,
                enter = fadeIn(animationSpec = tween(400)) +
                        slideInVertically(animationSpec = tween(400))
            ) {
                ConsultationCard(
                    title = "Konsultasi Psikolog",
                    description = "Berbicara dengan psikolog profesional untuk kesehatan mental Anda",
                    icon = Icons.Default.Psychology,
                    gradientColors = listOf(PsychologyBlue, PsychologyBlueDark),
                    onClickAction = {
                        openWhatsApp(
                            context,
                            phoneNumber,
                            "Halo, saya ingin berkonsultasi dengan Psikolog melalui ZCMH App 🧠"
                        )
                    }
                )
            }

            // Card Hypnotherapist dengan animasi
            AnimatedVisibility(
                visible = isHypnoVisible,
                enter = fadeIn(animationSpec = tween(400)) +
                        slideInVertically(animationSpec = tween(400))
            ) {
                ConsultationCard(
                    title = "Konsultasi Hypnotherapist",
                    description = "Terapi hypnosis untuk mengatasi masalah psikologis lebih dalam",
                    icon = Icons.Default.SelfImprovement,
                    gradientColors = listOf(HypnoViolet, HypnoVioletDark),
                    onClickAction = {
                        openWhatsApp(
                            context,
                            phoneNumber,
                            "Halo, saya ingin berkonsultasi dengan Hypnotherapist melalui ZCMH App 🌙"
                        )
                    }
                )
            }

            // Info Box
            InfoBox()
        }
    }
}

@Composable
fun HeaderSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Animated icon dengan heartbeat effect
        val infiniteTransition = rememberInfiniteTransition(label = "heartbeat")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.12f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )

        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MintGreen.copy(alpha = 0.2f),
                            MintGreen.copy(alpha = 0.05f)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.HealthAndSafety,
                contentDescription = null,
                tint = MintGreen,
                modifier = Modifier.size(60.dp)
            )
        }

        Text(
            text = "Pilih Layanan Konsultasi",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
        )

        Text(
            text = "Kami siap membantu kesehatan mental Anda",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = DarkText.copy(alpha = 0.6f)
            ),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

@Composable
fun ConsultationCard(
    title: String,
    description: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClickAction: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable {
                isPressed = true
                onClickAction()
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 10.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(gradientColors)
                )
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Icon section dengan background
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                // Text section
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    )

                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    )
                }

                // Arrow icon
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }

    // Reset pressed state
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
            isPressed = false
        }
    }
}

@Composable
fun InfoBox() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = ConsultGold.copy(alpha = 0.12f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = ConsultGold.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = ConsultGold,
                    modifier = Modifier.size(26.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Layanan Terpercaya",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Konsultasi akan dilakukan melalui WhatsApp dengan profesional bersertifikat",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = DarkText.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                )
            }
        }
    }
}

// Fungsi untuk membuka WhatsApp
fun openWhatsApp(context: android.content.Context, phoneNumber: String, message: String) {
    try {
        val url = "https://wa.me/$phoneNumber?text=${Uri.encode(message)}"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback jika WhatsApp tidak terinstall
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://wa.me/$phoneNumber?text=${Uri.encode(message)}")
        }
        context.startActivity(intent)
    }
}