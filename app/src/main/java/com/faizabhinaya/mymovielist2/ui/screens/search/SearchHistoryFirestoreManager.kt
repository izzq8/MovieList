package com.faizabhinaya.mymovielist2.ui.screens.search

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object SearchHistoryFirestoreManager {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserId(): String {
        val user = auth.currentUser
        return user?.uid ?: run {
            val result = auth.signInAnonymously()
            result.result?.user?.uid ?: "anonymous"
        }
    }

    suspend fun saveQuery(query: String) {
        val userId = getUserId()
        val docRef = firestore.collection("users").document(userId).collection("search_history").document(query)
        val data = mapOf("query" to query, "timestamp" to System.currentTimeMillis())
        docRef.set(data).await()
    }

    suspend fun getHistory(limit: Long = 10): List<String> {
        val userId = getUserId()
        val snapshot = firestore.collection("users").document(userId)
            .collection("search_history")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(limit)
            .get().await()
        return snapshot.documents.mapNotNull { it.getString("query") }
    }

    suspend fun removeQuery(query: String) {
        val userId = getUserId()
        firestore.collection("users").document(userId)
            .collection("search_history").document(query).delete().await()
    }

    suspend fun clearHistory() {
        val userId = getUserId()
        val collection = firestore.collection("users").document(userId).collection("search_history")
        val snapshot = collection.get().await()
        snapshot.documents.forEach { it.reference.delete().await() }
    }
}

