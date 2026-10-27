package com.vztechcore.vzsync.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.vztechcore.vzsync.domain.UserProfile

class SecurePrefs private constructor(private val prefs: SharedPreferences) {

    fun saveAuth(username: String, token: String) {
        prefs.edit()
            .putString(KEY_USERNAME, username)
            .putString(KEY_TOKEN, token)
            .apply()
    }

    fun saveProfile(profile: UserProfile) {
        prefs.edit()
            .putString(KEY_DISPLAY_NAME, profile.displayName)
            .putString(KEY_AVATAR_URL, profile.avatarUrl)
            .putString(KEY_BIO, profile.bio)
            .putInt(KEY_PUBLIC_REPOS, profile.publicRepos)
            .putInt(KEY_FOLLOWERS, profile.followers)
            .putInt(KEY_FOLLOWING, profile.following)
            .putString(KEY_CREATED_AT, profile.createdAt)
            .apply()
    }

    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)
    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getCachedProfile(): UserProfile? {
        val username = getUsername() ?: return null
        val displayName = prefs.getString(KEY_DISPLAY_NAME, null) ?: return null
        val avatarUrl = prefs.getString(KEY_AVATAR_URL, null) ?: return null
        return UserProfile(
            username = username,
            displayName = displayName,
            avatarUrl = avatarUrl,
            bio = prefs.getString(KEY_BIO, null),
            publicRepos = prefs.getInt(KEY_PUBLIC_REPOS, 0),
            followers = prefs.getInt(KEY_FOLLOWERS, 0),
            following = prefs.getInt(KEY_FOLLOWING, 0),
            createdAt = prefs.getString(KEY_CREATED_AT, null),
        )
    }

    fun saveFolder(uri: String, name: String, path: String?) {
        prefs.edit()
            .putString(KEY_FOLDER_URI, uri)
            .putString(KEY_FOLDER_NAME, name)
            .putString(KEY_FOLDER_PATH, path)
            .apply()
    }

    fun getFolderUri(): String? = prefs.getString(KEY_FOLDER_URI, null)
    fun getFolderName(): String? = prefs.getString(KEY_FOLDER_NAME, null)
    fun getFolderPath(): String? = prefs.getString(KEY_FOLDER_PATH, null)

    fun clearAuth() {
        prefs.edit()
            .remove(KEY_USERNAME)
            .remove(KEY_TOKEN)
            .remove(KEY_DISPLAY_NAME)
            .remove(KEY_AVATAR_URL)
            .remove(KEY_BIO)
            .remove(KEY_PUBLIC_REPOS)
            .remove(KEY_FOLLOWERS)
            .remove(KEY_FOLLOWING)
            .remove(KEY_CREATED_AT)
            .apply()
    }

    fun clearFolder() {
        prefs.edit()
            .remove(KEY_FOLDER_URI)
            .remove(KEY_FOLDER_NAME)
            .remove(KEY_FOLDER_PATH)
            .apply()
    }

    companion object {
        private const val PREFS_FILE = "vz_secure_prefs"
        private const val KEY_USERNAME = "username"
        private const val KEY_TOKEN = "token"
        private const val KEY_DISPLAY_NAME = "display_name"
        private const val KEY_AVATAR_URL = "avatar_url"
        private const val KEY_BIO = "bio"
        private const val KEY_PUBLIC_REPOS = "public_repos"
        private const val KEY_FOLLOWERS = "followers"
        private const val KEY_FOLLOWING = "following"
        private const val KEY_CREATED_AT = "created_at"
        private const val KEY_FOLDER_URI = "folder_uri"
        private const val KEY_FOLDER_NAME = "folder_name"
        private const val KEY_FOLDER_PATH = "folder_path"

        @Volatile
        private var instance: SecurePrefs? = null

        fun get(context: Context): SecurePrefs {
            return instance ?: synchronized(this) {
                instance ?: create(context.applicationContext).also { instance = it }
            }
        }

        private fun create(context: Context): SecurePrefs {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val prefs = EncryptedSharedPreferences.create(
                context,
                PREFS_FILE,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
            return SecurePrefs(prefs)
        }
    }
}