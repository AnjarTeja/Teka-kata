package com.example.tekakata.data

data class Level(
    val id: Int,
    val tema: String,
    val kataKunci: List<String>,
    val ukuranGrid: Int
)

object LevelData {
    val levels = listOf(
        Level(
            id = 1,
            tema = "Hewan Darat",
            kataKunci = listOf("KUCING", "ANJING", "BURUNG", "IKAN", "HARIMAU", "GAJAH", "KELINCI"),
            ukuranGrid = 8
        ),
        Level(
            id = 2,
            tema = "Buah-buahan",
            kataKunci = listOf("APEL", "MANGGA", "PISANG", "ANGGUR", "JERUK", "SEMANGKA", "PEPAYA"),
            ukuranGrid = 8
        ),
        Level(
            id = 3,
            tema = "Warna",
            kataKunci = listOf("MERAH", "BIRU", "HIJAU", "KUNING", "PUTIH", "HITAM", "UNGU"),
            ukuranGrid = 8
        ),
        Level(
            id = 4,
            tema = "Musim Dingin",
            kataKunci = listOf("BEKU", "BIRU", "DINGIN", "GEMETAR", "KERIPUT", "KULKAS", "LAPISAN", "SALJU"),
            ukuranGrid = 9
        ),
        Level(
            id = 5,
            tema = "Hewan Laut",
            kataKunci = listOf("HIU", "PAUS", "LUMBA", "KEPITING", "GURITA", "BINTANG", "KERANG"),
            ukuranGrid = 9
        ),
        Level(
            id = 6,
            tema = "Kendaraan",
            kataKunci = listOf("MOBIL", "SEPEDA", "KERETA", "PESAWAT", "KAPAL", "TRUK", "BECAK"),
            ukuranGrid = 9
        ),
        Level(
            id = 7,
            tema = "Profesi",
            kataKunci = listOf("DOKTER", "GURU", "POLISI", "PILOT", "KOKI", "PETANI", "NELAYAN"),
            ukuranGrid = 10
        ),
        Level(
            id = 8,
            tema = "Benda di Rumah",
            kataKunci = listOf("MEJA", "KURSI", "LEMARI", "PINTU", "JENDELA", "KASUR", "LAMPU"),
            ukuranGrid = 10
        ),
        Level(
            id = 9,
            tema = "Alam",
            kataKunci = listOf("GUNUNG", "SUNGAI", "LAUT", "AWAN", "HUJAN", "POHON", "BUNGA"),
            ukuranGrid = 10
        ),
        Level(
            id = 10,
            tema = "Campuran",
            kataKunci = listOf("BINTANG", "ROKET", "SINGA", "MATAHARI", "BUMI", "BULAN", "KOMET"),
            ukuranGrid = 10
        )
    )

    fun getLevelById(id: Int): Level = levels.first { it.id == id }
}
