package com.example.myapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CreateAccountViewModel : ViewModel() {

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

    private val _nameString: MutableLiveData<String> = MutableLiveData("")
    val nameString : LiveData<String> = _nameString

    private val _newEmailString: MutableLiveData<String> = MutableLiveData("")
    val newEmailString : LiveData<String> = _newEmailString

    private val _newPasswordString: MutableLiveData<String> = MutableLiveData("")
    val newPasswordString: LiveData<String> = _newPasswordString

    /*
        REQUEST BODY for POST /api/users/register:
            "email": "user1@mail.com",
            "name": "string", [ignore]
            "password": "password"
     */
    fun createAccount(
        email: String,
        name: String,
        password: String,
        callback: (User?) -> Unit
    ){
        viewModelScope.launch {
            _viewState.postValue(ViewState.Loading) //loading
            try{
                //Response Body returns User
                val responseBody = apiService.registerUser(
                    apiKey = "7c020d82-368e-4d63-abbc-be98dc7e7730",
                    RegisterRequestBody(email, name, password)
                )
                _viewState.postValue(ViewState.Success) //success state
                _userState.postValue(responseBody)
                Log.d("CreateAccount", "Response: $responseBody")
                callback(responseBody)
            } catch (error: retrofit2.HttpException) {
                _viewState.postValue(ViewState.Error("Failed HTTP Response")) //fail state
                Log.e("CreateAccount", "HTTP Error: ${error.code()}, ${error.message()}")
                callback(null)
            } catch (error: Exception){
                _viewState.postValue(ViewState.Error("Failed, other reasons")) //fail state
                error.printStackTrace()
                callback(null)
            }
        }
    }

}

/*
    fun createAccount(email: String, password: String, callback: (User?) -> Unit){
        viewModelScope.launch {
            try{
                val response = apiService.registerUser("7c020d82-368e-4d63-abbc-be98dc7e7730")
                Log.d("CreateAccount", "Response: $response")

                callback(response)
            } catch (error: retrofit2.HttpException) {
                Log.e("CreateAccount", "HTTP Error: ${error.code()}")
                callback(null)
            } catch (error: Exception){
                callback(null)
            }
        }
    }

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