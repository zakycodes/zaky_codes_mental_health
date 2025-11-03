package com.zakycodes.zcmh.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// ✅ Warna baru untuk WhatsApp button
val WhatsAppColor = Color(0xFF6FBFAE)

// 🎯 Floating WhatsApp Button - Posisi KIRI BAWAH
@Composable
fun FloatingWhatsAppButton(
    phoneNumber: String = "6289691789422",
    message: String = "Halo, saya tertarik konsultasi dari aplikasi ZCMH"
) {
    val context = LocalContext.current

    // Pulse animation effect
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .padding(16.dp)
            .size(64.dp)
    ) {
        // Outer pulse ring
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(scale)
                .clip(CircleShape)
                .background(WhatsAppColor.copy(alpha = 0.3f))
        )

        // Main WhatsApp button
        FloatingActionButton(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://wa.me/$phoneNumber?text=${Uri.encode(message)}")
                }
                context.startActivity(intent)
            },
            containerColor = WhatsAppColor,
            contentColor = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        ) {
            Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = "Konsultasi WhatsApp",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

// 💎 Professional Help Card - ALWAYS EXPANDED
@Composable
fun ProfessionalHelpCard(
    modifier: Modifier = Modifier,
    phoneNumber: String = "6289691789422",
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // 🎨 Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF6366F1),
                                    Color(0xFF8B5CF6)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Butuh Bantuan Profesional?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Konsultasi dengan ahli",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 📝 Info Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Aplikasi ini adalah alat bantu. Untuk kondisi anxiety atau mental health yang serius, konsultasi dengan profesional sangat direkomendasikan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.3f
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🎯 Service Cards - CLICKABLE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Psikolog Card
                ClickableServiceCard(
                    icon = Icons.Default.Psychology,
                    title = "Psikolog",
                    subtitle = "Konseling profesional",
                    color = Color(0xFF6366F1),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val msg = "Halo, saya ingin konsultasi dengan Psikolog untuk anxiety dari aplikasi ZCMH"
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://wa.me/$phoneNumber?text=${Uri.encode(msg)}")
                        }
                        context.startActivity(intent)
                    }
                )

                // Hipnoterapi Card
                ClickableServiceCard(
                    icon = Icons.Default.SelfImprovement,
                    title = "Hipnoterapi",
                    subtitle = "Terapi relaksasi",
                    color = Color(0xFF8B5CF6),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val msg = "Halo, saya ingin konsultasi Hipnoterapi untuk anxiety dari aplikasi ZCMH"
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://wa.me/$phoneNumber?text=${Uri.encode(msg)}")
                        }
                        context.startActivity(intent)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 💚 WhatsApp CTA Button
            Button(
                onClick = {
                    val msg = "Halo, saya ingin berkonsultasi mengenai kesehatan mental dari aplikasi ZCMH"
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://wa.me/$phoneNumber?text=${Uri.encode(msg)}")
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WhatsAppColor
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Hubungi via WhatsApp",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dismiss button
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    "Sembunyikan untuk saat ini",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

// 🎨 Clickable Service Card Component
@Composable
fun ClickableServiceCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}