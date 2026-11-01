package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundscapeType
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun SessionTimerCard(
    remainingSeconds: Int,
    totalSeconds: Int,
    isRunning: Boolean,
    timerLabel: String,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSelectPreset: (Int, String) -> Unit,
    // Soundscape
    isSoundscapePlaying: Boolean,
    soundscapeType: SoundscapeType,
    soundscapeVolume: Float,
    onToggleSoundscape: () -> Unit,
    onSelectSoundscapeType: (SoundscapeType) -> Unit,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val mins = remainingSeconds / 60
    val secs = remainingSeconds % 60
    val formattedTime = String.format("%02d:%02d", mins, secs)

    var soundMenuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("session_timer_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        border = BorderStroke(1.dp, Slate800)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .then(
                                if (isRunning) Modifier.clip(CircleShape) else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = CircleShape,
                            color = if (isRunning) Emerald400 else Color(0xFF64748B)
                        ) {}
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FOCUS & ROUTINE TIMER",
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                Text(
                    text = timerLabel,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Digital Clock Display
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = formattedTime,
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp,
                    modifier = Modifier.testTag("timer_digital_display")
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isRunning) "Session active" else if (remainingSeconds < totalSeconds) "Paused" else "Ready to begin",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "•", color = Color(0xFF64748B), fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Real-time Chime & Soundscape",
                        color = Emerald400,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Presets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PresetChip(
                    label = "Meditation • 20m",
                    isSelected = totalSeconds == 1200,
                    onClick = { onSelectPreset(1200, "Morning Meditation (20m)") },
                    modifier = Modifier.weight(1f)
                )
                PresetChip(
                    label = "Yoga • 30m",
                    isSelected = totalSeconds == 1800,
                    onClick = { onSelectPreset(1800, "Sunrise Yoga (30m)") },
                    modifier = Modifier.weight(1f)
                )
                PresetChip(
                    label = "Deep • 45m",
                    isSelected = totalSeconds == 2700,
                    onClick = { onSelectPreset(2700, "Deep Yoga Flow (45m)") },
                    modifier = Modifier.weight(1f)
                )
                PresetChip(
                    label = "Stretch • 10m",
                    isSelected = totalSeconds == 600,
                    onClick = { onSelectPreset(600, "Warm-Up / Stretch (10m)") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Timer Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (isRunning) onPauseTimer() else onStartTimer()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) Amber500 else Emerald500
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .testTag("timer_toggle_btn")
                ) {
                    Text(
                        text = if (isRunning) "Pause Timer" else "Start Timer",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }

                OutlinedButton(
                    onClick = onResetTimer,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFCBD5E1)),
                    border = BorderStroke(1.dp, Slate700),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("timer_reset_btn")
                ) {
                    Text(text = "Reset", fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Slate800)
            Spacer(modifier = Modifier.height(12.dp))

            // Soundscape Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Selector Button
                Box {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { soundMenuExpanded = true }
                            .testTag("soundscape_select_btn"),
                        color = Slate800,
                        border = BorderStroke(1.dp, Slate700)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🎵", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = soundscapeType.title.take(18) + "...",
                                color = Color(0xFFE2E8F0),
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                            Text(text = " ▾", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                    }

                    DropdownMenu(
                        expanded = soundMenuExpanded,
                        onDismissRequest = { soundMenuExpanded = false }
                    ) {
                        SoundscapeType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(text = type.title, fontSize = 12.sp) },
                                onClick = {
                                    onSelectSoundscapeType(type)
                                    soundMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Ambient Audio Toggle Button
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onToggleSoundscape() }
                        .testTag("ambient_sound_toggle_btn"),
                    color = Slate800,
                    border = BorderStroke(
                        1.dp,
                        if (isSoundscapePlaying) Emerald400 else Slate700
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSoundscapePlaying) "⏸️" else "▶️",
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isSoundscapePlaying) "Pause Sound" else "Play Harmonic",
                            color = if (isSoundscapePlaying) Emerald400 else Color(0xFFCBD5E1),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Volume Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔉 Volume",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    modifier = Modifier.width(60.dp)
                )
                Slider(
                    value = soundscapeVolume,
                    onValueChange = onVolumeChange,
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = Emerald400,
                        activeTrackColor = Emerald500,
                        inactiveTrackColor = Slate800
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun PresetChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = Slate800,
        border = BorderStroke(
            1.dp,
            if (isSelected) Emerald400 else Slate700
        )
    ) {
        Text(
            text = label,
            fontSize = 9.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Emerald400 else Color(0xFFCBD5E1),
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
            maxLines = 1,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
