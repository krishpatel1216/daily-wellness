package com.example.data.repository

import com.example.data.db.RoutineDao
import com.example.data.db.WellnessDataDefaults
import com.example.data.model.RoutineTask
import com.example.data.model.UserProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class WellnessRepository(private val dao: RoutineDao) {

    val allTasks: Flow<List<RoutineTask>> = dao.getAllTasks()
    val userProgress: Flow<UserProgress?> = dao.getUserProgress()

    suspend fun ensureDataPopulated() {
        val existingTasks = dao.getAllTasks().firstOrNull()
        if (existingTasks.isNullOrEmpty()) {
            dao.insertTasks(WellnessDataDefaults.getAllInitialTasks())
        }
        val existingProgress = dao.getUserProgress().firstOrNull()
        if (existingProgress == null) {
            dao.setUserProgress(
                UserProgress(
                    id = "krish_protocol",
                    streakCount = 1,
                    waterGlasses = 4,
                    journalText = "",
                    activeSplitKey = "back-biceps",
                    isLedActive = false,
                    ledPresetIndex = 0,
                    isJarvisVoiceEnabled = true,
                    weeklyStatus = "1,1,1,1,1,1,0"
                )
            )
        }
    }

    suspend fun setTaskCompleted(id: String, completed: Boolean) {
        dao.setTaskCompleted(id, completed)
    }

    suspend fun resetAllRoutines() {
        dao.resetAllCompleted()
    }

    suspend fun addCustomTask(category: String, title: String, time: String, desc: String) {
        val task = RoutineTask(
            id = "custom-" + UUID.randomUUID().toString().take(8),
            category = category,
            title = title,
            time = time,
            desc = desc,
            isCompleted = false,
            isCustom = true,
            orderIndex = 100
        )
        dao.insertTask(task)
    }

    suspend fun setWaterGlasses(glasses: Int) {
        val bounded = glasses.coerceIn(0, 10)
        dao.updateWaterGlasses(bounded)
    }

    suspend fun updateJournal(text: String) {
        dao.updateJournal(text)
    }

    suspend fun setActiveSplit(splitKey: String) {
        dao.updateActiveSplit(splitKey)
    }

    suspend fun setLed(isActive: Boolean, presetIndex: Int) {
        dao.updateLed(isActive, presetIndex)
    }

    suspend fun setVoiceEnabled(enabled: Boolean) {
        dao.updateVoiceEnabled(enabled)
    }

    suspend fun incrementStreak(todayDate: String) {
        val current = dao.getUserProgress().firstOrNull() ?: UserProgress()
        if (current.lastCompletedDate != todayDate) {
            val newStreak = current.streakCount + 1
            dao.updateStreak(newStreak, todayDate)
        }
    }
}
