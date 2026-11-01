package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Sky400
import com.example.ui.theme.Sky500
import com.example.ui.theme.Sky600
import com.example.ui.theme.Sky950
import java.util.Locale

@Composable
fun HydrationCard(
    waterGlasses: Int,
    onAddGlass: () -> Unit,
    onRemoveGlass: () -> Unit,
    onSelectGlass: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val liters = String.format(Locale.US, "%.1f", waterGlasses * 0.25f)
    val percent = (waterGlasses * 10).coerceIn(0, 100)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hydration_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, Sky500.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💧", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "5:00 AM MORNING & DAILY HYDRATION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = Sky600
                        )
                        Text(
                            text = "Essential 500ml glass before morning meditation & yoga",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${liters}L",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Sky600
                    )
                    Text(
                        text = "/ 2.5L Goal",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 10 Interactive Glasses
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (i in 1..10) {
                    val isFilled = i <= waterGlasses
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 1.5.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onSelectGlass(i) }
                            .testTag("water_glass_$i"),
                        shape = RoundedCornerShape(6.dp),
                        color = if (isFilled) Sky500 else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(
                            1.dp,
                            if (isFilled) Sky500 else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 5.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isFilled) "💧" else "🥛",
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${i * 250}",
                                fontSize = 7.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isFilled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onAddGlass,
                        colors = ButtonDefaults.buttonColors(containerColor = Sky500),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("add_water_btn")
                    ) {
                        Text(text = "+ Add Glass (250ml)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = onRemoveGlass,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("remove_water_btn")
                    ) {
                        Text(text = "- Undo", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Text(
                    text = "$waterGlasses of 10 glasses ($percent%)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Sky600
                )
            }
        }
    }
}
