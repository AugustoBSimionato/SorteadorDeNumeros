package com.example.sorteadordenumeros

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.random.Random

@Serializable
data class UiState(
    val drawAmountNumber: Int = 2,
    val initialLimit: Int = 1,
    val finalLimit: Int = 100,
    val shouldRepeatNumbers: Boolean = true,
    val currentDrawNumber: Int = 0,
    val drawNumbers: List<Int> = emptyList()
)

class SorteioViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun setDrawAmountNumber(drawAmountNumber: Int) {
        _uiState.value = _uiState.value.copy(drawAmountNumber = drawAmountNumber)
    }

    fun setInitialLimit(initialLimit: Int) {
        _uiState.value = _uiState.value.copy(initialLimit = initialLimit)
    }

    fun setFinalLimit(finalLimit: Int) {
        _uiState.value = _uiState.value.copy(finalLimit = finalLimit)
    }

    fun setShouldRepeatNumbers(shouldRepeatNumbers: Boolean) {
        _uiState.value = _uiState.value.copy(shouldRepeatNumbers = shouldRepeatNumbers)
    }

    fun drawNumbers() {
        val from = minOf(_uiState.value.initialLimit, _uiState.value.finalLimit)
        val to = maxOf(_uiState.value.initialLimit, _uiState.value.finalLimit)
        val range = from..to
        val rangeSize = to - from + 1

        val drawNumbers = mutableListOf<Int>()
        val shouldRepeat = _uiState.value.shouldRepeatNumbers

        if (!shouldRepeat && _uiState.value.drawAmountNumber >= rangeSize) {
            drawNumbers.addAll(range.shuffled().take(_uiState.value.drawAmountNumber))
        } else if (!shouldRepeat) {
            val available = range.toMutableList()
            repeat(_uiState.value.drawAmountNumber) {
                if (available.isNotEmpty()) {
                    val index = Random.nextInt(available.size)
                    drawNumbers.add(available.removeAt(index))
                }
            }
        } else {
            repeat(_uiState.value.drawAmountNumber) {
                drawNumbers.add(Random.nextInt(from, to + 1))
            }
        }

        _uiState.value =
            _uiState.value.copy(
                currentDrawNumber = _uiState.value.currentDrawNumber + 1,
                drawNumbers = drawNumbers.toList()
            )

        val gsonString = GsonBuilder().setPrettyPrinting().create().toJson(_uiState.value)
        val serializationString = Json { encodeDefaults = true }.encodeToString(_uiState.value)

        Log.d("SorteioViewModel", "[Gson] UiState serializado: ${gsonString}")
        Log.d("SorteioViewModel", "[Serialization] UiState serializado: ${serializationString}")

        Log.d("SorteioViewModel", "[Gson] UiState desserializado: ${GsonBuilder().setPrettyPrinting().create().fromJson(gsonString,
            UiState::class.java)}")
        Log.d("SorteioViewModel", "[Serialization] UiState desserializado: ${Json { encodeDefaults = true }.decodeFromString<UiState>(serializationString)}")
    }
}