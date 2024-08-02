package com.example.myapp

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CreateAccountViewModel (
    private val datastore: DataStore<Preferences>
) : ViewModel() {
    //todo

    val userIdFlow: Flow<Int?> = datastore.data.map { preferences ->
        preferences[PreferenceKeys.USER_ID]
    }

    val bearerToken: Flow<String?> = datastore.data.map { preferences ->
        preferences[PreferenceKeys.BEARER_TOKEN]
    }

    fun saveUserID(userID: Int){
        viewModelScope.launch(Dispatchers.IO){
            datastore.edit {
                preferences ->
                preferences[PreferenceKeys.USER_ID] = userID
            }
        }
    }

    fun saveBearerToken(bearerToken: String){
        viewModelScope.launch(Dispatchers.IO) {
            datastore.edit {
                preferences ->
                preferences[PreferenceKeys.BEARER_TOKEN] = bearerToken
            }
        }
    }
}