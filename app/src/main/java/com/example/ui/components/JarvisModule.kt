package com.example.ui.components

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Cyan300
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Cyan900
import com.example.ui.theme.Cyan950
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JarvisModule(
    dialogue: String,
    isSpeaking: Boolean,
    isVoiceEnabled: Boolean,
    onGreetSir: () -> Unit,
    onToggleVoice: () -> Unit,
    onCommandClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Speech Recognition Launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
                ?.lowercase() ?: ""

            when {
                spokenText.contains("meditat") -> onCommandClick("meditation")
                spokenText.contains("yoga") -> onCommandClick("yoga")
                spokenText.contains("workout") || spokenText.contains("exercise") -> onCommandClick("workout")
                spokenText.contains("water") || spokenText.contains("drink") -> onCommandClick("water")
                spokenText.contains("light") || spokenText.contains("led") -> onCommandClick("light")
                spokenText.contains("music") || spokenText.contains("sound") -> onCommandClick("music")
                spokenText.contains("status") || spokenText.contains("progress") -> onCommandClick("status")
            }
        }
    }

    // Arc Reactor spinning animation
    val infiniteTransition = rememberInfiniteTransition(label = "arcSpin")
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val corePulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "corePulse"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("jarvis_module_card")
            .shadow(12.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Slate950),
        border = androidx.compose.foundation.BorderStroke(1.dp, Cyan900.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Slate950,
                            Color(0xFF042F2E).copy(alpha = 0.85f),
                            Slate950
                        )
                    )
                )
                .padding(16.dp)
        ) {
            // Header Row: Arc Reactor + Title + Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Animated Arc Reactor Orb
                    Box(
                        modifier = Modifier.size(46.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Outer spinning ring
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .rotate(ringRotation)
                                .border(
                                    width = 2.dp,
                                    brush = Brush.sweepGradient(
                                        listOf(Cyan400, Color.Transparent, Cyan400, Color.Transparent)
                                    ),
                                    shape = CircleShape
                                )
                        )
                        // Inner pulsing core
                        Box(
                            modifier = Modifier
                                .size((26 * corePulse).dp)
                                .clip(CircleShape)
                                .background(Cyan500.copy(alpha = 0.35f))
                                .border(1.5.dp, Cyan300, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🤖", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "J.A.R.V.I.S. ASSISTANT",
                                color = Cyan400,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isSpeaking) Cyan300 else Emerald400)
                            )
                        }
                        Text(
                            text = if (isSpeaking) "Transmitting to Krish Patel..." else "Assigned to: Krish Patel",
                            color = Cyan300.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Greet and Voice buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onGreetSir() }
                            .testTag("jarvis_greet_btn"),
                        color = Cyan950,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Cyan900)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🔊", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Greet Sir",
                                color = Cyan300,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onToggleVoice() }
                            .testTag("jarvis_voice_toggle"),
                        color = Slate800,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
                    ) {
                        Text(
                            text = if (isVoiceEnabled) "Voice: On" else "Voice: Muted",
                            color = if (isVoiceEnabled) Cyan400 else Color(0xFFF87171),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dialogue Box
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("jarvis_dialogue_box"),
                shape = RoundedCornerShape(10.dp),
                color = Color.Black.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Cyan500.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = ">",
                        color = Cyan400,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = dialogue,
                        color = Cyan300,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subheader + Mic Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Voice Commands & Quick Protocols:",
                    color = Cyan300.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )

                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak a protocol command, Krish sir...")
                            }
                            try {
                                speechLauncher.launch(intent)
                            } catch (_: Exception) {}
                        }
                        .testTag("jarvis_speak_command_btn"),
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🎙️", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Speak Command",
                            color = Cyan400,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Quick Command Chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CommandChip(label = "\"Start 5:00 AM Meditation\"", onClick = { onCommandClick("meditation") })
                CommandChip(label = "\"Start Sunrise Yoga\"", onClick = { onCommandClick("yoga") })
                CommandChip(label = "\"Brief Today's Workout\"", onClick = { onCommandClick("workout") })
                CommandChip(label = "\"Log 5:00 AM Water\"", onClick = { onCommandClick("water") })
                CommandChip(label = "\"Turn on LED Edge Glow\"", onClick = { onCommandClick("light") })
                CommandChip(label = "\"Play 432Hz Zen Audio\"", onClick = { onCommandClick("music") })
                CommandChip(label = "\"Report Routine Status\"", onClick = { onCommandClick("status") })
            }
        }
    }
}

@Composable
private fun CommandChip(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = Cyan950.copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Cyan900.copy(alpha = 0.7f))
    ) {
        Text(
            text = label,
            color = Cyan300,
            fontSize = 10.5.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
        )
    }
}
