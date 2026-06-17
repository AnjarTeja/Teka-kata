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

    private val _hintCount = MutableStateFlow(0)
    val hintCount: StateFlow<Int> = _hintCount.asStateFlow()
    private val maxHints = 3

    private val _showConfetti = MutableStateFlow(false)
    val showConfetti: StateFlow<Boolean> = _showConfetti.asStateFlow()

    private val _starRating = MutableStateFlow(0)
    val starRating: StateFlow<Int> = _starRating.asStateFlow()

    private var _lockedDirection: Pair<Int, Int>? = null

    private val _lastFoundWord = MutableStateFlow<String?>(null)
    val lastFoundWord: StateFlow<String?> = _lastFoundWord.asStateFlow()

    private val _hintGlowCells = MutableStateFlow<Set<Pair<Int, Int>>>(emptySet())
    val hintGlowCells: StateFlow<Set<Pair<Int, Int>>> = _hintGlowCells.asStateFlow()

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
        _showConfetti.value = false
        _starRating.value = 0
        _lastFoundWord.value = null
        _hintGlowCells.value = emptySet()
    }

    fun startSelection(row: Int, col: Int) {
        val level = _currentLevel.value ?: return
        if (row < 0 || row >= level.ukuranGrid || col < 0 || col >= level.ukuranGrid) return
        if (_isLevelComplete.value) return
        _selectedCells.value = listOf(Pair(row, col))
        _lockedDirection = null
    }

    fun updateSelection(row: Int, col: Int) {
        val level = _currentLevel.value ?: return
        if (row < 0 || row >= level.ukuranGrid || col < 0 || col >= level.ukuranGrid) return
        if (_isLevelComplete.value) return

        val current = _selectedCells.value
        if (current.isEmpty()) return
        if (current.size == 1 && _lockedDirection == null) {
            val first = current.first()
            val dr = row - first.first
            val dc = col - first.second

            val absDr = kotlin.math.abs(dr)
            val absDc = kotlin.math.abs(dc)

            val validDirection = when {
                dr == 0 && dc != 0 -> true
                dc == 0 && dr != 0 -> true
                absDr == absDc && absDr > 0 -> true
                else -> false
            }

            if (validDirection) {
                val normDr = dr.coerceIn(-1, 1)
                val normDc = dc.coerceIn(-1, 1)
                _lockedDirection = Pair(normDr, normDc)

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

        if (current.size >= 2) {
            val prevCell = current[current.size - 2]
            if (row == prevCell.first && col == prevCell.second) {
                _selectedCells.value = current.dropLast(1)
                return
            }
        }

        if (current.size >= 2 && row == firstCell.first && col == firstCell.second) {
            _selectedCells.value = listOf(firstCell)
            _lockedDirection = null
            return
        }

        val nextRow = lastCell.first + lockedDir.first
        val nextCol = lastCell.second + lockedDir.second

        if (row == nextRow && col == nextCol) {
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

        val selectedString = selected.joinToString("") { (r, c) ->
            _grid.value[r][c].toString()
        }

        if (selectedString in level.kataKunci && selectedString !in _foundWords.value) {
            _foundWords.value = _foundWords.value + selectedString
            _foundCells.value = _foundCells.value + selected.toSet()
            _lastFoundWord.value = selectedString

            if (_foundWords.value.size == level.kataKunci.size) {
                val stars = calculateStars(_hintCount.value)
                _starRating.value = stars
                _showConfetti.value = true
                _isLevelComplete.value = true
            }
        }

        _selectedCells.value = emptyList()
        _lockedDirection = null
    }

    fun clearLastFoundWord() {
        _lastFoundWord.value = null
    }

    fun giveHint() {
        val level = _currentLevel.value ?: return
        if (_isLevelComplete.value) return
        if (_hintCount.value >= maxHints) return

        val firstUnfound = level.kataKunci.firstOrNull { it !in _foundWords.value } ?: return
        val cells = WordSearchEngine.findWordInGrid(_grid.value, firstUnfound, level.ukuranGrid)
        val firstCell = cells?.firstOrNull() ?: return
        val allCells = cells ?: return

        _hintCount.value++
        viewModelScope.launch {
            _hintIndices.value = firstCell
            _hintGlowCells.value = allCells.toSet()
            delay(2000)
            _hintIndices.value = null
            _hintGlowCells.value = emptySet()
        }
    }

    fun resetSelection() {
        _selectedCells.value = emptyList()
        _lockedDirection = null
    }

    fun resetAllAnswers() {
        _foundWords.value = emptySet()
        _foundCells.value = emptySet()
        _selectedCells.value = emptyList()
        _isLevelComplete.value = false
        _hintIndices.value = null
        _hintCount.value = 0
        _lockedDirection = null
        _showConfetti.value = false
        _starRating.value = 0
        _lastFoundWord.value = null
        _hintGlowCells.value = emptySet()
    }

    fun dismissConfetti() {
        _showConfetti.value = false
    }

    private fun calculateStars(hintsUsed: Int): Int {
        return when {
            hintsUsed == 0 -> 3
            hintsUsed <= 2 -> 2
            else -> 1
        }
    }
}
