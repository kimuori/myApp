package com.example.myapp

/*
    Reference:
    [Assignment 2]
    https://chintanjoshi1.medium.com/using-sharedpreferences-in-android-jetpack-compose-f8e970ffbf06
    https://stackoverflow.com/questions/77989288/error-implementing-shared-preferences-in-kotlin-jetpack-compose
    https://www.youtube.com/watch?v=tD0wi5sH0aQ

 */

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PreferencesManager (private val context: Context) {

    //only one instance of dataStore is necessary
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("UserTodoData")

        val USER_ID_KEY = stringPreferencesKey("user_id")
        val USER_TOKEN_KEY = stringPreferencesKey("bearerToken")

        val USER_EMAIL_KEY = stringPreferencesKey("email")
        val USER_PASS_KEY = stringPreferencesKey("password")

    }

    val getUserId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_TOKEN_KEY] ?: ""
        }

    suspend fun saveUserId (userId: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
        }
    }

    val getToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_TOKEN_KEY] ?: ""
        }

    suspend fun saveToken (bearerToken: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = bearerToken
        }
    }

    val getEmail: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_EMAIL_KEY] ?: ""
        }

    suspend fun saveEmail (email: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_EMAIL_KEY] = email
        }
    }

    val getPassword: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_PASS_KEY] ?: ""
        }

    suspend fun savePassword (password: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_PASS_KEY] = password
        }
    }

    /*
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

    fun saveData(key: String, value: String){
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getData(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue)?: defaultValue
    }

     */
}