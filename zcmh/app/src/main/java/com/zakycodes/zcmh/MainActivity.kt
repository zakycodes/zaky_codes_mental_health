package com.zakycodes.zcmh

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.zakycodes.zcmh.ui.components.BottomNavBar
import com.zakycodes.zcmh.ui.navigation.Navigation
import com.zakycodes.zcmh.ui.theme.ZCMHTheme
import com.zakycodes.zcmh.ui.viewmodel.MusicViewModel
import com.zakycodes.zcmh.utils.MusicControlHandler
import com.zakycodes.zcmh.ui.components.FloatingWhatsAppButton
import androidx.compose.material3.FabPosition

class MainActivity : ComponentActivity() {

    private val musicViewModel: MusicViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted
        } else {
            // Permission denied
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Hubungkan ViewModel dengan Handler
        MusicControlHandler.setViewModel(musicViewModel)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            ZCMHTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ZCMHApp(musicViewModel = musicViewModel)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicControlHandler.clearViewModel()
    }
}

@Composable
fun ZCMHApp(musicViewModel: MusicViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) },
        floatingActionButton = {
            FloatingWhatsAppButton(
                phoneNumber = "6289691789422",
                message = "Halo, saya ingin konsultasi dari aplikasi ZCMH"
            )
        },
        floatingActionButtonPosition = androidx.compose.material3.FabPosition.Start // ✅ TAMBAH BARIS INI (KIRI BAWAH)
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Navigation(
                navController = navController,
                musicViewModel = musicViewModel
            )
        }
    }
}