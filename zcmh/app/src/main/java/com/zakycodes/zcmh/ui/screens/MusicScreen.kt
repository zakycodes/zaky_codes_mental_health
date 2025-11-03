package com.zakycodes.zcmh.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zakycodes.zcmh.data.model.MusicCategory
import com.zakycodes.zcmh.data.model.MusicTrack
import com.zakycodes.zcmh.ui.theme.MintGreen
import com.zakycodes.zcmh.ui.theme.SoftBlue
import com.zakycodes.zcmh.ui.viewmodel.MusicViewModel

@Composable
fun MusicScreen(
    viewModel: MusicViewModel
) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isLooping by viewModel.isLooping.collectAsState()
    val currentTrackIndex by viewModel.currentTrackIndex.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val selectedTimer by viewModel.selectedTimer.collectAsState()
    val musicTracks by viewModel.musicTracks.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var showTimerDialog by remember { mutableStateOf(false) }
    var isPlayerExpanded by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    // Smooth pulsing animation for playing indicator
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
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
                        text = "Musik Relaksasi",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "Frekuensi yang menenangkan",
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category Filter
            CategoryFilterChips(
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    viewModel.filterByCategory(category)
                }
            )

            // Player Card
            AnimatedVisibility(
                visible = isPlaying || currentTrackIndex >= 0,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                if (currentTrackIndex < musicTracks.size) {
                    ModernPlayerCard(
                        track = musicTracks[currentTrackIndex],
                        isPlaying = isPlaying,
                        isLooping = isLooping,
                        currentPosition = currentPosition,
                        duration = duration,
                        selectedTimer = selectedTimer,
                        isExpanded = isPlayerExpanded,
                        pulseAlpha = pulseAlpha,
                        onExpandChange = { isPlayerExpanded = it },
                        onPlayPauseClick = { viewModel.togglePlayPause() },
                        onStopClick = { viewModel.stopMusic() },
                        onPreviousClick = { viewModel.previousTrack() },
                        onNextClick = { viewModel.nextTrack() },
                        onLoopClick = { viewModel.toggleLoop() },
                        onTimerClick = { showTimerDialog = true },
                        onSeek = { viewModel.seekTo(it) },
                        formatTime = { viewModel.formatTime(it) }
                    )
                }
            }

            // Playlist Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Playlist",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Surface(
                    color = SoftBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${musicTracks.size} lagu",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = SoftBlue,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Playlist
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(musicTracks) { index, track ->
                    MusicTrackCard(
                        track = track,
                        isCurrentlyPlaying = isPlaying && currentTrackIndex == index,
                        pulseAlpha = if (isPlaying && currentTrackIndex == index) pulseAlpha else 1f,
                        onClick = { viewModel.playTrack(index) }
                    )
                }
            }
        }
    }

    // Timer Dialog
    if (showTimerDialog) {
        AlertDialog(
            onDismissRequest = { showTimerDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(SoftBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        tint = SoftBlue,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    "Set Timer Auto-Stop",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(15, 30, 60).forEach { minutes ->
                        FilledTonalButton(
                            onClick = {
                                viewModel.setTimer(minutes)
                                showTimerDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = SoftBlue.copy(alpha = 0.1f),
                                contentColor = SoftBlue
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Timer,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("$minutes menit", fontWeight = FontWeight.Medium)
                        }
                    }

                    if (selectedTimer != null) {
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                        TextButton(
                            onClick = {
                                viewModel.cancelTimer()
                                showTimerDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color(0xFFEF4444)
                            )
                        ) {
                            Icon(
                                Icons.Default.Cancel,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Batalkan Timer", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTimerDialog = false }) {
                    Text("Tutup", fontWeight = FontWeight.SemiBold)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    // Info Dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(SoftBlue, MintGreen)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    "Info Ilmiah: Binaural Beats",
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
                    InfoSection(
                        title = "📊 Temuan Penelitian",
                        content = "Penelitian menunjukkan binaural beats dapat mengurangi anxiety hingga 26,3% (dibanding 11,1% pada kelompok placebo). Frekuensi spesifik mempengaruhi gelombang otak dan mood secara berbeda.",
                        color = MintGreen
                    )

                    // Categories
                    Text(
                        "🎵 Kategori Frekuensi",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    FrequencyCard(
                        icon = "💚",
                        title = "Relax/Anxiety (3-7 Hz)",
                        description = "Delta (3 Hz) & Theta (6-7 Hz) terbukti mengurangi anxiety dan menginduksi relaksasi mendalam. Ideal untuk mengatasi kecemasan dan stres.",
                        color = MintGreen
                    )

                    FrequencyCard(
                        icon = "💜",
                        title = "Sleep (2-3 Hz)",
                        description = "Delta (2-3 Hz) meningkatkan deep sleep (stage 3) dan mengurangi drowsiness. Dengarkan 15-30 menit sebelum tidur untuk hasil optimal.",
                        color = Color(0xFF9C27B0)
                    )

                    FrequencyCard(
                        icon = "💙",
                        title = "Focus (40 Hz)",
                        description = "Gamma (40 Hz) meningkatkan konsentrasi, working memory, dan fokus. Efektif untuk produktivitas kerja/belajar. Gunakan 5-10 menit sebelum atau saat bekerja.",
                        color = SoftBlue
                    )

                    // Usage Tips
                    InfoSection(
                        title = "💡 Tips Penggunaan",
                        content = "• WAJIB gunakan headphones/earbuds stereo\n" +
                                "• Volume comfortable, tidak terlalu keras (<85 dB)\n" +
                                "• Durasi: 15-30 menit untuk relaksasi/tidur\n" +
                                "• Durasi: 5-10 menit untuk fokus\n" +
                                "• Gunakan sebagai terapi pendukung, bukan pengganti terapi profesional",
                        color = SoftBlue
                    )

                    // Warning
                    Surface(
                        color = Color(0xFFFEE2E2),
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
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "Catatan Penting",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEF4444)
                                )
                            }
                            Text(
                                "Efektivitas binaural beats bervariasi per individu. Ini adalah terapi pendukung (adjunct therapy), bukan pengganti pengobatan profesional untuk kondisi mental serius.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF7F1D1D),
                                lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.5f
                            )
                        }
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
                            "• PMC: Auditory Beat Stimulation and its Effects on Cognition and Mood States\n" +
                                    "• MDPI: The Efficiency of Binaural Beats on Anxiety and Depression (2024)\n" +
                                    "• Oxford Academic: Effect of dynamic binaural beats on sleep quality (2024)",
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
                        containerColor = SoftBlue.copy(alpha = 0.1f),
                        contentColor = SoftBlue
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
fun CategoryFilterChips(
    selectedCategory: MusicCategory?,
    onCategorySelected: (MusicCategory?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) },
            label = { Text("Semua", fontWeight = FontWeight.Medium) },
            leadingIcon = {
                Icon(
                    Icons.Default.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = SoftBlue,
                selectedLabelColor = Color.White,
                selectedLeadingIconColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )

        MusicCategory.values().forEach { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        "${category.displayName} • ${category.frequency}",
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = when (category) {
                            MusicCategory.RELAX -> Icons.Default.Spa
                            MusicCategory.SLEEP -> Icons.Default.Bedtime
                            MusicCategory.FOCUS -> Icons.Default.Psychology
                        },
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = when (category) {
                        MusicCategory.RELAX -> MintGreen
                        MusicCategory.SLEEP -> Color(0xFF9C27B0)
                        MusicCategory.FOCUS -> SoftBlue
                    },
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun ModernPlayerCard(
    track: MusicTrack,
    isPlaying: Boolean,
    isLooping: Boolean,
    currentPosition: Int,
    duration: Int,
    selectedTimer: Int?,
    isExpanded: Boolean,
    pulseAlpha: Float,
    onExpandChange: (Boolean) -> Unit,
    onPlayPauseClick: () -> Unit,
    onStopClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onLoopClick: () -> Unit,
    onTimerClick: () -> Unit,
    onSeek: (Int) -> Unit,
    formatTime: (Int) -> String
) {
    // FIXED: Gunakan Card biasa dengan border, bukan ElevatedCard
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isPlaying) MintGreen.copy(alpha = 0.3f) else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp  // FIXED: No elevation, pakai border saja
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpandChange(!isExpanded) }
                .padding(16.dp)
        ) {
            // Mini Player
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Album Art
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    SoftBlue.copy(alpha = if (isPlaying) pulseAlpha else 1f),
                                    MintGreen.copy(alpha = if (isPlaying) pulseAlpha else 1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.GraphicEq else Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Track Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = track.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Play/Pause Button
                FilledIconButton(
                    onClick = onPlayPauseClick,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (isPlaying) MintGreen else SoftBlue
                    ),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White
                    )
                }

                // Expand Icon
                IconButton(
                    onClick = { onExpandChange(!isExpanded) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color(0xFF64748B)
                    )
                }
            }

            // Expanded Player
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Progress Bar
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Slider(
                            value = if (duration > 0) currentPosition.toFloat() else 0f,
                            onValueChange = { onSeek(it.toInt()) },
                            valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = SoftBlue,
                                activeTrackColor = SoftBlue,
                                inactiveTrackColor = Color(0xFFE2E8F0)
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = formatTime(currentPosition),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = formatTime(duration),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Control Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Loop
                        IconButton(
                            onClick = onLoopClick,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isLooping) MintGreen.copy(0.15f)
                                    else Color(0xFFF1F5F9)
                                )
                        ) {
                            Icon(
                                Icons.Default.Repeat,
                                contentDescription = "Loop",
                                tint = if (isLooping) MintGreen else Color(0xFF64748B),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Previous
                        FilledIconButton(
                            onClick = onPreviousClick,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = Color(0xFFF1F5F9),
                                contentColor = Color(0xFF1E293B)
                            ),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                Icons.Default.SkipPrevious,
                                contentDescription = "Previous",
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // Next
                        FilledIconButton(
                            onClick = onNextClick,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = Color(0xFFF1F5F9),
                                contentColor = Color(0xFF1E293B)
                            ),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                Icons.Default.SkipNext,
                                contentDescription = "Next",
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // Timer
                        IconButton(
                            onClick = onTimerClick,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selectedTimer != null) SoftBlue.copy(0.15f)
                                    else Color(0xFFF1F5F9)
                                )
                        ) {
                            Icon(
                                Icons.Default.Timer,
                                contentDescription = "Timer",
                                tint = if (selectedTimer != null) SoftBlue else Color(0xFF64748B),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Stop & Timer Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onStopClick,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFEF4444)
                            )
                        ) {
                            Icon(
                                Icons.Default.Stop,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Stop", fontWeight = FontWeight.Medium)
                        }

                        if (selectedTimer != null) {
                            Surface(
                                color = SoftBlue.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = SoftBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        "$selectedTimer min",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = SoftBlue,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MusicTrackCard(
    track: MusicTrack,
    isCurrentlyPlaying: Boolean,
    pulseAlpha: Float,
    onClick: () -> Unit
) {
    // FIXED: Gunakan Card biasa dengan border
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = if (isCurrentlyPlaying) 1.5.dp else 0.5.dp,
                color = if (isCurrentlyPlaying) MintGreen.copy(alpha = 0.4f) else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentlyPlaying)
                MintGreen.copy(alpha = 0.08f)
            else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp  // FIXED: No elevation
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isCurrentlyPlaying)
                            Brush.linearGradient(
                                colors = listOf(
                                    SoftBlue.copy(alpha = pulseAlpha),
                                    MintGreen.copy(alpha = pulseAlpha)
                                )
                            )
                        else
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFF1F5F9),
                                    Color(0xFFE2E8F0)
                                )
                            )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCurrentlyPlaying) Icons.Default.GraphicEq else Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = if (isCurrentlyPlaying) Color.White else Color(0xFF64748B),
                    modifier = Modifier.size(22.dp)
                )
            }

            // Track Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isCurrentlyPlaying) MintGreen else Color(0xFF1E293B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = track.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Play Arrow
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = if (isCurrentlyPlaying) MintGreen else Color(0xFF94A3B8),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun InfoSection(
    title: String,
    content: String,
    color: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF334155),
            lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.5f
        )
    }
}

@Composable
fun FrequencyCard(
    icon: String,
    title: String,
    description: String,
    color: Color
) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.titleLarge
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF334155),
                    lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.5f
                )
            }
        }
    }
}