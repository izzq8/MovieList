package com.faizabhinaya.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import java.util.Locale

object LocaleHelper {
    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"

    fun setLocale(context: Context, language: String): Context {
        persist(context, language)

        val locale = Locale(language)
        Locale.setDefault(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, locale)
        } else {
            updateResourcesLegacy(context, locale)
        }
    }

    private fun persist(context: Context, language: String) {
        val preferences = context.getSharedPreferences("language_settings", Context.MODE_PRIVATE)
        preferences.edit().putString(SELECTED_LANGUAGE, language).apply()
    }

    fun getLanguage(context: Context): String {
        val preferences = context.getSharedPreferences("language_settings", Context.MODE_PRIVATE)
        return preferences.getString(SELECTED_LANGUAGE, Locale.getDefault().language) ?: "en"
    }

    private fun updateResources(context: Context, locale: Locale): Context {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return context.createConfigurationContext(configuration)
    }

    @Suppress("DEPRECATION")
    private fun updateResourcesLegacy(context: Context, locale: Locale): Context {
        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        configuration.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale)
        }
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }
}
