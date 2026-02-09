package com.foodchain.iotsolution.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.foodchain.iotsolution.core.constants.AppConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = AppConstants.DATASTORE_NAME)

@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_PROFILE_IMAGE = stringPreferencesKey("user_profile_image")
        val MQTT_BROKER_URL = stringPreferencesKey("mqtt_broker_url")
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val LAST_LATITUDE = stringPreferencesKey("last_latitude")
        val LAST_LONGITUDE = stringPreferencesKey("last_longitude")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DEVICE_ALERTS_ENABLED = booleanPreferencesKey("device_alerts_enabled")
        val CONNECTION_ALERTS_ENABLED = booleanPreferencesKey("connection_alerts_enabled")
    }

    val authToken: Flow<String?> = context.dataStore.data.map { it[AUTH_TOKEN] }

    val isAuthenticated: Flow<Boolean> = context.dataStore.data.map { prefs ->
        !prefs[AUTH_TOKEN].isNullOrEmpty()
    }

    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_DARK_THEME] ?: false
    }

    val mqttBrokerUrl: Flow<String?> = context.dataStore.data.map { it[MQTT_BROKER_URL] }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[NOTIFICATIONS_ENABLED] ?: true
    }

    val deviceAlertsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[DEVICE_ALERTS_ENABLED] ?: true
    }

    val connectionAlertsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[CONNECTION_ALERTS_ENABLED] ?: true
    }

    suspend fun getAuthToken(): String? {
        return context.dataStore.data.first()[AUTH_TOKEN]
    }

    suspend fun saveAuthTokens(token: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[AUTH_TOKEN] = token
            prefs[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun saveUserData(id: String, email: String, name: String, profileImage: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = id
            prefs[USER_EMAIL] = email
            prefs[USER_NAME] = name
            prefs[USER_PROFILE_IMAGE] = profileImage
        }
    }

    suspend fun getUserName(): String? {
        return context.dataStore.data.first()[USER_NAME]
    }

    suspend fun getUserEmail(): String? {
        return context.dataStore.data.first()[USER_EMAIL]
    }

    suspend fun saveMqttBrokerUrl(url: String) {
        context.dataStore.edit { prefs ->
            prefs[MQTT_BROKER_URL] = url
        }
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_DARK_THEME] = isDark
        }
    }

    suspend fun saveLastLocation(lat: Double, lon: Double) {
        context.dataStore.edit { prefs ->
            prefs[LAST_LATITUDE] = lat.toString()
            prefs[LAST_LONGITUDE] = lon.toString()
        }
    }

    suspend fun getLastLocation(): Pair<Double, Double>? {
        val prefs = context.dataStore.data.first()
        val lat = prefs[LAST_LATITUDE]?.toDoubleOrNull()
        val lon = prefs[LAST_LONGITUDE]?.toDoubleOrNull()
        return if (lat != null && lon != null) Pair(lat, lon) else null
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setDeviceAlertsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DEVICE_ALERTS_ENABLED] = enabled
        }
    }

    suspend fun setConnectionAlertsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[CONNECTION_ALERTS_ENABLED] = enabled
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(AUTH_TOKEN)
            prefs.remove(REFRESH_TOKEN)
            prefs.remove(USER_ID)
            prefs.remove(USER_EMAIL)
            prefs.remove(USER_NAME)
            prefs.remove(USER_PROFILE_IMAGE)
        }
    }
}
