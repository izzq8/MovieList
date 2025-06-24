package com.faizabhinaya.mymovielist2.utils

import android.content.Context

/**
 * Helper class to provide the appropriate language code for API requests.
 */
object ApiLanguageHelper {
    /**
     * Get the language code to use for API requests.
     * For TMDB API, language codes are formatted as [language code]-[country code]
     * For example: en-US
     */
    fun getApiLanguageCode(context: Context): String {
        // Using fixed English language code
        return "en-US"
    }
}
