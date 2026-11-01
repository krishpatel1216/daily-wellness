package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routine_tasks")
data class RoutineTask(
    @PrimaryKey val id: String,
    val category: String, // "morning", "back-biceps", "chest-triceps", "legs-shoulders", "sunday-rest"
    val title: String,
    val time: String,
    val desc: String,
    val isCompleted: Boolean = false,
    val isCustom: Boolean = false,
    val orderIndex: Int = 0
)

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val id: String = "krish_protocol",
    val streakCount: Int = 1,
    val lastCompletedDate: String = "",
    val waterGlasses: Int = 4,
    val journalText: String = "",
    val activeSplitKey: String = "back-biceps",
    val isLedActive: Boolean = false,
    val ledPresetIndex: Int = 0, // 0: Sunrise Gold, 1: Zen Emerald, 2: Calm Cyan, 3: Cosmic Violet
    val isJarvisVoiceEnabled: Boolean = true,
    val weeklyStatus: String = "1,1,1,1,1,1,0" // Monday to Sunday completion status
)

data class WeeklyDayInfo(
    val dayName: String,
    val splitTag: String,
    val isCompleted: Boolean,
    val isToday: Boolean,
    val medMins: Int,
    val workoutMins: Int
)

data class SplitDefinition(
    val key: String,
    val name: String,
    val daysLabel: String,
    val dayRange: String,
    val defaultTasks: List<RoutineTask>
)
