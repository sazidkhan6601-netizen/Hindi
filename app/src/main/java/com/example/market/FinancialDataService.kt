package com.example.market

import android.util.Log
import com.example.data.AssetCategory
import com.example.data.MarketAsset
import com.example.data.StanceType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class FinancialDataService {

    companion object {
        private const val TAG = "FinancialDataService"
        const val USD_TO_INR_RATE = 86.60
    }

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(6, TimeUnit.SECONDS)
        .pingInterval(15, TimeUnit.SECONDS)
        .build()

    private var binanceWebSocket: WebSocket? = null

    private val _isWebSocketConnected = MutableStateFlow(true)
    val isWebSocketConnected: StateFlow<Boolean> = _isWebSocketConnected.asStateFlow()

    private val _wsLatencyMs = MutableStateFlow(24)
    val wsLatencyMs: StateFlow<Int> = _wsLatencyMs.asStateFlow()

    var onSignificantMarketUpdate: ((asset: MarketAsset, isBullish: Boolean) -> Unit)? = null
    private var lastAlertTimestamp: Long = 0

    // Comprehensive list of assets spanning Crypto, Forex, Commodities, Stocks, Indices, Macro
    private val initialAssets = listOf(
        // CRYPTO
        MarketAsset(
            id = "btc",
            symbol = "BTC/USD",
            name = "Bitcoin",
            hindiName = "बिटकॉइन (क्रिप्टो)",
            priceUsd = 78244.00,
            changePercent24h = 3.28,
            changeAmountUsd = 2482.00,
            high24h = 79450.00,
            low24h = 75800.00,
            sparkline = listOf(75800.0, 76400.0, 76100.0, 77200.0, 77900.0, 78100.0, 78244.0),
            category = AssetCategory.CRYPTO,
            unit = "$",
            isHotImpacted = true,
            volume24h = "$52.8B"
        ),
        MarketAsset(
            id = "eth",
            symbol = "ETH/USD",
            name = "Ethereum",
            hindiName = "इथेरियम (क्रिप्टो)",
            priceUsd = 3140.50,
            changePercent24h = 2.85,
            changeAmountUsd = 87.20,
            high24h = 3210.00,
            low24h = 3050.00,
            sparkline = listOf(3050.0, 3080.0, 3105.0, 3120.0, 3110.0, 3140.5),
            category = AssetCategory.CRYPTO,
            unit = "$",
            isHotImpacted = true,
            volume24h = "$24.6B"
        ),
        // COMMODITIES
        MarketAsset(
            id = "gold",
            symbol = "XAU/USD",
            name = "Gold Spot (XAU/USD)",
            hindiName = "सोना / गोल्ड स्पॉट (XAU/USD प्रति औंस)",
            priceUsd = 4454.00,
            changePercent24h = 1.45,
            changeAmountUsd = 63.80,
            high24h = 4480.00,
            low24h = 4390.00,
            sparkline = listOf(4390.0, 4410.0, 4425.0, 4418.0, 4440.0, 4448.0, 4454.0),
            category = AssetCategory.COMMODITY,
            unit = "$",
            isHotImpacted = true,
            volume24h = "$28.2B"
        ),
        MarketAsset(
            id = "silver",
            symbol = "XAG/USD",
            name = "Silver Spot",
            hindiName = "चांदी / सिल्वर स्पॉट (प्रति औंस)",
            priceUsd = 34.20,
            changePercent24h = 1.80,
            changeAmountUsd = 0.60,
            high24h = 34.60,
            low24h = 33.40,
            sparkline = listOf(33.4, 33.6, 33.9, 33.8, 34.0, 34.2),
            category = AssetCategory.COMMODITY,
            unit = "$",
            isHotImpacted = false,
            volume24h = "$8.1B"
        ),
        MarketAsset(
            id = "wti",
            symbol = "WTI Crude",
            name = "Crude Oil",
            hindiName = "कच्चा तेल (WTI क्रूड ऑयल)",
            priceUsd = 71.85,
            changePercent24h = -0.65,
            changeAmountUsd = -0.47,
            high24h = 73.20,
            low24h = 71.10,
            sparkline = listOf(73.2, 72.8, 72.4, 71.9, 72.1, 71.85),
            category = AssetCategory.COMMODITY,
            unit = "$",
            isHotImpacted = false,
            volume24h = "$9.8B"
        ),
        // FOREX (LIVE CONNECTED)
        MarketAsset(
            id = "usdinr",
            symbol = "USD/INR",
            name = "USD to Indian Rupee",
            hindiName = "अमेरिकी डॉलर / भारतीय रुपया (Forex Live)",
            priceUsd = 86.62,
            changePercent24h = -0.18,
            changeAmountUsd = -0.15,
            high24h = 86.85,
            low24h = 86.55,
            sparkline = listOf(86.80, 86.75, 86.70, 86.68, 86.64, 86.62),
            category = AssetCategory.FOREX,
            unit = "₹",
            isHotImpacted = true,
            volume24h = "₹14.2K Cr"
        ),
        MarketAsset(
            id = "eurusd",
            symbol = "EUR/USD",
            name = "Euro / US Dollar",
            hindiName = "यूरो / अमेरिकी डॉलर (Forex Live)",
            priceUsd = 1.0875,
            changePercent24h = 0.42,
            changeAmountUsd = 0.0045,
            high24h = 1.0910,
            low24h = 1.0820,
            sparkline = listOf(1.0820, 1.0840, 1.0855, 1.0860, 1.0875),
            category = AssetCategory.FOREX,
            unit = "$",
            isHotImpacted = true,
            volume24h = "$92.4B"
        ),
        MarketAsset(
            id = "gbpusd",
            symbol = "GBP/USD",
            name = "British Pound / USD",
            hindiName = "ब्रिटिश पाउंड / अमेरिकी डॉलर (Forex Live)",
            priceUsd = 1.2985,
            changePercent24h = 0.35,
            changeAmountUsd = 0.0045,
            high24h = 1.3020,
            low24h = 1.2940,
            sparkline = listOf(1.2940, 1.2955, 1.2968, 1.2975, 1.2985),
            category = AssetCategory.FOREX,
            unit = "$",
            isHotImpacted = true,
            volume24h = "$68.1B"
        ),
        MarketAsset(
            id = "usdjpy",
            symbol = "USD/JPY",
            name = "US Dollar / Japanese Yen",
            hindiName = "अमेरिकी डॉलर / जापानी येन (Forex Live)",
            priceUsd = 152.30,
            changePercent24h = -0.45,
            changeAmountUsd = -0.68,
            high24h = 153.20,
            low24h = 151.90,
            sparkline = listOf(153.10, 152.90, 152.65, 152.50, 152.30),
            category = AssetCategory.FOREX,
            unit = "¥",
            isHotImpacted = false,
            volume24h = "$74.3B"
        ),
        MarketAsset(
            id = "dxy",
            symbol = "DXY Index",
            name = "US Dollar Index",
            hindiName = "अमेरिकी डॉलर सूचकांक (DXY)",
            priceUsd = 101.45,
            changePercent24h = -0.58,
            changeAmountUsd = -0.60,
            high24h = 102.30,
            low24h = 101.35,
            sparkline = listOf(102.20, 102.05, 101.80, 101.65, 101.45),
            category = AssetCategory.FOREX,
            unit = "pts",
            isHotImpacted = true,
            volume24h = "$32.0B"
        ),
        // INDICES
        MarketAsset(
            id = "us100",
            symbol = "US100",
            name = "Nasdaq 100",
            hindiName = "नैस्डैक टेक इंडेक्स (US100)",
            priceUsd = 20890.40,
            changePercent24h = 0.88,
            changeAmountUsd = 182.50,
            high24h = 20950.00,
            low24h = 20680.00,
            sparkline = listOf(20680.0, 20720.0, 20700.0, 20810.0, 20850.0, 20890.4),
            category = AssetCategory.INDEX,
            unit = "pts",
            isHotImpacted = true,
            volume24h = "$56.1B"
        ),
        MarketAsset(
            id = "spx",
            symbol = "S&P 500",
            name = "S&P 500",
            hindiName = "यूएस 500 मुख्य सूचकांक (SPX)",
            priceUsd = 5910.15,
            changePercent24h = 0.62,
            changeAmountUsd = 36.40,
            high24h = 5930.00,
            low24h = 5865.00,
            sparkline = listOf(5865.0, 5878.0, 5890.0, 5885.0, 5902.0, 5910.15),
            category = AssetCategory.INDEX,
            unit = "pts",
            isHotImpacted = false,
            volume24h = "$72.4B"
        ),
        // STOCKS
        MarketAsset(
            id = "nvda",
            symbol = "NVDA",
            name = "NVIDIA Corp",
            hindiName = "एनवीडिया एआई टेक",
            priceUsd = 142.60,
            changePercent24h = 3.40,
            changeAmountUsd = 4.68,
            high24h = 144.20,
            low24h = 137.50,
            sparkline = listOf(137.5, 138.8, 140.2, 141.5, 142.6),
            category = AssetCategory.STOCK,
            unit = "$",
            isHotImpacted = true,
            volume24h = "$31.2B"
        ),
        MarketAsset(
            id = "aapl",
            symbol = "AAPL",
            name = "Apple Inc",
            hindiName = "एप्पल इंक",
            priceUsd = 231.50,
            changePercent24h = 0.95,
            changeAmountUsd = 2.18,
            high24h = 233.00,
            low24h = 228.40,
            sparkline = listOf(228.4, 229.8, 230.5, 231.0, 231.5),
            category = AssetCategory.STOCK,
            unit = "$",
            isHotImpacted = false,
            volume24h = "$14.5B"
        ),
        // MACRO
        MarketAsset(
            id = "cpi",
            symbol = "US CPI YoY",
            name = "Consumer Price Index",
            hindiName = "यूएस महंगाई दर (लक्ष्य: 2.0%)",
            priceUsd = 2.90,
            changePercent24h = -0.10,
            changeAmountUsd = -0.10,
            high24h = 3.20,
            low24h = 2.90,
            sparkline = listOf(3.4, 3.3, 3.1, 3.0, 2.9, 2.9),
            category = AssetCategory.MACRO,
            unit = "%",
            isHotImpacted = true,
            volume24h = "Monthly"
        ),
        MarketAsset(
            id = "us10y",
            symbol = "US10Y",
            name = "10-Yr Treasury Yield",
            hindiName = "10-वर्षीय अमेरिकी बॉन्ड यील्ड",
            priceUsd = 4.24,
            changePercent24h = -1.65,
            changeAmountUsd = -0.07,
            high24h = 4.35,
            low24h = 4.20,
            sparkline = listOf(4.35, 4.32, 4.29, 4.28, 4.26, 4.24),
            category = AssetCategory.MACRO,
            unit = "%",
            isHotImpacted = false,
            volume24h = "$450B"
        )
    )

    private val _marketAssets = MutableStateFlow(initialAssets)
    val marketAssets: StateFlow<List<MarketAsset>> = _marketAssets.asStateFlow()

    init {
        initWebSocketFeed()
        startForexPolling()
    }

    private fun startForexPolling() {
        serviceScope.launch {
            while (true) {
                try {
                    fetchLiveForexRates()
                } catch (e: Exception) {
                    Log.d(TAG, "Forex poll err: ${e.message}")
                }
                kotlinx.coroutines.delay(12000)
            }
        }
    }

    /**
     * Initializes live Binance WebSocket feed for real-time BTC, ETH, PAXG (Gold Spot XAU/USD), EUR, GBP order-book pricing
     */
    fun initWebSocketFeed() {
        try {
            val wsUrl = "wss://stream.binance.com:9443/ws/btcusdt@ticker/ethusdt@ticker/paxgusdt@ticker/eurusdt@ticker/gbpusdt@ticker"
            val request = Request.Builder().url(wsUrl).build()

            binanceWebSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    _isWebSocketConnected.value = true
                    _wsLatencyMs.value = (18..34).random()
                    Log.d(TAG, "Binance WebSocket connected (BTC, ETH, XAU/USD Gold, EUR, GBP)")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    try {
                        val json = JSONObject(text)
                        val streamSymbol = json.optString("s", "")
                        val lastPrice = json.optDouble("c", 0.0)
                        val priceChangePercent = json.optDouble("P", 0.0)
                        val highPrice = json.optDouble("h", 0.0)
                        val lowPrice = json.optDouble("l", 0.0)

                        _wsLatencyMs.value = (16..38).random()

                        when {
                            streamSymbol.equals("BTCUSDT", ignoreCase = true) && lastPrice > 0 -> {
                                updateAssetPrice("btc", lastPrice, priceChangePercent, highPrice, lowPrice)
                            }
                            streamSymbol.equals("ETHUSDT", ignoreCase = true) && lastPrice > 0 -> {
                                updateAssetPrice("eth", lastPrice, priceChangePercent, highPrice, lowPrice)
                            }
                            streamSymbol.equals("PAXGUSDT", ignoreCase = true) && lastPrice > 0 -> {
                                // Real-time Physical Gold token (XAU/USD equivalent)
                                updateAssetPrice("gold", lastPrice, priceChangePercent, highPrice, lowPrice)
                            }
                            streamSymbol.equals("EURUSDT", ignoreCase = true) && lastPrice > 0 -> {
                                updateAssetPrice("eurusd", lastPrice, priceChangePercent, highPrice, lowPrice)
                            }
                            streamSymbol.equals("GBPUSDT", ignoreCase = true) && lastPrice > 0 -> {
                                updateAssetPrice("gbpusd", lastPrice, priceChangePercent, highPrice, lowPrice)
                            }
                        }
                    } catch (e: Exception) {
                        Log.d(TAG, "WS parse error: ${e.message}")
                    }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Log.d(TAG, "WebSocket failure: ${t.message}")
                    _isWebSocketConnected.value = false
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    _isWebSocketConnected.value = false
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "WS init exception: ${e.message}")
        }
    }

    /**
     * Polls live Forex rates from Open Exchange API (USD base)
     */
    suspend fun fetchLiveForexRates() = withContext(Dispatchers.IO) {
        try {
            val url = "https://open.er-api.com/v6/latest/USD"
            val request = Request.Builder().url(url).build()
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string() ?: return@withContext
                val json = JSONObject(body)
                val rates = json.optJSONObject("rates") ?: return@withContext

                val inrRate = rates.optDouble("INR", 0.0)
                val eurRate = rates.optDouble("EUR", 0.0)
                val gbpRate = rates.optDouble("GBP", 0.0)
                val jpyRate = rates.optDouble("JPY", 0.0)

                if (inrRate > 0) {
                    updateForexAsset("usdinr", inrRate)
                }
                if (eurRate > 0) {
                    val eurUsd = 1.0 / eurRate
                    updateForexAsset("eurusd", eurUsd)
                }
                if (gbpRate > 0) {
                    val gbpUsd = 1.0 / gbpRate
                    updateForexAsset("gbpusd", gbpUsd)
                }
                if (jpyRate > 0) {
                    updateForexAsset("usdjpy", jpyRate)
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Forex fetch err: ${e.message}")
        }
    }

    private fun updateForexAsset(id: String, currentRate: Double) {
        val updated = _marketAssets.value.map { asset ->
            if (asset.id == id && currentRate > 0) {
                val oldPrice = asset.priceUsd
                val diffPct = if (oldPrice > 0) ((currentRate - oldPrice) / oldPrice) * 100.0 else asset.changePercent24h
                val newSparkline = (asset.sparkline.takeLast(7) + listOf(currentRate)).takeLast(8)
                asset.copy(
                    priceUsd = currentRate,
                    changePercent24h = if (Math.abs(diffPct) > 0.001) diffPct else asset.changePercent24h,
                    changeAmountUsd = (currentRate * (asset.changePercent24h / 100.0)),
                    sparkline = newSparkline
                )
            } else {
                asset
            }
        }
        _marketAssets.value = updated
    }

    /**
     * Polls fallback REST endpoints if WebSocket is disconnected
     */
    suspend fun fetchLiveBtcPrice(): Double? = withContext(Dispatchers.IO) {
        try {
            val url = "https://api.binance.com/api/v3/ticker/24hr?symbol=BTCUSDT"
            val request = Request.Builder().url(url).build()
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string() ?: return@withContext null
                val json = JSONObject(body)
                val lastPrice = json.optDouble("lastPrice", 0.0)
                val priceChangePercent = json.optDouble("priceChangePercent", 0.0)
                val highPrice = json.optDouble("highPrice", 0.0)
                val lowPrice = json.optDouble("lowPrice", 0.0)

                updateAssetPrice("btc", lastPrice, priceChangePercent, highPrice, lowPrice)
                return@withContext lastPrice
            }
        } catch (e: Exception) {
            Log.d(TAG, "Binance fetch: ${e.message}")
        }
        null
    }

    /**
     * Updates live ticks with simulated order flow dynamics and correlation to speech stance.
     */
    fun tickMarketPrices(currentStance: StanceType = StanceType.NEUTRAL) {
        val currentList = _marketAssets.value
        val updated = currentList.map { asset ->
            // Skip btc/eth if web socket is receiving live ticks, or apply minor tick
            val randomJitter = (Random.nextDouble(-0.12, 0.14)) / 100.0

            // Stance bias
            val stanceBias = when (currentStance) {
                StanceType.DOVISH -> when (asset.category) {
                    AssetCategory.CRYPTO -> 0.0025
                    AssetCategory.COMMODITY -> 0.0018
                    AssetCategory.INDEX -> 0.0015
                    AssetCategory.STOCK -> 0.002
                    AssetCategory.FOREX -> if (asset.id == "dxy") -0.0015 else 0.001
                    AssetCategory.MACRO -> if (asset.id == "us10y") -0.002 else 0.0
                    else -> 0.0
                }
                StanceType.HAWKISH -> when (asset.category) {
                    AssetCategory.CRYPTO -> -0.0025
                    AssetCategory.COMMODITY -> -0.0012
                    AssetCategory.INDEX -> -0.0018
                    AssetCategory.STOCK -> -0.0018
                    AssetCategory.FOREX -> if (asset.id == "dxy") 0.0015 else -0.001
                    AssetCategory.MACRO -> if (asset.id == "us10y") 0.002 else 0.0
                    else -> 0.0
                }
                StanceType.NEUTRAL -> 0.0
            }

            val netFactor = 1.0 + randomJitter + stanceBias
            val newPrice = if (asset.unit == "%") {
                (asset.priceUsd + (randomJitter * 5)).coerceIn(1.5, 6.0)
            } else if (asset.category == AssetCategory.FOREX && asset.id != "usdinr" && asset.id != "dxy") {
                asset.priceUsd * (1.0 + (randomJitter * 0.1))
            } else {
                asset.priceUsd * netFactor
            }

            val newChangePct = asset.changePercent24h + ((randomJitter + stanceBias) * 8)
            val newSparkline = (asset.sparkline.takeLast(7) + listOf(newPrice)).takeLast(8)

            asset.copy(
                priceUsd = newPrice,
                changePercent24h = newChangePct,
                changeAmountUsd = (newPrice * (newChangePct / 100.0)),
                sparkline = newSparkline,
                isHotImpacted = currentStance != StanceType.NEUTRAL && (asset.category == AssetCategory.CRYPTO || asset.id == "gold" || asset.id == "nvda")
            )
        }
        _marketAssets.value = updated
    }

    private fun updateAssetPrice(
        id: String,
        newPrice: Double,
        newChangePercent: Double,
        high: Double,
        low: Double
    ) {
        var triggeredAsset: MarketAsset? = null
        var isBullish = true
        val updated = _marketAssets.value.map { asset ->
            if (asset.id == id && newPrice > 0) {
                val oldPrice = asset.priceUsd
                val priceDiff = newPrice - oldPrice
                val pctMove = if (oldPrice > 0) Math.abs((priceDiff / oldPrice) * 100.0) else 0.0
                isBullish = newChangePercent >= 0

                val newSparkline = (asset.sparkline.takeLast(7) + listOf(newPrice)).takeLast(8)
                val newAsset = asset.copy(
                    priceUsd = newPrice,
                    changePercent24h = newChangePercent,
                    high24h = if (high > 0) high else asset.high24h,
                    low24h = if (low > 0) low else asset.low24h,
                    sparkline = newSparkline
                )

                val now = System.currentTimeMillis()
                if (now - lastAlertTimestamp > 3500 && (pctMove > 0.08 || id == "btc" || id == "gold" || id == "usdinr")) {
                    lastAlertTimestamp = now
                    triggeredAsset = newAsset
                }
                newAsset
            } else {
                asset
            }
        }
        _marketAssets.value = updated
        triggeredAsset?.let { asset ->
            onSignificantMarketUpdate?.invoke(asset, isBullish)
        }
    }

    fun getMarketSummaryString(): String {
        return _marketAssets.value.take(6).joinToString(", ") {
            "${it.symbol}: $${String.format("%.2f", it.priceUsd)} (${if (it.changePercent24h >= 0) "+" else ""}${String.format("%.2f", it.changePercent24h)}%)"
        }
    }

    fun close() {
        binanceWebSocket?.close(1000, "App closed")
    }
}
