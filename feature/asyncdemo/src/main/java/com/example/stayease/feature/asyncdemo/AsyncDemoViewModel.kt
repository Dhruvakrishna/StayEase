package com.example.stayease.feature.asyncdemo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stayease.data.remote.api.OverpassApi
import com.example.stayease.data.remote.query.staysAround
import com.example.stayease.domain.model.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.system.measureTimeMillis

data class AsyncUiState(
  val running: Boolean = false,
  val resultText: String = "Run a demo to see async behavior.",
  val lastMs: Long? = null
)

@HiltViewModel
class AsyncDemoViewModel @Inject constructor(private val api: OverpassApi) : ViewModel() {
  private val _state = MutableStateFlow(AsyncUiState())
  val state = _state.asStateFlow()
  private var job: Job? = null

  fun cancel() { job?.cancel(); _state.value = _state.value.copy(running = false, resultText = "Cancelled.") }

  fun runParallelQueries() {
    if (_state.value.running) return
    job = viewModelScope.launch {
      _state.value = _state.value.copy(running = true, resultText = "Running parallel Overpass queries...")
      try {
        val pivot = GeoPoint(41.8781, -87.6298)
        val elapsed = measureTimeMillis {
          val a = async { api.query(staysAround(pivot, 1500)) }
          val b = async { api.query(staysAround(pivot, 2500)) }
          val c = async { api.query(staysAround(pivot, 3500)) }
          val total = a.await().elements.size + b.await().elements.size + c.await().elements.size
          _state.value = _state.value.copy(resultText = "Parallel queries complete. Elements: $total")
        }
        _state.value = _state.value.copy(running = false, lastMs = elapsed)
      } catch (t: Throwable) {
        _state.value = _state.value.copy(running = false, resultText = "Error: ${t.message}")
      }
    }
  }

  fun runRetryBackoff() {
    if (_state.value.running) return
    job = viewModelScope.launch {
      _state.value = _state.value.copy(running = true, resultText = "Running retry with backoff...")
      try {
        var attempt = 0
        var delayMs = 250L
        val elapsed = measureTimeMillis {
          while (true) {
            attempt++
            val ok = tryCall(attempt)
            if (ok) break
            delay(delayMs); delayMs = (delayMs * 2).coerceAtMost(2000L)
          }
        }
        _state.value = _state.value.copy(running = false, lastMs = elapsed, resultText = "Retry succeeded after $attempt attempts.")
      } catch (t: Throwable) {
        _state.value = _state.value.copy(running = false, resultText = "Error: ${t.message}")
      }
    }
  }

  private suspend fun tryCall(attempt: Int): Boolean {
    val pivot = GeoPoint(41.8781, -87.6298)
    api.query(staysAround(pivot, 1200))
    return attempt >= 3
  }
}
