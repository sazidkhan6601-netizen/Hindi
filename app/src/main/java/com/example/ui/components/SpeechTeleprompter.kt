package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FedSpeechFeed
import com.example.data.FedSpeechScenario
import com.example.data.StanceType
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BearishRedDim
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.BullishGreenDim
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueDim
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldAccentDim
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SpeechTeleprompter(
    scenario: FedSpeechScenario,
    englishText: String,
    hindiText: String,
    takeawayHi: String,
    stance: StanceType,
    isStreaming: Boolean,
    isMuted: Boolean,
    speechSpeed: Float,
    isHapticsEnabled: Boolean = true,
    onToggleStream: () -> Unit,
    onToggleMute: () -> Unit,
    onChangeSpeed: (Float) -> Unit,
    onReplayAudio: () -> Unit,
    onToggleHaptics: () -> Unit = {},
    onSelectScenario: (String) -> Unit = {},
    onDownloadReport: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .testTag("teleprompter_container")
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Speaker selection horizontal chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FedSpeechFeed.scenarios.forEach { s ->
                    val isSelected = s.id == scenario.id
                    val isWarsh = s.speaker.contains("Warsh", ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) (if (isWarsh) GoldAccentDim else ElectricBlueDim) else SurfaceCardElevated)
                            .border(
                                1.dp,
                                if (isSelected) (if (isWarsh) GoldAccent else ElectricBlue) else BorderSubtle,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { onSelectScenario(s.id) }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isWarsh) {
                                Text(
                                    text = "🏛️ ",
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = if (isWarsh) "Kevin Warsh (Chair)" else s.speaker.substringBefore(" ("),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp
                                ),
                                color = if (isSelected) (if (isWarsh) GoldAccent else ElectricBlue) else TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Header bar: Speaker & Stance Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (scenario.speaker.contains("Warsh")) GoldAccentDim else ElectricBlueDim),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = "Speaker",
                            tint = if (scenario.speaker.contains("Warsh")) GoldAccent else ElectricBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = scenario.speaker,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = scenario.eventName,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }
                }

                // Stance Pill
                val stanceBg = when (stance) {
                    StanceType.DOVISH -> BullishGreenDim
                    StanceType.HAWKISH -> BearishRedDim
                    StanceType.NEUTRAL -> GoldAccentDim
                }
                val stanceColor = when (stance) {
                    StanceType.DOVISH -> BullishGreen
                    StanceType.HAWKISH -> BearishRed
                    StanceType.NEUTRAL -> GoldAccent
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(stanceBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stance.name,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = stanceColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Highlight: Real-time Hindi Translation Subtitle (Voice & Text)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceCardElevated)
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIVE HINDI TRANSLATION (हिंदी अनुवाद)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = GoldAccent
                            )
                        }

                        // Speaker Active Indicator
                        if (!isMuted) {
                            Row(
                                modifier = Modifier.clickable { onReplayAudio() },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Speaker",
                                    tint = BullishGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Replay Voice",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = BullishGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    AnimatedContent(
                        targetState = hindiText,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "hindiSubtitles"
                    ) { text ->
                        Text(
                            text = text.ifEmpty { "ऑडियो स्ट्रीम का इंतज़ार किया जा रहा है..." },
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 17.sp,
                                lineHeight = 26.sp
                            ),
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Original English Speech Stream
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "ORIGINAL SPEECH (ENGLISH PCM INPUT):",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = englishText.ifEmpty { "Listening for speech..." },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    ),
                    color = TextSecondary
                )
            }

            if (takeawayHi.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(GoldAccentDim)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = takeawayHi,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        ),
                        color = GoldAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Playback and Audio Controls Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleStream,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isStreaming) ElectricBlue else BullishGreen)
                            .testTag("toggle_stream_button")
                    ) {
                        Icon(
                            imageVector = if (isStreaming) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Stream Control",
                            tint = SurfaceCard,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onToggleMute,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SurfaceCardElevated)
                            .testTag("toggle_mute_button")
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Mute Voice",
                            tint = if (isMuted) BearishRed else TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onToggleHaptics,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isHapticsEnabled) ElectricBlueDim else SurfaceCardElevated)
                            .testTag("toggle_haptics_button")
                    ) {
                        Icon(
                            imageVector = if (isHapticsEnabled) Icons.Default.Vibration else Icons.Default.NotificationsActive,
                            contentDescription = "Toggle Haptics & Notification Tone",
                            tint = if (isHapticsEnabled) ElectricBlue else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onDownloadReport,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GoldAccentDim)
                            .testTag("teleprompter_download_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download Speech & Hindi Transcript Report",
                            tint = GoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Speed Selector (0.8x, 1.0x, 1.25x)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceCardElevated)
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(0.8f, 1.0f, 1.25f).forEach { speed ->
                        val isSelected = speechSpeed == speed
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) ElectricBlue else SurfaceCardElevated)
                                .clickable { onChangeSpeed(speed) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${speed}x",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 10.sp
                                ),
                                color = if (isSelected) SurfaceCard else TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
