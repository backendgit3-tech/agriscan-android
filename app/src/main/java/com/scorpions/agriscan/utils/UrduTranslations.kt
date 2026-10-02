package com.scorpions.agriscan.utils

object UrduTranslations {

    val diseaseNames = mapOf(
        "Tomato Early Blight" to "ٹماٹر ارلی بلائٹ",
        "Tomato Late Blight" to "ٹماٹر لیٹ بلائٹ",
        "Tomato Bacterial Spot" to "ٹماٹر بیکٹیریل دھبہ",
        "Tomato Healthy" to "ٹماٹر صحت مند",
        "Potato Early Blight" to "آلو ارلی بلائٹ",
        "Potato Late Blight" to "آلو لیٹ بلائٹ",
        "Potato Healthy" to "آلو صحت مند"
    )

    val uiStrings = mapOf(
        "home" to "ہوم",
        "diagnose" to "تشخیص",
        "history" to "تاریخ",
        "about" to "تعارف",
        "scan_leaf" to "پتا اسکین کریں",
        "take_photo" to "تصویر لیں",
        "choose_gallery" to "گیلری سے منتخب کریں",
        "local_ai" to "مقامی AI",
        "hybrid_ai" to "ہائبرڈ AI",
        "qwen_ai" to "کیون AI",
        "analyze" to "تجزیہ کریں",
        "diagnosis_complete" to "تشخیص مکمل",
        "confidence" to "اعتماد",
        "treatment" to "علاج",
        "ai_source" to "AI ماخذ",
        "retry" to "دوبارہ کوشش",
        "server_unavailable" to "سرور دستیاب نہیں",
        "no_history" to "کوئی تاریخ نہیں",
        "scan_another" to "ایک اور پتا اسکین کریں",
        "low_confidence" to "کم اعتماد",
        "try_clearer_photo" to "براہ کرم صاف تصویر لیں",
        "recent_scans" to "حالیہ اسکین",
        "export_csv" to "CSV ایکسپورٹ",
        "language" to "زبان",
        "login" to "لاگ ان",
        "register" to "رجسٹر",
        "phone" to "فون نمبر",
        "password" to "پاس ورڈ",
        "full_name" to "پورا نام",
        "district" to "ضلع",
        "logout" to "لاگ آؤٹ",
        "your_ai_assistant" to "آپ کا AI فصل معاون",
        "tagline" to "اسکین کریں۔ سمجھیں۔ عمل کریں۔ اگائیں۔"
    )

    fun translateDisease(english: String): String {
        return diseaseNames[english] ?: english
    }

    fun ui(key: String): String {
        return uiStrings[key] ?: key
    }
}
