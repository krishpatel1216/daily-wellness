package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.assistant.JarvisVoiceManager
import com.example.audio.SoundscapeEngine
import com.example.audio.SoundscapeType
import com.example.data.db.AppDatabase
import com.example.data.db.WellnessDataDefaults
import com.example.data.model.RoutineTask
import com.example.data.model.SplitDefinition
import com.example.data.model.UserProgress
import com.example.data.model.WeeklyDayInfo
import com.example.data.repository.WellnessRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RoutineState(
    val morningTasks: List<RoutineTask> = emptyList(),
    val workoutTasks: List<RoutineTask> = emptyList(),
    val currentSplit: SplitDefinition = WellnessDataDefaults.workoutSplits[0],
    val userProgress: UserProgress = UserProgress(),
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val progressPercent: Int = 0,
    val isAllCompleted: Boolean = false,
    val weeklyDays: List<WeeklyDayInfo> = emptyList()
)

data class TimerState(
    val remainingSeconds: Int = 1200,
    val totalSeconds: Int = 1200,
    val isRunning: Boolean = false,
    val label: String = "Morning Meditation (20m)"
)

data class SoundscapeState(
    val isPlaying: Boolean = false,
    val type: SoundscapeType = SoundscapeType.HZ_432,
    val volume: Float = 0.25f
)

class WellnessViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WellnessRepository
    private val soundscapeEngine = SoundscapeEngine()
    val jarvisVoiceManager = JarvisVoiceManager(application)

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val _soundscapeState = MutableStateFlow(SoundscapeState())
    val soundscapeState: StateFlow<SoundscapeState> = _soundscapeState.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    private val _showInfoDialog = MutableStateFlow(false)
    val showInfoDialog: StateFlow<Boolean> = _showInfoDialog.asStateFlow()

    private var timerJob: Job? = null

    init {
        val database = AppDatabase.getInstance(application)
        repository = WellnessRepository(database.routineDao())
        viewModelScope.launch {
            repository.ensureDataPopulated()
        }
    }

    val routineState: StateFlow<RoutineState> = combine(
        repository.allTasks,
        repository.userProgress
    ) { allTasks, progressOrNull ->
        val progress = progressOrNull ?: UserProgress()
        val splitKey = progress.activeSplitKey
        val currentSplit = WellnessDataDefaults.workoutSplits.find { it.key == splitKey }
            ?: WellnessDataDefaults.workoutSplits[0]

        val morningList = allTasks.filter { it.category == "morning" }
        val workoutList = allTasks.filter { it.category == splitKey }

        val morningDone = morningList.count { it.isCompleted }
        val workoutDone = workoutList.count { it.isCompleted }
        val completed = morningDone + workoutDone
        val total = morningList.size + workoutList.size
        val percent = if (total > 0) (completed * 100) / total else 0
        val allDone = total > 0 && completed == total

        val weekly = buildWeeklyDays(progress.weeklyStatus, allDone)

        RoutineState(
            morningTasks = morningList,
            workoutTasks = workoutList,
            currentSplit = currentSplit,
            userProgress = progress,
            completedCount = completed,
            totalCount = total,
            progressPercent = percent,
            isAllCompleted = allDone,
            weeklyDays = weekly
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RoutineState()
    )

    private fun buildWeeklyDays(statusString: String, isTodayDone: Boolean): List<WeeklyDayInfo> {
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val splits = listOf("Back/Bi", "Chest/Tri", "Legs/Sh", "Back/Bi", "Chest/Tri", "Legs/Sh", "Rest")
        val tokens = statusString.split(",")
        return days.mapIndexed { index, day ->
            val isCompleted = if (index == 6) isTodayDone else (tokens.getOrNull(index) == "1")
            WeeklyDayInfo(
                dayName = day,
                splitTag = splits[index],
                isCompleted = isCompleted,
                isToday = (index == 6),
                medMins = 20,
                workoutMins = if (index == 6) 0 else 45
            )
        }
    }

    // Task Actions
    fun toggleTask(id: String, isCurrentlyCompleted: Boolean) {
        viewModelScope.launch {
            val newState = !isCurrentlyCompleted
            repository.setTaskCompleted(id, newState)

            val current = routineState.value
            val newCompletedCount = if (newState) current.completedCount + 1 else current.completedCount - 1
            if (newCompletedCount == current.totalCount && current.totalCount > 0) {
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                repository.incrementStreak(today)
                jarvisVoiceManager.speak("Remarkable discipline Krish sir! All routines are complete for today. Your streak is secured.")
            }
        }
    }

    fun resetAllRoutines() {
        viewModelScope.launch {
            repository.resetAllRoutines()
            jarvisVoiceManager.speak("All daily routine checks have been reset, Krish sir. Ready for a new day.")
        }
    }

    fun selectSplit(splitKey: String, announce: Boolean = true) {
        viewModelScope.launch {
            repository.setActiveSplit(splitKey)
            val split = WellnessDataDefaults.workoutSplits.find { it.key == splitKey }
            if (announce && split != null) {
                jarvisVoiceManager.speak("Switched to ${split.name} protocol for ${split.dayRange}, Krish sir.")
            }
        }
    }

    fun addCustomTask(category: String, title: String, time: String, desc: String) {
        viewModelScope.launch {
            repository.addCustomTask(category, title, time, desc)
            jarvisVoiceManager.speak("Added $title to your routine, Krish sir.")
            _showAddDialog.value = false
        }
    }

    // Hydration
    fun addWaterGlass() {
        viewModelScope.launch {
            val current = routineState.value.userProgress.waterGlasses
            if (current < 10) {
                val next = current + 1
                repository.setWaterGlasses(next)
                val liters = String.format(Locale.US, "%.1f", next * 0.25f)
                if (next == 10) {
                    jarvisVoiceManager.speak("Outstanding Krish sir! Daily hydration target of 2.5 liters achieved.")
                } else {
                    jarvisVoiceManager.speak("Logged 250 milliliters of water. Current total: $liters liters, Krish sir.")
                }
            }
        }
    }

    fun removeWaterGlass() {
        viewModelScope.launch {
            val current = routineState.value.userProgress.waterGlasses
            if (current > 0) {
                repository.setWaterGlasses(current - 1)
            }
        }
    }

    fun setWaterGlasses(count: Int) {
        viewModelScope.launch {
            repository.setWaterGlasses(count)
        }
    }

    // Journal
    fun updateJournalText(text: String) {
        viewModelScope.launch {
            repository.updateJournal(text)
        }
    }

    // Ambient LED Edge Light
    fun toggleLed() {
        viewModelScope.launch {
            val current = routineState.value.userProgress
            val newActive = !current.isLedActive
            repository.setLed(newActive, current.ledPresetIndex)
            jarvisVoiceManager.speak(if (newActive) "Ambient LED edge lighting illuminated, Krish sir." else "LED lighting disengaged.")
        }
    }

    fun setLedPreset(index: Int) {
        viewModelScope.launch {
            val current = routineState.value.userProgress
            repository.setLed(current.isLedActive, index)
        }
    }

    // JARVIS Voice Toggle
    fun toggleJarvisVoice() {
        viewModelScope.launch {
            val current = routineState.value.userProgress.isJarvisVoiceEnabled
            val newVoice = !current
            repository.setVoiceEnabled(newVoice)
            jarvisVoiceManager.isVoiceEnabled = newVoice
            if (newVoice) {
                jarvisVoiceManager.speak("Voice output online, Krish sir.")
            } else {
                jarvisVoiceManager.stop()
                jarvisVoiceManager.speak("", displayOverride = "\"Voice muted, Krish sir. Operating in silent visual mode.\"")
            }
        }
    }

    fun greetSir() {
        jarvisVoiceManager.greet(routineState.value.currentSplit.name)
    }

    fun executeJarvisCommand(cmd: String) {
        when (cmd) {
            "meditation" -> {
                selectTimerPreset(1200, "Morning Meditation (20m)")
                startTimer()
                if (!routineState.value.userProgress.isLedActive) toggleLed()
                jarvisVoiceManager.speak("Engaging 20-minute meditation protocol, Krish sir. Ambient lighting active.")
            }
            "yoga" -> {
                selectTimerPreset(1800, "Sunrise Yoga (30m)")
                startTimer()
                jarvisVoiceManager.speak("Commencing 30-minute sunrise yoga sequence, Krish sir. Stay present.")
            }
            "workout" -> {
                jarvisVoiceManager.speak("Today's focus is ${routineState.value.currentSplit.name}, Krish sir. Warm-up followed by heavy compound lifts and accessory sets.")
            }
            "water" -> {
                addWaterGlass()
            }
            "light" -> {
                toggleLed()
            }
            "music" -> {
                toggleSoundscape()
                jarvisVoiceManager.speak(if (_soundscapeState.value.isPlaying) "Broadcasting 432 hertz zen harmonic frequency, Krish sir." else "Ambient sound paused.")
            }
            "status" -> {
                val p = routineState.value.userProgress
                val liters = String.format(Locale.US, "%.1f", p.waterGlasses * 0.25f)
                jarvisVoiceManager.speak("Status report: Streak is ${p.streakCount} days, Krish sir. Water logged at $liters liters. ${routineState.value.completedCount} of ${routineState.value.totalCount} routines completed.")
            }
        }
    }

    // Timer Controls
    fun selectTimerPreset(seconds: Int, label: String) {
        resetTimer()
        _timerState.update {
            it.copy(
                remainingSeconds = seconds,
                totalSeconds = seconds,
                label = label
            )
        }
    }

    fun startTimer() {
        if (_timerState.value.isRunning) return
        _timerState.update { it.copy(isRunning = true) }

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && _timerState.value.remainingSeconds > 0) {
                delay(1000)
                _timerState.update { it.copy(remainingSeconds = it.remainingSeconds - 1) }
            }
            if (_timerState.value.remainingSeconds <= 0) {
                _timerState.update { it.copy(isRunning = false) }
                soundscapeEngine.playCompletionChime()
                triggerHapticFeedback()
                jarvisVoiceManager.speak("${_timerState.value.label} session completed, Krish sir. Excellent focus.")
            }
        }
    }

    fun pauseTimer() {
        _timerState.update { it.copy(isRunning = false) }
        timerJob?.cancel()
        timerJob = null
    }

    fun resetTimer() {
        pauseTimer()
        _timerState.update { it.copy(remainingSeconds = it.totalSeconds) }
    }

    private fun triggerHapticFeedback() {
        try {
            val app = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(800, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val v = app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                v?.vibrate(800)
            }
        } catch (_: Exception) {}
    }

    // Soundscape Controls
    fun toggleSoundscape() {
        if (_soundscapeState.value.isPlaying) {
            soundscapeEngine.stop()
            _soundscapeState.update { it.copy(isPlaying = false) }
        } else {
            soundscapeEngine.volume = _soundscapeState.value.volume
            soundscapeEngine.start(_soundscapeState.value.type)
            _soundscapeState.update { it.copy(isPlaying = true) }
        }
    }

    fun setSoundscapeType(type: SoundscapeType) {
        _soundscapeState.update { it.copy(type = type) }
        if (_soundscapeState.value.isPlaying) {
            soundscapeEngine.start(type)
        }
    }

    fun setSoundscapeVolume(vol: Float) {
        val bounded = vol.coerceIn(0f, 1f)
        _soundscapeState.update { it.copy(volume = bounded) }
        soundscapeEngine.volume = bounded
    }

    // Dialog state
    fun setShowAddDialog(show: Boolean) {
        _showAddDialog.value = show
    }

    fun setShowInfoDialog(show: Boolean) {
        _showInfoDialog.value = show
    }

    override fun onCleared() {
        super.onCleared()
        soundscapeEngine.stop()
        jarvisVoiceManager.release()
    }
}
