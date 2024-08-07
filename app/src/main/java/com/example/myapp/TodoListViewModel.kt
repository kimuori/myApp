package com.example.myapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TodoListViewModel : ViewModel() {

    private val apiService: ApiService = ApiClient.apiService

    private val _descriptionString: MutableLiveData<String> = MutableLiveData("")
    val descriptionString: LiveData<String> = _descriptionString

    private val _completedBoolean: MutableLiveData<Boolean> = MutableLiveData(false) //false by default
    val completedBoolean: LiveData<Boolean> = _completedBoolean

    /*
        REQUEST BODY for POST /api/users/{user_id}/todos:
          "description": string
		  "completed": boolean
		  "meta": {} [ignore]

		NOTE: use apikey,
		use the provided user_id via "id" and auth the "token".
     */
    fun addTodoObject(
        description: String,
        completed: Boolean,
    ) {
        viewModelScope.launch {
            try{
                val response = apiService.createTodos(
                    apiKey= "7c020d82-368e-4d63-abbc-be98dc7e7730",
                    bearerToken= "token",
                    userId= 291,
                    TodoRequestBody(description, completed)
                )
                Log.d("LogInAccount", "Response: $response")
                //callback(response)
            } catch (error: retrofit2.HttpException) {
                Log.e("LogInAccount", "HTTP Error: ${error.code()}")
                //(null)
            } catch (error: Exception){
                //callback(null)
            }
        }
    }

    // No Response Body required
    fun showAllTodos() {
        viewModelScope.launch {
            try{
                val response = apiService.getAllTodos(
                    apiKey="7c020d82-368e-4d63-abbc-be98dc7e7730",
                    bearerToken = "token",
                    userId = 291
                )
                Log.d("LogInAccount", "Response: $response")
                //callback(response)
            } catch (error: retrofit2.HttpException) {
                Log.e("LogInAccount", "HTTP Error: ${error.code()}")
                //(null)
            } catch (error: Exception){
                //callback(null)
            }
        }
    }

}