package com.example.data

object FedSpeechFeed {

    val scenarios: List<FedSpeechScenario> = listOf(
        FedSpeechScenario(
            id = "warsh_liquidity_framework",
            title = "US Fed Chairman Kevin Warsh: Policy & Liquidity Framework",
            titleHi = "अमेरिकी फेड चेयरमैन केविन मैक्सवेल वार्श: मौद्रिक नीति और लिक्विडिटी फ्रेमवर्क",
            speaker = "Kevin Maxwell Warsh (US Fed Chairman)",
            eventName = "Federal Reserve Board Policy Address - Washington D.C.",
            description = "US Fed Chairman Kevin Maxwell Warsh outlines economic growth trajectory, liquidity normalization, and interest rate stability.",
            defaultStance = StanceType.NEUTRAL,
            chunks = listOf(
                SpeechTranslationChunk(
                    id = "kmw_1",
                    orderIndex = 1,
                    englishText = "Good morning. As Chairman of the Federal Reserve, our mandate is clear: maintain price stability, promote disciplined economic expansion, and safeguard dollar credibility.",
                    hindiTranslation = "शुभ प्रभात। फेडरल रिजर्व के अध्यक्ष (US Fed Chairman) के रूप में, हमारा जनादेश स्पष्ट है: मूल्य स्थिरता बनाए रखना, अनुशासित आर्थिक विकास को बढ़ावा देना और डॉलर की साख की रक्षा करना।",
                    timestampFormatted = "00:15",
                    stance = StanceType.NEUTRAL,
                    keyTakeawayHi = "फेड चेयरमैन केविन वार्श: डॉलर की साख और मूल्य स्थिरता पर सर्वोच्च प्राथमिकता।"
                ),
                SpeechTranslationChunk(
                    id = "kmw_2",
                    orderIndex = 2,
                    englishText = "We are closely monitoring financial asset markets, credit spreads, and global currency liquidity across forex and sovereign debt channels.",
                    hindiTranslation = "हम विदेशी मुद्रा (Forex), बॉन्ड यील्ड और वैश्विक वित्तीय परिसंपत्ति बाजारों में लिक्विडिटी और क्रेडिट स्प्रेड की बारीकी से निगरानी कर रहे हैं।",
                    timestampFormatted = "00:50",
                    stance = StanceType.DOVISH,
                    keyTakeawayHi = "ग्लोबल लिक्विडिटी और वित्तीय बाजारों की स्थिरता पर फेड की मजबूत नजर।"
                ),
                SpeechTranslationChunk(
                    id = "kmw_3",
                    orderIndex = 3,
                    englishText = "Prudent monetary strategy requires proactive balance sheet management to foster sustainable private sector investment and productivity gains.",
                    hindiTranslation = "विवेकपूर्ण मौद्रिक नीति के लिए निजी क्षेत्र के निवेश और उत्पादकता वृद्धि को बढ़ावा देने हेतु प्रोएक्टिव बैलेंस शीट प्रबंधन आवश्यक है।",
                    timestampFormatted = "01:25",
                    stance = StanceType.DOVISH,
                    keyTakeawayHi = "उत्पादकता और निजी निवेश बढ़ाने के लिए संतुलित मौद्रिक दृष्टिकोण।"
                ),
                SpeechTranslationChunk(
                    id = "kmw_4",
                    orderIndex = 4,
                    englishText = "Should inflation risks accelerate, the Committee stands ready to adjust open market operations swiftly to anchor long-term expectations.",
                    hindiTranslation = "यदि मुद्रास्फीति के जोखिम बढ़ते हैं, तो ओपन मार्केट कमेटी दीर्घकालिक अपेक्षाओं को नियंत्रित करने के लिए तुरंत नीतिगत कदम उठाने को तैयार है।",
                    timestampFormatted = "02:00",
                    stance = StanceType.HAWKISH,
                    keyTakeawayHi = "मुद्रास्फीति बढ़ने पर सख्त कार्रवाई के लिए फेड पूरी तरह सतर्क।"
                )
            )
        ),
        FedSpeechScenario(
            id = "warsh_growth_rates",
            title = "Kevin Warsh: Strategic Rates & Capital Market Health",
            titleHi = "केविन वार्श: रणनीतिक ब्याज दरें और पूंजी बाजार स्थिरता",
            speaker = "Kevin Maxwell Warsh (US Fed Chairman)",
            eventName = "Economic Club of New York Keynote",
            description = "Fed Chair Kevin Maxwell Warsh delivers comprehensive guidance on interest rate trajectory and market liquidity.",
            defaultStance = StanceType.DOVISH,
            chunks = listOf(
                SpeechTranslationChunk(
                    id = "kmw_b1",
                    orderIndex = 1,
                    englishText = "The American financial architecture remains exceptionally robust, and monetary policy must encourage capital formation and technological competitiveness.",
                    hindiTranslation = "अमेरिकी वित्तीय ढांचा बेहद मजबूत है, और मौद्रic नीति को पूंजी निर्माण और तकनीकी प्रतिस्पर्धा को प्रोत्साहित करना चाहिए।",
                    timestampFormatted = "00:12",
                    stance = StanceType.DOVISH,
                    keyTakeawayHi = "फेड चेयरमैन: पूंजी निर्माण और बाजार प्रतिस्पर्धा को प्रोत्साहन।"
                ),
                SpeechTranslationChunk(
                    id = "kmw_b2",
                    orderIndex = 2,
                    englishText = "We see meaningful stabilization in core price indicators, allowing for calibrated flexibility in our benchmark policy rate corridor.",
                    hindiTranslation = "हम कोर मूल्य संकेतकों में सार्थक स्थिरता देख रहे हैं, जिससे हमारी बेंचमार्क नीतिगत ब्याज दर सीमा में लचीलापन संभव हुआ है।",
                    timestampFormatted = "00:48",
                    stance = StanceType.DOVISH,
                    keyTakeawayHi = "बेंचमार्क ब्याज दरों में लचीलेपन और स्थिरता के सकारात्मक संकेत।"
                ),
                SpeechTranslationChunk(
                    id = "kmw_b3",
                    orderIndex = 3,
                    englishText = "Central bank credibility relies on transparent communication with market participants, ensuring predictable liquidity across global trading desks.",
                    hindiTranslation = "केंद्रीय बैंक की विश्वसनीयता बाजार सहभागियों के साथ पारदर्शी संचार और वैश्विक व्यापारिक डेस्क पर पूर्वानुमेय लिक्विडिटी पर निर्भर करती है।",
                    timestampFormatted = "01:30",
                    stance = StanceType.NEUTRAL,
                    keyTakeawayHi = "बाजार के लिए पारदर्शी नीतियां और स्पष्ट लिक्विडिटी मार्गदर्शन।"
                )
            )
        ),
        FedSpeechScenario(
            id = "fomc_rate_cut",
            title = "FOMC Rate Cut & Economic Projections",
            titleHi = "एफओएमसी ब्याज दर कटौती और आर्थिक दृष्टिकोण",
            speaker = "Jerome Powell (Fed Chairman)",
            eventName = "FOMC Press Conference - Washington D.C.",
            description = "Fed Chairman discusses inflation trajectory, labor market normalization, and 50bps rate easing pathway.",
            defaultStance = StanceType.DOVISH,
            chunks = listOf(
                SpeechTranslationChunk(
                    id = "fomc_1",
                    orderIndex = 1,
                    englishText = "Good afternoon. Today, the Federal Open Market Committee decided to lower our policy interest rate by 50 basis points to support maximum employment.",
                    hindiTranslation = "शुभ दोपहर। आज, फेडरल ओपन मार्केट कमेटी ने रोजगार को अधिकतम स्तर पर बनाए रखने के लिए अपनी नीतिगत ब्याज दर में 50 आधार अंकों (0.50%) की कटौती करने का निर्णय लिया है।",
                    timestampFormatted = "00:15",
                    stance = StanceType.DOVISH,
                    keyTakeawayHi = "फेड ने 50 bps की बड़ी दर कटौती की, विकास को प्राथमिकता दी।"
                ),
                SpeechTranslationChunk(
                    id = "fomc_2",
                    orderIndex = 2,
                    englishText = "Inflation has eased substantially toward our 2 percent longer-run objective, though it remains somewhat elevated.",
                    hindiTranslation = "महंगाई दर हमारे 2 प्रतिशत के दीर्घकालिक लक्ष्य की ओर काफी हद तक कम हुई है, हालांकि यह अभी भी थोड़ी ऊपर बनी हुई है।",
                    timestampFormatted = "00:45",
                    stance = StanceType.DOVISH,
                    keyTakeawayHi = "महंगाई 2% लक्ष्य के करीब आ रही है।"
                ),
                SpeechTranslationChunk(
                    id = "fomc_3",
                    orderIndex = 3,
                    englishText = "The labor market is no longer overheated; conditions are now less tight than those that prevailed before the pandemic.",
                    hindiTranslation = "लेबर मार्केट अब अत्यधिक गर्म नहीं है; रोजगार की स्थितियां अब महामारी से पहले के सामान्य स्तर पर आ गई हैं।",
                    timestampFormatted = "01:20",
                    stance = StanceType.NEUTRAL,
                    keyTakeawayHi = "नौकरियों का बाजार स्थिर हो रहा है।"
                ),
                SpeechTranslationChunk(
                    id = "fomc_4",
                    orderIndex = 4,
                    englishText = "We are confident that with an appropriate recalibration of our monetary policy stance, strength in the labor market can be maintained.",
                    hindiTranslation = "हमें पूरा विश्वास है कि हमारी मौद्रिक नीति में इस समायोजन से रोजगार बाजार की मजबूती को बरकरार रखा जा सकता है।",
                    timestampFormatted = "01:55",
                    stance = StanceType.DOVISH,
                    keyTakeawayHi = "आर्थिक मंदी रोकने के लिए नीति में ढील जारी रहेगी।"
                ),
                SpeechTranslationChunk(
                    id = "fomc_5",
                    orderIndex = 5,
                    englishText = "We will continue to determine the pace and extent of future adjustments based on incoming data, the evolving outlook, and the balance of risks.",
                    hindiTranslation = "हम भविष्य की ब्याज दरों के फैसलों को आने वाले आर्थिक आंकड़ों, बदलते परिदृश्य और जोखिमों के संतुलन के आधार पर ही तय करेंगे।",
                    timestampFormatted = "02:30",
                    stance = StanceType.NEUTRAL,
                    keyTakeawayHi = "अगली कटौती डेटा (CPI/Jobs) पर निर्भर करेगी।"
                )
            )
        ),
        FedSpeechScenario(
            id = "jackson_hole",
            title = "Jackson Hole: 'The Time Has Come'",
            titleHi = "जैकसन होल संबोधन: नीतिगत बदलाव का समय आ गया है",
            speaker = "Jerome Powell (Fed Chairman)",
            eventName = "Jackson Hole Economic Policy Symposium",
            description = "Landmark speech signaling monetary policy pivot and inflation victory roadmap.",
            defaultStance = StanceType.DOVISH,
            chunks = listOf(
                SpeechTranslationChunk(
                    id = "jh_1",
                    orderIndex = 1,
                    englishText = "The time has come for policy to adjust. The direction of travel is clear, and the timing and pace of rate cuts will depend on incoming data.",
                    hindiTranslation = "अब मौद्रिक नीति में बदलाव करने का समय आ गया है। हमारी दिशा बिल्कुल स्पष्ट है, और ब्याज दरों में कटौती का समय व गति आने वाले आंकड़ों पर निर्भर करेगी।",
                    timestampFormatted = "00:10",
                    stance = StanceType.DOVISH,
                    keyTakeawayHi = "स्पष्ट संदेश: ब्याज दरों में कटौती का चक्र शुरू हो गया है।"
                ),
                SpeechTranslationChunk(
                    id = "jh_2",
                    orderIndex = 2,
                    englishText = "My confidence has grown that inflation is on a sustainable path back to 2 percent.",
                    hindiTranslation = "मेरा यह विश्वास और मजबूत हुआ है कि महंगाई दर लगातार 2 प्रतिशत के लक्ष्य पर वापस आने की राह पर है।",
                    timestampFormatted = "00:40",
                    stance = StanceType.DOVISH,
                    keyTakeawayHi = "महंगाई नियंत्रण में पूरी सफलता का भरोसा।"
                ),
                SpeechTranslationChunk(
                    id = "jh_3",
                    orderIndex = 3,
                    englishText = "We do not seek or welcome further cooling in labor market conditions.",
                    hindiTranslation = "हम लेबर मार्केट में और अधिक गिरावट या नौकरियों में किसी भी तरह की कमजोरी का स्वागत नहीं करेंगे।",
                    timestampFormatted = "01:10",
                    stance = StanceType.DOVISH,
                    keyTakeawayHi = "फेड नौकरियों के नुकसान को बर्दाश्त नहीं करेगा।"
                ),
                SpeechTranslationChunk(
                    id = "jh_4",
                    orderIndex = 4,
                    englishText = "Overall, the economy continues to grow at a solid pace, but we are well-positioned to respond to any emerging risks.",
                    hindiTranslation = "कुल मिलाकर, अर्थव्यवस्था मजबूत गति से आगे बढ़ रही है, और हम किसी भी उभरते जोखिम से निपटने के लिए पूरी तरह तैयार हैं।",
                    timestampFormatted = "01:40",
                    stance = StanceType.NEUTRAL,
                    keyTakeawayHi = "अमेरिकी अर्थव्यवस्था में मंदी की कोई तात्कालिक आशंका नहीं।"
                )
            )
        ),
        FedSpeechScenario(
            id = "cpi_sticky",
            title = "CPI Presser: Higher for Longer Caution",
            titleHi = "मुद्रास्फीति प्रेस कॉन्फ्रेंस: सतर्क रुख और सख्त नीति",
            speaker = "Jerome Powell (Fed Chairman)",
            eventName = "Federal Reserve Board Briefing",
            description = "Hawkish remarks responding to sticky Core CPI numbers and disciplined tightening.",
            defaultStance = StanceType.HAWKISH,
            chunks = listOf(
                SpeechTranslationChunk(
                    id = "cpi_1",
                    orderIndex = 1,
                    englishText = "Recent monthly readings on consumer price inflation have shown a lack of further progress toward our 2 percent objective.",
                    hindiTranslation = "उपभोक्ता मूल्य सूचकांक (CPI) के हालिया मासिक आंकड़ों से पता चलता है कि हमारे 2% के लक्ष्य की ओर अपेक्षित प्रगति नहीं हुई है।",
                    timestampFormatted = "00:15",
                    stance = StanceType.HAWKISH,
                    keyTakeawayHi = "महंगाई अभी भी जिद्दी है, कटौती में देरी संभव।"
                ),
                SpeechTranslationChunk(
                    id = "cpi_2",
                    orderIndex = 2,
                    englishText = "If inflation persists, we are prepared to maintain the current restrictive level of policy interest rates for as long as needed.",
                    hindiTranslation = "यदि महंगाई इसी तरह बनी रहती है, तो हम जब तक आवश्यक हो वर्तमान उच्च और प्रतिबंधात्मक ब्याज दरों को बनाए रखने के लिए तैयार हैं।",
                    timestampFormatted = "00:50",
                    stance = StanceType.HAWKISH,
                    keyTakeawayHi = "ब्याज दरें लंबे समय तक ऊंची (Higher for Longer) रहेंगी।"
                ),
                SpeechTranslationChunk(
                    id = "cpi_3",
                    orderIndex = 3,
                    englishText = "Restoring price stability is essential to set the stage for achieving maximum employment and stable prices over the longer run.",
                    hindiTranslation = "दीर्घकाल में मजबूत रोजगार और स्थिर कीमतें हासिल करने के लिए सबसे पहले कीमतों में स्थिरता लाना अत्यंत आवश्यक है।",
                    timestampFormatted = "01:25",
                    stance = StanceType.HAWKISH,
                    keyTakeawayHi = "पहला लक्ष्य महंगाई को पूरी तरह कुचलना है।"
                )
            )
        )
    )

    val quickVoiceQuestions = listOf(
        "US Fed Chairman Kevin Maxwell Warsh ka policy stance kya hai?",
        "Kevin Warsh ke statements se Gold (XAU/USD) par kya asar padega?",
        "Bitcoin aur Crypto par Fed Chairman ke faisle ka prabhav?",
        "USD/INR aur Forex market par Kevin Warsh ki liquidity policy ka asar?",
        "Agli FOMC meeting mein interest rates aur CPI projections kya hain?"
    )
}
