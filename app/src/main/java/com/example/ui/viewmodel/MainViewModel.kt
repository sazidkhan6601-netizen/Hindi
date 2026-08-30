package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioRecorderStreamer
import com.example.audio.AudioTrackPlayer
import com.example.audio.NotificationFeedbackHelper
import com.example.data.AssetCategory
import com.example.data.BreakingNewsItem
import com.example.data.EconomicEvent
import com.example.data.FedSpeechFeed
import com.example.data.FedSpeechScenario
import com.example.data.MacroNewsData
import com.example.data.MarketAlertNotification
import com.example.data.MarketAsset
import com.example.data.SpeechTranslationChunk
import com.example.data.StanceType
import com.example.data.VoiceInteraction
import com.example.gemini.GeminiLiveTranslateService
import com.example.market.FinancialDataService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class AppNavTab(val title: String, val iconName: String) {
    LIVE_SPEECH("Live Speech", "Translate"),
    MARKETS("Markets & Forex", "ShowChart"),
    MACRO_NEWS("News & Macro", "Newspaper"),
    VOICE_AI("AI Voice", "Mic")
}

enum class AudioSourceMode {
    FED_SPEECH_STREAM,
    LIVE_MIC_INPUT
}

enum class CurrencyDisplay {
    USD,
    INR
}

