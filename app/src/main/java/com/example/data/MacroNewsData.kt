package com.example.data

object MacroNewsData {

    val economicEvents = listOf(
        EconomicEvent(
            id = "cpi_yoy",
            title = "US Consumer Price Index (CPI YoY)",
            titleHi = "अमेरिकी वार्षिक उपभोक्ता महंगाई दर (CPI)",
            country = "USD",
            flag = "🇺🇸",
            impact = ImpactLevel.HIGH,
            timeFormatted = "18:00 IST",
            dateFormatted = "Today",
            actual = "2.9%",
            forecast = "3.0%",
            previous = "3.2%",
            status = EventStatus.RELEASED,
            analysisHi = "महंगाई 2.9% आई जो अनुमान (3.0%) से कम है। इससे फेड द्वारा ब्याज दर कटौती की संभावना 85% बढ़ गई है।",
            isHot = true
        ),
        EconomicEvent(
            id = "fed_rate_decision",
            title = "FOMC Fed Interest Rate Decision",
            titleHi = "फेडरल रिजर्व ब्याज दर निर्णय एवं वक्तव्य",
            country = "USD",
            flag = "🇺🇸",
            impact = ImpactLevel.HIGH,
            timeFormatted = "23:30 IST",
            dateFormatted = "Tonight",
            actual = "4.75%",
            forecast = "4.75%",
            previous = "5.25%",
            status = EventStatus.RELEASED,
            analysisHi = "फेड ने ब्याज दरों में 50 bps की कटौती की। बिटकॉइन और गोल्ड में तेज उछाल देखा गया।",
            isHot = true
        ),
        EconomicEvent(
            id = "core_pce",
            title = "Core PCE Price Index (MoM)",
            titleHi = "कोर पीसीई मुद्रास्फीति सूचकांक (फेड का पसंदीदा पैमाना)",
            country = "USD",
            flag = "🇺🇸",
            impact = ImpactLevel.HIGH,
            timeFormatted = "18:00 IST",
            dateFormatted = "Upcoming (Friday)",
            actual = null,
            forecast = "0.2%",
            previous = "0.2%",
            status = EventStatus.UPCOMING,
            analysisHi = "अगर PCE 0.2% या उससे कम आता है तो डॉलर इंडेक्स (DXY) में और गिरावट देखने को मिलेगी।",
            isHot = false
        ),
        EconomicEvent(
            id = "nfp_jobs",
            title = "Non-Farm Payrolls (NFP)",
            titleHi = "अमेरिकी गैर-कृषि रोजगार (NFP आंकड़े)",
            country = "USD",
            flag = "🇺🇸",
            impact = ImpactLevel.HIGH,
            timeFormatted = "18:00 IST",
            dateFormatted = "Next Week",
            actual = null,
            forecast = "165K",
            previous = "142K",
            status = EventStatus.UPCOMING,
            analysisHi = "लेबर मार्केट में नरमी फेड को ब्याज दरें तेजी से घटाने के लिए प्रेरित करेगी।",
            isHot = false
        ),
        EconomicEvent(
            id = "rbi_mpc",
            title = "RBI Monetary Policy Committee (Repo Rate)",
            titleHi = "भारतीय रिजर्व बैंक (RBI) रेपो दर फैसला",
            country = "INR",
            flag = "🇮🇳",
            impact = ImpactLevel.MEDIUM,
            timeFormatted = "10:00 IST",
            dateFormatted = "Next Month",
            actual = null,
            forecast = "6.50%",
            previous = "6.50%",
            status = EventStatus.UPCOMING,
            analysisHi = "फेड कटौती के बाद आरबीआई भी आने वाली तिमाहियों में दरें घटाने पर विचार कर सकता है।",
            isHot = false
        )
    )

    val breakingNewsList = listOf(
        BreakingNewsItem(
            id = "news_1",
            titleEn = "BLOOMBERG: Bitcoin breaks $95,000 as Powell confirms dovish liquidity cycle opening.",
            titleHi = "ब्लूमबर्ग: पॉवेल द्वारा लिक्विडिटी चक्र खोलने की पुष्टि के बाद बिटकॉइन 95,000 डॉलर के पार निकला।",
            source = "Bloomberg Terminal",
            timeAgo = "2m ago",
            sentiment = StanceType.DOVISH,
            keyTakeawayHi = "क्रिप्टो और टेक शेयरों में भारी वॉल्यूम के साथ तेजी।",
            tags = listOf("CRYPTO", "FED", "BTC")
        ),
        BreakingNewsItem(
            id = "news_2",
            titleEn = "REUTERS: Gold Spot hits fresh record high near $2,750 on declining US Treasury yields.",
            titleHi = "रॉयटर्स: अमेरिकी बॉन्ड यील्ड में गिरावट के कारण गोल्ड स्पॉट 2,750 डॉलर के नए रिकॉर्ड पर पहुंचा।",
            source = "Reuters Financial",
            timeAgo = "8m ago",
            sentiment = StanceType.DOVISH,
            keyTakeawayHi = "कम ब्याज दरों से सोने की मांग में वैश्विक उछाल।",
            tags = listOf("GOLD", "FOREX", "XAU")
        ),
        BreakingNewsItem(
            id = "news_3",
            titleEn = "FOREX FACTORY: USD/INR stabilizes around 86.60 as DXY drops below 101.50.",
            titleHi = "फॉरेक्स फैक्टरी: डॉलर इंडेक्स (DXY) 101.50 से नीचे फिसलने से रुपया स्थिर हुआ।",
            source = "Forex Factory Live",
            timeAgo = "14m ago",
            sentiment = StanceType.NEUTRAL,
            keyTakeawayHi = "डॉलर में कमजोरी से उभरते बाजारों (इमर्जिंग मार्केट्स) को राहत।",
            tags = listOf("FOREX", "USDINR", "DXY")
        ),
        BreakingNewsItem(
            id = "news_4",
            titleEn = "CNBC: Nvidia and Nasdaq 100 surge as lower cost of capital boosts AI hyperscalers.",
            titleHi = "सीएनबीसी: ब्याज दर घटने से कम लागत का लाभ मिलने पर एनवीडिया और नैस्डैक 100 में जोरदार तेजी।",
            source = "CNBC Tech Markets",
            timeAgo = "22m ago",
            sentiment = StanceType.DOVISH,
            keyTakeawayHi = "टेक और सेमीकंडक्टर सेक्टर के लिए अनुकूल मौद्रिक माहौल।",
            tags = listOf("STOCKS", "US100", "NVDA")
        ),
        BreakingNewsItem(
            id = "news_5",
            titleEn = "FED WIRE: FOMC statement removes prior references to heightened inflation risks.",
            titleHi = "फेड वायर: एफओएमसी के नए बयान से अत्यधिक महंगाई जोखिम वाले पुराने संदर्भ हटाए गए।",
            source = "Federal Reserve Press",
            timeAgo = "35m ago",
            sentiment = StanceType.DOVISH,
            keyTakeawayHi = "नीतिगत रुख पूरी तरह ग्रोथ और रोजगार सपोर्ट की ओर शिफ्ट।",
            tags = listOf("MACRO", "FOMC", "POWELL")
        )
    )
}
