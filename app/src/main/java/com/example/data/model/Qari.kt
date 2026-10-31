package com.example.data.model

import android.content.Context
import com.example.R
import org.json.JSONArray
import java.io.InputStreamReader

data class Qari(
    val id: String,
    val name: String,
    val arabicName: String,
    val localDrawableResId: Int? = null,
    val serverUrl: String,
    val fallbackServerUrl: String = "",
    val totalSurahs: Int = 114,
    val location: String = "Saudi Arabia",
    val style: String = "Hafs An Assem",
    val isTop: Boolean = false,
    val isNew: Boolean = false
) {
    fun getSurahAudioUrl(surahNumber: Int): String {
        if (id == "mossad") {
            // Curated serene recitations for Abdul Rahman Mossad
            return when (surahNumber) {
                32 -> "https://archive.org/download/20210117_20210117_1138/032%20As-Sajda%20-%20%D8%A7%D9%84%D8%B3%D8%AC%D8%AF%D8%A9.mp3"
                73 -> "https://archive.org/download/20210117_20210117_1138/073%20Al-Muzzammil%20-%20%D8%A7%D9%84%D9%85%D8%B2%D9%85%D9%84.mp3"
                78 -> "https://archive.org/download/20210117_20210117_1138/078%20An-Naba%27%20-%20%D8%A7%D9%84%D9%86%D8%A8%D8%A3.mp3"
                88 -> "https://archive.org/download/20210117_20210117_1138/088%20Al-Ghaashiyah%20-%20%D8%A7%D9%84%D8%BA%D8%A7%D8%B4%D9%8A%D8%A9.mp3"
                100 -> "https://archive.org/download/20210117_20210117_1138/100%20%20Al-%27Aadiyaat%20-%20%D8%A7%D9%84%D8%B9%D8%A7%D8%AF%D9%8A%D8%A7%D8%AA.mp3"
                107 -> "https://archive.org/download/20210117_20210117_1138/107%20Al-Maa%27oon%20-%20%D8%A7%D9%84%D9%85%D8%A7%D8%B9%D9%88%D9%86.mp3"
                else -> {
                    // Continuous calm recitation session by Abdul Rahman Mossad
                    "https://archive.org/download/reciter-abdul-rahman-mossad-1/%D8%AA%D9%84%D8%A7%D9%88%D8%A9%20%D9%87%D8%A7%D8%AF%D8%A6%D8%A9%20%D8%A8%D8%B5%D9%88%D8%AA%20%D8%B9%D8%A8%D8%AF%D8%A7%D9%84%D8%B1%D8%AD%D9%85%D9%86%20%D9%85%D8%B3%D8%B9%D8%AF%20%F0%9F%96%A4%F0%9F%A4%8D%20%D8%B3%D8%A7%D8%B9%D8%A9%20%D9%83%D8%A7%D9%85%D9%84%D8%A9%20%F0%9F%95%9B%F0%9F%8E%A7%20%20_%20Reciter%20Abdul%20Rahman%20Mossad%20%281%29.mp4"
                }
            }
        }
        val formattedNum = String.format("%03d", surahNumber)
        val cleanServer = if (serverUrl.endsWith("/")) serverUrl else "$serverUrl/"
        return "$cleanServer$formattedNum.mp3"
    }
}

