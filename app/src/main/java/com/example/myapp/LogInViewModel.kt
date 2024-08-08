package com.example.myapp

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class LogInViewModel () : ViewModel() {

    private val apiService: ApiService = ApiClient.apiService

    //Added for testing
    private val _userState = MutableLiveData<User>()
    val userState: LiveData<User> get() = _userState

    //created to a view state
    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> get() = _viewState

    sealed class ViewState {
        data object Loading : ViewState()
        data class Error(val message: String) : ViewState()
        data object Success : ViewState()
    }

    private suspend fun getUser(): User{
        return apiService.getUser()
    }

    private val _emailString: MutableLiveData<String> = MutableLiveData("")
    //val emailString : LiveData<String> = _emailString
    val emailString : LiveData<String>
        get() = _emailString

    fun getEmail(){
        viewModelScope.launch {
            val result = getUser()
            _emailString.postValue(result.email)
        }
    }

    private val _passwordString: MutableLiveData<String> = MutableLiveData("")
    val passwordString: LiveData<String> = _passwordString

    /*
        REQUEST BODY for POST /api/users/login:
          "email": "user1@mail.com",
		  "password": "password"

		"email" and "password" parameters required for LoginRequestBody
     */
    fun loginAccount(
        email: String,
        password: String,
        callback: (User?) -> Unit
    ) {
        viewModelScope.launch {
            _viewState.postValue(ViewState.Loading) //loading
            try{
                //Response Body returns User
                val responseBody = apiService.loginUser(
                    apiKey= "7c020d82-368e-4d63-abbc-be98dc7e7730",
                    LoginRequestBody(email, password)
                )
                _viewState.postValue(ViewState.Success) //success state
                _userState.postValue(responseBody) //show result for userState
                Log.d("LogInAccount", "Response: $responseBody")
                callback(responseBody)
            } catch (error: retrofit2.HttpException) {
                _viewState.postValue(ViewState.Error("Failed HTTP Response")) //error state
                Log.e("LogInAccount", "HTTP Error: ${error.code()}, ${error.message()}")
                callback(null)
            } catch (error: Exception){
                _viewState.postValue(ViewState.Error("Failed, other reasons")) //error state
                error.printStackTrace()
                callback(null)
            }
        }
    }
}
/*
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

     */