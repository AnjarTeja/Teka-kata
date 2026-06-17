package com.example.tekakata.engine

object WordSearchEngine {

    private val DIRECTIONS = listOf(
        intArrayOf(0, 1),   // Kanan
        intArrayOf(1, 0),   // Bawah
        intArrayOf(1, 1)    // Diagonal Kanan Bawah
    )

    fun generateGrid(words: List<String>, size: Int): Array<CharArray> {
        val grid = Array(size) { CharArray(size) { ' ' } }

        // Urutkan kata dari terpanjang ke terpendek agar yang panjang dapat tempat dulu
        val sortedWords = words.sortedByDescending { it.length }

        for (word in sortedWords) {
            var placed = false
            repeat(100) { attempt ->
                if (placed) return@repeat
                val dir = DIRECTIONS.random()
                val dr = dir[0]
                val dc = dir[1]

                val maxRow = if (dr == 0) size - 1 else size - word.length
                val maxCol = if (dc == 0) size - 1 else size - word.length

                if (maxRow < 0 || maxCol < 0) return@repeat

                val startRow = (0..maxRow).random()
                val startCol = (0..maxCol).random()

                if (canPlace(grid, word, startRow, startCol, dr, dc, size)) {
                    placeWord(grid, word, startRow, startCol, dr, dc)
                    placed = true
                }
            }
            if (!placed) {
                throw IllegalStateException("Gagal menempatkan kata '$word' di grid setelah 100 percobaan.")
            }
        }

        // Isi sel kosong dengan huruf acak
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (grid[r][c] == ' ') {
                    grid[r][c] = ('A'..'Z').random()
                }
            }
        }

        return grid
    }

    private fun canPlace(
        grid: Array<CharArray>,
        word: String,
        row: Int,
        col: Int,
        dr: Int,
        dc: Int,
        size: Int
    ): Boolean {
        for (i in word.indices) {
            val r = row + i * dr
            val c = col + i * dc
            if (r >= size || c >= size) return false
            if (grid[r][c] != ' ' && grid[r][c] != word[i]) return false
        }
        return true
    }

    private fun placeWord(
        grid: Array<CharArray>,
        word: String,
        row: Int,
        col: Int,
        dr: Int,
        dc: Int
    ) {
        for (i in word.indices) {
            grid[row + i * dr][col + i * dc] = word[i]
        }
    }

    /**
     * Mencari posisi semua huruf sebuah kata di grid.
     * Mengembalikan list koordinat (row, col) untuk setiap huruf, atau null jika tidak ditemukan.
     */
    fun findWordInGrid(
        grid: Array<CharArray>,
        word: String,
        size: Int
    ): List<Pair<Int, Int>>? {
        for (r in 0 until size) {
            for (c in 0 until size) {
                for (dir in DIRECTIONS) {
                    val dr = dir[0]
                    val dc = dir[1]
                    val cells = mutableListOf<Pair<Int, Int>>()
                    var found = true
                    for (i in word.indices) {
                        val nr = r + i * dr
                        val nc = c + i * dc
                        if (nr >= size || nc >= size || grid[nr][nc] != word[i]) {
                            found = false
                            break
                        }
                        cells.add(Pair(nr, nc))
                    }
                    if (found) return cells
                }
            }
        }
        return null
    }
}
