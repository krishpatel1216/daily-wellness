package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WeeklyDayInfo
import com.example.ui.theme.Amber400
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Indigo600

@Composable
fun WeeklyDisciplineGrid(
    days: List<WeeklyDayInfo>,
    streakCount: Int,
    modifier: Modifier = Modifier
) {
    val completedCount = days.count { it.isCompleted }
    val percent = (completedCount * 100) / 7

    val totalMedMins = days.sumOf { if (it.isCompleted) it.medMins else 0 }
    val totalWorkoutMins = days.sumOf { if (it.isCompleted) it.workoutMins else 0 }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weekly_discipline_grid_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📊", fontSize = 14.sp)
                    Spacer(modifier = Modifier.padding(start = 6.dp))
                    Text(
                        text = "WEEKLY CONSISTENCY & DISCIPLINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "This Week: $percent% Complete",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Emerald500
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 7 Days Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                days.forEach { day ->
                    DayPill(day = day, modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))

            // Footer stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🧘 Mindful: ${totalMedMins}m",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "💪 Workout: ${totalWorkoutMins}m",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "🔥 Streak: $streakCount Days",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Amber500
                )
            }
        }
    }
}

@Composable
private fun DayPill(
    day: WeeklyDayInfo,
    modifier: Modifier = Modifier
) {
    val containerColor = when {
        day.isCompleted -> Emerald500.copy(alpha = 0.15f)
        day.isToday -> Amber500.copy(alpha = 0.15f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val borderColor = when {
        day.isToday -> Amber500
        day.isCompleted -> Emerald500.copy(alpha = 0.6f)
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        border = BorderStroke(if (day.isToday) 1.5.dp else 1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = day.dayName,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = day.splitTag,
                fontSize = 8.5.sp,
                color = Indigo500,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = when {
                    day.isCompleted -> "✓"
                    day.isToday -> "⏳"
                    else -> "○"
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = when {
                    day.isCompleted -> Emerald500
                    day.isToday -> Amber500
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}
