package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FedStanceGauge
import com.example.ui.components.LiveAudioVisualizer
import com.example.ui.components.MacroNewsCalendarView
import com.example.ui.components.MarketAlertBanner
import com.example.ui.components.MarketImpactSummary
import com.example.ui.components.MarketTickerRow
import com.example.ui.components.MarketsForexView
import com.example.ui.components.SpeechTeleprompter
import com.example.ui.components.VoiceQuerySection
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueDim
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldAccentDim
import com.example.ui.theme.MidnightBg
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.CurrencyDisplay
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val marketAssets by viewModel.marketAssets.collectAsState()
    val filteredMarketAssets by viewModel.filteredMarketAssets.collectAsState()
    val waveformData by viewModel.liveWaveform.collectAsState()
    val isWsConnected by viewModel.isWebSocketConnected.collectAsState()
    val wsLatencyMs by viewModel.wsLatencyMs.collectAsState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBg),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ElectricBlueDim),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = "App Logo",
                                tint = ElectricBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "FedLive Hindi",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(BullishGreen.copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "LIVE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = BullishGreen
                                    )
                                }
                            }
                            Text(
                                text = "Real-time 16kHz PCM → Hindi Live API & WebSockets",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = TextSecondary
                            )
                        }
                    }
                },
                actions = {
                    // Haptic & Sound Alerts Toggle
                    IconButton(
                        onClick = { viewModel.toggleHapticsAlerts() },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (uiState.isHapticsAlertsEnabled) ElectricBlueDim else SurfaceCardElevated)
                            .testTag("haptic_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (uiState.isHapticsAlertsEnabled) Icons.Default.Vibration else Icons.Default.NotificationsOff,
                            contentDescription = "Toggle Market Alert Notifications",
                            tint = if (uiState.isHapticsAlertsEnabled) ElectricBlue else TextSecondary,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Download & Export Report / APK Info
                    IconButton(
                        onClick = { viewModel.openDownloadDialog() },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(GoldAccentDim)
                            .testTag("download_export_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download & Export Report / App",
                            tint = GoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Currency Toggle ($ / ₹)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceCardElevated)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                            .clickable { viewModel.toggleCurrency() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("currency_toggle_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (uiState.currency == CurrencyDisplay.USD) "$" else "₹",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                color = GoldAccent
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = uiState.currency.name,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MidnightBg
                ),
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceCard,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationBarItem(
                    selected = uiState.currentTab == AppNavTab.LIVE_SPEECH,
                    onClick = { viewModel.selectTab(AppNavTab.LIVE_SPEECH) },
                    icon = { Icon(Icons.Default.GraphicEq, contentDescription = "Live Speech") },
                    label = { Text("Live Speech", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricBlue,
                        selectedTextColor = ElectricBlue,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = ElectricBlueDim
                    ),
                    modifier = Modifier.testTag("nav_tab_live_speech")
                )

                NavigationBarItem(
                    selected = uiState.currentTab == AppNavTab.MARKETS,
                    onClick = { viewModel.selectTab(AppNavTab.MARKETS) },
                    icon = { Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = "Markets") },
                    label = { Text("Markets & Forex", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricBlue,
                        selectedTextColor = ElectricBlue,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = ElectricBlueDim
                    ),
                    modifier = Modifier.testTag("nav_tab_markets")
                )

                NavigationBarItem(
                    selected = uiState.currentTab == AppNavTab.MACRO_NEWS,
                    onClick = { viewModel.selectTab(AppNavTab.MACRO_NEWS) },
                    icon = { Icon(Icons.Default.Newspaper, contentDescription = "News & Macro") },
                    label = { Text("News & Macro", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricBlue,
                        selectedTextColor = ElectricBlue,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = ElectricBlueDim
                    ),
                    modifier = Modifier.testTag("nav_tab_news_macro")
                )

                NavigationBarItem(
                    selected = uiState.currentTab == AppNavTab.VOICE_AI,
                    onClick = { viewModel.selectTab(AppNavTab.VOICE_AI) },
                    icon = { Icon(Icons.Default.Mic, contentDescription = "AI Voice") },
                    label = { Text("AI Voice", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricBlue,
                        selectedTextColor = ElectricBlue,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = ElectricBlueDim
                    ),
                    modifier = Modifier.testTag("nav_tab_voice_ai")
                )
            }
        },
        containerColor = MidnightBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Significant Market & Fed Alert Banner (Haptic / Sound feedback popup)
            item {
                MarketAlertBanner(
                    alert = uiState.activeAlertBanner,
                    onDismiss = { viewModel.dismissAlertBanner() }
                )
            }

            // Live Market Ticker Row (Always visible at top for real-time awareness)
            item {
                MarketTickerRow(
                    assets = marketAssets,
                    currency = uiState.currency
                )
            }

            when (uiState.currentTab) {
                AppNavTab.LIVE_SPEECH -> {
                    // Audio Streaming Pipeline Visualizer (16kHz PCM input / 24kHz speaker output)
                    item {
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            LiveAudioVisualizer(
                                waveformData = waveformData,
                                isActive = uiState.isStreamingActive || uiState.isRecordingMic,
                                isRecordingMic = uiState.isRecordingMic,
                                sourceMode = uiState.sourceMode,
                                latencyMs = uiState.streamLatencyMs
                            )
                        }
                    }

                    // Speech Teleprompter (Live Hindi Subtitles & Native Audio Output)
                    item {
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            SpeechTeleprompter(
                                scenario = uiState.currentScenario,
                                englishText = uiState.activeSubtitlesEn,
                                hindiText = uiState.activeSubtitlesHi,
                                takeawayHi = uiState.currentTakeawayHi,
                                stance = uiState.currentStance,
                                isStreaming = uiState.isStreamingActive,
                                isMuted = uiState.isAudioMuted,
                                speechSpeed = uiState.speechSpeed,
                                isHapticsEnabled = uiState.isHapticsAlertsEnabled,
                                onToggleStream = { viewModel.toggleStreaming() },
                                onToggleMute = { viewModel.toggleMute() },
                                onChangeSpeed = { speed -> viewModel.setPlaybackSpeed(speed) },
                                onReplayAudio = {
                                    viewModel.audioPlayer.speakHindiText(uiState.activeSubtitlesHi)
                                },
                                onToggleHaptics = { viewModel.toggleHapticsAlerts() },
                                onSelectScenario = { id -> viewModel.selectScenario(id) },
                                onDownloadReport = { viewModel.openDownloadDialog() }
                            )
                        }
                    }

                    // Fed Monetary Stance & Policy Impact Gauge
                    item {
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            FedStanceGauge(
                                currentStance = uiState.currentStance,
                                stanceScore = uiState.stanceScore,
                                selectedScenarioId = uiState.currentScenario.id,
                                onSelectScenario = { id -> viewModel.selectScenario(id) }
                            )
                        }
                    }

                    // Translation History Log
                    item {
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            MarketImpactSummary(history = uiState.translationHistory)
                        }
                    }
                }

                AppNavTab.MARKETS -> {
                    // Real-time WebSockets, Watchlist, Forex, Stocks, Crypto, Commodities, DXY
                    item {
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            MarketsForexView(
                                assets = filteredMarketAssets,
                                selectedCategory = uiState.selectedCategory,
                                currency = uiState.currency,
                                isWsConnected = isWsConnected,
                                wsLatencyMs = wsLatencyMs,
                                onSelectCategory = { cat -> viewModel.selectCategory(cat) },
                                onSpeakAssetInfo = { text -> viewModel.speakCustomHindi(text) }
                            )
                        }
                    }
                }

                AppNavTab.MACRO_NEWS -> {
                    // Economic Calendar (CPI, FOMC, PCE) + Breaking Financial News with Hindi Voice
                    item {
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            MacroNewsCalendarView(
                                events = uiState.economicEvents,
                                news = uiState.breakingNews,
                                onSpeakText = { text -> viewModel.speakCustomHindi(text) }
                            )
                        }
                    }
                }

                AppNavTab.VOICE_AI -> {
                    // Interactive Voice & Microphone Query Input
                    item {
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            VoiceQuerySection(
                                isRecordingMic = uiState.isRecordingMic,
                                isProcessingAi = uiState.isProcessingAi,
                                statusMessage = uiState.statusMessage,
                                interactions = uiState.voiceInteractions,
                                onToggleMic = { viewModel.toggleMicrophoneInput() },
                                onAskQuestion = { query -> viewModel.askQuestion(query) },
                                onPlayVoice = { item -> viewModel.playInteractionVoice(item) }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (uiState.showDownloadDialog) {
        val context = LocalContext.current
        DownloadExportDialog(
            onDismiss = { viewModel.closeDownloadDialog() },
            onShareReport = {
                val reportText = viewModel.generateExportReportText()
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "US Fed Live Hindi Speech & Market Intelligence Report")
                    putExtra(Intent.EXTRA_TEXT, reportText)
                }
                context.startActivity(Intent.createChooser(intent, "Share / Download Report via"))
            },
            onCopyClipboard = {
                val reportText = viewModel.generateExportReportText()
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                val clip = ClipData.newPlainText("Fed Hindi Market Report", reportText)
                clipboard?.setPrimaryClip(clip)
                Toast.makeText(context, "Report copied to clipboard!", Toast.LENGTH_SHORT).show()
                viewModel.closeDownloadDialog()
            }
        )
    }
}

