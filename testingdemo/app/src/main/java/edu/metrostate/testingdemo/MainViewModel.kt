package edu.metrostate.testingdemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val api: ApiService,
): ViewModel() {

    private val _userState = MutableLiveData<User>()
    val userState: LiveData<User> get() = _userState

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> get() = _viewState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _viewState.postValue(ViewState.Loading)
            try {
                val result = api.login(username, password)
                _viewState.postValue(ViewState.Success)
                _userState.postValue(result)
            } catch (ex: Exception) {
                ex.printStackTrace()
                _viewState.postValue(ViewState.Error("Failed"))
            }
        }
    }

    sealed class ViewState {
        data object Loading : ViewState()
        data class Error(val message: String) : ViewState()
        data object Success : ViewState()
    }

}