package com.zakycodes.zcmh.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zakycodes.zcmh.ui.theme.SoftBlue
import com.zakycodes.zcmh.ui.theme.MintGreen
import com.zakycodes.zcmh.ui.viewmodel.AlarmViewModel

private const val IS_DEVELOPMENT_MODE = false

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AlarmScreen(
    viewModel: AlarmViewModel = viewModel()
) {
    val alarmSettings by viewModel.alarmSettings.collectAsState()
    val showConfirmationDialog by viewModel.showConfirmationDialog.collectAsState()
    val nextAlarmTime by viewModel.nextAlarmTime.collectAsState()
    val upcomingAlarms by viewModel.upcomingAlarms.collectAsState()
    val context = LocalContext.current

    var showInfoDialog by remember { mutableStateOf(false) }

    // Pulse animation for active alarm
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .statusBarsPadding()
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pengingat Makan",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "Setiap 4 Jam Sekali",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B)
                    )
                }

                IconButton(onClick = { showInfoDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = SoftBlue
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Alarm Icon with Animation
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(if (alarmSettings.isEnabled) pulseScale else 1f)
                    .background(
                        color = if (alarmSettings.isEnabled)
                            MintGreen.copy(alpha = 0.15f)
                        else Color(0xFFF1F5F9),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (alarmSettings.isEnabled) Icons.Default.AlarmOn else Icons.Default.Alarm,
                    contentDescription = "Alarm Icon",
                    modifier = Modifier.size(60.dp),
                    tint = if (alarmSettings.isEnabled) MintGreen else Color(0xFF64748B)
                )
            }

            // Main Status Card
            AnimatedContent(
                targetState = alarmSettings.isEnabled,
                transitionSpec = {
                    fadeIn() + expandVertically() with fadeOut() + shrinkVertically()
                },
                label = "status"
            ) { isEnabled ->
                if (isEnabled) {
                    ActiveAlarmCard(
                        nextAlarmTime = nextAlarmTime,
                        upcomingAlarms = upcomingAlarms
                    )
                } else {
                    InactiveAlarmCard()
                }
            }

            // Action Button
            Button(
                onClick = { viewModel.toggleAlarm() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (alarmSettings.isEnabled)
                        Color(0xFFEF4444)
                    else MintGreen
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Icon(
                    imageVector = if (alarmSettings.isEnabled) Icons.Default.NotificationsOff else Icons.Default.NotificationsActive,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (alarmSettings.isEnabled) "Matikan Alarm" else "Aktifkan Alarm",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Test Button (Development)
            if (alarmSettings.isEnabled && IS_DEVELOPMENT_MODE) {
                OutlinedButton(
                    onClick = {
                        val intent = android.content.Intent(
                            context,
                            com.zakycodes.zcmh.utils.AlarmReceiver::class.java
                        )
                        context.sendBroadcast(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SoftBlue
                    )
                ) {
                    Text(
                        text = "🧪 Test Alarm Sekarang",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Info Dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = MintGreen.copy(alpha = 0.15f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = MintGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    "Info Ilmiah: Pola Makan Teratur",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 500.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Research Finding
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "📊 Temuan Penelitian",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "Penelitian menunjukkan bahwa fluktuasi gula darah memiliki peran penting dalam anxiety dan depresi. Lonjakan adrenalin terjadi sekitar 4-5 jam setelah makan, yang dapat memicu kecemasan atau ketakutan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF334155),
                            lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.5f
                        )
                    }

                    // Benefits
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "✅ Manfaat Makan Teratur",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        BenefitsList()
                    }

                    // Case Study
                    Surface(
                        color = SoftBlue.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = SoftBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "Studi Kasus",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SoftBlue
                                )
                            }
                            Text(
                                "Pasien dengan GAD mengalami penurunan gejala anxiety dari 8/10 menjadi 4-5/10 setelah modifikasi diet dengan makan teratur setiap 3-4 jam.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF334155),
                                lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.5f
                            )
                        }
                    }

                    // Tips
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "💡 Tips Optimal",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        TipsList()
                    }

                    // References
                    Divider()
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "📚 Sumber",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            "• PMC: Generalized Anxiety Disorder and Hypoglycemia Symptoms Improved with Diet Modification (2016)\n" +
                                    "• Frontiers in Nutrition: Association of sugar consumption with risk of depression and anxiety (2024)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B),
                            lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.5f
                        )
                    }
                }
            },
            confirmButton = {
                FilledTonalButton(
                    onClick = { showInfoDialog = false },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MintGreen.copy(alpha = 0.15f),
                        contentColor = MintGreen
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Tutup", fontWeight = FontWeight.SemiBold)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    // Confirmation Dialog
    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissConfirmationDialog() },
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = MintGreen.copy(alpha = 0.15f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MintGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            title = {
                Text(
                    "Alarm Diaktifkan!",
                    fontWeight = FontWeight.Bold,
                    color = MintGreen
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "Alarm makan Anda sudah aktif dan siap mengingatkan Anda.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF334155)
                    )

                    if (nextAlarmTime.isNotEmpty()) {
                        Surface(
                            color = MintGreen.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    "Alarm berikutnya:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    nextAlarmTime,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MintGreen
                                )
                            }
                        }
                    }

                    Surface(
                        color = SoftBlue.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = SoftBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                "Pastikan volume HP tidak silent!",
                                style = MaterialTheme.typography.bodySmall,
                                color = SoftBlue,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                FilledTonalButton(
                    onClick = { viewModel.dismissConfirmationDialog() },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MintGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Siap!", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun ActiveAlarmCard(
    nextAlarmTime: String,
    upcomingAlarms: List<String>
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MintGreen,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Alarm Aktif",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MintGreen
                )
            }

            if (nextAlarmTime.isNotEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Alarm berikutnya:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = nextAlarmTime,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = SoftBlue
                    )
                }
            }

            if (upcomingAlarms.isNotEmpty()) {
                Divider(color = Color(0xFFE2E8F0))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Jadwal Selanjutnya:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B)
                    )

                    upcomingAlarms.take(3).forEach { time ->
                        Surface(
                            color = Color(0xFFF8F9FA),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Restaurant,
                                    contentDescription = null,
                                    tint = MintGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = time,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF334155)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InactiveAlarmCard() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 1.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.AlarmOff,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color(0xFF94A3B8)
            )

            Text(
                text = "Alarm Tidak Aktif",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B)
            )

            Text(
                text = "Aktifkan alarm untuk mendapatkan pengingat makan teratur setiap 4 jam",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color(0xFF94A3B8),
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.5f
            )
        }
    }
}

@Composable
fun BenefitsList() {
    val benefits = listOf(
        "Mencegah penurunan drastis gula darah",
        "Mengurangi gejala anxiety & mood swings",
        "Mencegah GERD (asam lambung)",
        "Stabilitas energi & konsentrasi"
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        benefits.forEach { benefit ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MintGreen,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = benefit,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF334155),
                    lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.4f
                )
            }
        }
    }
}

@Composable
fun TipsList() {
    val tips = listOf(
        "Kombinasi: Protein + Lemak sehat + Serat",
        "Hindari karbohidrat olahan",
        "Makan porsi kecil tapi sering",
        "Hindari makan 2-3 jam sebelum tidur"
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        tips.forEach { tip ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    "•",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MintGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = tip,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF334155),
                    lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.4f
                )
            }
        }
    }
}