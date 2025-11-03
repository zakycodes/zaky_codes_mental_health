package com.zakycodes.zcmh.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.zakycodes.zcmh.data.model.JournalEntry
import com.zakycodes.zcmh.ui.theme.MintGreen
import com.zakycodes.zcmh.ui.theme.SoftBlue
import com.zakycodes.zcmh.ui.viewmodel.JournalViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun JournalScreen(
    navController: NavController,
    viewModel: JournalViewModel = viewModel()
) {
    val journals by viewModel.allJournals.collectAsState()
    var showInfoDialog by remember { mutableStateOf(false) }

    // Subtle animation for FAB
    val fabScale by rememberInfiniteTransition(label = "fab").animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
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
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Gratitude Journal",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "Tuliskan hal yang kamu syukuri",
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

            // Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 88.dp) // Space for FAB + bottom nav
            ) {
                AnimatedContent(
                    targetState = journals.isEmpty(),
                    transitionSpec = {
                        fadeIn() + expandVertically() with fadeOut() + shrinkVertically()
                    },
                    label = "content"
                ) { isEmpty ->
                    if (isEmpty) {
                        EmptyJournalState()
                    } else {
                        JournalList(
                            journals = journals,
                            onJournalClick = { journal ->
                                viewModel.selectJournal(journal)
                                navController.navigate("journal_detail/${journal.id}")
                            }
                        )
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = {
                navController.navigate("journal_detail/new")
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .padding(bottom = 72.dp)
                .then(
                    if (journals.isEmpty()) Modifier.scale(fabScale) else Modifier
                ),
            containerColor = MintGreen,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Tambah Jurnal",
                modifier = Modifier.size(28.dp)
            )
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
                        Icons.Default.AutoStories,
                        contentDescription = null,
                        tint = MintGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    "Info Ilmiah: Gratitude Journal",
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
                            "📊 Temuan Penelitian",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            "Meta-analisis dari 64 studi menunjukkan bahwa gratitude journaling menghasilkan: rasa syukur 4% lebih tinggi, kepuasan hidup 6,86% lebih tinggi, dan mengurangi gejala anxiety 7,76% serta depresi 6,89%.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF334155),
                            lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.5f
                        )
                    }

                    // Benefits
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "✅ Manfaat Gratitude Journal",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        JournalBenefitsList()
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
                                "Penelitian pada 201 pasien dengan krisis bunuh diri menunjukkan bahwa gratitude diary selama 7 hari meningkatkan kondisi psikologis lebih baik dibanding food diary (kontrol).",
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
                        JournalTipsList()
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
                            "• PMC: The effects of gratitude interventions (2023)\n" +
                                    "• PMC: A Brief Gratitude Writing Intervention During COVID-19 Pandemic (2022)\n" +
                                    "• Depression and Anxiety: Gratitude journaling for suicidal patients (2019)",
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
}

@Composable
fun EmptyJournalState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon with gradient background
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = MintGreen.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = MintGreen.copy(alpha = 0.6f)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Belum ada jurnal",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )

                Text(
                    text = "Mulai menulis untuk merasakan manfaat\ngratitude journaling",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF94A3B8),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.5f
                )
            }

            // Hint
            Surface(
                color = SoftBlue.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = SoftBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        "Tekan tombol + untuk mulai",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftBlue,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun JournalList(
    journals: List<JournalEntry>,
    onJournalClick: (JournalEntry) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Stats header
        item {
            Surface(
                color = MintGreen.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AutoStories,
                        contentDescription = null,
                        tint = MintGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "${journals.size} Jurnal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MintGreen
                        )
                        Text(
                            text = "Terus menulis untuk hidup lebih bahagia",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }

        items(journals, key = { it.id }) { journal ->
            JournalCard(
                journal = journal,
                onClick = { onJournalClick(journal) }
            )
        }
    }
}

@Composable
fun JournalCard(
    journal: JournalEntry,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 1.dp,
            hoveredElevation = 4.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = journal.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Date
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = SoftBlue,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = journal.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
            }

            // Preview
            Text(
                text = journal.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = Color(0xFF475569),
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.6f
            )

            // Divider
            Divider(
                color = Color(0xFFE2E8F0),
                thickness = 1.dp
            )

            // Read more
            Text(
                text = "Baca selengkapnya →",
                style = MaterialTheme.typography.bodySmall,
                color = MintGreen,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun JournalBenefitsList() {
    val benefits = listOf(
        "Mengurangi stres & emosi negatif",
        "Meningkatkan kesehatan mental",
        "Melepaskan serotonin & dopamine",
        "Mengatur hormon stres (cortisol)",
        "Efek bertahan hingga 1 bulan"
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
fun JournalTipsList() {
    val tips = listOf(
        "Tulis 3-5 hal yang disyukuri per hari",
        "Frekuensi: 2-4x per minggu",
        "Waktu ideal: malam sebelum tidur (10-15 menit)",
        "Fokus pada hal spesifik, bukan umum",
        "Rasakan emosi saat menulis"
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