@Composable
fun DownloadExportDialog(
    onDismiss: () -> Unit,
    onShareReport: () -> Unit,
    onCopyClipboard: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceCard,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download",
                    tint = GoldAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Download & Export Options",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Download live speech transcripts, Hindi translations, stance analytics, and real-time market snapshots directly to your device.",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )

                // Option 1: Share / Download Full Report
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceCardElevated,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable { onShareReport() }
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldAccentDim),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = GoldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Export & Save Full Report (Text / File)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Save to Files, Drive, Email, or WhatsApp",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            )
                        }
                    }
                }

                // Option 2: Copy to Clipboard
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceCardElevated,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                        .clickable { onCopyClipboard() }
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ElectricBlueDim),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = ElectricBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Copy Transcript & Snapshot",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Quickly copy complete text to clipboard",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            )
                        }
                    }
                }

                // Option 3: APK / Source Code Download Information
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceCardElevated,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BullishGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Android,
                                contentDescription = "APK Export",
                                tint = BullishGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Download APK / Export Project",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BullishGreen
                                )
                            )
                            Text(
                                text = "Use AI Studio top menu → Export as ZIP or Generate APK to install on your Android phone.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text("Close", color = MidnightBg, fontWeight = FontWeight.Bold)
            }
        }
    )
}

