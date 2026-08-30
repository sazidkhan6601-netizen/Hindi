package com.example.data

enum class AssetCategory(val displayName: String) {
    ALL("All Assets"),
    CRYPTO("Crypto"),
    FOREX("Forex"),
    COMMODITY("Commodities"),
    INDEX("Indices"),
    STOCK("Stocks"),
    MACRO("Macro Data")
}

data class MarketAsset(
    val id: String,
    val symbol: String,
    val name: String,
    val hindiName: String,
    val priceUsd: Double,
    val changePercent24h: Double,
    val changeAmountUsd: Double,
    val high24h: Double,
    val low24h: Double,
    val sparkline: List<Double>,
    val category: AssetCategory,
    val unit: String = "$",
    val isHotImpacted: Boolean = false,
    val volume24h: String = "$1.2B"
)

enum class StanceType(val labelEn: String, val labelHi: String) {
    HAWKISH("Hawkish (सख्त नीति)", "महंगाई रोकने के लिए ब्याज दरें ऊंची रखने का संकेत"),
    DOVISH("Dovish (नरम नीति)", "अर्थव्यवस्था को बढ़ावा देने के लिए ब्याज दरों में कटौती का संकेत"),
    NEUTRAL("Neutral (संतुलित)", "आंकड़ों पर आधारित संतुलित रुख")
}

enum class ImpactLevel(val label: String) {
    HIGH("High Impact 🔥"),
    MEDIUM("Medium Impact ⚡"),
    LOW("Low Impact")
}

enum class EventStatus {
    RELEASED,
    UPCOMING
}

data class EconomicEvent(
    val id: String,
    val title: String,
    val titleHi: String,
    val country: String,
    val flag: String,
    val impact: ImpactLevel,
    val timeFormatted: String,
    val dateFormatted: String,
    val actual: String?,
    val forecast: String,
    val previous: String,
    val status: EventStatus,
    val analysisHi: String,
    val isHot: Boolean = false
)

data class BreakingNewsItem(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val source: String,
    val timeAgo: String,
    val sentiment: StanceType,
    val keyTakeawayHi: String,
    val tags: List<String>
)

data class StanceAssessment(
    val type: StanceType,
    val score: Int, // 0 = Extreme Hawkish, 50 = Neutral, 100 = Extreme Dovish
    val explanationHi: String,
    val impactBtc: String,
    val impactGold: String,
    val impactStocks: String
)

data class SpeechTranslationChunk(
    val id: String,
    val orderIndex: Int,
    val englishText: String,
    val hindiTranslation: String,
    val timestampFormatted: String,
    val stance: StanceType,
    val keyTakeawayHi: String,
    val isLiveStreaming: Boolean = false
)

data class FedSpeechScenario(
    val id: String,
    val title: String,
    val titleHi: String,
    val speaker: String,
    val eventName: String,
    val description: String,
    val defaultStance: StanceType,
    val chunks: List<SpeechTranslationChunk>
)

data class VoiceInteraction(
    val id: String,
    val queryText: String,
    val responseHindi: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPlaying: Boolean = false
)

data class MarketAlertNotification(
    val id: String = System.currentTimeMillis().toString(),
    val title: String,
    val messageHi: String,
    val isBullish: Boolean,
    val timestampFormatted: String,
    val iconType: String = "MARKET" // "MARKET" or "FED_ANNOUNCEMENT"
)

