package com.zakycodes.zcmh.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.zakycodes.zcmh.ui.theme.MintGreen
import com.zakycodes.zcmh.ui.theme.SoftBlue
import com.zakycodes.zcmh.ui.viewmodel.JournalViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalDetailScreen(
    navController: NavController,
    journalId: String,
    viewModel: JournalViewModel = viewModel()
) {
    val isNewJournal = journalId == "new"
    val allJournals by viewModel.allJournals.collectAsState()
    val showDeleteDialog by viewModel.showDeleteDialog.collectAsState()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(isNewJournal) }
    var showPromptsDialog by remember { mutableStateOf(false) }
    var currentJournal by remember { mutableStateOf<com.zakycodes.zcmh.data.model.JournalEntry?>(null) }
    var wordCount by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()

    // Update word count
    LaunchedEffect(content) {
        wordCount = content.split(Regex("\\s+")).filter { it.isNotBlank() }.size
    }

    // Load data
    LaunchedEffect(journalId, allJournals) {
        if (!isNewJournal) {
            val journal = allJournals.find { it.id == journalId.toIntOrNull() }
            journal?.let {
                currentJournal = it
                title = it.title
                content = it.content
                viewModel.selectJournal(it)
            }
        } else {
            title = ""
            content = ""
            isEditing = true
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            // Clean Top Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF2A6D9C)
                        )
                    }

                    // Title
                    Text(
                        text = if (isNewJournal) "Jurnal Baru"
                        else if (isEditing) "Edit Jurnal"
                        else "Detail Jurnal",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B)
                    )

                    // Action buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (!isNewJournal && !isEditing) {
                            IconButton(onClick = { isEditing = true }) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = MintGreen
                                )
                            }
                            IconButton(onClick = { viewModel.showDeleteConfirmation() }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color(0xFFEF4444)
                                )
                            }
                        }

                        if (isEditing) {
                            FilledIconButton(
                                onClick = {
                                    scope.launch {
                                        if (title.isNotBlank() && content.isNotBlank()) {
                                            if (isNewJournal) {
                                                viewModel.insertJournal(title, content)
                                            } else {
                                                currentJournal?.let { journal ->
                                                    viewModel.updateJournal(
                                                        journal.copy(
                                                            title = title,
                                                            content = content
                                                        )
                                                    )
                                                }
                                            }
                                            navController.popBackStack()
                                        }
                                    }
                                },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MintGreen
                                )
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Save",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Prompts Button
            AnimatedVisibility(
                visible = isEditing,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                OutlinedButton(
                    onClick = { showPromptsDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SoftBlue
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(
                            colors = listOf(SoftBlue, MintGreen)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Lihat Pertanyaan Panduan",
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Title Section
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
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isEditing) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Judul") },
                            placeholder = { Text("Tuliskan judul yang bermakna...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SoftBlue,
                                focusedLabelColor = SoftBlue,
                                cursorColor = SoftBlue
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )

                            // Info Row
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                InfoChip(
                                    icon = Icons.Default.CalendarToday,
                                    text = currentJournal?.date ?: "",
                                    color = SoftBlue
                                )
                                InfoChip(
                                    icon = Icons.Default.TextFields,
                                    text = "$wordCount kata",
                                    color = MintGreen
                                )
                            }
                        }
                    }
                }
            }

            // Stats (editing mode only)
            AnimatedVisibility(
                visible = isEditing,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = "Karakter",
                        value = content.length.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Kata",
                        value = wordCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Baris",
                        value = content.lines().size.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Content Section
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
                    modifier = Modifier.padding(20.dp)
                ) {
                    if (isEditing) {
                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            label = { Text("Tuliskan rasa syukurmu") },
                            placeholder = {
                                Text(
                                    "Hari ini saya bersyukur karena...\n\n" +
                                            "💭 Ceritakan momen bahagiamu\n" +
                                            "✨ Rasakan emosi positifmu\n" +
                                            "🌟 Syukuri hal-hal kecil"
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 300.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SoftBlue,
                                focusedLabelColor = SoftBlue,
                                cursorColor = SoftBlue
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        Text(
                            text = content.ifBlank { "Tidak ada konten" },
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF334155),
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.6f
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Prompts Dialog
    if (showPromptsDialog) {
        AlertDialog(
            onDismissRequest = { showPromptsDialog = false },
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
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    "Pertanyaan Panduan",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(
                    modifier = Modifier.height(400.dp)
                ) {
                    Text(
                        "Pilih salah satu pertanyaan untuk memulai menulis:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(viewModel.getGratitudePrompts()) { prompt ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        "•",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MintGreen
                                    )
                                    Text(
                                        text = prompt,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF334155),
                                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4f
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                FilledTonalButton(
                    onClick = { showPromptsDialog = false },
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

    // Delete Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteDialog() },
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEE2E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    "Hapus Jurnal?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Jurnal yang dihapus tidak bisa dikembalikan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
            },
            confirmButton = {
                FilledTonalButton(
                    onClick = {
                        currentJournal?.let { viewModel.deleteJournal(it) }
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFFFEE2E2),
                        contentColor = Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Hapus", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissDeleteDialog() },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Batal", fontWeight = FontWeight.SemiBold)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: Color
) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = color
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color(0xFFF8F9FA),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SoftBlue
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8)
            )
        }
    }
}