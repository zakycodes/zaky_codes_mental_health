package com.zakycodes.zcmh.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.zakycodes.zcmh.ui.screens.*
import com.zakycodes.zcmh.ui.viewmodel.MusicViewModel

@Composable
fun Navigation(
    navController: NavHostController,
    musicViewModel: MusicViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "alarm"
    ) {
        composable("alarm") {
            AlarmScreen()
        }

        composable("journal") {
            JournalScreen(navController = navController)
        }

        composable("journal_detail/{journalId}") { backStackEntry ->
            val journalId = backStackEntry.arguments?.getString("journalId") ?: "new"
            JournalDetailScreen(
                navController = navController,
                journalId = journalId
            )
        }

        composable("music") {
            MusicScreen(viewModel = musicViewModel)
        }

        // ✨ TAMBAHAN BARU - Route Konsultasi
        composable("consultation") {
            ConsultationScreen()
        }
    }
}