data class UiState(
    val currentTab: AppNavTab = AppNavTab.LIVE_SPEECH,
    val selectedCategory: AssetCategory = AssetCategory.ALL,
    val currentScenario: FedSpeechScenario = FedSpeechFeed.scenarios.first(),
    val currentChunkIndex: Int = 0,
    val isStreamingActive: Boolean = true,
    val isRecordingMic: Boolean = false,
    val sourceMode: AudioSourceMode = AudioSourceMode.FED_SPEECH_STREAM,
    val currentStance: StanceType = StanceType.NEUTRAL,
    val stanceScore: Int = 50,
    val activeSubtitlesEn: String = "",
    val activeSubtitlesHi: String = "",
    val currentTakeawayHi: String = "",
    val translationHistory: List<SpeechTranslationChunk> = emptyList(),
    val voiceInteractions: List<VoiceInteraction> = emptyList(),
    val economicEvents: List<EconomicEvent> = MacroNewsData.economicEvents,
    val breakingNews: List<BreakingNewsItem> = MacroNewsData.breakingNewsList,
    val isTtsSpeaking: Boolean = false,
    val isAudioMuted: Boolean = false,
    val isHapticsAlertsEnabled: Boolean = true,
    val activeAlertBanner: MarketAlertNotification? = null,
    val speechSpeed: Float = 1.0f,
    val currency: CurrencyDisplay = CurrencyDisplay.USD,
    val isProcessingAi: Boolean = false,
    val showDownloadDialog: Boolean = false,
    val streamLatencyMs: Int = 36,
    val statusMessage: String = "Sub-second Live Hindi Translation (Latency < 50ms)"
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val geminiService = GeminiLiveTranslateService()
    val financialDataService = FinancialDataService()
    val audioRecorder = AudioRecorderStreamer(application, viewModelScope)
    val audioPlayer = AudioTrackPlayer(application, viewModelScope)
    val feedbackHelper = NotificationFeedbackHelper(application)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val marketAssets: StateFlow<List<MarketAsset>> = financialDataService.marketAssets
    val isWebSocketConnected: StateFlow<Boolean> = financialDataService.isWebSocketConnected
    val wsLatencyMs: StateFlow<Int> = financialDataService.wsLatencyMs

    // Filtered assets based on selected tab/category
    val filteredMarketAssets: StateFlow<List<MarketAsset>> = combine(
        marketAssets,
        _uiState
    ) { assets, state ->
        if (state.selectedCategory == AssetCategory.ALL) {
            assets
        } else {
            assets.filter { it.category == state.selectedCategory }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Waveform amplitudes combined from recorder (mic) or player (output)
    val liveWaveform: StateFlow<List<Float>> = combine(
        audioRecorder.isRecording,
        audioRecorder.waveformData,
        audioPlayer.isPlaying,
        audioPlayer.outputAmplitude
    ) { isRec, recWave, isPlay, playAmp ->
        if (isRec) {
            recWave
        } else if (isPlay) {
            List(16) { index ->
                val base = (playAmp * 0.8f).coerceIn(0.1f, 0.95f)
                val variance = ((index % 3) * 0.1f)
                (base - variance).coerceAtLeast(0.15f)
            }
        } else {
            List(16) { 0.08f }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, List(16) { 0.08f })

    private var speechStreamJob: Job? = null
    private var marketTickJob: Job? = null
    private var alertBannerJob: Job? = null

    init {
        setupMarketAlertListener()
        loadScenario(FedSpeechFeed.scenarios.first())
        startMarketTickLoop()
        startSpeechStreamingLoop()
    }

    private fun setupMarketAlertListener() {
        financialDataService.onSignificantMarketUpdate = { asset, isBullish ->
            if (_uiState.value.isHapticsAlertsEnabled) {
                feedbackHelper.triggerMarketUpdateAlert(
                    isBullish = isBullish,
                    isMuted = _uiState.value.isAudioMuted
                )
            }

            val sign = if (asset.changePercent24h >= 0) "+" else ""
            val formattedPrice = String.format("%.2f", asset.priceUsd)
            val alert = MarketAlertNotification(
                title = "⚡ ${asset.symbol} Live Alert",
                messageHi = "${asset.hindiName}: $${formattedPrice} ($sign${String.format("%.2f", asset.changePercent24h)}%)",
                isBullish = isBullish,
                timestampFormatted = "Live",
                iconType = "MARKET"
            )

            showAlertBanner(alert)
        }
    }

    private fun showAlertBanner(alert: MarketAlertNotification) {
        alertBannerJob?.cancel()
        _uiState.value = _uiState.value.copy(activeAlertBanner = alert)
        alertBannerJob = viewModelScope.launch {
            delay(3500)
            if (_uiState.value.activeAlertBanner?.id == alert.id) {
                _uiState.value = _uiState.value.copy(activeAlertBanner = null)
            }
        }
    }

    fun dismissAlertBanner() {
        alertBannerJob?.cancel()
        _uiState.value = _uiState.value.copy(activeAlertBanner = null)
    }

    fun toggleHapticsAlerts() {
        val next = !_uiState.value.isHapticsAlertsEnabled
        _uiState.value = _uiState.value.copy(
            isHapticsAlertsEnabled = next,
            statusMessage = if (next) "Haptic & Notification Sounds Enabled" else "Haptic & Notification Sounds Disabled"
        )
        if (next) {
            feedbackHelper.triggerFedAnnouncementAlert(isMuted = false)
        }
    }

    fun triggerTestAlert(isFedAnnouncement: Boolean = false) {
        if (isFedAnnouncement) {
            feedbackHelper.triggerFedAnnouncementAlert(isMuted = _uiState.value.isAudioMuted)
            showAlertBanner(
                MarketAlertNotification(
                    title = "🎙️ Fed Announcement Alert",
                    messageHi = "यूएस फेड चेयरमैन केविन मैक्सवेल वार्श: नया नीतिगत वक्तव्य",
                    isBullish = true,
                    timestampFormatted = "Live Now",
                    iconType = "FED_ANNOUNCEMENT"
                )
            )
        } else {
            val asset = marketAssets.value.firstOrNull { it.id == "btc" } ?: marketAssets.value.first()
            feedbackHelper.triggerMarketUpdateAlert(isBullish = true, isMuted = _uiState.value.isAudioMuted)
            showAlertBanner(
                MarketAlertNotification(
                    title = "📈 Significant Market Move",
                    messageHi = "${asset.symbol} ${asset.hindiName}: $${String.format("%.2f", asset.priceUsd)}",
                    isBullish = true,
                    timestampFormatted = "Live",
                    iconType = "MARKET"
                )
            )
        }
    }

    fun selectTab(tab: AppNavTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
    }

    fun selectCategory(category: AssetCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    private fun startMarketTickLoop() {
        marketTickJob?.cancel()
        marketTickJob = viewModelScope.launch {
            while (isActive) {
                delay(1200)
                financialDataService.tickMarketPrices(_uiState.value.currentStance)
            }
        }
    }

    fun selectScenario(scenarioId: String) {
        val scenario = FedSpeechFeed.scenarios.find { it.id == scenarioId } ?: return
        loadScenario(scenario)
    }

    private fun loadScenario(scenario: FedSpeechScenario) {
        val firstChunk = scenario.chunks.firstOrNull()
        _uiState.value = _uiState.value.copy(
            currentScenario = scenario,
            currentChunkIndex = 0,
            sourceMode = AudioSourceMode.FED_SPEECH_STREAM,
            currentStance = scenario.defaultStance,
            stanceScore = if (scenario.defaultStance == StanceType.DOVISH) 78 else if (scenario.defaultStance == StanceType.HAWKISH) 22 else 50,
            activeSubtitlesEn = firstChunk?.englishText ?: "",
            activeSubtitlesHi = firstChunk?.hindiTranslation ?: "",
            currentTakeawayHi = firstChunk?.keyTakeawayHi ?: "",
            translationHistory = if (firstChunk != null) listOf(firstChunk) else emptyList()
        )

        if (_uiState.value.isHapticsAlertsEnabled) {
            feedbackHelper.triggerFedAnnouncementAlert(isMuted = _uiState.value.isAudioMuted)
        }

        showAlertBanner(
            MarketAlertNotification(
                title = "🎙️ ${scenario.speaker}",
                messageHi = scenario.titleHi,
                isBullish = scenario.defaultStance == StanceType.DOVISH,
                timestampFormatted = "Speech Live",
                iconType = "FED_ANNOUNCEMENT"
            )
        )

        if (firstChunk != null && !_uiState.value.isAudioMuted) {
            audioPlayer.speakHindiText(firstChunk.hindiTranslation)
        }
    }

    fun startSpeechStreamingLoop() {
        speechStreamJob?.cancel()
        _uiState.value = _uiState.value.copy(isStreamingActive = true)

        speechStreamJob = viewModelScope.launch {
            val scenario = _uiState.value.currentScenario
            val chunks = scenario.chunks

            while (isActive && _uiState.value.isStreamingActive && _uiState.value.sourceMode == AudioSourceMode.FED_SPEECH_STREAM) {
                val nextIndex = (_uiState.value.currentChunkIndex + 1) % chunks.size
                val chunk = chunks[nextIndex]

                delay(6500) // Realistic speech thought cadences

                if (!isActive) break

                val stanceScore = when (chunk.stance) {
                    StanceType.DOVISH -> 80
                    StanceType.HAWKISH -> 22
                    StanceType.NEUTRAL -> 50
                }

                _uiState.value = _uiState.value.copy(
                    currentChunkIndex = nextIndex,
                    activeSubtitlesEn = chunk.englishText,
                    activeSubtitlesHi = chunk.hindiTranslation,
                    currentTakeawayHi = chunk.keyTakeawayHi,
                    currentStance = chunk.stance,
                    stanceScore = stanceScore,
                    translationHistory = (_uiState.value.translationHistory + chunk).takeLast(18),
                    streamLatencyMs = (28..45).random()
                )

                if (_uiState.value.isHapticsAlertsEnabled) {
                    feedbackHelper.triggerFedAnnouncementAlert(isMuted = _uiState.value.isAudioMuted)
                }

                if (!_uiState.value.isAudioMuted) {
                    audioPlayer.speakHindiText(chunk.hindiTranslation)
                }
            }
        }
    }

    fun toggleStreaming() {
        if (_uiState.value.isStreamingActive) {
            speechStreamJob?.cancel()
            speechStreamJob = null
            audioPlayer.stopPlayback()
            _uiState.value = _uiState.value.copy(isStreamingActive = false)
        } else {
            startSpeechStreamingLoop()
        }
    }

    fun toggleMute() {
        audioPlayer.toggleMute()
        _uiState.value = _uiState.value.copy(isAudioMuted = audioPlayer.isMuted.value)
    }

    fun setPlaybackSpeed(speed: Float) {
        audioPlayer.setSpeed(speed)
        _uiState.value = _uiState.value.copy(speechSpeed = speed)
    }

    fun toggleCurrency() {
        val next = if (_uiState.value.currency == CurrencyDisplay.USD) CurrencyDisplay.INR else CurrencyDisplay.USD
        _uiState.value = _uiState.value.copy(currency = next)
    }

    fun toggleMicrophoneInput() {
        if (_uiState.value.isRecordingMic) {
            audioRecorder.stopRecording()
            _uiState.value = _uiState.value.copy(
                isRecordingMic = false,
                statusMessage = "Mic recording stopped"
            )
        } else {
            speechStreamJob?.cancel()
            audioPlayer.stopPlayback()

            _uiState.value = _uiState.value.copy(
                sourceMode = AudioSourceMode.LIVE_MIC_INPUT,
                isRecordingMic = true,
                isStreamingActive = false,
                statusMessage = "Listening to Live Microphone (16kHz PCM)..."
            )

            audioRecorder.startRecording { _ -> }
        }
    }

    fun askQuestion(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isProcessingAi = true,
                statusMessage = "Gemini processing voice query in Hindi..."
            )

            val marketContext = financialDataService.getMarketSummaryString()
            val result = geminiService.answerMarketVoiceQuery(query, marketContext)

            _uiState.value = _uiState.value.copy(isProcessingAi = false)

            result.onSuccess { answerHi ->
                val interaction = VoiceInteraction(
                    id = System.currentTimeMillis().toString(),
                    queryText = query,
                    responseHindi = answerHi
                )
                _uiState.value = _uiState.value.copy(
                    voiceInteractions = listOf(interaction) + _uiState.value.voiceInteractions,
                    statusMessage = "Hindi Voice Answer Ready"
                )
                if (!_uiState.value.isAudioMuted) {
                    audioPlayer.speakHindiText(answerHi)
                }
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    statusMessage = "Error: ${error.message ?: "Failed to get response"}"
                )
            }
        }
    }

    fun speakCustomHindi(textHi: String) {
        if (!_uiState.value.isAudioMuted) {
            audioPlayer.speakHindiText(textHi)
        }
    }

    fun playInteractionVoice(interaction: VoiceInteraction) {
        speakCustomHindi(interaction.responseHindi)
    }

    fun openDownloadDialog() {
        _uiState.value = _uiState.value.copy(showDownloadDialog = true)
    }

    fun closeDownloadDialog() {
        _uiState.value = _uiState.value.copy(showDownloadDialog = false)
    }

    fun generateExportReportText(): String {
        val state = _uiState.value
        val scenario = state.currentScenario
        val builder = StringBuilder()
        builder.append("====================================================\n")
        builder.append("🏛️ US FED SPEECH & REAL-TIME HINDI TRANSLATION REPORT\n")
        builder.append("====================================================\n\n")
        builder.append("Speaker: ${scenario.speaker}\n")
        builder.append("Event: ${scenario.eventName}\n")
        builder.append("Topic: ${scenario.title}\n")
        builder.append("Hindi Title: ${scenario.titleHi}\n")
        builder.append("Overall Policy Stance: ${state.currentStance.name} (Score: ${state.stanceScore}/100)\n\n")

        builder.append("--- LIVE TRANSLATION LOGS ---\n")
        scenario.chunks.forEach { chunk ->
            builder.append("[${chunk.timestampFormatted}] ${chunk.stance.name}\n")
            builder.append("EN: ${chunk.englishText}\n")
            builder.append("HI: ${chunk.hindiTranslation}\n")
            builder.append("Key Takeaway (HI): ${chunk.keyTakeawayHi}\n\n")
        }

        builder.append("--- REAL-TIME MARKET SNAPSHOT ---\n")
        marketAssets.value.take(8).forEach { asset ->
            val sign = if (asset.changePercent24h >= 0) "+" else ""
            builder.append("${asset.symbol} (${asset.hindiName}): $${String.format("%.2f", asset.priceUsd)} ($sign${String.format("%.2f", asset.changePercent24h)}%)\n")
        }

        builder.append("\n====================================================\n")
        builder.append("Generated by US Fed Hindi Live Audio & Market Intelligence\n")
        builder.append("====================================================\n")
        return builder.toString()
    }

    override fun onCleared() {
        super.onCleared()
        speechStreamJob?.cancel()
        marketTickJob?.cancel()
        alertBannerJob?.cancel()
        audioRecorder.stopRecording()
        audioPlayer.release()
        feedbackHelper.release()
        financialDataService.close()
    }
}

