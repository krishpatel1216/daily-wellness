package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.RoutineTask
import com.example.data.model.UserProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {

    @Query("SELECT * FROM routine_tasks ORDER BY orderIndex ASC")
    fun getAllTasks(): Flow<List<RoutineTask>>

    @Query("SELECT * FROM routine_tasks WHERE category = :category ORDER BY orderIndex ASC")
    fun getTasksByCategory(category: String): Flow<List<RoutineTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<RoutineTask>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: RoutineTask)

    @Query("UPDATE routine_tasks SET isCompleted = :completed WHERE id = :id")
    suspend fun setTaskCompleted(id: String, completed: Boolean)

    @Query("UPDATE routine_tasks SET isCompleted = 0")
    suspend fun resetAllCompleted()

    @Query("DELETE FROM routine_tasks WHERE id = :id")
    suspend fun deleteTask(id: String)

    @Query("SELECT * FROM user_progress WHERE id = 'krish_protocol' LIMIT 1")
    fun getUserProgress(): Flow<UserProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setUserProgress(progress: UserProgress)

    @Query("UPDATE user_progress SET waterGlasses = :glasses WHERE id = 'krish_protocol'")
    suspend fun updateWaterGlasses(glasses: Int)

    @Query("UPDATE user_progress SET journalText = :text WHERE id = 'krish_protocol'")
    suspend fun updateJournal(text: String)

    @Query("UPDATE user_progress SET activeSplitKey = :splitKey WHERE id = 'krish_protocol'")
    suspend fun updateActiveSplit(splitKey: String)

    @Query("UPDATE user_progress SET isLedActive = :isActive, ledPresetIndex = :presetIndex WHERE id = 'krish_protocol'")
    suspend fun updateLed(isActive: Boolean, presetIndex: Int)

    @Query("UPDATE user_progress SET isJarvisVoiceEnabled = :enabled WHERE id = 'krish_protocol'")
    suspend fun updateVoiceEnabled(enabled: Boolean)

    @Query("UPDATE user_progress SET streakCount = :streak, lastCompletedDate = :date WHERE id = 'krish_protocol'")
    suspend fun updateStreak(streak: Int, date: String)
}
