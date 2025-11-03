//package com.zakycodes.zcmh.data.model
//
//import androidx.annotation.RawRes
//
//data class MusicTrack(
//    val id: Int,
//    @RawRes val resourceId: Int,
//    val title: String,
//    val description: String
//)


package com.zakycodes.zcmh.data.model

import androidx.annotation.RawRes

// Enum untuk kategori musik
enum class MusicCategory(val displayName: String, val frequency: String) {
    RELAX("Relax/Anxiety", "3-7 Hz Theta"),
    SLEEP("Sleep", "2-3 Hz Delta"),
    FOCUS("Focus", "40 Hz Gamma")
}

// Data class untuk track musik
data class MusicTrack(
    val id: Int,
    @RawRes val resourceId: Int,
    val title: String,
    val description: String,
    val category: MusicCategory  // ← TAMBAHAN BARU
)