object QariCatalog {
    // Curated Tier-1 Renowned Qurra with dedicated real portraits
    val featuredQaris: List<Qari> = listOf(
        Qari(
            id = "yasser",
            name = "Yasser Al-Dosari",
            arabicName = "ياسر الدوسري",
            localDrawableResId = R.drawable.qari_yasser,
            serverUrl = "https://cdn.mp3quran.net/audio/yasser-dosari/r1/",
            fallbackServerUrl = "https://server11.mp3quran.net/yasser/",
            totalSurahs = 114,
            location = "Grand Mosque Imam, Mecca",
            style = "Heart-Touching Recitation",
            isTop = true
        ),
        Qari(
            id = "mossad",
            name = "Abdul Rahman Mossad",
            arabicName = "عبدالرحمن مسعد",
            localDrawableResId = R.drawable.qari_mossad,
            serverUrl = "https://archive.org/download/20210117_20210117_1138/",
            fallbackServerUrl = "https://archive.org/download/reciter-abdul-rahman-mossad-1/",
            totalSurahs = 114,
            location = "Saudi Arabia / Egypt",
            style = "Deep Calm & Reverence",
            isTop = true,
            isNew = true
        ),
        Qari(
            id = "luhaidan",
            name = "Muhammad Al-Luhaidan",
            arabicName = "محمد اللحيدان",
            localDrawableResId = R.drawable.qari_luhaidan,
            serverUrl = "https://cdn.mp3quran.net/audio/muhammad-luhaidan/r1/",
            fallbackServerUrl = "https://download.quranicaudio.com/quran/muhammad_alhaidan/",
            totalSurahs = 114,
            location = "Al-Nasir Mosque Imam, Riyadh",
            style = "Emotional & Soul-Stirring",
            isTop = true,
            isNew = true
        ),
        Qari(
            id = "mishary",
            name = "Mishary Alafasy",
            arabicName = "مشاري راشد العفاسي",
            localDrawableResId = R.drawable.qari_mishary,
            serverUrl = "https://cdn.mp3quran.net/audio/mishary-alafasy/r1/",
            fallbackServerUrl = "https://server8.mp3quran.net/afs/",
            totalSurahs = 114,
            location = "Kuwait",
            style = "Melodic & Renowned",
            isTop = true
        ),
        Qari(
            id = "sudais",
            name = "Abdulrahman Al-Sudais",
            arabicName = "عبدالرحمن السديس",
            localDrawableResId = R.drawable.qari_sudais,
            serverUrl = "https://cdn.mp3quran.net/audio/abdulrahman-sudais/r1/",
            fallbackServerUrl = "https://server11.mp3quran.net/sds/",
            totalSurahs = 114,
            location = "Mecca (Grand Mosque Imam)",
            style = "Grand Mosque Cadence",
            isTop = true
        ),
        Qari(
            id = "maher",
            name = "Maher Al-Muaiqly",
            arabicName = "ماهر المعيقلي",
            localDrawableResId = R.drawable.qari_maher,
            serverUrl = "https://cdn.mp3quran.net/audio/maher-muaiqly/r1/",
            fallbackServerUrl = "https://server12.mp3quran.net/maher/",
            totalSurahs = 114,
            location = "Grand Mosque, Mecca",
            style = "Crystal Clarity & Warmth",
            isTop = true
        ),
        Qari(
            id = "shuraim",
            name = "Saud Al-Shuraim",
            arabicName = "سعود الشريم",
            localDrawableResId = R.drawable.qari_shuraim,
            serverUrl = "https://cdn.mp3quran.net/audio/saud-shuraim/r1/",
            fallbackServerUrl = "https://server11.mp3quran.net/shur/",
            totalSurahs = 114,
            location = "Mecca",
            style = "Rhythmic & Reverent"
        ),
        Qari(
            id = "ghamdi",
            name = "Saad Al-Ghamdi",
            arabicName = "سعد الغامدي",
            localDrawableResId = R.drawable.qari_ghamdi,
            serverUrl = "https://cdn.mp3quran.net/audio/saad-ghamdi/r1/",
            fallbackServerUrl = "https://server7.mp3quran.net/s_gmd/",
            totalSurahs = 114,
            location = "Dammam, Saudi Arabia",
            style = "Meditative Pace"
        ),
        Qari(
            id = "minshawi",
            name = "Mohamed Siddiq Al-Minshawi",
            arabicName = "محمد صديق المنشاوي",
            localDrawableResId = R.drawable.qari_minshawi,
            serverUrl = "https://cdn.mp3quran.net/audio/muhammad-minshawi/r1/",
            totalSurahs = 114,
            location = "Egypt",
            style = "Golden Era Classic",
            isTop = true
        ),
        Qari(
            id = "ajmi",
            name = "Ahmad Al-Ajmi",
            arabicName = "أحمد بن علي العجمي",
            localDrawableResId = R.drawable.qari_ajmi,
            serverUrl = "https://cdn.mp3quran.net/audio/ahmad-ajmi/r1/",
            fallbackServerUrl = "https://server10.mp3quran.net/ajm/",
            totalSurahs = 114,
            location = "Khobar, Saudi Arabia",
            style = "Solemn & Inspiring",
            isNew = true
        ),
        Qari(
            id = "oosi",
            name = "Abdulrahman Al-Oosi",
            arabicName = "عبدالرحمن العوسي",
            localDrawableResId = R.drawable.qari_oosi,
            serverUrl = "https://cdn.mp3quran.net/audio/abdulrahman-oosi/r1/",
            totalSurahs = 114,
            location = "Khobar, Saudi Arabia",
            style = "Peaceful & Calming",
            isNew = true
        )
    )

    val defaultQari: Qari = featuredQaris[0]

    private var cachedAllQaris: List<Qari>? = null

    // Load full 100+ Reciters catalog from assets (matching Quranify's 100+ reciters)
    fun getAllQaris(context: Context): List<Qari> {
        cachedAllQaris?.let { return it }

        val combined = mutableListOf<Qari>()
        combined.addAll(featuredQaris)

        try {
            context.assets.open("reciters.json").use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    val jsonString = reader.readText()
                    val array = JSONArray(jsonString)
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        val id = obj.getString("id")
                        // If already in featured, skip duplicate
                        if (combined.none { it.id == id }) {
                            combined.add(
                                Qari(
                                    id = id,
                                    name = obj.getString("name"),
                                    arabicName = obj.optString("arabicName", obj.getString("name")),
                                    localDrawableResId = null, // Will use stylized calligraphic initial avatar
                                    serverUrl = obj.getString("serverUrl"),
                                    totalSurahs = obj.optInt("totalSurahs", 114),
                                    style = obj.optString("style", "Hafs An Assem"),
                                    location = "Islamic World"
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        cachedAllQaris = combined
        return combined
    }

    fun getById(id: String, context: Context? = null): Qari {
        featuredQaris.firstOrNull { it.id == id }?.let { return it }
        if (context != null) {
            getAllQaris(context).firstOrNull { it.id == id }?.let { return it }
        }
        return defaultQari
    }
}
