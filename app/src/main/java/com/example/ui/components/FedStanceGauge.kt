package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FedSpeechFeed
import com.example.data.StanceType
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BearishRedDim
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.BullishGreenDim
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldAccentDim
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun FedStanceGauge(
    currentStance: StanceType,
    stanceScore: Int, // 0 (Hawkish) to 100 (Dovish)
    selectedScenarioId: String,
    onSelectScenario: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedScore by animateFloatAsState(
        targetValue = stanceScore / 100f,
        animationSpec = tween(600),
        label = "stanceScoreAnim"
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .testTag("stance_gauge_container")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "FED MONETARY STANCE METER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = ElectricBlue
                    )
                }

                Text(
                    text = "${stanceScore}% DOVISH",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (stanceScore >= 50) BullishGreen else BearishRed
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Visual Gauge Bar (Hawkish 0% <-----> Dovish 100%)
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "HAWKISH (सख्त दरें)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = BearishRed
                    )
                    Text(
                        text = "NEUTRAL",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = TextMuted
                    )
                    Text(
                        text = "DOVISH (दरों में कटौती)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = BullishGreen
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Continuous Bar with Needle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(BearishRed, GoldAccent, BullishGreen)
                            )
                        )
                ) {
                    // Needle marker
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedScore.coerceIn(0.05f, 0.95f))
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(TextPrimary)
                                .border(2.dp, SurfaceCard, CircleShape)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stance Market Correlation Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StanceImpactCard(
                    title = "Bitcoin (BTC)",
                    impact = if (currentStance == StanceType.DOVISH) "Bullish (तेजी)" else "Consolidating (दबाव)",
                    isPositive = currentStance == StanceType.DOVISH,
                    modifier = Modifier.weight(1f)
                )
                StanceImpactCard(
                    title = "Gold (XAU)",
                    impact = if (currentStance == StanceType.DOVISH) "High Demand (मजबूत)" else "Neutral / Flat",
                    isPositive = currentStance != StanceType.HAWKISH,
                    modifier = Modifier.weight(1f)
                )
                StanceImpactCard(
                    title = "US100 / Tech",
                    impact = if (currentStance == StanceType.DOVISH) "Growth Rally" else "Yield Pressure",
                    isPositive = currentStance == StanceType.DOVISH,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Speech Scenario Switcher
            Text(
                text = "SELECT LIVE SPEECH FEED / SCENARIO:",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                ),
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            FedSpeechFeed.scenarios.forEach { scenario ->
                val isSelected = scenario.id == selectedScenarioId
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) SurfaceCardElevated else SurfaceCard)
                        .border(
                            1.dp,
                            if (isSelected) ElectricBlue else BorderSubtle,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onSelectScenario(scenario.id) }
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = scenario.titleHi,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = if (isSelected) ElectricBlue else TextPrimary
                        )
                        Text(
                            text = scenario.title,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = TextSecondary
                        )
                    }

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(ElectricBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Active",
                                tint = SurfaceCard,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StanceImpactCard(
    title: String,
    impact: String,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceCardElevated)
            .border(
                1.dp,
                if (isPositive) BullishGreen.copy(alpha = 0.3f) else BearishRed.copy(alpha = 0.3f),
                RoundedCornerShape(10.dp)
            )
            .padding(8.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                ),
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                    contentDescription = null,
                    tint = if (isPositive) BullishGreen else BearishRed,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = impact,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = if (isPositive) BullishGreen else BearishRed,
                    maxLines = 1
                )
            }
        }
    }
}
