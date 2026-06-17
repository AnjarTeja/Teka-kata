package com.example.tekakata.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tekakata.data.Level
import com.example.tekakata.data.LevelData
import com.example.tekakata.engine.WordSearchEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val _grid = MutableStateFlow<Array<CharArray>>(emptyArray())
    val grid: StateFlow<Array<CharArray>> = _grid.asStateFlow()

    private val _foundWords = MutableStateFlow<Set<String>>(emptySet())
    val foundWords: StateFlow<Set<String>> = _foundWords.asStateFlow()

    private val _foundCells = MutableStateFlow<Set<Pair<Int, Int>>>(emptySet())
    val foundCells: StateFlow<Set<Pair<Int, Int>>> = _foundCells.asStateFlow()

    private val _selectedCells = MutableStateFlow<List<Pair<Int, Int>>>(emptyList())
    val selectedCells: StateFlow<List<Pair<Int, Int>>> = _selectedCells.asStateFlow()

    private val _isLevelComplete = MutableStateFlow(false)
    val isLevelComplete: StateFlow<Boolean> = _isLevelComplete.asStateFlow()

    private val _hintIndices = MutableStateFlow<Pair<Int, Int>?>(null)
    val hintIndices: StateFlow<Pair<Int, Int>?> = _hintIndices.asStateFlow()

    private val _currentLevel = MutableStateFlow<Level?>(null)
    val currentLevel: StateFlow<Level?> = _currentLevel.asStateFlow()

    // Batas petunjuk: maksimal 3x per level
    private val _hintCount = MutableStateFlow(0)
    val hintCount: StateFlow<Int> = _hintCount.asStateFlow()
    private val maxHints = 3

    // Arah drag yang terkunci: (deltaRow, deltaCol) — hanya boleh satu arah
    private var _lockedDirection: Pair<Int, Int>? = null

    fun initLevel(levelId: Int) {
        val level = LevelData.getLevelById(levelId)
        _currentLevel.value = level
        _grid.value = WordSearchEngine.generateGrid(level.kataKunci, level.ukuranGrid)
        _foundWords.value = emptySet()
        _foundCells.value = emptySet()
        _selectedCells.value = emptyList()
        _isLevelComplete.value = false
        _hintIndices.value = null
        _hintCount.value = 0
        _lockedDirection = null
    }

    fun startSelection(row: Int, col: Int) {
        val level = _currentLevel.value ?: return
        if (row < 0 || row >= level.ukuranGrid || col < 0 || col >= level.ukuranGrid) return
        if (_isLevelComplete.value) return
        _selectedCells.value = listOf(Pair(row, col))
        _lockedDirection = null // Reset arah saat mulai drag baru
    }

    fun updateSelection(row: Int, col: Int) {
        val level = _currentLevel.value ?: return
        if (row < 0 || row >= level.ukuranGrid || col < 0 || col >= level.ukuranGrid) return
        if (_isLevelComplete.value) return

        val current = _selectedCells.value
        if (current.isEmpty()) return
        if (current.size == 1 && _lockedDirection == null) {
            // Belum ada arah terkunci, tentukan arah dari sel pertama ke sel ini
            val first = current.first()
            val dr = row - first.first
            val dc = col - first.second

            // Hanya izinkan arah lurus: horizontal, vertikal, atau diagonal sempurna
            val absDr = kotlin.math.abs(dr)
            val absDc = kotlin.math.abs(dc)

            val validDirection = when {
                dr == 0 && dc != 0 -> true  // Horizontal
                dc == 0 && dr != 0 -> true  // Vertikal
                absDr == absDc && absDr > 0 -> true  // Diagonal sempurna
                else -> false
            }

            if (validDirection) {
                // Normalisasi ke -1, 0, atau 1
                val normDr = dr.coerceIn(-1, 1)
                val normDc = dc.coerceIn(-1, 1)
                _lockedDirection = Pair(normDr, normDc)

                // Tambahkan semua sel dari first sampai (row, col) sepanjang garis lurus
                val steps = maxOf(absDr, absDc)
                val newCells = mutableListOf(first)
                for (i in 1..steps) {
                    newCells.add(Pair(first.first + i * normDr, first.second + i * normDc))
                }
                _selectedCells.value = newCells
            }
            return
        }

        val lockedDir = _lockedDirection ?: return
        val lastCell = current.last()
        val firstCell = current.first()

        // Cek apakah user mundur (backtrack) — menghapus sel terakhir
        if (current.size >= 2) {
            val prevCell = current[current.size - 2]
            if (row == prevCell.first && col == prevCell.second) {
                _selectedCells.value = current.dropLast(1)
                return
            }
        }

        // Cek mundur ke first cell (hapus semua kecuali first)
        if (current.size >= 2 && row == firstCell.first && col == firstCell.second) {
            _selectedCells.value = listOf(firstCell)
            _lockedDirection = null
            return
        }

        // Hanya izinkan extend ke arah yang terkunci dari sel terakhir
        val nextRow = lastCell.first + lockedDir.first
        val nextCol = lastCell.second + lockedDir.second

        if (row == nextRow && col == nextCol) {
            // Pastikan tidak keluar batas grid
            if (nextRow in 0 until level.ukuranGrid && nextCol in 0 until level.ukuranGrid) {
                _selectedCells.value = current + Pair(nextRow, nextCol)
            }
        }
    }

    fun endSelection() {
        val level = _currentLevel.value ?: return
        val selected = _selectedCells.value

        if (selected.isEmpty()) {
            _lockedDirection = null
            return
        }

        // Gabungkan huruf dari sel yang dipilih
        val selectedString = selected.joinToString("") { (r, c) ->
            _grid.value[r][c].toString()
        }

        // Cek apakah string tersebut ada di daftar kata dan belum ditemukan
        if (selectedString in level.kataKunci && selectedString !in _foundWords.value) {
            _foundWords.value = _foundWords.value + selectedString
            _foundCells.value = _foundCells.value + selected.toSet()

            // Cek apakah semua kata sudah ditemukan
            if (_foundWords.value.size == level.kataKunci.size) {
                _isLevelComplete.value = true
            }
        }

        _selectedCells.value = emptyList()
        _lockedDirection = null
    }

    fun giveHint() {
        val level = _currentLevel.value ?: return
        if (_isLevelComplete.value) return
        if (_hintCount.value >= maxHints) return

        val firstUnfound = level.kataKunci.firstOrNull { it !in _foundWords.value } ?: return
        val cells = WordSearchEngine.findWordInGrid(_grid.value, firstUnfound, level.ukuranGrid)
        val firstCell = cells?.firstOrNull() ?: return

        _hintCount.value++
        viewModelScope.launch {
            _hintIndices.value = firstCell
            delay(1500)
            _hintIndices.value = null
        }
    }

    fun resetSelection() {
        _selectedCells.value = emptyList()
        _lockedDirection = null
    }

    // Reset semua jawaban yang sudah ditemukan — dipanggil setelah konfirmasi dialog
    fun resetAllAnswers() {
        _foundWords.value = emptySet()
        _foundCells.value = emptySet()
        _selectedCells.value = emptyList()
        _isLevelComplete.value = false
        _hintIndices.value = null
        _hintCount.value = 0
        _lockedDirection = null
    }
}
