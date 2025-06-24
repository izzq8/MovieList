package com.faizabhinaya.mymovielist2.ui.screens.search

import android.content.Context

object SearchHistoryManager {
    private const val PREFS_NAME = "search_history_prefs"
    private const val KEY_HISTORY = "search_history_list"
    private const val MAX_HISTORY = 10

    fun saveQuery(context: Context, query: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val history = getHistory(context).toMutableList()
        history.remove(query)
        history.add(0, query)
        if (history.size > MAX_HISTORY) history.removeAt(history.lastIndex)
        prefs.edit().putStringSet(KEY_HISTORY, history.toSet()).apply()
    }

    fun getHistory(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_HISTORY, emptySet())
            ?.toList()
            ?.sortedByDescending { prefs.getLong(it, 0L) }
            ?: emptyList()
    }

    fun clearHistory(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_HISTORY).apply()
    }

    fun removeQuery(context: Context, query: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val history = getHistory(context).toMutableList()
        history.remove(query)
        prefs.edit().putStringSet(KEY_HISTORY, history.toSet()).apply()
    }
}
