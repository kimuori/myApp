package com.example.myapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TodoListViewModel : ViewModel() {

    private val apiService: ApiService = ApiClient.apiService

    //Added for testing to-do lists state
    private val _todoListState = MutableLiveData<List<Todo>>()
    val todoListState: LiveData<List<Todo>> get() = _todoListState

    //Added for testing to-do state
    private val _todoState = MutableLiveData<Todo>()
    val todoState: LiveData<Todo> get() = _todoState

    //created to a view state
    private val _viewState = MutableLiveData<ViewState>()
    val viewStates: LiveData<ViewState> get() = _viewState

    sealed class ViewState {
        data object Loading : ViewState()
        data class Error(val message: String) : ViewState()
        data object Success : ViewState()
    }

    private val _descriptionString: MutableLiveData<String> = MutableLiveData("")
    val descriptionString: LiveData<String> = _descriptionString

    private val _completedBoolean: MutableLiveData<Boolean> = MutableLiveData(false) //false by default
    val completedBoolean: LiveData<Boolean> = _completedBoolean

    private val _liveList: MutableLiveData<List<Todo>> = MutableLiveData<List<Todo>>()
    val liveList: LiveData<List<Todo>> = _liveList

    //NOTE: No Request @Body required.
    fun showAllTodos(
        userId: String,
        bearerToken: String,
        callback: (List<Todo>?) -> Unit
    ) {
        viewModelScope.launch {
            _viewState.postValue(ViewState.Loading) //loading
            try{
                //Response Body returns List<To-do>
                val responseBody = apiService.getAllTodos(
                    apiKey = "7c020d82-368e-4d63-abbc-be98dc7e7730",
                    bearerToken = bearerToken,
                    userId = userId.toInt()
                )
                _viewState.postValue(ViewState.Success) //success state
                _todoListState.postValue(responseBody)
                Log.d("ShowAllTodoObjects", "Response: $responseBody")
                callback(responseBody)
            } catch (error: retrofit2.HttpException) {
                _viewState.postValue(ViewState.Error("Failed HTTP Response")) //error state
                Log.e("ShowAllTodoObjects", "HTTP Error: ${error.code()}, ${error.message()}")
                callback(null)
            } catch (error: Exception){
                _viewState.postValue(ViewState.Error("Failed, other reasons")) //error state
                callback(null)
            }
        }
    }

    /*
        REQUEST BODY for POST /api/users/{user_id}/todos:
          "description": string
		  "completed": boolean
		  "meta": {} [ignore]

		NOTE: use apikey,
		use the provided user_id via "id" and auth the "token".

		The "description" and "completed" parameters required for TodoRequestBody
     */
    fun addTodoObject(
        userId: String,
        bearerToken: String,
        description: String,
        completed: Boolean,
        callback: (Todo?) -> Unit
    ) {
        viewModelScope.launch {
            _viewState.postValue(ViewState.Loading) //loading
            try{
                //Response Body returns To-do object
                val responseBody = apiService.createTodos(
                    apiKey= "7c020d82-368e-4d63-abbc-be98dc7e7730",
                    bearerToken= bearerToken,
                    userId= userId.toInt(),
                    TodoRequestBody(description, completed)
                )
                _viewState.postValue(ViewState.Success) //success state
                _todoState.postValue(responseBody)
                Log.d("AddingTodoObject", "Response: $responseBody")
                callback(responseBody)
            } catch (error: retrofit2.HttpException) {
                _viewState.postValue(ViewState.Error("Failed HTTP Request"))
                Log.e("AddingTodoObject", "HTTP Error: ${error.code()}, ${error.message()}")
                callback (null)
            } catch (error: Exception){
                _viewState.postValue(ViewState.Error("Failed, other reason"))
                error.printStackTrace()
                callback(null)
            }
        }
    }

}