package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddCustomTaskDialog
import com.example.ui.components.HydrationCard
import com.example.ui.components.JarvisModule
import com.example.ui.components.JournalCard
import com.example.ui.components.LedControlsPanel
import com.example.ui.components.LedGlowFrame
import com.example.ui.components.ProtocolInfoDialog
import com.example.ui.components.RoutineListSection
import com.example.ui.components.SessionTimerCard
import com.example.ui.components.WeeklyDisciplineGrid
import com.example.ui.components.WorkoutSplitSelector
import com.example.ui.theme.Amber500
import com.example.ui.theme.Cyan500
import com.example.ui.theme.DailyWellnessTheme
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Indigo600
import com.example.ui.viewmodel.RoutineState
import com.example.ui.viewmodel.SoundscapeState
import com.example.ui.viewmodel.TimerState
import com.example.ui.viewmodel.WellnessViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: WellnessViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val systemDark = isSystemInDarkTheme()
            var userDarkTheme by remember { mutableStateOf<Boolean?>(null) }
            val isDark = userDarkTheme ?: systemDark

            val routineState by viewModel.routineState.collectAsStateWithLifecycle()
            val timerState by viewModel.timerState.collectAsStateWithLifecycle()
            val soundscapeState by viewModel.soundscapeState.collectAsStateWithLifecycle()
            val jarvisDialogue by viewModel.jarvisVoiceManager.lastDialogue.collectAsStateWithLifecycle()
            val isSpeaking by viewModel.jarvisVoiceManager.isSpeaking.collectAsStateWithLifecycle()
            val showAddDialog by viewModel.showAddDialog.collectAsStateWithLifecycle()
            val showInfoDialog by viewModel.showInfoDialog.collectAsStateWithLifecycle()

            DailyWellnessTheme(darkTheme = isDark) {
                LedGlowFrame(
                    isActive = routineState.userProgress.isLedActive,
                    presetIndex = routineState.userProgress.ledPresetIndex
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = { viewModel.setShowAddDialog(true) },
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White,
                                shape = CircleShape,
                                modifier = Modifier.testTag("fab_add_task")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Routine or Exercise")
                            }
                        }
                    ) { innerPadding ->
                        WellnessScreen(
                            routineState = routineState,
                            timerState = timerState,
                            soundscapeState = soundscapeState,
                            jarvisDialogue = jarvisDialogue,
                            isSpeaking = isSpeaking,
                            isDarkTheme = isDark,
                            onToggleTheme = {
                                userDarkTheme = !isDark
                            },
                            viewModel = viewModel,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )

                        if (showAddDialog) {
                            AddCustomTaskDialog(
                                currentSplitKey = routineState.currentSplit.key,
                                onDismiss = { viewModel.setShowAddDialog(false) },
                                onAdd = { category, title, time, desc ->
                                    viewModel.addCustomTask(category, title, time, desc)
                                }
                            )
                        }

                        if (showInfoDialog) {
                            ProtocolInfoDialog(
                                onDismiss = { viewModel.setShowInfoDialog(false) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WellnessScreen(
    routineState: RoutineState,
    timerState: TimerState,
    soundscapeState: SoundscapeState,
    jarvisDialogue: String,
    isSpeaking: Boolean,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    viewModel: WellnessViewModel,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        viewModel.greetSir()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("wellness_main_column"),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top utility bar (constrained width for adaptive view)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
            ) {
                TopUtilityBar(
                    isLedActive = routineState.userProgress.isLedActive,
                    onToggleLed = { viewModel.toggleLed() },
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme,
                    onShowInfo = { viewModel.setShowInfoDialog(true) }
                )
            }
        }

        // Header Section: Title, Streak & Progress
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
            ) {
                HeaderSection(
                    streakCount = routineState.userProgress.streakCount,
                    completedCount = routineState.completedCount,
                    totalCount = routineState.totalCount,
                    progressPercent = routineState.progressPercent,
                    onResetAll = { viewModel.resetAllRoutines() }
                )
            }
        }

        // Celebration Banner (if 100% complete)
        if (routineState.isAllCompleted) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 640.dp)
                ) {
                    CelebrationBanner(streakCount = routineState.userProgress.streakCount)
                }
            }
        }

        // J.A.R.V.I.S. Module
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
            ) {
                JarvisModule(
                    dialogue = jarvisDialogue,
                    isSpeaking = isSpeaking,
                    isVoiceEnabled = routineState.userProgress.isJarvisVoiceEnabled,
                    onGreetSir = { viewModel.greetSir() },
                    onToggleVoice = { viewModel.toggleJarvisVoice() },
                    onCommandClick = { cmd -> viewModel.executeJarvisCommand(cmd) }
                )
            }
        }

        // Collapsible LED Preset Panel
        if (routineState.userProgress.isLedActive) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 640.dp)
                ) {
                    LedControlsPanel(
                        activePresetIndex = routineState.userProgress.ledPresetIndex,
                        onSelectPreset = { viewModel.setLedPreset(it) }
                    )
                }
            }
        }

        // Workout Split Selector (6-Day 2x Weekly Program)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
            ) {
                WorkoutSplitSelector(
                    currentSplit = routineState.currentSplit,
                    onSelectSplit = { viewModel.selectSplit(it) }
                )
            }
        }

        // Weekly Consistency & Discipline Grid
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
            ) {
                WeeklyDisciplineGrid(
                    days = routineState.weeklyDays,
                    streakCount = routineState.userProgress.streakCount
                )
            }
        }

        // 5:00 AM Morning & Daily Hydration
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
            ) {
                HydrationCard(
                    waterGlasses = routineState.userProgress.waterGlasses,
                    onAddGlass = { viewModel.addWaterGlass() },
                    onRemoveGlass = { viewModel.removeWaterGlass() },
                    onSelectGlass = { viewModel.setWaterGlasses(it) }
                )
            }
        }

        // 5:00 AM Daily Intention Journal
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
            ) {
                JournalCard(
                    journalText = routineState.userProgress.journalText,
                    onJournalChange = { viewModel.updateJournalText(it) }
                )
            }
        }

        // Built-in Session Timer & Soundscapes
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
            ) {
                SessionTimerCard(
                    remainingSeconds = timerState.remainingSeconds,
                    totalSeconds = timerState.totalSeconds,
                    isRunning = timerState.isRunning,
                    timerLabel = timerState.label,
                    onStartTimer = { viewModel.startTimer() },
                    onPauseTimer = { viewModel.pauseTimer() },
                    onResetTimer = { viewModel.resetTimer() },
                    onSelectPreset = { secs, name -> viewModel.selectTimerPreset(secs, name) },
                    isSoundscapePlaying = soundscapeState.isPlaying,
                    soundscapeType = soundscapeState.type,
                    soundscapeVolume = soundscapeState.volume,
                    onToggleSoundscape = { viewModel.toggleSoundscape() },
                    onSelectSoundscapeType = { viewModel.setSoundscapeType(it) },
                    onVolumeChange = { viewModel.setSoundscapeVolume(it) }
                )
            }
        }

        // 5:00 AM Morning Plan Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
            ) {
                RoutineListSection(
                    title = "5:00 AM Morning Plan (Daily)",
                    headerColor = Amber500,
                    tasks = routineState.morningTasks,
                    onToggleTask = { id, done -> viewModel.toggleTask(id, done) }
                )
            }
        }

        // Daily Workout Plan Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
            ) {
                RoutineListSection(
                    title = "Workout Plan • ${routineState.currentSplit.name}",
                    headerColor = Indigo600,
                    tasks = routineState.workoutTasks,
                    onToggleTask = { id, done -> viewModel.toggleTask(id, done) }
                )
            }
        }

        // Add Custom Routine Trigger button
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
            ) {
                Button(
                    onClick = { viewModel.setShowAddDialog(true) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_custom_item_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "➕ Add Custom Routine or Exercise",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        // Bottom spacing for FAB
        item {
            Spacer(modifier = Modifier.height(56.dp))
        }
    }
}

@Composable
fun TopUtilityBar(
    isLedActive: Boolean,
    onToggleLed: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onShowInfo: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsePing")
    val pingScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pingScale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("top_utility_bar"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User Profile Badge
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Cyan500.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, Cyan500.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .scale(pingScale)
                        .clip(CircleShape)
                        .background(Cyan500)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Krish Patel's Protocol",
                    color = Cyan500,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Buttons: LED Toggle, Info, Theme Toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // LED Light Toggle
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onToggleLed() }
                    .testTag("led_toggle_btn"),
                shape = RoundedCornerShape(8.dp),
                color = if (isLedActive) Amber500.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(
                    1.dp,
                    if (isLedActive) Amber500 else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "✨", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isLedActive) "LED: On" else "LED: Off",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isLedActive) Amber500 else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Guide
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onShowInfo() }
                    .testTag("protocol_guide_btn"),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Text(
                    text = "📱 Guide",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                )
            }

            // Theme Toggle
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onToggleTheme() }
                    .testTag("theme_toggle_btn"),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Text(
                    text = if (isDarkTheme) "🌙 Dark" else "☀️ Light",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
fun HeaderSection(
    streakCount: Int,
    completedCount: Int,
    totalCount: Int,
    progressPercent: Int,
    onResetAll: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Daily Wellness Routine",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "5:00 AM rituals, timed meditation & 6-day split workouts.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Amber500.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, Amber500.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🔥", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "$streakCount Day Streak",
                            color = Amber500,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$progressPercent%",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Emerald500
                )
                Text(
                    text = "completed",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Progress Bar
        LinearProgressIndicator(
            progress = { (progressPercent / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .testTag("daily_progress_bar"),
            color = Emerald500,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$completedCount of $totalCount completed",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TextButton(
                onClick = onResetAll,
                modifier = Modifier.testTag("reset_all_btn")
            ) {
                Text(
                    text = "Reset All",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CelebrationBanner(streakCount: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("congrats_banner"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Emerald500.copy(alpha = 0.15f)
        ),
        border = BorderStroke(1.dp, Emerald500.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎉 All routines completed for today!",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Emerald500
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Great discipline Krish sir! Your $streakCount-day streak is secured for today.",
                fontSize = 11.5.sp,
                color = Emerald500,
                textAlign = TextAlign.Center
            )
        }
    }
}
