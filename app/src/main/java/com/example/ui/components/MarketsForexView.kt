package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AssetCategory
import com.example.data.MarketAsset
import com.example.market.FinancialDataService
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CurrencyDisplay
import java.util.Locale

@Composable
fun MarketsForexView(
    assets: List<MarketAsset>,
    selectedCategory: AssetCategory,
    currency: CurrencyDisplay,
    isWsConnected: Boolean,
    wsLatencyMs: Int,
    onSelectCategory: (AssetCategory) -> Unit,
    onSpeakAssetInfo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // WebSocket Status Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceCard)
                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
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
                            .background(if (isWsConnected) BullishGreen else BearishRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isWsConnected) "WebSocket Feed: Live" else "WebSocket Feed: Reconnecting",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isWsConnected) BullishGreen else BearishRed
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "Latency",
                        tint = ElectricBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$wsLatencyMs ms delay",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = TextSecondary
                    )
                }
            }
        }

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(AssetCategory.values()) { category ->
                val isSelected = category == selectedCategory
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) ElectricBlue else SurfaceCard)
                        .border(1.dp, if (isSelected) ElectricBlue else BorderSubtle, RoundedCornerShape(20.dp))
                        .clickable { onSelectCategory(category) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("category_chip_${category.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        ),
                        color = if (isSelected) Color.White else TextSecondary
                    )
                }
            }
        }

        // Assets List
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            assets.forEach { asset ->
                AssetCardItem(
                    asset = asset,
                    currency = currency,
                    onSpeak = {
                        val priceText = if (currency == CurrencyDisplay.INR && asset.unit != "%") {
                            "₹${String.format(Locale.US, "%,.2f", asset.priceUsd * FinancialDataService.USD_TO_INR_RATE)}"
                        } else {
                            "${asset.unit}${String.format(Locale.US, "%,.2f", asset.priceUsd)}"
                        }
                        val trendText = if (asset.changePercent24h >= 0) "तेजी के साथ ${String.format(Locale.US, "%.2f", asset.changePercent24h)} प्रतिशत ऊपर" else "गिरावट के साथ ${String.format(Locale.US, "%.2f", Math.abs(asset.changePercent24h))} प्रतिशत नीचे"
                        val textToRead = "${asset.name}, ${asset.hindiName}. वर्तमान मूल्य $priceText. 24 घंटे में $trendText है।"
                        onSpeakAssetInfo(textToRead)
                    }
                )
            }
        }
    }
}

@Composable
private fun AssetCardItem(
    asset: MarketAsset,
    currency: CurrencyDisplay,
    onSpeak: () -> Unit
) {
    val isPositive = asset.changePercent24h >= 0
    val trendColor = if (isPositive) BullishGreen else BearishRed

    val displayPrice = if (currency == CurrencyDisplay.INR && asset.unit != "%") {
        "₹" + String.format(Locale.US, "%,.2f", asset.priceUsd * FinancialDataService.USD_TO_INR_RATE)
    } else {
        asset.unit + if (asset.priceUsd < 10 && asset.unit == "$") {
            String.format(Locale.US, "%.4f", asset.priceUsd)
        } else {
            String.format(Locale.US, "%,.2f", asset.priceUsd)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .border(
                1.dp,
                if (asset.isHotImpacted) GoldAccent.copy(alpha = 0.5f) else BorderSubtle,
                RoundedCornerShape(14.dp)
            )
            .clickable { onSpeak() }
            .padding(14.dp)
            .testTag("asset_card_${asset.id}")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header Row: Symbol & Hindi Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = asset.symbol,
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
                            .background(SurfaceCardElevated)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = asset.category.name,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextMuted
                        )
                    }
                    if (asset.isHotImpacted) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = "Hot Impact",
                            tint = GoldAccent,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Speak Asset",
                    tint = ElectricBlue,
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = asset.hindiName,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = TextSecondary
            )

            // Middle Row: Price, Sparkline, 24h Change
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = displayPrice,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        ),
                        color = TextPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = null,
                            tint = trendColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${if (isPositive) "+" else ""}${String.format(Locale.US, "%.2f", asset.changePercent24h)}%",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = trendColor
                        )
                    }
                }

                // Sparkline
                Box(
                    modifier = Modifier
                        .width(90.dp)
                        .height(36.dp)
                ) {
                    SparklineCanvas(
                        points = asset.sparkline,
                        lineColor = trendColor
                    )
                }

                // 24h High/Low Stats
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "24h Vol: ${asset.volume24h}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = TextMuted
                    )
                    Text(
                        text = "H: ${String.format(Locale.US, "%.1f", asset.high24h)} | L: ${String.format(Locale.US, "%.1f", asset.low24h)}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun SparklineCanvas(
    points: List<Double>,
    lineColor: Color
) {
    if (points.size < 2) return

    Canvas(modifier = Modifier.fillMaxSize()) {
        val min = points.minOrNull() ?: 0.0
        val max = points.maxOrNull() ?: 1.0
        val range = (max - min).coerceAtLeast(0.0001)

        val stepX = size.width / (points.size - 1)
        val path = Path()

        points.forEachIndexed { index, price ->
            val x = index * stepX
            val normalizedY = ((price - min) / range).toFloat()
            val y = size.height - (normalizedY * size.height)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
