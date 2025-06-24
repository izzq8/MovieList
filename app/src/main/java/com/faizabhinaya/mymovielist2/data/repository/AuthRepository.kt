package com.faizabhinaya.mymovielist2.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    val isUserLoggedIn: Boolean
        get() = firebaseAuth.currentUser != null

    suspend fun signIn(email: String, password: String): FirebaseUser? = withContext(Dispatchers.IO) {
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun signUp(email: String, password: String): FirebaseUser? = withContext(Dispatchers.IO) {
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun resetPassword(email: String) = withContext(Dispatchers.IO) {
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            throw e
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    // Fungsi baru untuk memperbarui profil pengguna
    suspend fun updateProfile(displayName: String, photoUrl: String?) = withContext(Dispatchers.IO) {
        try {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .apply {
                    photoUrl?.let { setPhotoUri(android.net.Uri.parse(it)) }
                }
                .build()

            currentUser?.updateProfile(profileUpdates)?.await()

            // Update Firestore
            currentUser?.let { user ->
                val userData = hashMapOf<String, Any>()
                if (displayName.isNotEmpty()) {
                    userData["displayName"] = displayName
                }
                if (photoUrl != null) {
                    userData["photoBase64"] = photoUrl
                }
                usersCollection.document(user.uid).set(userData, SetOptions.merge()).await()
            }

            true
        } catch (e: Exception) {
            throw e
        }
    }

    // Fungsi untuk memperbarui password
    suspend fun updatePassword(newPassword: String) = withContext(Dispatchers.IO) {
        try {
            currentUser?.updatePassword(newPassword)?.await()
            true
        } catch (e: Exception) {
            throw e
        }
    }

    // Fungsi untuk melakukan re-autentikasi (diperlukan untuk operasi sensitif)
    suspend fun reauthenticate(password: String) = withContext(Dispatchers.IO) {
        try {
            val credential = com.google.firebase.auth.EmailAuthProvider
                .getCredential(currentUser?.email ?: "", password)
            currentUser?.reauthenticate(credential)?.await()
            true
        } catch (e: Exception) {
            throw e
        }
    }

    // Fungsi untuk menghapus akun pengguna
    suspend fun deleteAccount() = withContext(Dispatchers.IO) {
        try {
            currentUser?.delete()?.await()
            true
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getUserData(): Map<String, Any>? = withContext(Dispatchers.IO) {
        try {
            currentUser?.let { user ->
                val document = usersCollection.document(user.uid).get().await()
                if (document.exists()) {
                    return@withContext document.data
                }
            }
            null
        } catch (e: Exception) {
            throw e
        }
    }
}
