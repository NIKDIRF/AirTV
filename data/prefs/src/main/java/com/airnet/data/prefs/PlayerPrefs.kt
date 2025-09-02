package com.airnet.data.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerPrefs @Inject constructor(
    private val ds: DataStore<Preferences>
) {
    private val KEY_SPEED = floatPreferencesKey("speed")
    private val KEY_ZOOM = booleanPreferencesKey("zoom")
    private val KEY_LAST_ID = stringPreferencesKey("last_channel_id")

    val speedFlow = ds.data.map { it[KEY_SPEED] ?: 1f }
    val zoomFlow = ds.data.map { it[KEY_ZOOM] ?: false }
    val lastIdFlow = ds.data.map { it[KEY_LAST_ID] ?: "" }

    suspend fun setSpeed(v: Float) {
        ds.edit { it[KEY_SPEED] = v }
    }

    suspend fun setZoom(v: Boolean) {
        ds.edit { it[KEY_ZOOM] = v }
    }

    suspend fun setLastId(id: String) {
        ds.edit { it[KEY_LAST_ID] = id }
    }
}