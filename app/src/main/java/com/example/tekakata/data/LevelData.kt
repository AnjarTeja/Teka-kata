package com.example.tekakata.data

import androidx.compose.ui.graphics.Color

data class Level(
    val id: Int,
    val tema: String,
    val kataKunci: List<String>,
    val ukuranGrid: Int,
    val bgColorTop: Color = Color(0xFF6C63FF),
    val bgColorBottom: Color = Color(0xFFE7F5FF)
)

object LevelData {
    val levels = listOf(
        Level(
            id = 1, tema = "Hewan Darat",
            kataKunci = listOf("KUCING", "ANJING", "BURUNG", "IKAN", "HARIMAU", "GAJAH", "KELINCI"),
            ukuranGrid = 8,
            bgColorTop = Color(0xFF795548), bgColorBottom = Color(0xFFD7CCC8)
        ),
        Level(
            id = 2, tema = "Buah-buahan",
            kataKunci = listOf("APEL", "MANGGA", "PISANG", "ANGGUR", "JERUK", "SEMANGKA", "PEPAYA"),
            ukuranGrid = 8,
            bgColorTop = Color(0xFFFF7043), bgColorBottom = Color(0xFFFFF3E0)
        ),
        Level(
            id = 3, tema = "Warna",
            kataKunci = listOf("MERAH", "BIRU", "HIJAU", "KUNING", "PUTIH", "HITAM", "UNGU"),
            ukuranGrid = 8,
            bgColorTop = Color(0xFFAB47BC), bgColorBottom = Color(0xFFF3E5F5)
        ),
        Level(
            id = 4, tema = "Musim Dingin",
            kataKunci = listOf("BEKU", "BIRU", "DINGIN", "GEMETAR", "KERIPUT", "KULKAS", "LAPISAN", "SALJU"),
            ukuranGrid = 9,
            bgColorTop = Color(0xFF42A5F5), bgColorBottom = Color(0xFFE3F2FD)
        ),
        Level(
            id = 5, tema = "Hewan Laut",
            kataKunci = listOf("HIU", "PAUS", "LUMBA", "KEPITING", "GURITA", "BINTANG", "KERANG"),
            ukuranGrid = 9,
            bgColorTop = Color(0xFF00838F), bgColorBottom = Color(0xFFE0F7FA)
        ),
        Level(
            id = 6, tema = "Kendaraan",
            kataKunci = listOf("MOBIL", "SEPEDA", "KERETA", "PESAWAT", "KAPAL", "TRUK", "BECAK"),
            ukuranGrid = 9,
            bgColorTop = Color(0xFFEF5350), bgColorBottom = Color(0xFFFFEBEE)
        ),
        Level(
            id = 7, tema = "Profesi",
            kataKunci = listOf("DOKTER", "GURU", "POLISI", "PILOT", "KOKI", "PETANI", "NELAYAN"),
            ukuranGrid = 10,
            bgColorTop = Color(0xFF5C6BC0), bgColorBottom = Color(0xFFE8EAF6)
        ),
        Level(
            id = 8, tema = "Benda di Rumah",
            kataKunci = listOf("MEJA", "KURSI", "LEMARI", "PINTU", "JENDELA", "KASUR", "LAMPU"),
            ukuranGrid = 10,
            bgColorTop = Color(0xFFFFA726), bgColorBottom = Color(0xFFFFF8E1)
        ),
        Level(
            id = 9, tema = "Alam",
            kataKunci = listOf("GUNUNG", "SUNGAI", "LAUT", "AWAN", "HUJAN", "POHON", "BUNGA"),
            ukuranGrid = 10,
            bgColorTop = Color(0xFF66BB6A), bgColorBottom = Color(0xFFE8F5E9)
        ),
        Level(
            id = 10, tema = "Campuran",
            kataKunci = listOf("BINTANG", "ROKET", "SINGA", "MATAHARI", "BUMI", "BULAN", "KOMET"),
            ukuranGrid = 10,
            bgColorTop = Color(0xFF26A69A), bgColorBottom = Color(0xFFE0F2F1)
        ),
        Level(
            id = 11, tema = "Makanan",
            kataKunci = listOf("NASI", "ROTI", "SOTO", "BAKSO", "MIE", "TEMPE", "TAHU", "SATE"),
            ukuranGrid = 10,
            bgColorTop = Color(0xFFEC407A), bgColorBottom = Color(0xFFFCE4EC)
        ),
        Level(
            id = 12, tema = "Olahraga",
            kataKunci = listOf("BOLA", "TENIS", "RENANG", "LARI", "SEPAK", "BULU", "TANGKIS"),
            ukuranGrid = 10,
            bgColorTop = Color(0xFF29B6F6), bgColorBottom = Color(0xFFE1F5FE)
        ),
        Level(
            id = 13, tema = "Hewan Terbang",
            kataKunci = listOf("ELANG", "RAJAWALI", "MERPATI", "BEO", "GAGAK", "BURUNG", "KELELAWAR"),
            ukuranGrid = 11,
            bgColorTop = Color(0xFF7E57C2), bgColorBottom = Color(0xFFEDE7F6)
        ),
        Level(
            id = 14, tema = "Sekolah",
            kataKunci = listOf("PENSIL", "BUKU", "PAPAN", "KELAS", "GURU", "MURID", "TAS", "PENA"),
            ukuranGrid = 11,
            bgColorTop = Color(0xFFFF7043), bgColorBottom = Color(0xFFFBE9E7)
        ),
        Level(
            id = 15, tema = "Luar Angkasa",
            kataKunci = listOf("PLANET", "BINTANG", "KOMET", "GALAKSI", "NEBULA", "SATELIT", "ASTRONOT", "ROKET"),
            ukuranGrid = 11,
            bgColorTop = Color(0xFF1A237E), bgColorBottom = Color(0xFFBBDEFB)
        )
    )

    fun getLevelById(id: Int): Level = levels.first { it.id == id }
}
