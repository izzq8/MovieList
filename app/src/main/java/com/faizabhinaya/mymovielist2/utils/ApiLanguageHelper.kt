package com.faizabhinaya.mymovielist2.utils

import android.content.Context
import com.faizabhinaya.utils.LocaleHelper

/**
 * Helper class to get the appropriate language code for API requests
 * based on the user's selected app language.
 */
object ApiLanguageHelper {
    /**
     * Get the language code to use for API requests based on the user's selected language.
     * For TMDB API, language codes are formatted as [language code]-[country code]
     * For example: en-US, id-ID
     */
    fun getApiLanguageCode(context: Context): String {
        return when (LocaleHelper.getLanguage(context)) {
            "in" -> "id-ID" // Indonesian language code for TMDB API is "id-ID"
            else -> "en-US" // Default to English
        }
    }
}
